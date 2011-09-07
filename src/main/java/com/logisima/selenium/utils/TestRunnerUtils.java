package com.logisima.selenium.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.plugin.MojoExecutionException;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ConfirmHandler;
import com.gargoylesoftware.htmlunit.DefaultPageCreator;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.PromptHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.javascript.host.Window;

public class TestRunnerUtils {

    public static void copySeleniumToDir(File seleniumTarget) throws MojoExecutionException {
        File zipFile = new File(TestRunnerUtils.class.getResource("selenium.zip").getFile());
        ZipFile myZip;
        try {
            myZip = new ZipFile(zipFile);
            unzipFileIntoDirectory(myZip, seleniumTarget);
        } catch (ZipException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public static void unzipFileIntoDirectory(ZipFile zipFile, File jiniHomeParentDir) {
        Enumeration files = zipFile.entries();
        File f = null;
        FileOutputStream fos = null;

        while (files.hasMoreElements()) {
            try {
                ZipEntry entry = (ZipEntry) files.nextElement();
                InputStream eis = zipFile.getInputStream(entry);
                byte[] buffer = new byte[1024];
                int bytesRead = 0;

                f = new File(jiniHomeParentDir.getAbsolutePath() + File.separator + entry.getName());

                if (entry.isDirectory()) {
                    f.mkdirs();
                    continue;
                }
                else {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }

                fos = new FileOutputStream(f);

                while ((bytesRead = eis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                // ignore
                continue;
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        }
    }

    public static WebClient getWebClient() {
        WebClient firephoque = new WebClient(BrowserVersion.INTERNET_EXPLORER_8);
        firephoque.setPageCreator(new DefaultPageCreator() {

            @Override
            public Page createPage(WebResponse wr, WebWindow ww) throws IOException {
                Page page = createHtmlPage(wr, ww);
                return page;
            }
        });
        firephoque.setThrowExceptionOnFailingStatusCode(false);
        firephoque.setAlertHandler(new AlertHandler() {

            public void handleAlert(Page page, String string) {
                try {
                    Window window = (Window) page.getEnclosingWindow().getScriptObject();
                    window.custom_eval("parent.selenium.browserbot.recordedAlerts.push('" + string.replace("'", "\\'")
                            + "');");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        firephoque.setConfirmHandler(new ConfirmHandler() {

            public boolean handleConfirm(Page page, String string) {
                try {
                    Window window = (Window) page.getEnclosingWindow().getScriptObject();
                    Object result = window.custom_eval("parent.selenium.browserbot.recordedConfirmations.push('"
                            + string.replace("'", "\\'") + "');"
                            + "var result = parent.selenium.browserbot.nextConfirmResult;"
                            + "parent.selenium.browserbot.nextConfirmResult = true;" + "result");
                    return (Boolean) result;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
        firephoque.setPromptHandler(new PromptHandler() {

            public String handlePrompt(Page page, String string) {
                try {
                    Window window = (Window) page.getEnclosingWindow().getScriptObject();
                    Object result = window.custom_eval("parent.selenium.browserbot.recordedPrompts.push('"
                            + string.replace("'", "\\'")
                            + "');"
                            + "var result = !parent.selenium.browserbot.nextConfirmResult ? null : parent.selenium.browserbot.nextPromptResult;"
                            + "parent.selenium.browserbot.nextConfirmResult = true;"
                            + "parent.selenium.browserbot.nextPromptResult = '';" + "result");
                    return (String) result;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }
        });
        firephoque.setThrowExceptionOnScriptError(false);
        return firephoque;
    }

}

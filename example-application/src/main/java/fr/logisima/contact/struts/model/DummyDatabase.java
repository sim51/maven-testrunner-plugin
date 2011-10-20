package fr.logisima.contact.struts.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DummyDatabase {

    private static int                maxId    = -1;

    private static ArrayList<Contact> contacts = new ArrayList<Contact>();

    public final static Contact get(int id) {
        return (Contact) contacts.get(id);
    }

    public final static synchronized void create(Contact contact) {
        maxId++;
        contact.setId(maxId);
        contacts.add(maxId, contact);
    }

    public final static void delete(int id) {
        if (contacts.get(id) == null)
            throw new NullPointerException();
        contacts.set(id, null);
    }

    public final static void update(Contact contact) {
        contacts.set(contact.getId(), contact);
    }

    public final static List<Contact> search(String searchString) {
        ArrayList<Contact> result = new ArrayList<Contact>();
        for (Iterator<Contact> iter = contacts.iterator(); iter.hasNext();) {
            Contact element = iter.next();
            if (element != null && removeAccents(element.toString()).indexOf(removeAccents(searchString)) > -1)
                result.add(element);
        }
        return result;
    }

    public final static List<Contact> list() {
        ArrayList<Contact> result = new ArrayList<Contact>();
        for (Iterator<Contact> iter = contacts.iterator(); iter.hasNext();) {
            Contact element = iter.next();
            if (element != null)
                result.add(element);
        }
        return result;
    }

    private final static String removeAccents(String string) {
        String s = string.toLowerCase();
        s = s.replaceAll("[éèêë]", "e");
        s = s.replaceAll("[ùü]", "u");
        s = s.replaceAll("[ïî]", "i");
        s = s.replaceAll("[àâ]", "a");
        s = s.replaceAll("ô", "o");
        s = s.replaceAll("ç", "c");
        return s;
    }

    static {
        Contact contact;
        contact = new Contact();
        contact.setNom("SIMARD");
        contact.setPrenom("Benoît");
        contact.setTelephone("02 40 00 00 00");
        create(contact);
        contact = new Contact();
        contact.setNom("LEPONGE");
        contact.setPrenom("Bob");
        contact.setTelephone("02 41 11 11 11");
        create(contact);
        contact = new Contact();
        contact.setNom("HENDRIX");
        contact.setPrenom("Jimmy");
        contact.setTelephone("02 51 12 12 12");
        create(contact);
        contact = new Contact();
        contact.setNom("MARLEY");
        contact.setPrenom("Bob");
        contact.setTelephone("02 20 13 13 13");
        create(contact);
    }
}

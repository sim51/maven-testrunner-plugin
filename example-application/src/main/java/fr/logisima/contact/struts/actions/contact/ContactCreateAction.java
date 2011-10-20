package fr.logisima.contact.struts.actions.contact;

import fr.logisima.contact.struts.actions.SmileActionSupport;
import fr.logisima.contact.struts.model.Contact;
import fr.logisima.contact.struts.model.DummyDatabase;

public class ContactCreateAction extends SmileActionSupport {

    private String id;

    private String prenom;

    private String nom;

    private String telephone;

    public String form() {
        return INPUT;
    }

    public String execute() {
        // Création
        Contact contact = new Contact();
        contact.setPrenom(prenom);
        contact.setNom(nom);
        contact.setTelephone(telephone);
        DummyDatabase.create(contact);
        return SUCCESS;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}

package models;

public class Publisher {
    public Publisher(String name, String mail) {
        this.name = name;
        this.mail = mail;
    }

    public String name;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String mail;


}

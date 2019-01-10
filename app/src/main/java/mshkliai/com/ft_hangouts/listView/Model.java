package mshkliai.com.ft_hangouts.listView;

import android.graphics.Bitmap;

public class Model {
    private Bitmap photo;
    private String name, number, mail;

    public Model(String name, String number, String mail, Bitmap photo) {
        this.name = name;
        this.number = number;
        this.mail = mail;
        this.photo = photo;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        if (number.matches("^[0-9]*$")) {
            return "+" + number;
        }
        return number;
    }

    public String getMail() {
        return mail;
    }

    public void setName(String title) {
        this.name = title;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
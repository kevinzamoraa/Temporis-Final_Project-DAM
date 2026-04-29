package com.kevinzamora.temporis_androidapp.model;

import java.io.Serializable;

public class User implements Serializable {

    private String username, displayName, email,password,profilePhotoUrl,QrCode,uid/*,description*/;
    private int rol;

    public User() {
        this.username = "";
        this.displayName = "";
        this.email = "";
        this.password = "";
        this.profilePhotoUrl = "";
        this.QrCode = "";
        this.uid = "";
        this.rol = 0;
    }

    public User(String uid, String username, String email, String displayName, /*String description,*/ String profilePhotoUrl) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        /*this.description = description;*/
        this.profilePhotoUrl = profilePhotoUrl;
    }

    // Getters y setters

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }

   /* public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }*/

    public String getProfilePhotoUrl() { return profilePhotoUrl; }

    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getQrCode() { return QrCode; }

    public void setQrCode(String qrCode) { this.QrCode = qrCode; }

    public int getRol() { return rol; }

    public void setRol(int rol) { this.rol = rol; }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                "displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", profilePhotoUrl='" + profilePhotoUrl + '\'' +
                ", QrCode='" + QrCode + '\'' +
                ", uid=" + uid +
                ", rol=" + rol +
                /*", decription=" + description +*/
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // Comparamos los campos clave (puedes añadir más si quieres)
        return uid != null ? uid.equals(user.uid) : user.uid == null;
    }

    @Override
    public int hashCode() {
        return uid != null ? uid.hashCode() : 0;
    }
}


package com.droidekamobile.models;

public class UserData {
    private String email;
    private String firstname;
    private String lastName;
    private String username;
    private String password;

    public UserData() {

    }

    public UserData(String email, String firstname, String lastName, String username, String password) {
        this.email = email;
        this.firstname = firstname;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



}

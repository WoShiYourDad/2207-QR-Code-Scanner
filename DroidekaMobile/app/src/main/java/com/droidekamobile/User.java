package com.droidekamobile;

public class User {
    public String email, firstName, lastName, username, password;

    public User(){

    }

    public User(String email, String firstName, String lastName, String username, String password){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }
}

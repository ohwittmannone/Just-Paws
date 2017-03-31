package com.ohwittmannone.just_paws.models;

import java.util.List;

/**
 * Created by Courtney on 2017-02-24.
 */

public class User {

    private String email;
    private String id;
    private String name;
    private List<String> favourite;
    private boolean admin;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFavourite() {
        return favourite;
    }

    public void setFavourite(List<String> favourite) {
        this.favourite = favourite;
    }



    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}

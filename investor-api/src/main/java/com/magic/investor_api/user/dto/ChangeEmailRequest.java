package com.magic.investor_api.user.dto;

public class ChangeEmailRequest {


    private String newEmail;

    public ChangeEmailRequest(){

    }

    public String getNewEmail(){
        return newEmail;
    }

    public void setNewEmail(String newEmail){
        this.newEmail = newEmail;
    }
}

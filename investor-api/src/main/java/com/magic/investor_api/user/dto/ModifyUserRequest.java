package com.magic.investor_api.user.dto;

public class ModifyUserRequest {

    private String newEmail;
    private String newPassword;
    private String oldPassword;

    public ModifyUserRequest(){}

    public String getNewEmail(){
        return newEmail;
    }

    public void setNewEmail(String newEmail){
        this.newEmail = newEmail;
    }

    public String getNewPassword(){
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}

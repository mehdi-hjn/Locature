package com.ensa.locature.main.models;

import com.google.gson.annotations.SerializedName;

public class Transaction {

    @SerializedName("message")
    private String mMessage;

    public String getMessage() {
        return mMessage;
    }
}

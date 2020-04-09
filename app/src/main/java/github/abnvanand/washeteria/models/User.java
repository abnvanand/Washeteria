package github.abnvanand.washeteria.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("username")
    private String username;
    @SerializedName("token")
    private String token;
    @SerializedName("expiry")
    private String expiry;



}

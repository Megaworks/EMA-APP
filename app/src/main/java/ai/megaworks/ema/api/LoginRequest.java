package ai.megaworks.ema.api;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("id")
    public Long inputId;

    @SerializedName("userId")
    public String inputUserId;

    @SerializedName("phoneNumber")
    public String phoneNumber;

    @SerializedName("firebaseTokenKey")
    public String firebaseToken;

    public LoginRequest(Long inputId, String inputUserId, String phoneNumber) {
        this.inputId = inputId;
        this.inputUserId = inputUserId;
        this.phoneNumber = phoneNumber;
    }

    public Long getInputId() {
        return inputId;
    }

    public void setInputId(Long inputId) {
        this.inputId = inputId;
    }

    public String getInputUserId() {
        return inputUserId;
    }

    public void setInputUserId(String inputUserId) {
        this.inputUserId = inputUserId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}

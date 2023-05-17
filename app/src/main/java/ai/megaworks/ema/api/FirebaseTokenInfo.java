package ai.megaworks.ema.api;

import com.google.gson.annotations.SerializedName;

public class FirebaseTokenInfo {
    @SerializedName("subjectId")
    public Long id;

    @SerializedName("token")
    public String firebaseToken;

    public FirebaseTokenInfo(Long id, String firebaseToken) {
        this.id = id;
        this.firebaseToken = firebaseToken;
    }
}

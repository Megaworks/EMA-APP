package ai.megaworks.ema.domain.firebase;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FirebaseTokenInfo {

    @SerializedName("subjectId")
    private Long id;

    @SerializedName("token")
    private String firebaseToken;

}

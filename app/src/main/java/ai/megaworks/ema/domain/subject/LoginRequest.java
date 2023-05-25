package ai.megaworks.ema.domain.subject;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LoginRequest {
    @SerializedName("id")
    public Long inputId;

    @SerializedName("userId")
    public String inputUserId;

    @SerializedName("phoneNumber")
    public String phoneNumber;
}

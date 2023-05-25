package ai.megaworks.ema.domain.subject;

import lombok.Getter;

@Getter
public class LoginResponse {

    private Long id;

    private String hospitalId;

    private String groupId;

    private String name;

    private String userId;

    private String phoneNumber;

    private String firebaseTokenKey;

}

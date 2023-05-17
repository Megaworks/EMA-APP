package ai.megaworks.ema.api;

import java.time.LocalDateTime;

public class LoginResponse {

    private Long id;

    private String hospitalId;

    private String groupId;

    private String name;

    private String userId;

    private String phoneNumber;

    private String firebaseTokenKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirebaseTokenKey() {
        return firebaseTokenKey;
    }

    public void setFirebaseTokenKey(String firebaseTokenKey) {
        this.firebaseTokenKey = firebaseTokenKey;
    }
}

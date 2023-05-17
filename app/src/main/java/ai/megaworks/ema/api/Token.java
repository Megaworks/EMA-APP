package ai.megaworks.ema.api;

import com.google.gson.annotations.SerializedName;

public class Token {

    @SerializedName("token")
    private String token;
    private Long id;
    private boolean lastphq9;
    private String userName;
    private String userType;

    private String userTel;

    public Token (){
        super();
    }

    public Token(String token, Long id) {
        this.token = token;
        this.id = id;
    }

    public Token(String token, Long id, boolean lastphq9) {
        this.token = token;
        this.id = id;
        this.lastphq9 = lastphq9;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isLastphq9() {
        return lastphq9;
    }
    public void setLastphq9(boolean lastphq9) {
        this.lastphq9 = lastphq9;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserTel() {
        return userTel;
    }

    public void setUserTel(String userTel) {
        this.userTel = userTel;
    }

}

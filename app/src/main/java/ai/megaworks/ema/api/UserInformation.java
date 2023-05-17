package ai.megaworks.ema.api;

import com.google.gson.annotations.SerializedName;

public class UserInformation {

    @SerializedName("id")
    private Long id;
    @SerializedName("password")
    private String password;
    @SerializedName("name")
    private String name;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("sex")
    private String sex;
    @SerializedName("usertype")
    private String usertype;
    @SerializedName("email")
    private String email;

    private String address;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String age) {
        this.nickname = nickname;
    }

    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUsertype() {
        return usertype;
    }
    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UserInformation(Long id, String password, String name, String nickname, String birthday, String sex, String usertype, String email) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.birthday = birthday;
        this.sex = sex;
        this.usertype = usertype;
        this.email = email;
    }

    public UserInformation(Long id, String name, String nickname, String birthday, String sex, String usertype, String email) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.birthday = birthday;
        this.sex = sex;
        this.usertype = usertype;
        this.email = email;
    }

    public UserInformation(Long id, String usertype, String address) {
        this.id = id;
        this.usertype = usertype;
        this.address = address;
    }

    public UserInformation(Long id, String password) {
        this.id = id;
        this.password = password;
    }

    public UserInformation(String email) {
        this.email = email;
    }
}

package ai.megaworks.ema.api;

public class UserPHQ9 {

    private Long id;
    private Integer phq9;
    private String created;
    private Integer suicideanswer;


    public UserPHQ9() {
    }

    public UserPHQ9(Long id, Integer phq9, String created) {
        this.id = id;
        this.phq9 = phq9;
        this.created = created;
    }

    public UserPHQ9(Long id, Integer phq9, Integer suicideanswer) {
        this.id = id;
        this.phq9 = phq9;
        this.suicideanswer = suicideanswer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPhq9() {
        return phq9;
    }

    public void setPhq9(Integer phq9) {
        this.phq9 = phq9;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Integer getSuicideanswer() {
        return suicideanswer;
    }

    public void setSuicideanswer(Integer suicideanswer) {
        this.suicideanswer = suicideanswer;
    }
}

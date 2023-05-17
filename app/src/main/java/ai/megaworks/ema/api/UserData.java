package ai.megaworks.ema.api;

public class UserData {

    private Long id;
    private Integer score;
    private String testtime;
    private Integer checktest;
    private Integer emotion;
    private Integer voice;
    private String created;
    private String content;
    private String adminname;
    private String commenttype;

    public UserData(Long id, Integer score, String testtime, Integer suicideanswer, Integer emotion, Integer voice) {
        this.id = id;
        this.score = score;
        this.testtime = testtime;
        this.checktest = suicideanswer;
        this.emotion = emotion;
        this.voice = voice;
    }


    public UserData(Long id, Integer emotion, Integer voice, Integer score) {
        this.id = id;
        this.emotion = emotion;
        this.voice = voice;
        this.score = score;
    }

    public UserData(Long id, String testtime, Integer emotion, Integer voice) {
        this.id = id;
        this.testtime = testtime;
        this.emotion = emotion;
        this.voice = voice;
    }

    public UserData(Long id, String created, String content, String adminname, String commenttype) {
        this.id = id;
        this.created = created;
        this.content = content;
        this.adminname = adminname;
        this.commenttype = commenttype;
    }

    public UserData(String testtime, String created) {
        this.testtime = testtime;
        this.created = created;
    }

    public UserData(Long id, Integer score) {
        this.id = id;
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAdminname() {
        return adminname;
    }

    public void setAdminname(String adminname) {
        this.adminname = adminname;
    }

    public String getCommenttype() {
        return commenttype;
    }

    public void setCommenttype(String commenttype) {
        this.commenttype = commenttype;
    }

    public UserData(){}

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getTesttime() {
        return testtime;
    }

    public void setTesttime(String testtime) {
        this.testtime = testtime;
    }
    public Integer getSuicideanswer() {
        return checktest;
    }

    public void setSuicideanswer(Integer suicideanswer) {
        this.checktest = suicideanswer;
    }

    public Integer getEmotion() {
        return emotion;
    }
    public void setEmotion(Integer emotion) {
        this.emotion = emotion;
    }
    public void setVoice(Integer voice) {
        this.voice = voice;
    }
    public Integer getVoice() {
        return voice;
    }

    public Integer getAmEmotion(){
        if (testtime.equals("AM")){
            return emotion;
        }else{
            return 0;
        }

    }
    public Integer getPmEmotion(){
        if (testtime.equals("PM")){
            return emotion;
        }else{
            return 0;
        }

    }

}

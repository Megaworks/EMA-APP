package ai.megaworks.ema.api;

public class CommentData {
    private Long id;
    private Integer score;
    private Integer month;
    private Integer week;
    private String created;
    private String comment;

    public CommentData(Long id, Integer score, Integer month, Integer week, String comment) {
        this.id = id;
        this.score = score;
        this.month = month;
        this.week = week;
        this.comment = comment;
    }

    public CommentData(Long id, Integer month, Integer week) {
        this.id = id;
        this.month = month;
        this.week = week;
    }

    public CommentData(Long id, String created) {
        this.id = id;
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

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

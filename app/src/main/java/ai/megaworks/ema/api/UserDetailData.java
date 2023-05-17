package ai.megaworks.ema.api;

public class UserDetailData {

    private String amemotion;
    private String pmemotion;
    private String amvoice;
    private String pmvoice;
    private String created;
    private String phq9;
    private String content;

    public UserDetailData(){}

    public UserDetailData(String amemotion, String pmemotion, String amvoice, String pmvoice, String created) {
        this.amemotion = amemotion;
        this.pmemotion = pmemotion;
        this.amvoice = amvoice;
        this.pmvoice = pmvoice;
        this.created = created;
    }

    public UserDetailData(String created, String phq9) {
        this.created = created;
        this.phq9 = phq9;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhq9() {
        return phq9;
    }

    public void setPhq9(String phq9) {
        this.phq9 = phq9;
    }

    public String getAmemotion() {
        return amemotion;
    }

    public void setAmemotion(String amemotion) {
        this.amemotion = amemotion;
    }

    public String getPmemotion() {
        return pmemotion;
    }

    public void setPmemotion(String pmemotion) {
        this.pmemotion = pmemotion;
    }

    public String getAmvoice() {
        return amvoice;
    }

    public void setAmvoice(String amvoice) {
        this.amvoice = amvoice;
    }

    public String getPmvoice() {
        return pmvoice;
    }

    public void setPmvoice(String pmvoice) {
        this.pmvoice = pmvoice;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}

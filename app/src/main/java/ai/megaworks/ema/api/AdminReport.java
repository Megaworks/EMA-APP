package ai.megaworks.ema.api;

public class AdminReport {

    private Integer emotionLow;         // 기분 온도계 힘듦
    private Integer emotionMid;         // 기분 온도계 평범
    private Integer emotionHigh;      // 기분 온도계 좋음
    private Integer voiceNomal;         // 음성 테스트 보통
    private Integer voiceBad;         // 음성 테스트 주의

    public AdminReport(Integer emotionLow, Integer emotionMid, Integer emotionHigh, Integer voiceNomal, Integer voiceBad) {
        this.emotionLow = emotionLow;
        this.emotionMid = emotionMid;
        this.emotionHigh = emotionHigh;
        this.voiceNomal = voiceNomal;
        this.voiceBad = voiceBad;
    }

    public Integer getEmotionLow() {
        return emotionLow;
    }

    public void setEmotionLow(Integer emotionLow) {
        this.emotionLow = emotionLow;
    }

    public Integer getEmotionMid() {
        return emotionMid;
    }

    public void setEmotionMid(Integer emotionMid) {
        this.emotionMid = emotionMid;
    }

    public Integer getEmotionHigh() {
        return emotionHigh;
    }

    public void setEmotionHigh(Integer emotionHigh) {
        this.emotionHigh = emotionHigh;
    }

    public Integer getVoiceNomal() {
        return voiceNomal;
    }

    public void setVoiceNomal(Integer voiceNomal) {
        this.voiceNomal = voiceNomal;
    }

    public Integer getVoiceBad() {
        return voiceBad;
    }

    public void setVoiceBad(Integer voiceBad) {
        this.voiceBad = voiceBad;
    }
}

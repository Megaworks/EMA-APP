package ai.megaworks.ema;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class Score {
    private int temperatureScore;
    private int voiceScore;
    private int phq9Score;
    private int[] phq9Scores;
    private int totTempAm;
    private int totTempPm;
    private float avgVoice;
    private float avgTemp;
    private float totScore;

    private AppCompatActivity AppActivity;

    public Score(){}

    public Score(AppCompatActivity AppActivity, int temperatureScore, int voiceScore){
        this.AppActivity = AppActivity;
        this.temperatureScore = temperatureScore;
        this.voiceScore = voiceScore;
    }

    public Score (AppCompatActivity AppActivity, int temperatureScore, int voiceScore, int phq9Score){
        this.AppActivity = AppActivity;
        this.temperatureScore = temperatureScore;
        this.voiceScore = voiceScore;
        this.phq9Score = phq9Score;
    }

    public Score(AppCompatActivity AppActivity, int totTempAm, int totTempPm, float avgVoice) {
        this.AppActivity = AppActivity;
        this.totTempAm = totTempAm;
        this.totTempPm = totTempPm;
        this.avgVoice = avgVoice;
    }
    public Score(int totTempAm, int totTempPm, float avgVoice) {
        this.totTempAm = totTempAm;
        this.totTempPm = totTempPm;
        this.avgVoice = avgVoice;
    }

    public Score(AppCompatActivity AppActivity, float avgTemp, float avgVoice) {
        this.AppActivity = AppActivity;
        this.avgTemp = avgTemp;
        this.avgVoice = avgVoice;
    }

    public Score(float totScore) {
        this.totScore = totScore;
    }

    public float getTotalScore(){
        float totEmotion = ((this.totTempAm/7) + (this.totTempPm/7))/2;
        return totEmotion * this.avgVoice;
    }

    public String getFreudComment(int totScore){
        String[] evals1 = AppActivity.getResources().getStringArray(R.array.one_week_eval);
//        float totScore = this.avgTemp * this.avgVoice;

        String result = "AI 프로이드가 본 당신의 기분 점수는 ";
        if (totScore >= 61){
            result += ((int)totScore +"점으로 "+evals1[0]);
        }else if (totScore >= 35){
            result += ((int)totScore +"점으로 "+evals1[1]);
        }else {
            result += ((int)totScore +"점으로 "+evals1[2]);
        }

        if (avgTemp < 4 && avgVoice < 6){
            result = "AI 프로이드가 본 당신의 기분 점수는 ";
            result += ((int)totScore +"점으로 "+evals1[3]);
        }
        Log.d("FREUD_DATA_total",totScore+"/"+result);
        return result;
    }

    public String[] getOneWeekResult(){
        String[] evals1 = AppActivity.getResources().getStringArray(R.array.one_week_eval);
        int totalScore = temperatureScore * voiceScore;
        String[] results = new String[3];
        String result = "정상";
        String comment = "도움이 필요합니다.";

        if (totalScore >= 61){
            result = "정상";
            comment = evals1[0];
        }else if (totalScore >= 35){
            result = "우울의심";
            comment = evals1[1];
        }else {
            result = "우울";
            comment = evals1[2];
        }

        // 심각한 우울
        if (temperatureScore <= 4 && voiceScore <= 6){
            result = "심각한 우울";
            comment = evals1[3];
        }
        results[0] = result;
        results[1] = comment;
        results[2] = String.valueOf(totalScore);

        return results;
    }

    public String[] getTwoWeekResult(){
        String[] evals2 = AppActivity.getResources().getStringArray(R.array.two_week_eval);
        String[] result = getOneWeekResult();
        String[] results = new String[2];
        String result2 = "정상";
        String comment = "잘 하고 있습니다.";

        if (result[0].equals("정상")){
            if (phq9Score <= 4){
                result2 = "정상";
                comment = evals2[0];
            }else if (phq9Score <= 9){
                result2 = "정상(우울고려)";
                comment = evals2[1];
            }else if (phq9Score <= 19){
                result2 = "??";
                comment = evals2[2];
            }else if (phq9Score <= 27){
                result2 = "??";
                comment = evals2[3];
            }

        }else if(result[0].equals("우울의심")) {
            if (phq9Score <= 4) {
                result2 = "mild depression";
                comment = evals2[4];
            } else if (phq9Score <= 9) {
                result2 = "mild depression";
                comment = evals2[5];
            } else if (phq9Score <= 19) {
                result2 = "moderate depression";
                comment = evals2[6];
            } else if (phq9Score <= 27) {
                result2 = "moderate ~ severe depression";
                comment = evals2[7];
            }

        }else if(result[0].equals("우울")) {
            if (phq9Score <= 4) {
                result2 = "faking good";
                comment = evals2[8];
            } else if (phq9Score <= 9) {
                result2 = "moderate depression";
                comment = evals2[9];
            } else if (phq9Score <= 19) {
                result2 = "moderate depression";
                comment = evals2[10];
            } else if (phq9Score <= 27) {
                result2 = "severe depression";
                comment = evals2[11];
            }

        }else if(result[0].equals("심각한 우울")) {
            if (phq9Score <= 4) {
                result2 = "faking good";
                comment = evals2[12];
            } else if (phq9Score <= 9) {
                result2 = "severe depression";
                comment = evals2[13];
            } else if (phq9Score <= 19) {
                result2 = "severe depression";
                comment = evals2[14];
            } else if (phq9Score <= 27) {
                result2 = "severe depression";
                comment = evals2[15];
            }
        }
        results[0] = result2;
        results[1] = comment;

        return results;
    }

    public int getPhq9Score() {
        return phq9Score;
    }

    public void setPhq9Score(int phq9Score) {
        this.phq9Score = phq9Score;
    }
}

package ai.megaworks.ema.user;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;
import ai.megaworks.ema.Score;
import ai.megaworks.ema.api.CommentData;
import ai.megaworks.ema.api.MindcareApi;
import ai.megaworks.ema.api.RetrofitClient;
import ai.megaworks.ema.api.UserData;
import ai.megaworks.ema.api.UserPHQ9;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreudFeedbackActivity extends AppCompatActivity {

    private int totTempAm = 0;
    private int totTempPm = 0;
    private float avgVoice = 0;
    private float avgTemp = 0;
    private int totalScore = 0;
    private LocalDateTime periodDate = null;
    private Score score = new Score();

    private List<UserData> userData;
    private LinearLayout back,call, message1;  // 뒤로가기 버튼, 응급상담 요청, 메시지 보내기
    private AppCompatButton btnNext;  // 확인
    private ScrollView scrollView;
    private TextView title, totalResult, period;
    private CommentData setCommentDataValues = null;

    private MindcareApi mindcareApi = RetrofitClient.getRetrofitInterface();
    private RetrofitClient retrofitClient = RetrofitClient.getInstance();

    // 안드로이드 뒤로가기 버튼 기능
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frued_feedback);

        back = findViewById(R.id.back);
        btnNext = findViewById(R.id.btnNext);
        call = findViewById(R.id.call);
        totalResult = findViewById(R.id.totalResult);
        period = findViewById(R.id.period);
        scrollView = findViewById(R.id.scrollView);
        message1 = findViewById(R.id.message1);

//        getUserData();

        Intent getIntent = getIntent();
        totTempAm = getIntent.getIntExtra("totTempAM", 0);
        totTempPm = getIntent.getIntExtra("totTempPM", 0);
        avgVoice = getIntent.getFloatExtra("avgVoice", 0);
        avgTemp = getIntent.getFloatExtra("avgTemp", 0);
        periodDate = (LocalDateTime) getIntent.getSerializableExtra("periodDate");

        Log.d("FREUD_DATA",totTempAm+"//"+totTempPm+"//"+avgVoice+"//"+avgTemp);
        score = new Score(this, avgTemp, avgVoice);
        totalScore = (int) (avgTemp * avgVoice);
        Log.d("FREUD_COMMENT",score.getFreudComment(totalScore));
        if (totalScore <= 0){
            // 지난 데이터가 부족할떄
            totalResult.setText("지난주의 결과가 부족하여 분석할 수 없습니다.");
            period.setVisibility(View.INVISIBLE);
        } else {
            int month = getWeekValue(Global.DATE_FORMATTER2.format(Global.today))[0];
            int week = getWeekValue(Global.DATE_FORMATTER2.format(Global.today))[1];
//            int totalScore = (int) (avgTemp * avgVoice);
            Log.d("FREUD_COMMENT33333", score.getFreudComment(totalScore));
            totalResult.setText(score.getFreudComment(totalScore));
            if (periodDate != null) {
                period.setText(Global.getMondayOfWeek(Global.DATE_FORMATTER2.format(periodDate)) + " ~ " + Global.getMondayOfWeek(Global.DATE_FORMATTER2.format(periodDate)).plusDays(6));
            }
            setCommentDataValues = new CommentData(Global.TOKEN.getId(), totalScore, month, week, score.getFreudComment(totalScore));
            CommentData commentData = new CommentData(Global.TOKEN.getId(), month, week);
            getCommentData(commentData);
        }

        message1.setOnClickListener(view -> {
            Dialog dialog = new Dialog(view.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_message);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            dialog.findViewById(R.id.ok).setOnClickListener(v1 -> {

                EditText editText = (EditText) dialog.findViewById(R.id.content);
                Log.d("MESSAGE",editText.getText().toString());
                // 메시지 보내기
            });

            dialog.findViewById(R.id.cancel).setOnClickListener(v1 -> {
                dialog.dismiss();
            });
        });


        // 뒤로가기 리스너
        back.setOnClickListener(view -> finish());

        // 확인 리스너
        btnNext.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

        // 전화연결
        call.setOnClickListener(view -> {
            Intent tt = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:1577-0199"));
            try {
                startActivity(tt);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "전화 연결을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }

        });
        getPHQ9Data();
//        getTwoWeeks();

    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }


    public void getCommentData(CommentData commentData) {
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        mindcareApi.getCommentData("Bearer " + Global.TOKEN.getToken(), commentData).enqueue(new Callback<List<CommentData>>() {
            @Override
            public void onResponse(Call<List<CommentData>> call, Response<List<CommentData>> response) {
                if (response.isSuccessful()) {
                    Log.d("GET_COMMENT", response.body().size() + "");
                    List<CommentData> getCmt = response.body();
                    if (response.body().size() == 0) { // 값 없음
                        Log.d("SET_COMMENT", setCommentDataValues.getId() + "/" + setCommentDataValues.getMonth());
                        setCommentData(setCommentDataValues);
                    } else { // 값 있음
                        Log.d("GET_CREATED_CMT", getCmt.get(0).getCreated());
                        Log.d("GET_COMMENT_ok", "   ");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CommentData>> call, Throwable t) {

            }
        });
    }
    // 해당 일의 월, 주차 구하기
    public int[] getWeekValue(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date value = null;
        try {
            value = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        int[] result = new int[2];
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        result[0] = month;
        result[1] = week;

        return result;
    }

    public void setCommentData(CommentData commentData) {
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        mindcareApi.setCommentData("Bearer " + Global.TOKEN.getToken(), commentData).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Log.d("SETCOMMENT_DATA", response.body() + "");
//                    period.setText();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    public void getPHQ9Data() {
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        mindcareApi.getUserPHQ9("Bearer " + Global.TOKEN.getToken(), Global.TOKEN.getId()).enqueue(new Callback<List<UserPHQ9>>() {
            @Override
            public void onResponse(Call<List<UserPHQ9>> call, Response<List<UserPHQ9>> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                    List<UserPHQ9> userPHQ9s = response.body();
                    if (userPHQ9s.size() == 1) {
                        Log.d("GET_PHQ1", userPHQ9s.get(0).getCreated());
                    } else if (userPHQ9s.size() > 1) {
                        // phq9 이력이 2개이상 && 최근 이력(created)와 같은 주 일 경우 프로이드 피드백 2주차 결과로 판단
                        if (Global.getWeekValue(userPHQ9s.get(0).getCreated()) == Global.getWeekValue(Global.DATE_FORMATTER2.format(Global.today))) {
                            Log.d("GET_PHQ2", userPHQ9s.get(0).getPhq9() + "/" + totalScore);
                            score.setPhq9Score(userPHQ9s.get(0).getPhq9());
                            String[] results = score.getTwoWeekResult();
                            Log.d("GET_RESULTS", results[1]);

                        }
//                        for (int i=0; i<userPHQ9s.size();i++){
//                            Log.d("GET_PHQ2",userPHQ9s.get(i).getCreated()+"//"+i+"// today"+Global.today);
//                        }
                    }

                    Log.d("USER_PHQ9", userPHQ9s.size() + "");
                }
            }

            @Override
            public void onFailure(Call<List<UserPHQ9>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.checkedNetwork(this);
    }

    public void getTwoWeeks(){
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        mindcareApi.getTwoWeeksValue("Bearer " + Global.TOKEN.getToken(),Global.TOKEN.getId()).enqueue(new Callback<List<UserData>>() {
            @Override
            public void onResponse(Call<List<UserData>> call, Response<List<UserData>> response) {

            }

            @Override
            public void onFailure(Call<List<UserData>> call, Throwable t) {

            }
        });
    }
}

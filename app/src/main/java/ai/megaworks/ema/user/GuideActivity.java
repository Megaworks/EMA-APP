package ai.megaworks.ema.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;
import ai.megaworks.ema.api.MindcareApi;
import ai.megaworks.ema.api.RetrofitClient;
import ai.megaworks.ema.api.SurveyResultRequest;
import ai.megaworks.ema.api.UserData;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuideActivity extends AppCompatActivity {

    private LinearLayout back;

    private TextView title;
    private LinearLayout check_box_1, check_box_3;
    private TextView check1, check3, check1_comment, check3_comment, check_end1, check_end1_2, check_end1_3, check_end1_4, check_end2, checklist_date;
    private FrameLayout check_icon_1, check_icon_3;
    private AppCompatButton btnNext;
    private MediaPlayer mediaPlayer;


    RetrofitClient retrofitClient = RetrofitClient.getInstance();
    MindcareApi mindcareApi = RetrofitClient.getRetrofitInterface();

    // 검사 유형
    String testType = null;
    String tempValue = null;
    String voiceValue = null;
    String phq9Value = null;
    String testtime = null;
    int phq9score = 0;

    LocalDateTime today = LocalDateTime.now();

    // 안드로이드 뒤로가기 버튼 기능
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        back = findViewById(R.id.back);
        btnNext = findViewById(R.id.btnNext);
        title = findViewById(R.id.title);
        check_box_1 = findViewById(R.id.check_box_1);
        check_box_3 = findViewById(R.id.check_box_3);
        check1 = findViewById(R.id.check1);
        check3 = findViewById(R.id.check3);
        check1_comment = findViewById(R.id.check1_comment);
        check3_comment = findViewById(R.id.check3_comment);
        checklist_date = findViewById(R.id.checklist_date);
        check_end1 = findViewById(R.id.check_end1);
        check_end1_2 = findViewById(R.id.check_end1_2);
        check_end1_3 = findViewById(R.id.check_end1_3);
        check_end1_4 = findViewById(R.id.check_end1_4);
        check_end2 = findViewById(R.id.check_end2);
        check_icon_1 = findViewById(R.id.check_icon_1);
        check_icon_3 = findViewById(R.id.check_icon_3);

        check_icon_1.setVisibility(View.INVISIBLE);
        check_icon_3.setVisibility(View.INVISIBLE);

        check_end1.setVisibility(View.GONE);
        check_end1_2.setVisibility(View.GONE);
        check_end1_3.setVisibility(View.GONE);
        check_end1_4.setVisibility(View.GONE);
        check_end2.setVisibility(View.GONE);


        // 오늘 날짜
        testtime = Global.DATETIME_FORMATTER.format(today);
        checklist_date.setText(Global.DATE_FORMATTER.format(today));

        // 뒤로가기 리스너
        back.setOnClickListener(view -> finish());

        // 검사 유형에 따라 안내 화면 설정
        Intent intent = getIntent();
        tempValue = getIntent().getStringExtra("tempValue");
        voiceValue = getIntent().getStringExtra("voiceValue");
        testType = intent.getStringExtra("testType");
        phq9score = intent.getIntExtra("phq9score", 0);
        String results = intent.getStringExtra("results");
        String type = intent.getStringExtra("Type");

        title.setText("오전 검사 안내");
        check1_comment.setVisibility(View.VISIBLE);
//            check_box_1.setBackgroundResource(R.drawable.layout_bg_pink);
//            check1.setTextColor(Color.WHITE);

        /* 음성파일 시작 화면에서 재생*/
        if (Global.btnVoice) {
            mediaPlayer = MediaPlayer.create(GuideActivity.this, R.raw.test_start); //220922 soyi Kim
            mediaPlayer.start(); //220922 soyi Kim
        }



        // 결과화면일때
        if (testType.equals("AM4")) {
//            long startTime = intent.getLongExtra("time",0);
//            long endTime = System.currentTimeMillis();
//            int requiredTime = ((int) (endTime-startTime) )/1000;
            title.setText("오전 검사 종료");
            check_icon_1.setVisibility(View.VISIBLE);
            check_icon_3.setVisibility(View.VISIBLE);
            check_end1.setVisibility(View.VISIBLE);
            check_end1_2.setVisibility(View.VISIBLE);
            check_end1_3.setVisibility(View.VISIBLE);
            check_end1_4.setVisibility(View.VISIBLE);
            check_end2.setVisibility(View.VISIBLE);
            SharedPreferences sharedPreferences = getSharedPreferences("Test", MODE_PRIVATE);
            phq9Value = sharedPreferences.getString("PHQ9", null);
            Log.d("DDDDDD", phq9Value + "");
//            Score score = new Score(this,Integer.parseInt(tempValue),Integer.parseInt(voiceValue),Integer.parseInt(phq9Value));
//            String[] resultValue = score.getOneWeekResult();
//            Integer totalScore = Integer.parseInt(resultValue[2]);
//            Log.d("TOTALDATA",tempValue+"/"+voiceValue+"/"+phq9Value+"/"+totalScore);
//            check_end1.setText("기분 온도 : "+tempValue+"점   |  음성 테스트 결과 : "+(voiceValue.equals("0")?"보통":"주의"));
            check_end1_2.setText(tempValue + "점");

//            UserData userData = new UserData(Global.TOKEN.getId(), "AM", Integer.parseInt(tempValue), Integer.parseInt(voiceValue)); //
//            setUserData(userData);
            btnNext.setText("확인");

            /* 음성파일 종료 화면에서 재생*/
            if (Global.btnVoice) {
                mediaPlayer = MediaPlayer.create(GuideActivity.this, R.raw.test_end); //220922 soyi Kim
                mediaPlayer.start(); //220922 soyi Kim
            }


        }

        btnNext.setOnClickListener(view -> {
            if (testType.equals("AM1")) {
                // 오전 온도계로 이동
                Intent intent2 = new Intent(getApplicationContext(), TemperatureAMActivity.class);
//                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                startActivity(intent2);
//                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);

            } else if (testType.equals("AM2")) {
                // 오전 음성1
                Intent intent2 = new Intent(getApplicationContext(), RecordAMActivity.class);
                intent2.putExtra("tempValue", intent.getStringExtra("tempValue"));
                startActivity(intent2);

            } else if (testType.equals("AM4")) {
                // 홈화면으로 이동



                sendSurveyRequest(Global.TOKEN.getId(), 5L, tempValue, Global.strDate);



            }
        });

//        // 확인 리스너
//        btnNext.setOnClickListener(view -> {
//            Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent2);
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        /* 음성파일 화면 벗어나면 정지 */
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.checkedNetwork(this);
    }


/*
    //onCreate()안에 선언했으면 이부분 없애야 함.
    //220922 soyi Kim
    @Override
    protected void onPause() {
        super.onPause();
        *//* 음성파일 화면 벗어나면 정지 *//*
        mediaPlayer.stop();
        mediaPlayer.reset();
    }*/

//    //220922 soyi Kim
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        /* 음성파일 시스템 리소스 정리 (런타임 오류나서 숨김처리함) */
//        mediaPlayer.release();
//    }

//    @Override
//    public void onBackPressed() {
//        backKeyHandler.onBackPressed();
//    }


    public void sendSurveyRequest(Long subjectId, Long surveyId, String answer, String surveyAt){

        String endpoint = "survey/result";
        String postUrl = Global.AI_SERVER_URL+endpoint;
//        String postUrl= "http://"+ipv4Address+":"+portNumber+"/"+endpoint;

        File mergePath = new File(getExternalCacheDir().getAbsolutePath()+"/test1.wav");

        RequestBody postBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("subjectId", subjectId.toString())
                .addFormDataPart("surveyId", surveyId.toString())
                .addFormDataPart("surveyAt", surveyAt)
                .addFormDataPart("answer",answer)
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }


    public void setUserData(UserData userData) {
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        mindcareApi.setUserData("Bearer " + Global.TOKEN.getToken(), userData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("GETUSERDATA", response.isSuccessful()+"");
                    Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent2);

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "서버와 연결이 불안정합니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                Log.d("GETUSERDATA", t.toString());
            }
        });
    }

}

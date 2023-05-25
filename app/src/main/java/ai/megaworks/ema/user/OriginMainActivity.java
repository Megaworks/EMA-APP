package ai.megaworks.ema.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;
import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;
import ai.megaworks.ema.domain.survey.SurveyResultRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OriginMainActivity extends AppCompatActivity {

    private LinearLayout am_banner_contents, pm1_banner_contents, pm2_banner_contents;
    private FrameLayout btnAM, btnPM1, btnPM2;

    private TextView todayDate, user_name;
    private ImageView user_btn;

    private RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private IEmaService iEmaService = RetrofitClient.getRetrofitInterface();

    // 안드로이드 뒤로가기 버튼 기능
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    /* permission */
    static final int PERMISSIONS_REQUEST = 0x0000001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_org);

        user_btn = findViewById(R.id.user_btn);
        user_name = findViewById(R.id.user_name);

        btnAM = findViewById(R.id.btnAM);
        btnPM1 = findViewById(R.id.btnPM1);
        btnPM2 = findViewById(R.id.btnPM2);

        todayDate = findViewById(R.id.todayDate);

        am_banner_contents = findViewById(R.id.am_banner_contents);
        pm1_banner_contents = findViewById(R.id.pm1_banner_contents);
        pm2_banner_contents = findViewById(R.id.pm2_banner_contents);

        am_banner_contents.bringToFront(); // 오전/오후 검사 안에 글씨 최상단으로
        pm1_banner_contents.bringToFront();
        pm2_banner_contents.bringToFront();

        btnAM.setClipToOutline(true); // 오전/오후 검사 배경 모서리 둥글게
        btnPM1.setClipToOutline(true);
        btnPM2.setClipToOutline(true);

        buttonSetting();

        // 마이 페이지
        user_btn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
            startActivity(intent);
        });

        // 오늘 날짜 설정
        todayDate.setText(Global.dateToString(Global.DATETIME_FORMATTER1));
    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Global.TOKEN.getSubjectName() != null) {
            user_name.setText(Global.TOKEN.getSubjectName() + "님");
        }
    }

    private void buttonSetting() {
        checkAvailableSaveResult(2L, btnAM);
        checkAvailableSaveResult(3L, btnPM1);
        checkAvailableSaveResult(4L, btnPM2);
    }

    private void checkAvailableSaveResult(Long subSurveyId, FrameLayout button) {
        SurveyResultRequest request = SurveyResultRequest.builder()
                .subSurveyId(subSurveyId)
                .surveyAt(Global.dateToString(Global.DATE_FORMATTER2))
                .surveySubjectId(Global.TOKEN.getSurveySubjectId()).build();

        iEmaService.checkAvailableSaveResult(request).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Boolean result = response.body();
                    if (result) {
                        button.setOnClickListener(view -> {
                            moveToActivity(GuideActivity.class);
                        });
                    } else {
                        button.setAlpha(0.6f);
                        button.setOnClickListener(view -> {
                            Toast.makeText(getApplicationContext(), getString(R.string.already_done_survey), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });

    }


    private void moveToActivity(Class clazz) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);
    }
}

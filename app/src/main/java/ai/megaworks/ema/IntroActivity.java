package ai.megaworks.ema;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;
import ai.megaworks.ema.domain.subject.LoginRequest;
import ai.megaworks.ema.domain.subject.LoginResponse;
import ai.megaworks.ema.domain.survey.SurveySubjectRequest;
import ai.megaworks.ema.domain.survey.SurveySubjectResponse;
import ai.megaworks.ema.user.BackKeyHandler;
import ai.megaworks.ema.user.GuideActivity;
import ai.megaworks.ema.user.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    private RetrofitClient retrofitClient = RetrofitClient.getInstance();
    private IEmaService iEmaService = RetrofitClient.getRetrofitInterface();

    // 안드로이드 뒤로가기 버튼 기능
    private final BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        new Handler().postDelayed(() -> {

            // 자동 로그인 확인하기 위함
            SharedPreferences sharedPreferences = getSharedPreferences("USERINFO", MODE_PRIVATE);

            if (sharedPreferences.getLong("subjectId", 0L) != 0L) {
                Long subjectIdInfo = sharedPreferences.getLong("subjectId", 0L);
                String subjectTelInfo = sharedPreferences.getString("subjectTel", null);
                String inspectIdInfo = sharedPreferences.getString("inspectId", null);
                Global.TOKEN.setSubjectId(subjectIdInfo);
                Global.TOKEN.setInspectId(subjectTelInfo);
                Global.TOKEN.setSubjectTel(inspectIdInfo);

                login(subjectIdInfo, inspectIdInfo, subjectTelInfo);
            } else {
                moveToActivity(LoginActivity.class);
            }
        }, 1000);// 1초 정도 딜레이를 준 후 시작

    }

    public void login(Long subjectId, String inspectId, String subjectTel) {

        LoginRequest loginRequest = new LoginRequest(subjectId, inspectId, subjectTel);
        iEmaService.getLoginResponse(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d(TAG, response.isSuccessful() + "");
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse == null)
                        moveToActivity(LoginActivity.class);
                    SharedPreferences sharedPreferences = getSharedPreferences("USERINFO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putLong("subjectId", subjectId);
                    editor.putString("subjectTel", subjectTel);
                    editor.putString("inspectId", inspectId);
                    editor.commit();

                    Global.TOKEN.setSubjectId(loginResponse.getId());
                    Global.TOKEN.setSubjectTel(loginResponse.getPhoneNumber());
                    Global.TOKEN.setInspectId(loginResponse.getUserId());
                    Global.TOKEN.setSubjectName(loginResponse.getName());

                    // TODO : 설문조사가 한개인 경우이기 때문에 surveyId 고정
                    getSurveySubjectInfo(subjectId, 1L);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_login), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                moveToActivity(LoginActivity.class);
            }
        });
    }

    private void getSurveySubjectInfo(Long subjectId, Long surveyManagerId) {
        SurveySubjectRequest request = SurveySubjectRequest.builder()
                .subjectId(subjectId)
                .surveyManagerId(surveyManagerId).build();
        iEmaService.getSurveySubject(request).enqueue(new Callback<SurveySubjectResponse>() {
            @Override
            public void onResponse(Call<SurveySubjectResponse> call, Response<SurveySubjectResponse> response) {
                if (response.isSuccessful()) {
                    SurveySubjectResponse result = response.body();
                    Global.TOKEN.setSurveySubjectId(result.getId());
                    Global.TOKEN.setSurveyManagerId(surveyManagerId);
                    Global.TOKEN.setMainSurveyId(result.getMainSurveyId());
                    Global.TOKEN.setBaseSurveyId(result.getBaseSurveyId());
                    Global.TOKEN.setFollowUpSurveyId(result.getFollowUpSurveyId());
                    Global.TOKEN.setManualSurveyId(result.getManualSurveyId());

                    Date startDt, endDt;

                    try {
                        startDt = new SimpleDateFormat("yyyy-MM-dd").parse(result.getStartAt());
                        endDt = new SimpleDateFormat("yyyy-MM-dd").parse(result.getEndAt());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    Date today = new Date();

                    if (result.isDone() && result.isFinishedPostSurvey()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_available_survey), Toast.LENGTH_SHORT).show();
                        finishAffinity();
                    } else if (result.isDone()) {
                        Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
                        intent.putExtra("surveyId", result.getFollowUpSurveyId());
                        intent.putExtra("newSurvey", true);
                        startActivity(intent);
                    } else if ((!today.after(startDt)) || (!today.before(endDt))) {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_available_survey), Toast.LENGTH_SHORT).show();
                        finishAffinity();
                    } else if (result.isFinishedPreSurvey()) {
                        moveToActivity(MainActivity.class);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
                        intent.putExtra("surveyId", result.getBaseSurveyId());
                        intent.putExtra("newSurvey", true);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<SurveySubjectResponse> call, Throwable t) {

            }
        });
    }

    private void moveToActivity(Class clazz) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

}


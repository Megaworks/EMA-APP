package ai.megaworks.ema.user;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.IntroActivity;
import ai.megaworks.ema.R;
import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;
import ai.megaworks.ema.domain.survey.Survey;
import ai.megaworks.ema.domain.survey.SurveyResult;
import ai.megaworks.ema.layout.GuideItemFragment;
import ai.megaworks.ema.listener.Publisher;
import ai.megaworks.ema.listener.Subscriber;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManualActivity extends AppCompatActivity implements Publisher {

    private final String TAG = this.getClass().getName();

    private RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private IEmaService iEmaService = RetrofitClient.getRetrofitInterface();

    private TextView title;
    private static int nextCount = 0;

    private List<Survey> surveys;

    private int subSurveyCount = 0;

    private static Map<Long, List<SurveyResult>> surveyResultMap = new HashMap<>();
    private final List<Subscriber> subscribers = new ArrayList<>();

    private static List<Long> completedSurveyIds = new ArrayList<>();
    private Long completedSurveyId = -1L;

    private Dialog surveyResetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        Intent intent = getIntent();

        // 상위 Activity 에서 설문 조사 Index 전달
        Long surveyId = intent.getLongExtra("surveyId", -1);
        Boolean newSurvey = intent.getBooleanExtra("newSurvey", false);
        if (newSurvey) {
            surveyResultMap.clear();
            completedSurveyIds.clear();
        }

        // 하위 Activity(설문 조사) 에서 객체 전달
        List<SurveyResult> request = (List<SurveyResult>) intent.getSerializableExtra("surveyResult");
        completedSurveyId = intent.getLongExtra("completedSurveyId", -1);

        if (request != null && completedSurveyId != -1) {
            completedSurveyIds.add(completedSurveyId);
            for (SurveyResult surveyResult : request) {

                Long id = surveyResult.getSubSurveyId();
                if (!surveyResultMap.containsKey(id)) {
                    surveyResultMap.put(id, new ArrayList<>());
                }

                List<SurveyResult> result = surveyResultMap.get(id);
                result.add(surveyResult);
                surveyResultMap.put(id, result);
            }
        }

        drawSurveyListLayout(surveyId);

        LinearLayout back = findViewById(R.id.back);

        title = findViewById(R.id.title);
        AppCompatButton manualSurveyCompleted = findViewById(R.id.manualSurveyCompleted);

        TextView todayDate = findViewById(R.id.todayDate);

        LinearLayout survey_response = findViewById(R.id.survey_response);

        survey_response.setVisibility(View.INVISIBLE);

        todayDate.setText(Global.dateToString(Global.DATE_FORMATTER2));

        surveyResetDialog = new Dialog(this);
        surveyResetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        surveyResetDialog.setContentView(R.layout.dialog_reset);
        surveyResetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        surveyResetDialog.setCanceledOnTouchOutside(false);

        // 뒤로가기 리스너
        back.setOnClickListener(view -> onBackPressed());

        manualSurveyCompleted.setOnClickListener(view -> {
            makeSurveyResultRequest(surveyResultMap);
        });
    }

    public void drawSurveyListLayout(Long surveyId) {
        iEmaService.getSurveyInfo(surveyId).enqueue(new Callback<Survey>() {
            @Override
            public void onResponse(@NonNull Call<Survey> call, @NonNull Response<Survey> response) {
                if (response.isSuccessful()) {
                    Survey result = response.body();

                    title.setText(result.getQuestion());
                    surveys = result.getChildren();

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                        @Override
                        public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
                            super.onFragmentStarted(fm, f);
                            for (Long id : completedSurveyIds) {
                                ManualActivity.this.notifyAll(id);
                            }
                        }
                    }, false);

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    for (Survey survey : surveys) {
                        subSurveyCount += survey.getChildren().size();
                        GuideItemFragment fragment = new GuideItemFragment(getApplicationContext(), survey, surveyId, SurveyActivity.class);
                        fragmentTransaction.add(R.id.list, fragment);
                        attach(fragment);
                    }

                    fragmentTransaction.commitAllowingStateLoss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Survey> call, Throwable t) {
                Log.e(TAG, Arrays.toString(t.getStackTrace()) + "");
                Toast.makeText(getApplicationContext(), getString(R.string.error_network_with_server), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void makeSurveyResultRequest(Map<Long, List<SurveyResult>> resultMap) {

        if (resultMap.size() < subSurveyCount) {
            Toast.makeText(getApplicationContext(), getString(R.string.warn_not_all_completed_survey), Toast.LENGTH_SHORT).show();
            return;
        }

        finish();
    }

    @Override
    public void onBackPressed() {
        surveyResetDialog.show();
        surveyResetDialog.findViewById(R.id.ok).setOnClickListener(v -> {
            surveyResetDialog.dismiss();
            moveToActivity(MainActivity.class);
        });
        surveyResetDialog.findViewById(R.id.cancel).setOnClickListener(v -> {
            surveyResetDialog.dismiss();
        });
    }

    private void moveToActivity(Class clazz) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);
    }

    @Override
    public void attach(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void notifyAll(Long id) {
        for (Subscriber subscriber : subscribers) {
            subscriber.update(id);
        }
    }
}

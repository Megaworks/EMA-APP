package ai.megaworks.ema.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.megaworks.ema.R;
import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;
import ai.megaworks.ema.domain.survey.Survey;
import ai.megaworks.ema.domain.survey.SurveyResult;
import ai.megaworks.ema.layout.CustomSurveyFragment;
import ai.megaworks.ema.layout.ShortAnswerRangeItemFragment;
import ai.megaworks.ema.layout.TemperatureItemFragment;
import ai.megaworks.ema.layout.VoiceRecordItemFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyActivity extends AppCompatActivity {

    private LinearLayout btnBack;
    private AppCompatButton btnNext;

    private final String TAG = this.getClass().getName();

    private final RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private final IEmaService iEmaService = RetrofitClient.getRetrofitInterface();

    // 안드로이드 뒤로가기 버튼 기능
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    private List<SurveyResult> surveyResults = new ArrayList<>();

    private List<CustomSurveyFragment> fragments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        Intent intent = getIntent();
        Long surveyId = intent.getLongExtra("surveyId", -1);
        Long parentSurveyId = intent.getLongExtra("parentSurveyId", -1);

        btnBack = findViewById(R.id.back);
        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(view -> {
            Intent innerIntent = new Intent(getApplicationContext(), GuideActivity.class);

            for (CustomSurveyFragment fragment : fragments) {
                SurveyResult surveyResult = fragment.getSurveyResult();
                surveyResults.add(surveyResult);
            }

            innerIntent.putParcelableArrayListExtra("surveyResult", (ArrayList<? extends Parcelable>) surveyResults);
            innerIntent.putExtra("surveyId", parentSurveyId);
            startActivity(innerIntent);
        });

        drawSurveyListLayout(surveyId);

        btnBack.setOnClickListener(view -> finish());
    }

    public void drawSurveyListLayout(Long surveyId) {
        iEmaService.getSurveyInfo(surveyId).enqueue(new Callback<Survey>() {
            @Override
            public void onResponse(@NonNull Call<Survey> call, @NonNull Response<Survey> response) {
                if (response.isSuccessful()) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    Survey rootSurvey = response.body();

                    List<Survey> surveys = rootSurvey.getChildren();

                    CustomSurveyFragment fragment = null;
                    if (surveys.size() == 0) {
                        String type = rootSurvey.getType();
                        if (type.equals("TEMPRT")) {
                            fragment = new TemperatureItemFragment(getApplicationContext(), rootSurvey);
                        } else if (type.equals("RECORD")) {
                            fragment = new VoiceRecordItemFragment(getApplicationContext(), rootSurvey, getExternalCacheDir().getAbsolutePath());
                        }
                        fragmentTransaction.add(R.id.list, fragment);
                        fragments.add(fragment);
                    }

                    for (Survey survey : surveys) {
                        String type = survey.getType();
                        if (type == null) continue;
                        if (type.equals("TEMPRT")) {
                            fragment = new TemperatureItemFragment(getApplicationContext(), survey);
                        } else if (type.equals("RECORD")) {
                            fragment = new VoiceRecordItemFragment(getApplicationContext(), survey, getExternalCacheDir().getAbsolutePath());
                        } else if (type.equals("RANGE")) {
                            fragment = new ShortAnswerRangeItemFragment(getApplicationContext(), survey);
                        }
                        fragmentTransaction.add(R.id.list, fragment);
                        fragments.add(fragment);
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

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}

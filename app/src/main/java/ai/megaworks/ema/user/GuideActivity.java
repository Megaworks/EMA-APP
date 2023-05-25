package ai.megaworks.ema.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;
import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;
import ai.megaworks.ema.domain.survey.Survey;
import ai.megaworks.ema.domain.survey.SurveyResult;
import ai.megaworks.ema.layout.GuideItemFragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuideActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    private RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private IEmaService iEmaService = RetrofitClient.getRetrofitInterface();

    // 안드로이드 뒤로가기 버튼 기능
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    private TextView title;
    private static int nextCount = 0;

    private List<Survey> surveys;

    private static Map<Long, List<SurveyResult>> surveyResultMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        Intent intent = getIntent();

        // 상위 Activity 에서 설문 조사 Index 전달
        Long surveyId = intent.getLongExtra("surveyId", -1);
        Boolean newSurvey = intent.getBooleanExtra("newSurvey", false);
        if (newSurvey)
            surveyResultMap.clear();

        // 하위 Activity(설문 조사) 에서 객체 전달
        List<SurveyResult> request = (List<SurveyResult>) intent.getSerializableExtra("surveyResult");
        if (request != null) {
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
        AppCompatButton btnNext = findViewById(R.id.btnNext);

        TextView todayDate = findViewById(R.id.todayDate);

        LinearLayout survey_response = findViewById(R.id.survey_response);

        survey_response.setVisibility(View.INVISIBLE);

        todayDate.setText(Global.dateToString(Global.DATE_FORMATTER2));

        // 뒤로가기 리스너
        back.setOnClickListener(view -> finish());

        btnNext.setOnClickListener(view -> {
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
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    for (Survey survey : surveys) {
                        fragmentTransaction.add(R.id.list, new GuideItemFragment(getApplicationContext(), survey, surveyId, SurveyActivity.class));
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

    public void makeSurveyResultRequest(Map<Long, List<SurveyResult>> resultMap) {

        List<MultipartBody.Part> files = new ArrayList<>();
        Map<String, RequestBody> requestMap = new HashMap<>();

        for (Long key : resultMap.keySet()) {
            List<SurveyResult> results = resultMap.get(key);

            if (results == null || results.size() == 0) continue;

            requestMap.put("surveySubjectId", RequestBody.create(MediaType.parse("text/plain"), results.get(0).getSurveySubjectId().toString()));
            requestMap.put("subSurveyId", RequestBody.create(MediaType.parse("text/plain"), results.get(0).getSubSurveyId().toString()));
            requestMap.put("surveyAt", RequestBody.create(MediaType.parse("text/plain"), results.get(0).getSurveyAt()));

            for (SurveyResult result : results) {
                String filePath = result.getFilePath();
                String answer = result.getAnswer();
                if (answer != null)
                    requestMap.put("answer", RequestBody.create(MediaType.parse("text/plain"), result.getAnswer()));
                else if (filePath != null) {
                    String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                    RequestBody fileBody = RequestBody.create(MultipartBody.FORM, filePath);

                    MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", fileName, fileBody);
                    files.add(filePart);
                }
            }

            sendSurveyResultRequest(files, requestMap);

            requestMap.clear();
            files.clear();
        }
    }

    private void sendSurveyResultRequest(List<MultipartBody.Part> files, Map<String, RequestBody> requestMap) {

        iEmaService.saveSurvey(files, requestMap).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    moveToActivity(MainActivity.class, surveys.get(nextCount));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, Throwable t) {
                Log.e(TAG, Arrays.toString(t.getStackTrace()) + "");
                Toast.makeText(getApplicationContext(), getString(R.string.error_network_with_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getSurveyInfo() {
        iEmaService.getSurveyInfo(Global.TOKEN.getSurveyId()).enqueue(new Callback<Survey>() {
            @Override
            public void onResponse(@NonNull Call<Survey> call, @NonNull Response<Survey> response) {
                if (response.isSuccessful()) {
                    Survey result = response.body();
                    surveys = result.getChildren();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Survey> call, Throwable t) {
                Log.e(TAG, Arrays.toString(t.getStackTrace()) + "");
                Toast.makeText(getApplicationContext(), getString(R.string.error_network_with_server), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void moveToActivity(Class clazz, Survey data) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        intent.putExtra("surveyInfo", data);
        startActivity(intent);
    }
}

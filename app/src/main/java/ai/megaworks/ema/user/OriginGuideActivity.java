package ai.megaworks.ema.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OriginGuideActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    private RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private IEmaService iEmaService = RetrofitClient.getRetrofitInterface();

    // 안드로이드 뒤로가기 버튼 기능
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    private Class[] flowActivities;
    private static int nextCount = 0;

    private static List<Survey> surveys;
    private static List<SurveyResult> surveyResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        getSurveyInfo();

        Intent intent = getIntent();

        // 상위 Activity 에서 설문 조사 Index 전달
        int surveyId = intent.getIntExtra("surveyId", -1);

        // 하위 Activity(설문 조사) 에서 객체 전달
        SurveyResult request = (SurveyResult) intent.getSerializableExtra("surveyResult");
        if (request != null) {
            surveyResults.add(request);
            nextCount++;
        }

        LinearLayout back = findViewById(R.id.back);
        AppCompatButton btnNext = findViewById(R.id.btnNext);
        TextView title = findViewById(R.id.title);
        TextView todayDate = findViewById(R.id.todayDate);

        FrameLayout check_icon_1 = findViewById(R.id.check_icon_1);
        FrameLayout check_icon_2 = findViewById(R.id.check_icon_2);

        check_icon_1.setVisibility(View.INVISIBLE);
        check_icon_2.setVisibility(View.INVISIBLE);

        TextView survey1_comment = findViewById(R.id.survey1_comment);
        TextView survey2_comment = findViewById(R.id.survey2_comment);

        survey1_comment.setVisibility(View.INVISIBLE);
        survey2_comment.setVisibility(View.INVISIBLE);

        LinearLayout survey_response = findViewById(R.id.survey_response);

        survey_response.setVisibility(View.INVISIBLE);

        todayDate.setText(Global.dateToString(Global.DATE_FORMATTER2));

        // 뒤로가기 리스너
        back.setOnClickListener(view -> finish());

        // 타이틀 지정
        if (surveyId == 2) {
            title.setText("아침 8시 검사 안내");
            flowActivities = new Class[]{TemperatureAMActivity.class, RecordAMActivity.class, MainActivity.class};
        } else if (surveyId == 3) {
            title.setText("오후 1시 검사 안내");
            flowActivities = new Class[]{TemperatureAMActivity.class, RecordAMActivity.class, MainActivity.class};
        } else {
            title.setText("오후 6시 검사 안내");
            flowActivities = new Class[]{TemperatureAMActivity.class, RecordAMActivity.class, MainActivity.class};
        }

        // 설문조사 완료 이미지를 위한 작업
        if (nextCount == 0) {
            survey1_comment.setVisibility(View.VISIBLE);
        } else if (nextCount == 1) {
            check_icon_1.setVisibility(View.VISIBLE);
            survey2_comment.setVisibility(View.VISIBLE);
        } else if (nextCount == 2) {
            check_icon_1.setVisibility(View.VISIBLE);
            check_icon_2.setVisibility(View.VISIBLE);
            survey_response.setVisibility(View.VISIBLE);
        }

        btnNext.setOnClickListener(view -> {
            moveToActivity(flowActivities[nextCount], surveys.get(nextCount));
        });

        if (nextCount == flowActivities.length - 1) {
            survey_response.setVisibility(View.VISIBLE);
            btnNext.setText("확인");
            btnNext.setOnClickListener(view -> {
                makeSurveyResultRequest(surveyResults);
            });
        }
    }

    public void makeSurveyResultRequest(List<SurveyResult> results) {

        List<MultipartBody.Part> files = new ArrayList<>();
        Map<String, RequestBody> requestMap = new HashMap<>();

        for (SurveyResult result : results) {

            requestMap.put("surveySubjectId", RequestBody.create(MediaType.parse("text/plain"), result.getSurveySubjectId().toString()));
            requestMap.put("subSurveyId", RequestBody.create(MediaType.parse("text/plain"), result.getSubSurveyId().toString()));
            requestMap.put("surveyAt", RequestBody.create(MediaType.parse("text/plain"), result.getSurveyAt()));

//            if (result.getFilePaths() != null && result.getFilePaths().size() > 0) {
//
//                for (String filePath : result.getFilePaths()) {
//                    String fileName = filePath.substring(filePath.lastIndexOf("/"));
//                    RequestBody fileBody = RequestBody.create(MultipartBody.FORM, filePath);
//
//                    MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", fileName, fileBody);
//                    files.add(filePart);
//                }
//            } else {
//                requestMap.put("answer", RequestBody.create(MediaType.parse("text/plain"), result.getAnswer()));
//            }

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

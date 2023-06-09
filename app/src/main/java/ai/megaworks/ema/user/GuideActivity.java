package ai.megaworks.ema.user;

import android.app.AlertDialog;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.IntroActivity;
import ai.megaworks.ema.PermissionSupport;
import ai.megaworks.ema.R;
import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;
import ai.megaworks.ema.domain.survey.Survey;
import ai.megaworks.ema.domain.survey.SurveyResult;
import ai.megaworks.ema.domain.survey.SurveySubjectRequest;
import ai.megaworks.ema.layout.GuideItemFragment;
import ai.megaworks.ema.listener.Publisher;
import ai.megaworks.ema.listener.Subscriber;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuideActivity extends AppCompatActivity implements Publisher {

    private final String TAG = this.getClass().getName();

    private RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private IEmaService iEmaService = RetrofitClient.getRetrofitInterface();

    private TextView title;
    private static int nextCount = 0;

    private List<Survey> surveys;

    private Dialog surveyResetDialog;

    private int subSurveyCount = 0;

    private static Map<Long, List<SurveyResult>> surveyResultMap = new HashMap<>();
    private final List<Subscriber> subscribers = new ArrayList<>();

    private static List<Long> completedSurveyIds = new ArrayList<>();
    private Long completedSurveyId = -1L;

    private Dialog customProgressDialog = null;

    private Long surveyId;

    private PermissionSupport permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        permissionCheck();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.dialog_progress);
        customProgressDialog = builder.create();

        Intent intent = getIntent();

        // 상위 Activity 에서 설문 조사 Index 전달
        surveyId = intent.getLongExtra("surveyId", -1);
        Boolean newSurvey = intent.getBooleanExtra("newSurvey", false);
        if (newSurvey) {
            nextCount = 0;
            surveyResultMap.clear();
            completedSurveyIds.clear();
        }

        // 하위 Activity(설문 조사) 에서 객체 전달
        List<SurveyResult> request = (List<SurveyResult>) intent.getSerializableExtra("surveyResult");
        completedSurveyId = intent.getLongExtra("completedSurveyId", -1);

        if (request != null && completedSurveyId != -1) {
            // TODO 중복 체크
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
        AppCompatButton btnNext = findViewById(R.id.btnNext);

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
                    fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                        @Override
                        public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
                            super.onFragmentStarted(fm, f);
                            for (Long id : completedSurveyIds) {
                                GuideActivity.this.notifyAll(id);
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

    public void makeSurveyResultRequest(Map<Long, List<SurveyResult>> resultMap) {

        if (resultMap.size() < subSurveyCount) {
            Toast.makeText(getApplicationContext(), getString(R.string.warn_not_all_completed_survey), Toast.LENGTH_SHORT).show();
            return;
        }

        customProgressDialog.show();

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
                    File file = new File(filePath);
                    RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

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
                nextCount++;
                if (response.isSuccessful() && subSurveyCount == nextCount) {

                    nextCount = 0;

                    customProgressDialog.dismiss();
                    if (Objects.equals(surveyId, Global.TOKEN.getBaseSurveyId())) {
                        toggleBaseSurveyStatus();
                    } else if (Objects.equals(surveyId, Global.TOKEN.getFollowUpSurveyId())) {
                        toggleFollowUpSurveyStatus();
                    } else {
                        moveToActivity(MainActivity.class);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, Throwable t) {
                Log.e(TAG, Arrays.toString(t.getStackTrace()) + "");
                customProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.error_network_with_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void permissionCheck() {

        // PermissionSupport.java 클래스 객체 생성
        permission = new PermissionSupport(this, this);

        // 권한 체크 후 리턴이 false 로 들어오면
        if (!permission.checkPermission()) {
            //권한 요청
            permission.requestPermission();
        }
    }

    public void toggleBaseSurveyStatus() {
        SurveySubjectRequest request = SurveySubjectRequest.builder()
                .id(Global.TOKEN.getSurveySubjectId()).build();

        iEmaService.toggleBaseSurveyStatus(request).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    moveToActivity(MainActivity.class);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, t.getStackTrace() + "");
                Toast.makeText(getApplicationContext(), getString(R.string.error_network_with_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void toggleFollowUpSurveyStatus() {
        SurveySubjectRequest request = SurveySubjectRequest.builder()
                .id(Global.TOKEN.getSurveySubjectId()).build();

        iEmaService.toggleFollowUpSurveyStatus(request).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    moveToActivity(IntroActivity.class);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, t.getStackTrace() + "");
                Toast.makeText(getApplicationContext(), getString(R.string.error_network_with_server), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void moveToActivity(Class clazz) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        surveyResetDialog.show();
        surveyResetDialog.findViewById(R.id.ok).setOnClickListener(v -> {
            surveyResetDialog.dismiss();
            if (Objects.equals(surveyId, Global.TOKEN.getBaseSurveyId()) || Objects.equals(surveyId, Global.TOKEN.getFollowUpSurveyId()))
                moveToActivity(IntroActivity.class);
            else
                moveToActivity(MainActivity.class);
        });
        surveyResetDialog.findViewById(R.id.cancel).setOnClickListener(v -> {
            surveyResetDialog.dismiss();
        });
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

package ai.megaworks.ema.layout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.time.LocalTime;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;
import ai.megaworks.ema.databinding.FragmentSurveyButtonItemBinding;
import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;
import ai.megaworks.ema.domain.survey.Survey;
import ai.megaworks.ema.domain.survey.SurveyResultRequest;
import ai.megaworks.ema.user.GuideActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SurveyButtonItemFragment extends CustomSurveyFragment {

    private final Context context;

    private final Survey survey;

    private Class clazz;

    private final RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private final IEmaService iEmaService = RetrofitClient.getRetrofitInterface();


    public SurveyButtonItemFragment(Context context, Survey survey) {
        this.context = context;
        this.survey = survey;
    }

    public SurveyButtonItemFragment(Context context, Survey survey, Class clazz) {
        this.context = context;
        this.survey = survey;
        this.clazz = clazz;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSurveyButtonItemBinding binding = FragmentSurveyButtonItemBinding.inflate(inflater, container, false);

        binding.surveyQuestion.setText(this.survey.getQuestion());

        LocalTime startTime = LocalTime.parse(survey.getStartTime());
        LocalTime endTime = LocalTime.parse(survey.getEndTime());

        int currHour = Global.today.getHour();
        int currMinute = Global.today.getMinute();

        binding.surveyPeriod.setText(startTime.getHour() + "시 " + startTime.getMinute() + "분 ~ " + endTime.getHour() + " 시 " + endTime.getMinute() + "분 ");

        if (isBetween(currHour, startTime.getHour(), endTime.getHour())) {
            if (isBetween(currMinute, startTime.getMinute(), endTime.getMinute())) {
                checkAvailableSaveResult(this.survey.getId(), binding.frameLayout);
                if (this.clazz != null) {
                    binding.root.setOnClickListener(v -> {
                        moveToActivity(this.clazz);
                    });
                }
            }
        } else {
            binding.frameLayout.setAlpha(0.6f);
            binding.frameLayout.setOnClickListener(view -> {
                Toast.makeText(context, getString(R.string.no_available_time), Toast.LENGTH_SHORT).show();
            });
        }

        return binding.root;
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
                            Toast.makeText(context, getString(R.string.already_done_survey), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(context, clazz);
        intent.putExtra("surveyId", this.survey.getId());
        intent.putExtra("newSurvey", true);
        startActivity(intent);
    }


    private boolean isBetween(int pivot, int min, int max) {
        if (pivot >= min && pivot <= max) {
            return true;
        }
        return false;
    }

}
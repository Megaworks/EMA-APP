package ai.megaworks.ema.layout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.databinding.FragmentShortAnswerRangeItemBinding;
import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;
import ai.megaworks.ema.domain.survey.Survey;
import ai.megaworks.ema.domain.survey.SurveyResult;

public class ShortAnswerRangeItemFragment extends CustomSurveyFragment {

    private final Context context;

    private final Survey survey;
    private Class clazz;


    private final RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private final IEmaService iEmaService = RetrofitClient.getRetrofitInterface();


    public ShortAnswerRangeItemFragment(Context context, Survey survey) {
        this.context = context;
        this.survey = survey;
        this.surveyResult.setSubSurveyId(survey.getId());
        this.surveyResult.setSurveySubjectId(Global.TOKEN.getSurveySubjectId());
        this.surveyResult.setSurveyAt(Global.defaultDateStr);
        this.surveyResult.setAnswer("1");
    }

    public ShortAnswerRangeItemFragment(Context context, Survey survey, Class clazz) {
        this(context, survey);
        this.clazz = clazz;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentShortAnswerRangeItemBinding binding = FragmentShortAnswerRangeItemBinding.inflate(inflater, container, false);

        binding.survey.setText(survey.getQuestion());
        binding.slider.addOnChangeListener((slider, value, fromUser) -> {
            int roundValue = Math.round(value);
            binding.value.setText(roundValue + "점");
            this.surveyResult.setAnswer(String.valueOf(roundValue));

            System.out.println(surveyResult);

        });

        if (this.clazz != null) {
            binding.root.setOnClickListener(v -> {
                moveToActivity(this.clazz);
            });
        }

        return binding.root;
    }

    @Override
    public SurveyResult getSurveyResult() {
        return surveyResult;
    }

    private void moveToActivity(Class clazz) {
        Intent intent = new Intent(context, clazz);
        startActivity(intent);
    }

}
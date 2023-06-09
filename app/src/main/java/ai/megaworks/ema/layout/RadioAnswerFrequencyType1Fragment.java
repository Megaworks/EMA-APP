package ai.megaworks.ema.layout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;
import ai.megaworks.ema.databinding.FragmentRadioFreqeuncyType1ItemBinding;
import ai.megaworks.ema.domain.survey.Survey;
import ai.megaworks.ema.domain.survey.SurveyResult;

public class RadioAnswerFrequencyType1Fragment extends CustomSurveyFragment {

    private final Context context;

    private final Survey survey;

    private Class clazz;

    public RadioAnswerFrequencyType1Fragment(Context context, Survey survey) {
        this.context = context;
        this.survey = survey;
        this.surveyResult.setSubSurveyId(survey.getId());
        this.surveyResult.setSurveySubjectId(Global.TOKEN.getSurveySubjectId());
        this.surveyResult.setSurveyAt(Global.defaultDateStr);
    }

    public RadioAnswerFrequencyType1Fragment(Context context, Survey survey, Class clazz) {
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
        FragmentRadioFreqeuncyType1ItemBinding binding = FragmentRadioFreqeuncyType1ItemBinding.inflate(inflater, container, false);

        binding.survey.setText(survey.getQuestion());

        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId) {
                case R.id.radio_button_never:
                    this.surveyResult.setAnswer("0");
                    break;
                case R.id.radio_button_sometimes:
                    this.surveyResult.setAnswer("1");
                    break;
                case R.id.radio_button_often:
                    this.surveyResult.setAnswer("2");
                    break;
                case R.id.radio_button_all:
                    this.surveyResult.setAnswer("3");
                    break;
            }
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
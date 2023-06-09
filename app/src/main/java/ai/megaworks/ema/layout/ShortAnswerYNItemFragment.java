package ai.megaworks.ema.layout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.databinding.FragmentShortAnswerYnItemBinding;
import ai.megaworks.ema.domain.survey.Survey;
import ai.megaworks.ema.domain.survey.SurveyResult;

public class ShortAnswerYNItemFragment extends CustomSurveyFragment {

    private final Context context;

    private final Survey survey;

    private Class clazz;

    public ShortAnswerYNItemFragment(Context context, Survey survey) {
        this.context = context;
        this.survey = survey;
        this.surveyResult.setSubSurveyId(survey.getId());
        this.surveyResult.setSurveySubjectId(Global.TOKEN.getSurveySubjectId());
        this.surveyResult.setSurveyAt(Global.defaultDateStr);
    }

    public ShortAnswerYNItemFragment(Context context, Survey survey, Class clazz) {
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
        FragmentShortAnswerYnItemBinding binding = FragmentShortAnswerYnItemBinding.inflate(inflater, container, false);

        binding.survey.setText(survey.getQuestion());

        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = group.findViewById(checkedId);
            this.surveyResult.setAnswer(radioButton.getText().toString());
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
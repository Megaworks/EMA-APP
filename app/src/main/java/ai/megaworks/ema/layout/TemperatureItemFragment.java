package ai.megaworks.ema.layout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.slider.Slider;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;
import ai.megaworks.ema.databinding.FragmentTemperatureItemBinding;
import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;
import ai.megaworks.ema.domain.survey.Survey;
import ai.megaworks.ema.domain.survey.SurveyResult;

public class TemperatureItemFragment extends CustomSurveyFragment {

    private final Context context;

    private final Survey survey;

    private int tempValue = 1;

    private Class clazz;


    private final RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private final IEmaService iEmaService = RetrofitClient.getRetrofitInterface();


    public TemperatureItemFragment(Context context, Survey survey) {
        this.context = context;
        this.survey = survey;
        this.surveyResult.setSubSurveyId(survey.getId());
        this.surveyResult.setSurveySubjectId(Global.TOKEN.getSurveySubjectId());
        this.surveyResult.setSurveyAt(Global.defaultDateStr);
        this.surveyResult.setAnswer("1");
    }

    public TemperatureItemFragment(Context context, Survey survey, Class clazz) {
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
        FragmentTemperatureItemBinding binding = FragmentTemperatureItemBinding.inflate(inflater, container, false);

        binding.slider.addOnChangeListener((slider, value, fromUser) -> {
            int roundValue = Math.round(value);
            binding.tempValue.setText(roundValue + "점");
            this.surveyResult.setAnswer(String.valueOf(roundValue));

            System.out.println(surveyResult);

            if (value < 2) {
                binding.temperature.setImageResource(R.drawable.ic_temp1);
            } else if (value < 3) {
                binding.temperature.setImageResource(R.drawable.ic_temp2);
            } else if (value < 4) {
                binding.temperature.setImageResource(R.drawable.ic_temp3);
            } else if (value < 5) {
                binding.temperature.setImageResource(R.drawable.ic_temp4);
            } else if (value < 6) {
                binding.temperature.setImageResource(R.drawable.ic_temp5);
            } else if (value < 7) {
                binding.temperature.setImageResource(R.drawable.ic_temp6);
            } else if (value < 8) {
                binding.temperature.setImageResource(R.drawable.ic_temp7);
            } else if (value < 9) {
                binding.temperature.setImageResource(R.drawable.ic_temp8);
            } else if (value < 10) {
                binding.temperature.setImageResource(R.drawable.ic_temp9);
            } else {
                binding.temperature.setImageResource(R.drawable.ic_temp10);
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
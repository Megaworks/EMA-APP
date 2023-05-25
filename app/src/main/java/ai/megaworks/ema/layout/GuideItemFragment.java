package ai.megaworks.ema.layout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import ai.megaworks.ema.databinding.FragmentGuideItemBinding;
import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;
import ai.megaworks.ema.domain.survey.Survey;

public class GuideItemFragment extends CustomSurveyFragment {

    private final Context context;

    private final Survey survey;

    private Long parentSurveyId;
    private Class clazz;

    private final RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private final IEmaService iEmaService = RetrofitClient.getRetrofitInterface();


    public GuideItemFragment(Context context, Survey survey, Long parentSurveyId) {
        this.context = context;
        this.survey = survey;
        this.parentSurveyId = parentSurveyId;
    }

    public GuideItemFragment(Context context, Survey survey,  Long parentSurveyId, Class clazz) {
       this(context, survey, parentSurveyId);
        this.clazz = clazz;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentGuideItemBinding binding = FragmentGuideItemBinding.inflate(inflater, container, false);

        binding.survey.setText(this.survey.getQuestion());
        binding.surveyComment.setText(this.survey.getDescription());

        if (this.clazz != null) {
            binding.root.setOnClickListener(v -> {
                moveToActivity(this.clazz);
            });
        }

        return binding.root;
    }

    private void moveToActivity(Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra("surveyId", survey.getId());
        intent.putExtra("parentSurveyId", parentSurveyId);
        startActivity(intent);
    }

}
package ai.megaworks.ema.layout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;

import ai.megaworks.ema.R;
import ai.megaworks.ema.databinding.FragmentGuideItemBinding;
import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;
import ai.megaworks.ema.domain.survey.Survey;
import ai.megaworks.ema.listener.Subscriber;

public class GuideItemFragment extends CustomSurveyFragment implements Subscriber {

    private final Context context;

    private final Survey survey;

    private Long parentSurveyId;
    private Class clazz;

    private final RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private final IEmaService iEmaService = RetrofitClient.getRetrofitInterface();

    private FragmentGuideItemBinding binding;

    public GuideItemFragment(Context context, Survey survey, Long parentSurveyId) {
        this.context = context;
        this.survey = survey;
        this.parentSurveyId = parentSurveyId;
    }

    public GuideItemFragment(Context context, Survey survey, Long parentSurveyId, Class clazz) {
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
        binding = FragmentGuideItemBinding.inflate(inflater, container, false);

        binding.survey.setText(this.survey.getQuestion());

        String description = this.survey.getDescription();
        if(description != null && description.length() > 0)
            binding.surveyComment.setText(this.survey.getDescription());
        else {
            binding.surveyComment.setText("HH");
            binding.surveyComment.setVisibility(View.GONE);
        }

        binding.checkIcon.setVisibility(View.INVISIBLE);

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

    @Override
    public void update(Long id) {
        if (Objects.equals(survey.getId(), id) && binding != null) {
            binding.checkIcon.setVisibility(View.VISIBLE);
            binding.root.setOnClickListener(v -> {
                Toast.makeText(context, getString(R.string.already_done_survey), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
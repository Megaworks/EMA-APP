package ai.megaworks.ema.layout;

import androidx.fragment.app.Fragment;

import ai.megaworks.ema.domain.survey.SurveyResult;

public class CustomSurveyFragment extends Fragment {
    SurveyResult surveyResult;

    public CustomSurveyFragment() {
        surveyResult = SurveyResult.builder().build();
    }

    public SurveyResult getSurveyResult() {
        return this.surveyResult;
    }
}

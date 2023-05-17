package ai.megaworks.ema.api;

import java.util.List;

public class SurveyResultRequest {

    private Long subjectId;

    private Long surveyId;

    private String answer;

    private String surveyAt;

    public SurveyResultRequest(Long subjectId, Long surveyId, String answer, String surveyAt) {
        this.subjectId = subjectId;
        this.surveyId = surveyId;
        this.answer = answer;
        this.surveyAt = surveyAt;
    }
}

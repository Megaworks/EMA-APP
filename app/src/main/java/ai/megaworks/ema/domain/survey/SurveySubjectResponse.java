package ai.megaworks.ema.domain.survey;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SurveySubjectResponse {

    private Long id;

    private Long subjectId;

    private Long mainSurveyId;

    private Long baseSurveyId;

    private Long followUpSurveyId;

    private Long manualSurveyId;

    private String startAt;

    private String endAt;

    private boolean finishedPreSurvey;

    private boolean finishedPostSurvey;

    private boolean done;

}

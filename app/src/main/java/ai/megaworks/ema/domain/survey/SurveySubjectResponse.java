package ai.megaworks.ema.domain.survey;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SurveySubjectResponse {

    private Long id;

    private Long subjectId;

    private List<Survey> surveyList;

    private String startAt;

    private String endAt;

    private boolean finishedPreSurvey;

    private boolean finishedPostSurvey;

}

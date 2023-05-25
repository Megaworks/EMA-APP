package ai.megaworks.ema.domain.survey;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SurveySubjectRequest {

    private Long id;

    private Long surveyId;

    private Long subjectId;

    private LocalDate startAt;

    private LocalDate endAt;
}

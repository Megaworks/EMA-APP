package ai.megaworks.ema.domain;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @SerializedName("token")
    private String token;

    private Long subjectId;

    private String subjectName;

    private String subjectTel;

    private String inspectId;

    private Long surveySubjectId;

    private Long surveyId;
}

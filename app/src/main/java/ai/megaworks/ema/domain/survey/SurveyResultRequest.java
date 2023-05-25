package ai.megaworks.ema.domain.survey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@Data
@Builder
@AllArgsConstructor
public class SurveyResultRequest {

    private Long id;

    private Long surveySubjectId;

    private Long subSurveyId;

    private List<MultipartBody.Part> files;

    private String answer;

    private String surveyAt;

}


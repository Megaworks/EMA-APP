package ai.megaworks.ema.domain.survey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Survey implements Serializable {

    private Long id;

    private String question;

    private String description;

    private String type;

    private String startTime;

    private String endTime;

    private List<Survey> children = new ArrayList<>();

}

package ai.megaworks.ema.domain.survey;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SurveyResult implements Parcelable {

    private Long id;

    private Long surveySubjectId;

    private Long subSurveyId;

    private String filePath = "";

    private List<String> filePaths;

    private String answer = "";

    private String surveyAt;

    protected SurveyResult(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        if (in.readByte() == 0) {
            surveySubjectId = null;
        } else {
            surveySubjectId = in.readLong();
        }
        if (in.readByte() == 0) {
            subSurveyId = null;
        } else {
            subSurveyId = in.readLong();
        }
        filePath = in.readString();
        filePaths = in.createStringArrayList();
        answer = in.readString();
        surveyAt = in.readString();
    }

    public static final Creator<SurveyResult> CREATOR = new Creator<SurveyResult>() {
        @Override
        public SurveyResult createFromParcel(Parcel in) {
            return new SurveyResult(in);
        }

        @Override
        public SurveyResult[] newArray(int size) {
            return new SurveyResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        if (surveySubjectId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(surveySubjectId);
        }
        if (subSurveyId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(subSurveyId);
        }
        dest.writeString(filePath);
        dest.writeStringList(filePaths);
        dest.writeString(answer);
        dest.writeString(surveyAt);
    }
}


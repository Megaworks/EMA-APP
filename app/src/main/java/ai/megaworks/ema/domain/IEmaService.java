package ai.megaworks.ema.domain;

import java.util.List;
import java.util.Map;

import ai.megaworks.ema.domain.firebase.FirebaseTokenInfo;
import ai.megaworks.ema.domain.subject.LoginRequest;
import ai.megaworks.ema.domain.subject.LoginResponse;
import ai.megaworks.ema.domain.survey.Survey;
import ai.megaworks.ema.domain.survey.SurveyResultRequest;
import ai.megaworks.ema.domain.survey.SurveySubjectRequest;
import ai.megaworks.ema.domain.survey.SurveySubjectResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface IEmaService {

    // 해당 설문조사의 대상자인지 확인
    @POST("survey/subject/info")
    Call<SurveySubjectResponse> getSurveySubject(@Body SurveySubjectRequest request);

    @GET("survey")
    Call<Survey> getSurveyInfo(@Query("id") Long id);

    // 설문조사 가능 여부 확인
    @POST("survey/avail-save-result")
    Call<Boolean> checkAvailableSaveResult(@Body SurveyResultRequest request);

    // 사전 설문조사 상태값 변경
    @POST("survey/pre")
    Call<Void> togglePreSurveyStatus(@Body SurveySubjectRequest request);

    // 최종 설문조사 상태값 변경
    @POST("survey/post")
    Call<Void> togglePostSurveyStatus(@Body SurveySubjectRequest request);

    // 대상자 로그인
    @POST("/subject/login")
    Call<LoginResponse> getLoginResponse(@Body LoginRequest loginRequest);

    // 단일 설문조사 저장
    @Multipart
    @POST("/survey/result")
    Call<Boolean> saveSurvey(@Part List<MultipartBody.Part> files, @PartMap Map<String, RequestBody> request);

    // 복수 설문조사 저장
    @Multipart
    @POST("/survey/results")
    Call<Boolean> saveSurveys(@Part List<MultipartBody.Part> files, @PartMap Map<String, RequestBody> request);

    // Firebase Cloud Message Token 키 전송
    @POST("/firebase/token")
    Call<Boolean> setFirebaseMessageToken(@Body FirebaseTokenInfo firebaseTokenInfo);
}

package ai.megaworks.ema.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MindcareApi {
    //@통신 방식("통신 API명")

    // 회원가입
    @POST("/join")
    Call<Void> createUser(@Body UserInformation createUser);

    // 아이디 중복 검사
    @GET("/user/checkID")
    Call<Integer> checkID (@Query("id") String id);

    // 이메일 중복 검사
    @GET("/user/checkEmail")
    Call<Integer> checkEmail (@Query("email") String email);

    // 로그인
    @POST("/subject/login")
    Call<LoginResponse> getLoginResponse(@Body LoginRequest loginRequest);


    @POST("/survey/result")
    Call<Void> saveSurvey(@Body SurveyResultRequest surveyResultRequest);

    // 회원정보 가져오기 (관리자확인)
    @GET("/test")
    Call<List<UserInformation>> getUserInformation(@Header("Authorization") String token);
//    Call<List<UserInformation>> getUserInformation(@Header("Authorization") String token, @Query("usertype") String usertype);

    // 회원정보 가져오기 (마이페이지)
    @GET("/user/account")
    Call<List<UserInformation>> getUserInfo(@Header("Authorization") String token, @Query("id") Long id);

    // 회원정보 업데이트 (마이페이지)
    @POST("/user/account")
    Call<Void> updateUserInfo(@Header("Authorization") String token, @Body UserInformation userInformation);

    // 비밀번호 변경
    @POST("/user/password")
    Call<Void> updatePassword(@Header("Authorization")String token,@Body UserInformation userInformation);

    // 임시 비밀번호 발급
    @POST("/user/email")
    Call<Void> tempPassword(@Body UserInformation email);

    // 검사 결과 입력
    @POST("/user/data")
    Call<Void> setUserData(@Header("Authorization") String token, @Body UserData userData);

    // 검사 결과 정보 가져오기
    @GET("/user/data")
    Call<List<UserData>> getUserData(@Header("Authorization") String token, @Query("id") String id);

    // 검사 결과 정보 가져오기 날짜 순
    @GET("/user/databyorder")
    Call<List<UserData>> getUserDatabyorder(@Query("id") Long id);

    // 사용자 삭제
    @DELETE("/test")
    Call<Void> delUser(@Header("Authorization") String token, @Query("id") Long id);

    // PHQ9 결과 입력
    @POST("/user/phq9")
    Call<Void> setUserPHQ9(@Header("Authorization") String token, @Body UserPHQ9 userPHQ9);

    // PHQ9 결과 가져오기
    @GET("/user/phq9")
    Call<List<UserPHQ9>> getUserPHQ9(@Header("Authorization") String token, @Query("id") Long id);

    // 1주 데이터 가져오기
    @GET("/user/weekreport")
    Call<List<UserData>> getLastWeekValue(@Header("Authorization") String token, @Query("id") Long id);

    // 프로이드 1주 데이터 입력
    @POST("/user/feedback")
    Call<Boolean> setCommentData(@Header("Authorization") String token, @Body CommentData commentData);

    // 프로이드 1주 데이터 조회
    @POST("/user/selectfeedback")
    Call<List<CommentData>> getCommentData(@Header("Authorization") String token, @Body CommentData commentData);

    // 프로이드 2주 데이터 조회
    @GET("/user/report_from_2week")
    Call<List<UserData>> getTwoWeeksValue(@Header("Authorization") String token, @Query("id") Long id);

    // 1주데이터 조회 ( 금주 월 ~ 현재)
    @GET("/user/report_from_monday")
    Call<ReportData> getThisWeekData(@Header("Authorization") String token, @Query("id") Long id);

    // 종합 점수 업데이트
    @POST("/user/score")
    Call<Void> updateScore(@Header("Authorization") String token, @Body UserData userData);

    // FCM 토큰 저장
    @POST("/firebase/token")
    Call<Boolean> setFCMtoken(@Body FirebaseTokenInfo firebaseTokenInfo);

/** 관리자 기능 */
    // 기분 온도계 평균데이터
    @GET("/admin/reportweek")
    Call<List<UserData>> getEmotionReport(@Header("Authorization") String token, @Query("created") String created);

    @POST("/admin/report")
    Call<List<AdminReport>> getDayValue(@Header("Authorization") String token, @Body UserData userData); //userdata(testtime, date)

    // phq9 데이터
    @GET("/admin/phq9report")
    Call<List<UserPHQ9>> getAdminPHQ9(@Header("Authorization") String token, @Query("created") String created);

    // 프로이드 분석
    @GET("/admin/reportfeedback")
    Call<List<CommentData>> getFruedReport(@Header("Authorization") String token, @Query("created") String created);

    // 상세정보 프로이드 분석
    @POST("/user/reportfeedback")
    Call<List<CommentData>> getFruedDetail(@Header("Authorization") String token, @Body CommentData commentData);



}

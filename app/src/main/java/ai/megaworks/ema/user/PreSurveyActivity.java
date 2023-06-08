package ai.megaworks.ema.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.messaging.FirebaseMessaging;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.PermissionSupport;
import ai.megaworks.ema.R;
import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;
import ai.megaworks.ema.domain.firebase.FirebaseTokenInfo;
import ai.megaworks.ema.domain.survey.SurveySubjectRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreSurveyActivity extends AppCompatActivity {

    private AppCompatButton btnNext;

    private TextView user_name;

    private ImageView user_btn;

    private RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private IEmaService iEmaService = RetrofitClient.getRetrofitInterface();

    private final static int PERMISSIONS_REQUEST = 0x0000001;
    private final String TAG = this.getClass().getName();

    private PermissionSupport permission;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_survey);

        permissionCheck();

        user_btn = findViewById(R.id.user_btn);

        user_name = findViewById(R.id.user_name);
        user_name.setText(Global.TOKEN.getSubjectName());

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(view -> {
            savePreSurvey();
        });

        // 마이페이지
        user_btn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
            startActivity(intent);
        });

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, task.getException() + "");
                return;
            }

            String firebaseCloudMessageToken = task.getResult();
            Log.d(TAG, firebaseCloudMessageToken);
            setFirebaseMessageToken(Global.TOKEN.getSubjectId(), firebaseCloudMessageToken);

        });
    }

    // 권한 체크
    private void permissionCheck() {

        // PermissionSupport.java 클래스 객체 생성
        permission = new PermissionSupport(this, this);

        // 권한 체크 후 리턴이 false로 들어오면
        if (!permission.checkPermission()) {
            //권한 요청
            permission.requestPermission();
        }
    }

    public void setFirebaseMessageToken(Long id, String token) {

        FirebaseTokenInfo firebaseTokenInfo = new FirebaseTokenInfo(id, token);
        iEmaService.setFirebaseMessageToken(firebaseTokenInfo).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e(TAG, t.getStackTrace() + "");
                Toast.makeText(getApplicationContext(), getString(R.string.error_network_with_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void savePreSurvey() {
        SurveySubjectRequest request = SurveySubjectRequest.builder()
                .id(Global.TOKEN.getSurveySubjectId()).build();

        iEmaService.togglePreSurveyStatus(request).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    moveToActivity(MainActivity.class);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, t.getStackTrace() + "");
                Toast.makeText(getApplicationContext(), getString(R.string.error_network_with_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void moveToActivity(Class clazz) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);
    }
}

package ai.megaworks.ema;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import ai.megaworks.ema.api.LoginRequest;
import ai.megaworks.ema.api.LoginResponse;
import ai.megaworks.ema.api.MindcareApi;
import ai.megaworks.ema.api.RetrofitClient;
import ai.megaworks.ema.firebase.FirebaseMessagingService;
import ai.megaworks.ema.user.BackKeyHandler;
import ai.megaworks.ema.user.MainActivity;
import ai.megaworks.ema.user.TempPasswordActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private AppCompatButton btnLogin;
    private EditText loginId, loginInspectId, loginUserTel;
    String TAG = "LoginActivityTAG";

    // 안드로이드 뒤로가기 버튼 기능
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginId = findViewById(R.id.loginId);
        loginInspectId = findViewById(R.id.loginInspectId);
        loginUserTel = findViewById(R.id.loginUserTel);

        btnLogin = findViewById(R.id.btnLogin);

        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);

        Long id = sharedPreferences.getLong("Id", 0L);
        // 저장된 ID 불러옴옴
        if (id != 0L) {
            loginId.setText(id.toString());
        }

        if (sharedPreferences.getString("Token", null) != null) {
            Long userid = sharedPreferences.getLong("Id", 0L);
            String userType = sharedPreferences.getString("UserType", null);
            String userName = sharedPreferences.getString("UserName", null);
            Global.TOKEN.setId(userid);
            Global.TOKEN.setUserName(userName);
            Global.TOKEN.setUserType(userType);
            ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
//                if (userType.equals("U")){
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(intent);
//                }else if (userType.equals("A")){
//                    Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
//                    startActivity(intent);
//                }
            } else {
                Toast.makeText(getApplicationContext(), "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
            }


        }
        // 메인으로 이동
        btnLogin.setOnClickListener(view -> {
            ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo == null) {
                Toast.makeText(getApplicationContext(), "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                LoginService();
            }
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    public void AutoLogin() { // Token 받아서 처리
        SharedPreferences sharedPreferences = getSharedPreferences("File", MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", null);
        if (token != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    public void LoginService() {
        Long subjectId = Long.parseLong(loginId.getText().toString());
        String inspectId = loginInspectId.getText().toString();
        String subjectTel = loginUserTel.getText().toString();

        LoginRequest loginRequest = new LoginRequest(subjectId, inspectId, subjectTel);

        RetrofitClient retrofitClient = RetrofitClient.getInstance();
        MindcareApi mindcareApi = RetrofitClient.getRetrofitInterface();
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
        // 로그인시 저장된 Token 있으면 메인으로
        if (sharedPreferences.getString("Token", null) != null) {
            Long userid = sharedPreferences.getLong("Id", 0L);
            String userName = sharedPreferences.getString("UserName", null);
            String userType = sharedPreferences.getString("UserType", null);
            String userPhoneNumber = sharedPreferences.getString("UserTel", null);
            Global.TOKEN.setId(userid);
            Global.TOKEN.setUserName(userName);
            Global.TOKEN.setUserType(userType);
            Global.TOKEN.setUserTel(userPhoneNumber);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            mindcareApi.getLoginResponse(loginRequest).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    Log.d(TAG, "" + response.isSuccessful());
                    if (response.isSuccessful()) {
                        LoginResponse result = response.body();

                        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("Id", subjectId);
                        editor.putString("UserTel", subjectTel);
                        editor.putString("UserType", result.getGroupId());
                        editor.putString("UserName", result.getName());
                        editor.commit();

                        Global.TOKEN.setId(subjectId);
                        Global.TOKEN.setUserName(result.getName());
                        Global.TOKEN.setUserType(result.getGroupId());
                        Global.TOKEN.setUserTel(result.getPhoneNumber());

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

//                        if (result.getUsertype().equals("U")){
//                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                            startActivity(intent);
//                        }else if (result.getUsertype().equals("A")){
//                            Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
//                            startActivity(intent);
//                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "아이디, 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
//                    Toast.makeText(getApplicationContext(),"인터넷 연결을 확인해주세요.",Toast.LENGTH_SHORT).show();
                }
            });
        }


    }
}


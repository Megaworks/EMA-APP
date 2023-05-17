package ai.megaworks.ema.user;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.LoginActivity;
import ai.megaworks.ema.R;
import ai.megaworks.ema.api.MindcareApi;
import ai.megaworks.ema.api.RetrofitClient;
import ai.megaworks.ema.api.UserInformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TempPasswordActivity extends AppCompatActivity {
    private LinearLayout back;
    private EditText email;
    private Button tempPassword;

    RetrofitClient retrofitClient = RetrofitClient.getInstance();
    MindcareApi mindcareApi = RetrofitClient.getRetrofitInterface();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_password);

        this.back = findViewById(R.id.back);
        this.email = findViewById(R.id.email);
        this.tempPassword = findViewById(R.id.tempPassword);

        this.back.setOnClickListener(v -> finish());
        Pattern patternEmail = Patterns.EMAIL_ADDRESS;

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String userEmail = email.getText().toString().replaceAll("\\s+", "");
                if(userEmail.isEmpty()){
                    tempPassword.setBackgroundResource(R.drawable.bg_layout_purple_radius1);
                }else if (!patternEmail.matcher(email.getText().toString()).matches()){
                    tempPassword.setBackgroundResource(R.drawable.bg_layout_purple_radius1);
                }else{
                    tempPassword.setBackgroundResource(R.drawable.bg_layout_purple_radius1_1);
                }
            }
        });

        this.tempPassword.setOnClickListener(v ->
        {
            final String userEmail = this.email.getText().toString().replaceAll("\\s+", "");
            if (userEmail.isEmpty()) {
                Toast.makeText(v.getContext(), "이메일 주소를 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }else if (!patternEmail.matcher(email.getText().toString()).matches()){
                Toast.makeText(v.getContext(), "올바른 이메일 형식으로 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            UserInformation userInformation = new UserInformation(userEmail);
            retrofitClient = RetrofitClient.getInstance();
            mindcareApi = RetrofitClient.getRetrofitInterface();

            mindcareApi.checkEmail(userEmail).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.isSuccessful()){
                        int result = response.body();

                        // 존재하는 이메일 없는 경우
                        if( result == 0){
                            Toast.makeText(v.getContext(), "일치하는 이메일이 없습니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            retrofitClient = RetrofitClient.getInstance();
                            mindcareApi = RetrofitClient.getRetrofitInterface();
                            mindcareApi.tempPassword(userInformation).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call2, Response<Void> response2) {
                                    if (response2.isSuccessful()) {
                                        Log.d("TEMP_PASSWORD", "response.message()");
                                        Dialog dialog = new Dialog(v.getContext());
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.dialog_password);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.show();

                                        dialog.findViewById(R.id.back_to_login).setOnClickListener(view -> {
                                            Toast toast = Toast.makeText(v.getContext(), "임시 비밀번호 전송이 완료되었습니다.", Toast.LENGTH_SHORT);
                                            toast.show();
                                            dialog.dismiss();
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            startActivity(intent);
                                        });


                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call2, Throwable t) {
                                    Toast.makeText(v.getContext(), "임시 비밀번호 전송을 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Log.d("EMAIL_CHECK_ERR",t.getMessage());
                }
            });


        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.checkedNetwork(this);
    }

}
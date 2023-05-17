package ai.megaworks.ema.user;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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

public class UpdatePasswordActivity extends AppCompatActivity implements TextWatcher {

    private LinearLayout  back;
    private AppCompatButton signUp;
    private EditText password1, password2;
    private TextView checkedPassword;

    // 안드로이드 뒤로가기 버튼 기능
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    // API
    private MindcareApi mindcareApi;
    private RetrofitClient retrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
        checkedPassword = findViewById(R.id.checkedPassword);
        signUp = findViewById(R.id.signUp);
        back = findViewById(R.id.back);

        /**
         * watcher 등록
         */
        this.password1.addTextChangedListener(this);
        this.password2.addTextChangedListener(this);

        String pattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,15}$";
        Pattern patternEmail = Patterns.EMAIL_ADDRESS;

        // 비밀번호 변경 버튼
        signUp.setOnClickListener(view -> {
            if(password1.getText().toString().isEmpty()){
                Toast.makeText(this, "비밀번호를 입력하십시오.", Toast.LENGTH_SHORT).show();
            }else if(!Pattern.matches(pattern, password1.getText().toString())){
                Toast.makeText(this, "영문 대소문자, 숫자, 특수문자 포함 8~15자 입력해 주세요.", Toast.LENGTH_SHORT).show();
            }else if(password2.getText().toString().isEmpty()){
                Toast.makeText(this, "비밀번호 확인을 입력하십시오.", Toast.LENGTH_SHORT).show();
            }else if(!password1.getText().toString().equals(password2.getText().toString())){
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }else{
                Dialog dialog = new Dialog(view.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_update_password);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                dialog.findViewById(R.id.ok).setOnClickListener(v -> {
                    SharedPreferences sharedPreferences2 = getSharedPreferences("TOKEN",MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                    editor2.putString("Token",null);
                    editor2.commit();
                    editor2.commit();
                    updatePassword();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                });
                dialog.findViewById(R.id.cancel).setOnClickListener(v -> {
                    dialog.dismiss();
                });
            }
        });
        
        // 뒤로가기 버튼
        back.setOnClickListener(view -> finish());
    }


    private boolean isCheckPassword()
    {
        final String strPassword1 = this.password1.getText().toString();
        final String strPassword2 = this.password2.getText().toString();
        return strPassword1.length() >= 1 && strPassword2.length() >= 1 && strPassword1.equals(strPassword2);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (isCheckPassword()){
            this.checkedPassword.setText("비밀번호가 일치합니다.");
            this.checkedPassword.setTextColor(getColor(R.color.title_color_purple1_1));

        } else {
            this.checkedPassword.setText("비밀번호가 일치하지 않습니다.");
            this.checkedPassword.setTextColor(getColor(R.color.title_color_purple1));

        }
    }

    @Override
    public void afterTextChanged(Editable editable) {    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    public void updatePassword(){
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        UserInformation userInformation = new UserInformation(Global.TOKEN.getId(),password1.getText().toString());
        mindcareApi.updatePassword("Bearer "+ Global.TOKEN.getToken(), userInformation).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("UPDATE_PASSWORD", response.body()+ "");
                    Toast.makeText(getApplicationContext(), "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.checkedNetwork(this);
    }

}

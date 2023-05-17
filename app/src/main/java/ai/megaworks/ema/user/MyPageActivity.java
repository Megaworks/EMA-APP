package ai.megaworks.ema.user;

import android.app.AlertDialog;
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
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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


public class MyPageActivity extends AppCompatActivity{

    private AppCompatButton male, female, password1, signUp, email_check;
    private LinearLayout back, mymessage;
    private EditText name, email, nickname ;
    private String gender;
    private String getEmail="";
    private TextView id, birthday, age, user_delete;
    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    RetrofitClient retrofitClient = RetrofitClient.getInstance();
    MindcareApi mindcareApi = RetrofitClient.getRetrofitInterface();

    // 유효성 검사
    Pattern email_reg = Pattern.compile("^[a-z0-9_+.-]+@([a-z0-9-]+\\.)+[a-z0-9]{2,4}$"); //이메일
    Pattern name_reg = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣].{0,2}$"); //이름 한글 3글자이하 제한

    private Date curDate = new Date();
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String today = dateFormat.format(curDate);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        Log.d("GLOBAL_VOICE33",Global.btnVoice+"");
        id = findViewById(R.id.id);
        password1 = findViewById(R.id.password1);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        nickname = findViewById(R.id.nickname);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        birthday = findViewById(R.id.birthday);
        signUp = findViewById(R.id.signUp);
        age = findViewById(R.id.age);
        back = findViewById(R.id.back);
        email_check = findViewById(R.id.email_check);
        //mymessage = findViewById(R.id.mymessage);
        user_delete = findViewById(R.id.user_delete);

        back.setOnClickListener(view -> finish());
        getUserInfo();

//        mymessage.setOnClickListener(view -> {
//            Intent intent = new Intent(getApplicationContext(), MyMessageActivity.class);
//            startActivity(intent);
//        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "m";
                male.setBackgroundResource(R.drawable.bg_layout_purple_radius1_1);
                male.setTextColor(Color.WHITE);
                female.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                female.setTextColor(getColor(R.color.title_color_purple3));
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "f";
                female.setBackgroundResource(R.drawable.bg_layout_purple_radius1_1);
                female.setTextColor(Color.WHITE);
                male.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                male.setTextColor(getColor(R.color.title_color_purple3));
            }
        });

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView textView = (TextView) view;
                final LocalDate parseDate = LocalDate.parse(textView.getText().toString(), DATE_FORMATTER);
                final LocalDate defaultDate = (parseDate == null) ? LocalDate.now() : parseDate;

                final View dialogView = View.inflate(textView.getContext(), R.layout.date_picker, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(textView.getContext()).create();
                DatePicker datePicker = dialogView.findViewById(R.id.date_picker);

                datePicker.updateDate(defaultDate.getYear(), defaultDate.getMonthValue() - 1, defaultDate.getDayOfMonth());
                dialogView.findViewById(R.id.set).setOnClickListener(v ->
                {
                    textView.setText(LocalDate.of(datePicker.getYear()
                            , datePicker.getMonth() + 1
                            , datePicker.getDayOfMonth()).format(DATE_FORMATTER));
                    alertDialog.dismiss();
                    age.setText(getAge(textView.getText().toString()));
                    age.setBackgroundResource(R.drawable.bg_layout_purple_radius1_1);
                });
                dialogView.findViewById(R.id.cancel).setOnClickListener(v1 -> {
                    alertDialog.dismiss();
                });
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setView(dialogView);
                alertDialog.show();


                /*
                MyDatePickerDialog dialog = new MyDatePickerDialog();
                dialog.show(getSupportFragmentManager(),"TEST");

                 */

            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("EMAIL_AFTER",email.getText().toString()+"//"+getEmail);
                if (!email.getText().toString().equals(getEmail) && !getEmail.equals("")){
                    email_check.setBackgroundResource(R.drawable.bg_layout_purple_radius1_1);
                }else{
                    email_check.setBackgroundResource(R.drawable.bg_layout_purple_radius1);
                }
            }
        });

        email_check.setOnClickListener(view -> {
            if (!email_reg.matcher(email.getText().toString()).matches()){
                Toast.makeText(view.getContext(), "올바른 이메일 형식으로 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }else{
                retrofitClient = retrofitClient.getInstance();
                mindcareApi = retrofitClient.getRetrofitInterface();
                mindcareApi.checkEmail(email.getText().toString()).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.isSuccessful()) {
                            Log.d("CHECK_EMAIL_", "Success");
                            String checkText = "";


                            if (response.body() == 0) { // 중복 x
                                checkText = "사용 가능한 이메일 입니다.";
                            } else { // 중복 o
                                checkText = "사용할 수 없는 이메일 입니다.";
                            }
                            Dialog dialog = new Dialog(view.getContext());
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_check_email);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                            TextView content = (TextView)dialog.findViewById(R.id.content);
                            content.setText(checkText);
                            dialog.findViewById(R.id.ok).setOnClickListener(v -> {
                                dialog.dismiss();
                            });
                            dialog.findViewById(R.id.cancel).setOnClickListener(v -> {
                                dialog.dismiss();
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {

                    }
                });
            }

        });

        password1.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), UpdatePasswordActivity.class);
            startActivity(intent);
        });

        String pattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,15}$";
        Pattern patternEmail = Patterns.EMAIL_ADDRESS;
        // 수정 버튼
        signUp.setOnClickListener(view -> {
            if (id.getText().toString().isEmpty()){
                Toast.makeText(this, "아이디를 입력하십시오.", Toast.LENGTH_SHORT).show();
            }
//            else if(password1.getText().toString().isEmpty()){
//                Toast.makeText(this, "비밀번호를 입력하십시오.", Toast.LENGTH_SHORT).show();
//            }else if(!Pattern.matches(pattern, password1.getText().toString())){
//                Toast.makeText(this, "영문 대소문자, 숫자, 특수문자 포함 8~15자 입력해 주세요.", Toast.LENGTH_SHORT).show();
//            }else if(password2.getText().toString().isEmpty()){
//                Toast.makeText(this, "비밀번호 확인을 입력하십시오.", Toast.LENGTH_SHORT).show();
//            }else if(!password1.getText().toString().equals(password2.getText().toString())){
//                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
//            }
            else if(name.getText().toString().isEmpty()){
                Toast.makeText(this, "이름을 입력하십시오.", Toast.LENGTH_SHORT).show();
            }else if(!name_reg.matcher(name.getText().toString()).matches()){
                Toast.makeText(this, "이름을 한글 3자 이하로 입력하세요.", Toast.LENGTH_SHORT).show();
            }else if(email.getText().toString().isEmpty()){
                Toast.makeText(this, "이메일을 입력하십시오.", Toast.LENGTH_SHORT).show();
            }else if(!patternEmail.matcher(email.getText().toString()).matches()){
                Toast.makeText(this, "E-Mail 형식으로 입력해 주세요", Toast.LENGTH_SHORT).show();
            }else if(nickname.getText().toString().isEmpty()){
                Toast.makeText(this, "닉네임을 입력하십시오.", Toast.LENGTH_SHORT).show();
            }else if(birthday.getText().equals(today)){
                Toast.makeText(this, "생년월일을 선택하십시오.", Toast.LENGTH_SHORT).show();
            }else if(gender == null){
                Toast.makeText(this, "성별을 선택하십시오.", Toast.LENGTH_SHORT).show();
            }else{
                Dialog dialog = new Dialog(view.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_update);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                dialog.findViewById(R.id.ok).setOnClickListener(v -> {
                    updateUserInfo();
                    Global.TOKEN.setUserName(name.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                });
                dialog.findViewById(R.id.cancel).setOnClickListener(v -> {
                    dialog.dismiss();
                });


            }
        });
        Intent intent = getIntent();
        Long userid = Global.TOKEN.getId();
        user_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(view.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_delete_user);
                TextView dialog_delete_user_title = dialog.findViewById(R.id.title);
                dialog_delete_user_title.setText("회원탈퇴");
                TextView dialog_delete_user_content = dialog.findViewById(R.id.content);
                dialog_delete_user_content.setText("정말 탈퇴 하시겠습니까?\n모든 정보가 삭제되고\n로그인 페이지로 이동합니다.");
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                dialog.findViewById(R.id.ok).setOnClickListener(v -> {
                    deleteUser(userid);
                    //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    //startActivity(intent);
                    //finish();
                    dialog.dismiss();
                });
                dialog.findViewById(R.id.cancel).setOnClickListener(v -> {
                    dialog.dismiss();
                });
                dialog.findViewById(R.id.cancel2).setOnClickListener(v -> {
                    dialog.dismiss();
                });
            }
        });

    } //onCreate


    public String getAge(String birthday){
        String[] arr = birthday.split("-");
        Calendar current = Calendar.getInstance();
        int birthYear = Integer.parseInt(arr[0]);
        int birthMonth = Integer.parseInt(arr[1]);
        int birthDay = Integer.parseInt(arr[2]);

        Log.v("birthdy",birthYear+"/"+birthMonth+"/"+birthDay);

        int currentYear = current.get(Calendar.YEAR);
        int currentMonth = current.get(Calendar.MONTH) + 1;
        int currentDay = current.get(Calendar.DAY_OF_MONTH);

        int myage = currentYear - birthYear;
        if (birthMonth * 100 + birthDay > currentMonth * 100 + currentDay){
            myage--;
        }
        return "만 "+myage+"세";
    }

    public void getUserInfo(){
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        mindcareApi.getUserInfo("Bearer "+ Global.TOKEN.getToken(), Global.TOKEN.getId()).enqueue(new Callback<List<UserInformation>>() {
            @Override
            public void onResponse(Call<List<UserInformation>> call, Response<List<UserInformation>> response) {
                if (response.isSuccessful()){
                    Log.d("MYPAGE_DATA",response.body().size()+"");
                    if(response.body().size() > 0){
                        UserInformation userInformation = response.body().get(0);
                        id.setText(userInformation.getId().toString());
                        name.setText(userInformation.getName());
                        email.setText(userInformation.getEmail());
                        getEmail = userInformation.getEmail();
                        nickname.setText(userInformation.getNickname());
                        birthday.setText(userInformation.getBirthday());
                        age.setText(getAge(birthday.getText().toString()));
                        if(userInformation.getSex().equals("m")){
                            male.setBackgroundResource(R.drawable.bg_layout_purple_radius1_1);
                            male.setTextColor(getColor(R.color.white));
                            female.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                            female.setTextColor(getColor(R.color.title_color_purple3));
                            gender = "m";
                        }else{
                            female.setBackgroundResource(R.drawable.bg_layout_purple_radius1_1);
                            female.setTextColor(getColor(R.color.white));
                            male.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                            male.setTextColor(getColor(R.color.title_color_purple3));
                            gender = "f";
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<List<UserInformation>> call, Throwable t) {

            }
        });

    }

    public void updateUserInfo(){
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        UserInformation userInformation = new UserInformation(Long.parseLong(id.getText().toString()),name.getText().toString(),nickname.getText().toString()
                ,birthday.getText().toString(),gender,"U",email.getText().toString());
        mindcareApi.updateUserInfo("Bearer "+ Global.TOKEN.getToken(), userInformation).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("MYPAGE_DATA", response.body()+ "");
//                    Global.TOKEN.setUserName(name.getText().toString());
                    Global.TOKEN.setUserName(userInformation.getName());
                    SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("UserName",userInformation.getName());
                    editor.commit();

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

    // 사용자 삭제
    public void deleteUser(Long id){
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        mindcareApi.delUser("Bearer "+Global.TOKEN.getToken(),id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Log.d("DELETE_USER",response.message());

                    SharedPreferences sharedPreferences2 = getSharedPreferences("TOKEN", MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                    editor2.putString("UserName", null);
                    editor2.putString("Token", null);
                    editor2.putString("Id", null);
                    editor2.putString("UserType", null);
                    editor2.commit();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(), "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }


}
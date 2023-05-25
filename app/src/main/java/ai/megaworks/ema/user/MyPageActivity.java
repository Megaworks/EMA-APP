package ai.megaworks.ema.user;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Pattern;

import ai.megaworks.ema.R;
import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;


public class MyPageActivity extends AppCompatActivity {

    private AppCompatButton male, female, password1, signUp, email_check;
    private LinearLayout back, mymessage;
    private EditText name, email, nickname;
    private String gender;
    private String getEmail = "";
    private TextView id, birthday, age, user_delete;
    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    RetrofitClient retrofitClient = RetrofitClient.getInstance();
    IEmaService IEmaService = RetrofitClient.getRetrofitInterface();

    // 유효성 검사
    Pattern email_reg = Pattern.compile("^[a-z0-9_+.-]+@([a-z0-9-]+\\.)+[a-z0-9]{2,4}$"); //이메일
    Pattern name_reg = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣].{0,2}$"); //이름 한글 3글자이하 제한

    private Date curDate = new Date();
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String today = dateFormat.format(curDate);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

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
        user_delete = findViewById(R.id.user_delete);

        back.setOnClickListener(view -> finish());
    } //onCreate

    @Override
    protected void onResume() {
        super.onResume();
    }


}
package ai.megaworks.ema.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;
import ai.megaworks.ema.api.MindcareApi;
import ai.megaworks.ema.api.RetrofitClient;
import ai.megaworks.ema.api.UserPHQ9;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PHQ9resultActivity extends AppCompatActivity {

    TextView totalScore, result;
    AppCompatButton btnNext;
    ImageView depressionImg;
    RetrofitClient retrofitClient = RetrofitClient.getInstance();
    MindcareApi mindcareApi = RetrofitClient.getRetrofitInterface();

    // 뒤로가기 버튼
    LinearLayout back;
    int score = 0;

    // 안드로이드 뒤로가기 버튼 기능
    private  BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phq9_result);

        back = findViewById(R.id.back);
        totalScore = findViewById(R.id.totalScore);
        result = findViewById(R.id.result);
        btnNext = findViewById(R.id.btnNext);
        depressionImg = findViewById(R.id.depressionImg);

        // 뒤로가기 리스너
        back.setOnClickListener(view -> finish());

        Intent intent = getIntent();
        String[] scores = intent.getStringArrayExtra("totalScore");
        String diagnosis ="결과없음";


        for (int i=0;i<scores.length;i++){
            score += Integer.parseInt(scores[i]);
        }
        Log.v("totalScore",score+"");

        if(score < 5){
            diagnosis = "정상";
            depressionImg.setImageResource(R.drawable.nondepressed);
        }else if(score < 10){
            diagnosis = "가벼운 우울감";
            depressionImg.setImageResource(R.drawable.depressed);
        }else{
            diagnosis = "심한 우울감";
            depressionImg.setImageResource(R.drawable.depressed);
        }
        totalScore.setText(String.valueOf(score));
        result.setText(diagnosis);
        // 임시저장
        SharedPreferences sharedPreferences = getSharedPreferences("Test", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PHQ9",String.valueOf(score));
        editor.putString("PHQ9R",diagnosis);
        // 자살문항
        if (Integer.parseInt(scores[8]) != 0){
            Log.d("PHQ9_9R",scores[8]+"");
        }
        setUserPHQ9(score, Integer.parseInt(scores[8]));
        Log.d("PHQ9_9R",scores[8]+"");
        editor.commit();
        //
        // 확인 리스너
        btnNext.setOnClickListener(view -> {
            Intent intent1 = new Intent(this, MainActivity.class);
            intent1.putExtra("PHQ9Score",score);
            startActivity(intent1);
        });
    }
    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }


    public void setUserPHQ9(int phq9, int ans9){
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        UserPHQ9 userPHQ9 = new UserPHQ9(Global.TOKEN.getId(), phq9, ans9);
        mindcareApi.setUserPHQ9("Bearer "+Global.TOKEN.getToken(), userPHQ9).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Log.d("PHQ99999999999",response.message()+"");
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

package ai.megaworks.ema.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.slider.Slider;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;
import ai.megaworks.ema.domain.survey.SurveyResult;

public class TemperatureActivity extends AppCompatActivity {

    private LinearLayout btnBack;
    private ImageView temperature;
    private AppCompatButton btnNext;
    private TextView tempValue;
    private Slider slider;
    String tempScore = "1";

    // 안드로이드 뒤로가기 버튼 기능
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_am);

        //로딩창 객체 생성
//        exampleDialog = new ExampleDialogAM(this);
        //로딩창을 투명하게
//        exampleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        exampleDialog.show();
//        exampleDialog.setCancelable(false);
        Intent intent = getIntent();
        Long surveyId = intent.getLongExtra("surveyId", -1);

        btnBack = findViewById(R.id.back);
        btnNext = findViewById(R.id.btnNext);
        temperature = findViewById(R.id.temperature);
        slider = findViewById(R.id.slider);
        tempValue = findViewById(R.id.tempValue);

        btnNext.setOnClickListener(view -> {
            Intent innerIntent = new Intent(getApplicationContext(), GuideActivity.class);
            SurveyResult surveyResultRequest = SurveyResult.builder()
                    .subSurveyId(5L)
                    .surveyAt(Global.dateToString(Global.DATE_FORMATTER2))
                    .answer(tempScore)
                    .surveySubjectId(Global.TOKEN.getSurveySubjectId())
                    .build();

            innerIntent.putExtra("surveyResult", surveyResultRequest);
            innerIntent.putExtra("surveyId", surveyId);
            startActivity(innerIntent);
        });

        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (value < 2) {
                    temperature.setImageResource(R.drawable.ic_temp1);
                    tempValue.setText("1점");
                    tempScore = "1";
                } else if (value < 3) {
                    temperature.setImageResource(R.drawable.ic_temp2);
                    tempValue.setText("2점");
                    tempScore = "2";
                } else if (value < 4) {
                    temperature.setImageResource(R.drawable.ic_temp3);
                    tempValue.setText("3점");
                    tempScore = "3";
                } else if (value < 5) {
                    temperature.setImageResource(R.drawable.ic_temp4);
                    tempValue.setText("4점");
                    tempScore = "4";
                } else if (value < 6) {
                    temperature.setImageResource(R.drawable.ic_temp5);
                    tempValue.setText("5점");
                    tempScore = "5";
                } else if (value < 7) {
                    temperature.setImageResource(R.drawable.ic_temp6);
                    tempValue.setText("6점");
                    tempScore = "6";
                } else if (value < 8) {
                    temperature.setImageResource(R.drawable.ic_temp7);
                    tempValue.setText("7점");
                    tempScore = "7";
                } else if (value < 9) {
                    temperature.setImageResource(R.drawable.ic_temp8);
                    tempValue.setText("8점");
                    tempScore = "8";
                } else if (value < 10) {
                    temperature.setImageResource(R.drawable.ic_temp9);
                    tempValue.setText("9점");
                    tempScore = "9";
                } else {
                    temperature.setImageResource(R.drawable.ic_temp10);
                    tempValue.setText("10점");
                    tempScore = "10";
                }
            }
        });
        btnBack.setOnClickListener(view -> finish());
    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

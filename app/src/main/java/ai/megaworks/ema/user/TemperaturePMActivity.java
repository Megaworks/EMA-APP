package ai.megaworks.ema.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.slider.Slider;

import java.time.LocalDate;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;

public class TemperaturePMActivity extends AppCompatActivity {

    private LinearLayout btnBack;
    private ImageView temperature;
    private AppCompatButton btnNext;
    private TextView tempValue;

    private Slider slider;
    LocalDate today = LocalDate.now();
    String TempScore = "1";

    // 안드로이드 뒤로가기 버튼 기능
    private  BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_pm);

        //로딩창 객체 생성
//        exampleDialog = new ExampleDialogPM(this);
        //로딩창을 투명하게
//        exampleDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        exampleDialog.show();
//        exampleDialog.setCancelable(false);

        btnBack = findViewById(R.id.back);
        btnNext = findViewById(R.id.btnNext);
        temperature = findViewById(R.id.temperature);
        slider = findViewById(R.id.slider);
        tempValue = findViewById(R.id.tempValue);

        btnNext.setOnClickListener(view -> {
        // 임시
            SharedPreferences sharedPreferences = getSharedPreferences("Test", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("TempPm",TempScore);
            editor.commit();
        // .

            // 월, 수, 금, 일
//            if (today.getDayOfWeek().getValue() == 1 || today.getDayOfWeek().getValue() == 3 || today.getDayOfWeek().getValue() == 5 || today.getDayOfWeek().getValue() == 7){
////                Intent intent = new Intent(getApplicationContext(), Record2PMActivity.class);
//                Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
//                intent.putExtra("testType","PM2_C");
//                Log.d("RECORDPM2",TempScore);
//                intent.putExtra("tempValue",TempScore);
//                startActivity(intent);
//            // 화, 목, 토
//            }else{
////                Intent intent = new Intent(getApplicationContext(), RecordPMActivity.class);
//                Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
//                intent.putExtra("testType","PM2_P");
//                Log.d("RECORDPM233",TempScore);
//                intent.putExtra("tempValue",TempScore);
//
//                startActivity(intent);
//            }
        });

        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if(value < 2){
                    temperature.setImageResource(R.drawable.ic_temp1);
                    tempValue.setText("1점");
                    TempScore = "1";
                }else if (value < 3){
                    temperature.setImageResource(R.drawable.ic_temp2);
                    tempValue.setText("2점");
                    TempScore = "2";
                }else if (value < 4){
                    temperature.setImageResource(R.drawable.ic_temp3);
                    tempValue.setText("3점");
                    TempScore = "3";
                }else if (value < 5){
                    temperature.setImageResource(R.drawable.ic_temp4);
                    tempValue.setText("4점");
                    TempScore = "4";
                }else if (value < 6){
                    temperature.setImageResource(R.drawable.ic_temp5);
                    tempValue.setText("5점");
                    TempScore = "5";
                }else if (value < 7){
                    temperature.setImageResource(R.drawable.ic_temp6);
                    tempValue.setText("6점");
                    TempScore = "6";
                }else if (value < 8){
                    temperature.setImageResource(R.drawable.ic_temp7);
                    tempValue.setText("7점");
                    TempScore = "7";
                }else if (value < 9){
                    temperature.setImageResource(R.drawable.ic_temp8);
                    tempValue.setText("8점");
                    TempScore = "8";
                }else if (value < 10){
                    temperature.setImageResource(R.drawable.ic_temp9);
                    tempValue.setText("9점");
                    TempScore = "9";
                }else {
                    temperature.setImageResource(R.drawable.ic_temp10);
                    tempValue.setText("10점");
                    TempScore = "10";
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

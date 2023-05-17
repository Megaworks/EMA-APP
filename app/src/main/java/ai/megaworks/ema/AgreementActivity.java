package ai.megaworks.ema;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import ai.megaworks.ema.user.BackKeyHandler;

public class AgreementActivity extends AppCompatActivity {

    private LinearLayout back;
    private AppCompatButton btnNext;
    private CheckBox agreeCheck;

    // 안드로이드 뒤로가기 버튼 기능
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement1);

        back = findViewById(R.id.back);
        agreeCheck = findViewById(R.id.agreeCheck);
        btnNext = findViewById(R.id.btnNext);

        agreeCheck.setOnClickListener(view -> {
            if (agreeCheck.isChecked()){
                btnNext.setBackgroundResource(R.drawable.bg_layout_purple_radius1_1);
            }else{
                btnNext.setBackgroundResource(R.drawable.bg_layout_purple_radius1);
            }
        });

        back.setOnClickListener(view ->  finish());



    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.checkedNetwork(this);
    }

}


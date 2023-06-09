package ai.megaworks.ema.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.IntroActivity;
import ai.megaworks.ema.R;
import ai.megaworks.ema.domain.IEmaService;
import ai.megaworks.ema.domain.RetrofitClient;
import ai.megaworks.ema.domain.survey.Survey;
import ai.megaworks.ema.layout.SurveyButtonItemFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView user_name;
    private final String TAG = this.getClass().getName();

    private final RetrofitClient retrofitClient = RetrofitClient.getInstance();

    private final IEmaService iEmaService = RetrofitClient.getRetrofitInterface();

    // 안드로이드 뒤로가기 버튼 기능
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView todayDate = findViewById(R.id.todayDate);

        ImageView user_btn = findViewById(R.id.user_btn);
        user_name = findViewById(R.id.user_name);

        ImageView manual_btn = findViewById(R.id.manual_btn);

        manual_btn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ManualActivity.class);
            intent.putExtra("surveyId", Global.TOKEN.getManualSurveyId());
            intent.putExtra("newSurvey", true);
            startActivity(intent);
        });

        // 마이 페이지
        user_btn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
            startActivity(intent);
        });

        // 오늘 날짜 설정
        todayDate.setText(Global.dateToString(Global.DATETIME_FORMATTER1));

        drawSurveyListLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Global.TOKEN.getSubjectName() != null) {
            user_name.setText(Global.TOKEN.getSubjectName() + "님");
        }
    }

    public void drawSurveyListLayout() {
        iEmaService.getSurveyInfo(Global.TOKEN.getMainSurveyId()).enqueue(new Callback<Survey>() {
            @Override
            public void onResponse(@NonNull Call<Survey> call, @NonNull Response<Survey> response) {
                if (response.isSuccessful()) {
                    Survey result = response.body();
                    List<Survey> surveys = result.getChildren();

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    for (Survey survey : surveys) {
                        fragmentTransaction.add(R.id.list, new SurveyButtonItemFragment(getApplicationContext(), survey, GuideActivity.class));
                    }

                    fragmentTransaction.commitAllowingStateLoss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Survey> call, Throwable t) {
                Log.e(TAG, Arrays.toString(t.getStackTrace()) + "");
                Toast.makeText(getApplicationContext(), getString(R.string.error_network_with_server), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }
}

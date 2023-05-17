package ai.megaworks.ema.user;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.util.List;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.LoginActivity;
import ai.megaworks.ema.R;
import ai.megaworks.ema.api.FirebaseTokenInfo;
import ai.megaworks.ema.api.MindcareApi;
import ai.megaworks.ema.api.RetrofitClient;
import ai.megaworks.ema.api.UserData;
import ai.megaworks.ema.api.UserInformation;
import ai.megaworks.ema.api.UserPHQ9;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnPHQ, btnReport, btnResult, logout, btnAdmin, voiceOnOff, am_banner_contents, pm_banner_contents;
    private FrameLayout btnAM, btnPM;
    private ImageView rotate_stroke, rotate_stroke1, rotate_stroke2;
    private View marginView, marginView1, marginView2;

    LocalDateTime today = LocalDateTime.now();
    BackgroundThread backgroundThread;
    RetrofitClient retrofitClient = RetrofitClient.getInstance();
    MindcareApi mindcareApi = RetrofitClient.getRetrofitInterface();
    // 중복 검사 방지
    private boolean isTestedAM = true;
    private boolean isTestedPM = true;

    private TextView todayDate, txtAM, txtPM, txtPHQ9, user_name, speaker_on_text, txtReport, textMain1;
    private ImageView user_btn, speaker_on, speaker_off, am_banner_bg, pm_banner_bg;
    private MediaPlayer mediaPlayer; //220922 soyi Kim

    ActivityResultLauncher<Intent> ActivityLauncher;

    private int phq9score;

    // 안드로이드 뒤로가기 버튼 기능
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    /* permission */
    static final int PERMISSIONS_REQUEST = 0x0000001;

    // 핸들러 메시지
    public int HM_SET_TIME = 1;
    public int HM_GET_DATA = 10; // getUserData
    public int HM_GET_PHQ9 = 11; // getPHQData
    public int HM_ERROR = 12; // getUserData


    public void OnCheckPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "앱 실행을 위해서는 권한을 설정해야 합니다", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "앱 실행을 위한 권한이 설정 되었습니다", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "앱 실행을 위한 권한이 취소 되었습니다", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OnCheckPermission();
        // 화면크기 고정하는 부분(onCreate에서는 적용이 바로 안되고있음)
//        Configuration configuration = getResources().getConfiguration();
//        configuration.fontScale = (float)1;
//
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        metrics.scaledDensity = configuration.fontScale * metrics.density;
//        configuration.densityDpi = (int) getResources().getDisplayMetrics().xdpi;
//        getBaseContext().getResources().updateConfiguration(configuration, metrics);
//
//        Log.d("GET_DISPLAY", metrics.widthPixels+"/"+metrics.heightPixels+"/"+metrics.densityDpi);
//        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.ai_frued);
//        if (Global.btnVoice) {
//            mediaPlayer.start();
//        }

        btnAM = findViewById(R.id.btnAM);
        btnPM = findViewById(R.id.btnPM);
        btnPHQ = findViewById(R.id.btnPHQ9);
        btnReport = findViewById(R.id.btnReport);
        // btnResult = findViewById(R.id.result);
        todayDate = findViewById(R.id.todayDate);
        logout = findViewById(R.id.logout);
        btnAdmin = findViewById(R.id.admin_btn);
        txtAM = findViewById(R.id.txtAM);
        txtPM = findViewById(R.id.txtPM);
        txtPHQ9 = findViewById(R.id.txtPHQ9);
        txtReport = findViewById(R.id.txtReport);
        user_btn = findViewById(R.id.user_btn);
        user_name = findViewById(R.id.user_name);
        speaker_on = findViewById(R.id.speaker_on);
        speaker_off = findViewById(R.id.speaker_off);
        speaker_on_text = findViewById(R.id.speaker_on_text);
        marginView = findViewById(R.id.marginView);
        marginView1 = findViewById(R.id.marginView1);
        marginView2 = findViewById(R.id.marginView2);
        textMain1 = findViewById(R.id.textMain1);

        am_banner_bg = findViewById(R.id.am_banner_bg);
        am_banner_contents = findViewById(R.id.am_banner_contents);
        pm_banner_bg = findViewById(R.id.pm_banner_bg);
        pm_banner_contents = findViewById(R.id.pm_banner_contents);




        am_banner_contents.bringToFront(); //오전오후검사 안에 글씨 최상단으로
        pm_banner_contents.bringToFront();
        btnAM.setClipToOutline(true); //오전오후검사 배경 모서리 둥글게
        btnPM.setClipToOutline(true);
        btnPHQ.setClipToOutline(true);
        marginView.setClipToOutline(true);
        marginView1.setClipToOutline(true);
        marginView2.setClipToOutline(true);

        /* 배경색 테두리 돌아가게 하는 애니메이션 */
        rotate_stroke = findViewById(R.id.rotate_stroke);
        rotate_stroke1 = findViewById(R.id.rotate_stroke1);
        rotate_stroke2 = findViewById(R.id.rotate_stroke2);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        rotate_stroke.startAnimation(animation);
        rotate_stroke1.startAnimation(animation);
        rotate_stroke2.startAnimation(animation);

        SharedPreferences sharedPreferences = getSharedPreferences("File", MODE_PRIVATE);
        Global.btnVoice = sharedPreferences.getBoolean("VoiceChecked", true);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.d("FCM_Fail", task.getException() + "");
                    return;
                }
                String FCM_Token = task.getResult();
                Log.d("FCM_TOKEN", FCM_Token);
                setFCMToken(Global.TOKEN.getId(), FCM_Token);
                SharedPreferences sharedPreferences2 = getSharedPreferences("TOKEN", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                editor2.putString("Token", FCM_Token);
                editor2.commit();


            }
        });

        if (Global.TOKEN.getUserType().equals("U")) {
            btnAdmin.setVisibility(View.GONE);
        }

        if (Global.btnVoice) {
            speaker_on.setImageResource(R.drawable.main_voice_icon);

        } else {
            speaker_on.setImageResource(R.drawable.main_no_voice_icon);
        }

//        /* 음성 출력 부분 */
//        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.ai_freud);
//        mediaPlayer.start(); //기본적으로 처음 시작하면 음성 나오게 함.
//
//        //speaker_on 버튼 클릭시 음성 OFF
//        speaker_on.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                speaker_on.setVisibility(View.GONE);
//                speaker_off.setVisibility(View.VISIBLE);
//                mediaPlayer.stop();
//                mediaPlayer.reset();
//            }
//        });
//        // speaker_off 버튼 클릭시 음성 ON
//        speaker_off.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                speaker_on.setVisibility(View.VISIBLE);
//                speaker_off.setVisibility(View.GONE);
//                mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.ai_freud);
//                mediaPlayer.start();
//            }
//        });


//        if (Global.TOKEN.getUserName() != null) {
//            user_name.setText(Global.TOKEN.getUserName() + "님");
//        }

        //btnResult.setVisibility(View.INVISIBLE);
        //btnResult.setEnabled(false);

        // 오늘날짜 설정
        todayDate.setText(Global.DATETIME_FORMATTER.format(today));

        // 검사 표시 및 기능 제어
        LocalDateTime nowTime = LocalDateTime.now();
        Intent getintent = getIntent();
        phq9score = getintent.getIntExtra("PHQ9Score", 0);

        // 마이페이지
        user_btn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MyPageActivity.class);
            startActivity(intent);
        });

        //로그아웃
        logout.setOnClickListener(view -> {
            Dialog dialog = new Dialog(view.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_logout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            dialog.findViewById(R.id.ok).setOnClickListener(v -> {
                SharedPreferences sharedPreferences2 = getSharedPreferences("TOKEN", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                editor2.putString("Token", null);
                editor2.commit();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                dialog.dismiss();
            });
            dialog.findViewById(R.id.cancel).setOnClickListener(v -> {
                dialog.dismiss();
            });
        });
    } // onCreate(){}

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    private final MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {

                activity.handleMessage(msg);
            }
        }
    }


    public void handleMessage(Message msg) {
        if (msg.what == HM_SET_TIME) {
            LocalDateTime now = LocalDateTime.now();
            todayDate.setText(Global.DATETIME_FORMATTER.format(now));
        }
        btnAM.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
            intent.putExtra("testType", "AM1");
            intent.putExtra("phq9score", phq9score);
            startActivity(intent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        backgroundThread = new BackgroundThread();
        backgroundThread.setRunning(true);
        backgroundThread.start();

        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.ai_frued);
        if (Global.btnVoice) {
            mediaPlayer.start();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("File", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        speaker_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.btnVoice) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    speaker_on.setImageResource(R.drawable.main_no_voice_icon);
//                    Global.btnVoice = false;
                } else {
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.ai_frued);
                    mediaPlayer.start();
                    speaker_on.setImageResource(R.drawable.main_voice_icon);
//                    Global.btnVoice = true;
                }
                Global.btnVoice = !Global.btnVoice;
                editor.putBoolean("VoiceChecked", Global.btnVoice);
                editor.commit();
            }
        });


//        mediaPlayer.start(); //기본적으로 처음 시작하면 음성 나오게 함.

        //speaker_on 버튼 클릭시 음성 OFF
//        speaker_on.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (Global.btnVoice) {
////                    mediaPlayer.start();
////                    speaker_on.setImageResource(R.drawable.baseline_volume_up_24);
//                    Global.btnVoice = false;
//                }else{
////                    mediaPlayer.stop();
////                    mediaPlayer.reset();
////                    speaker_on.setImageResource(R.drawable.baseline_volume_off_24);
//                    Global.btnVoice = true;
//                }
//            }
//        });
    }

    //220922 soyi Kim
    @Override
    protected void onPause() {
        super.onPause();
        /* 음성파일 화면 벗어나면 정지 */
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    @Override
    protected void onStop() {
        super.onStop();

        boolean retry = true;
        backgroundThread.setRunning(false);

        while (retry) {
            try {
                backgroundThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //220922 soyi Kim (런타임 오류나서 숨김처리함)
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mediaPlayer.release();
//    }

    public class BackgroundThread extends Thread {
        boolean running = false;

        void setRunning(boolean b) {
            running = b;
        }

        @Override
        public void run() {
            while (running) {
                Message message = mHandler.obtainMessage();
                message.what = HM_SET_TIME;
                mHandler.sendMessage(message);
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Global.checkedNetwork(this);
        Global.today = LocalDateTime.now();
        Log.d("RESUME", "HI");
        if (Global.TOKEN.getUserName() != null) {
            user_name.setText(Global.TOKEN.getUserName() + "님");
        }

    }

    public void getUserData(Long id) {
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        Message message = mHandler.obtainMessage();
        message.what = HM_GET_PHQ9;
        message.arg1 = 2;
        mindcareApi.getUserDatabyorder(id).enqueue(new Callback<List<UserData>>() {
            @Override
            public void onResponse(Call<List<UserData>> call, Response<List<UserData>> response) {
//                    Log.d("GETPHQ_RESUME","HI");
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size() != 0) {
                        List<UserData> userData = response.body();
                        LocalDateTime today = LocalDateTime.now();
                        if (userData.get(0) != null) {
                            String date = userData.get(0).getCreated();
                            LocalDateTime testDate = LocalDateTime.parse(date, Global.DATETIME_FORMATTER2);
                            // 오늘 검사 확인
                            boolean testCheck = false;
                            if (testDate.getDayOfYear() == today.getDayOfYear() && testDate.getYear() == today.getYear()) { // 오늘 검사를 했으면 true
                                testCheck = true;
                            }

                            // 오늘 검사를 한 경우
                            if (testDate.getDayOfWeek().getValue() == today.getDayOfWeek().getValue() && testCheck) {
//                                if (testDate.getHour() < 12) {
                                if (userData.get(0).getTesttime().equals("AM")) {
                                    // 오전 검사
                                    message.arg2 = 2;
                                    mHandler.sendMessage(message);
                                } else {
                                    // 오후 검사
                                    message.arg2 = 3;
                                    mHandler.sendMessage(message);
                                }
                            } else {
                                // 오늘 검사를 안함
                                message.arg2 = 1;
                                mHandler.sendMessage(message);
                            }
                        }
                    } else if (response.body() != null && response.body().size() == 0) {
                        // 오늘 검사를 안함
                        message.arg2 = 1;
                        mHandler.sendMessage(message);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserData>> call, Throwable t) {
                // 오류
                message.what = HM_ERROR;
                mHandler.sendMessage(message);
            }
        });
    }

    public void setFCMToken(Long id, String token) {
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();

        FirebaseTokenInfo firebaseTokenInfo = new FirebaseTokenInfo(id, token);
        mindcareApi.setFCMtoken(firebaseTokenInfo).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Log.d("SET_FCM", response.body().toString());

                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }


}

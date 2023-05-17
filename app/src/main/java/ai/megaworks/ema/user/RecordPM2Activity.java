package ai.megaworks.ema.user;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;
import ai.megaworks.ema.dialog.ProgressDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RecordPM2Activity extends AppCompatActivity {
    /* permission */
    int nCurrentPermission = 0;
    static final int PERMISSIONS_REQUEST = 0x0000001;

    /* audio recoder */
    private int recordingTime = 0;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private String filePath = null;
    private String voiceValue = null;
    private String tempValue = null;
//    private String WavFileName = "test1.pcm";
    private String WavFileName = "test2.pcm";
//    private AppCompatButton btnStart, btnReStart, btnRequest;

    private TextView recordingTimeText, recordComment2, title;//, progress2, progress3;;
    private int RecordSeq = 0;
    LinearLayout record3bg;

    /* pictures */
    private Integer[] positives = {R.drawable.p1_dog, R.drawable.p2_lake, R.drawable.p3_beach, R.drawable.p4_fireworks,
            R.drawable.p5_rainbow, R.drawable.p6_cat, R.drawable.p7_penguins, R.drawable.p8_siblings};
    private Integer[] negatives = {R.drawable.n1_war, R.drawable.n2_fire, R.drawable.n3_garbage, R.drawable.n4_dirt,
            R.drawable.n5_war, R.drawable.n6_explosion, R.drawable.n7_destruction, R.drawable.n8_crash };
    public DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    int seq = 0;

    private LocalDate today = LocalDate.now();
    private String TAG = "MainActivity_log";

    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format

    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    /* progress */
    ProgressDialog customProgressDialog;
    long startTime;

    private LinearLayout btnBack, record1;
    private ImageView record2, record3;
    private ImageView testImg;

    // 안드로이드 뒤로가기 버튼 기능
    private  BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_pm);

        Global.setThread();
        //로딩창 객체 생성
//        exampleDialogPM2 = new ExampleDialogPM2(this);
        //로딩창을 투명하게
//        exampleDialogPM2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        exampleDialogPM2.show();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        Log.v("bfsize", String.valueOf(bufferSize));

        //로딩창 객체 생성
        customProgressDialog = new ProgressDialog(this);
        //로딩창을 투명하게
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

//        btnStart = findViewById(R.id.btnStart);
//        btnReStart = findViewById(R.id.btnReStart);
//        btnRequest = findViewById(R.id.btnRequest);
        recordingTimeText = findViewById(R.id.recodingTime);
        recordComment2 = findViewById(R.id.recordComment2);
        btnBack = findViewById(R.id.back);
        testImg = findViewById(R.id.testImg);
        record1 = findViewById(R.id.record1);
        record2 = findViewById(R.id.record2);
        record3 = findViewById(R.id.record3);
//        progress2 = findViewById(R.id.progress2);
//        progress3 = findViewById(R.id.progress3);
        record3bg = findViewById(R.id.record3bg);
        title = findViewById(R.id.title);
        Intent intent = getIntent();
        tempValue = intent.getStringExtra("tempValue");

        testImg.setClipToOutline(true);

        title.setText("음성 테스트 2");
        record1.setEnabled(false);
        record3.setEnabled(false);

//        // 요일별 이미지 설정
//        SharedPreferences sharedPreferences = getSharedPreferences("File", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("Date",today.toString());
//        editor.apply();
//        seq = sharedPreferences.getInt("Sequence",0);
//
//        LocalDate dd =  LocalDate.parse(sharedPreferences.getString("Date",null), DATE_FORMATTER);
//        if (today.isAfter(dd) && !today.isEqual(dd)){
//            // 하루지난경우 그림 순서 +1, 저장
//            seq += 1;
//            if(seq < 7){
//                editor.putInt("Sequence",seq);
//            }else if(seq >= 7){
//                editor.putInt("Sequence",0);
//            }
//            editor.commit();
//        }

//        seq = Global.today.getDayOfWeek().getValue()-1;
//        testImg.setImageResource(positives[seq]);
        Random random = new Random();
        int rnd = random.nextInt(8);
        testImg.setImageResource(positives[rnd]);

        record1.setEnabled(false);
        record3.setEnabled(false);

//        btnStart.setOnClickListener(new View.OnClickListener() {
        record2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRecording = !isRecording;
                setRecodingButton(isRecording);
                RecordSeq ++;
            }
        });

//        btnRequest.setOnClickListener(new View.OnClickListener() {
        record3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.show();
                customProgressDialog.setCancelable(false);
//                File rawPath = new File(getExternalCacheDir().getAbsolutePath()+"/test1.pcm");
//                File newPath = new File(getExternalCacheDir().getAbsolutePath()+"/test1.wav");
                File rawPath2 = new File(getExternalCacheDir().getAbsolutePath()+"/test2.pcm");
                File newPath2 = new File(getExternalCacheDir().getAbsolutePath()+"/test2.wav");
                try {
//                    rawToWave(rawPath,newPath); // PCM to WAV
                    rawToWave(rawPath2,newPath2); // PCM to WAV
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String filePath1 = "test1.wav";
                String filePath2 = "test2.wav";
//                CombineWaveFile(filePath1,filePath2);

                connectServer();
//                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
//                startActivity(intent);
//
                recordingTime = 0;
            }
        });
        record1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(view.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_reset);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                dialog.findViewById(R.id.ok).setOnClickListener(v -> {
                    recordingTime = 0;
                    recordingThread = null;
                    timeHandler.removeMessages(0);
                    recordingTimeText.setText("0:00");
//                    isRecording = !isRecording;
                    isRecording = false;
//                    WavFileName = "test1.pcm";
                    WavFileName = "test2.pcm";
                    record2.setEnabled(true);
//                    progress3.setBackgroundResource(R.drawable.layout_bg_gray_circle);
//                    progress2.setBackgroundResource(R.color.title_color);
                    dialog.dismiss();
                });
                dialog.findViewById(R.id.cancel).setOnClickListener(v -> {
                    dialog.dismiss();
                });
            }
        });

        btnBack.setOnClickListener(view -> finish());
    }
    
    private void setRecodingButton(boolean recoding){
        if (recoding){
            startRecording(WavFileName);
//            record2.setImageResource(R.drawable.record4);
            record2.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            timeHandler.sendEmptyMessage(0);
        }else{
            stopRecording();
            isRecording=false;
//            record2.setImageResource(R.drawable.record2);
            record2.setBackgroundResource(R.drawable.bg_layout_circle_purple_gradient);
            timeHandler.removeMessages(0);
//            if (WavFileName.equals("test2.pcm")){
//                testImg.setImageResource(positives[seq]);
//                recordComment2.setText("다시 한 번 녹음 버튼을 누른 뒤\n사진을 보고 느낀점이나 생각을\n자유롭게 말하고 정지 버튼을 누른다음\n오른쪽 업로드 버튼을 눌러주세요.");
//                progress2.setBackgroundResource(R.drawable.layout_bg_gray_circle);
//                progress3.setBackgroundResource(R.color.title_color);
//                Log.v("recordC",RecordSeq+"");
//                if(RecordSeq == 3){
//                    record3bg.setBackgroundResource(R.drawable.layout_bg_rocord3_circle);
//                }
//            }
//            progress2.setBackgroundResource(R.drawable.layout_bg_gray_circle);
//            progress3.setBackgroundResource(R.color.title_color);
//            record3bg.setBackgroundResource(R.drawable.layout_bg_record3_circle);
        }
    }

    private void startRecording(String fileName) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        btnBack.setEnabled(false);
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile(fileName);
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];

        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    private void writeAudioDataToFile(String fileName) {
        // Write the output audio in byte
        filePath = getExternalCacheDir().getAbsolutePath()+"/"+fileName;
        short sData[] = new short[BufferElements2Rec];
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format
            recorder.read(sData, 0, BufferElements2Rec);
            try {
                // writes the data to file from buffer stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        // stops the recording activity
        if (recorder != null) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
            btnBack.setEnabled(true);
            record1.setEnabled(true);
            record3.setEnabled(true);
            record2.setEnabled(false);
            WavFileName="test2.pcm";
        }
    }


    Handler timeHandler = new Handler(){
        public void handleMessage(Message msg){
            recordingTime++;
            int min = recordingTime / 60;
            int sec = recordingTime % 60;
            recordingTimeText.setText(String.format("%02d:%02d",min,sec));
            Log.v("countTime",String.format("%02d:%02d",min,sec));

            timeHandler.sendEmptyMessageDelayed(0,1000);

            if(recordingTime == 31){
                isRecording = !isRecording;
                setRecodingButton(isRecording);

                File rawPath2 = new File(getExternalCacheDir().getAbsolutePath()+"/test2.pcm");
                File newPath2 = new File(getExternalCacheDir().getAbsolutePath()+"/test2.wav");
                try {
//                    rawToWave(rawPath,newPath); // PCM to WAV
                    rawToWave(rawPath2,newPath2); // PCM to WAV
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String filePath1 = "test1.wav";
                String filePath2 = "test2.wav";
                CombineWaveFile(filePath1,filePath2);
                customProgressDialog.show();
                customProgressDialog.setCancelable(false);
                connectServer();
                recordingTimeText.setText("0:00");
                recordingTime = 0;
                timeHandler.removeMessages(0);
            }
        }
    };

    /* Connect Flask */
    void connectServer(){
        startTime = System.currentTimeMillis();
        Log.v("starttime", String.valueOf(startTime));

//        String ipv4Address = "133.186.251.245";
//        String portNumber = "5001";
        String endpoint = "upload";
        String postUrl = Global.AI_SERVER_URL+endpoint;
//        String postUrl= "http://"+ipv4Address+":"+portNumber+"/"+endpoint;

        File mergePath = new File(getExternalCacheDir().getAbsolutePath()+"/test2.wav");
        RequestBody postBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", Global.TOKEN.getId()+"_"+Global.strDate+"_"+"PM_good.wav",RequestBody.create(MultipartBody.FORM, mergePath))
                .build();
        postRequest(postUrl, postBody);
    }

    void postRequest(String postUrl, RequestBody postBody) {
        Log.v("postUrl",postUrl);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
                customProgressDialog.dismiss();
            }

            @Override
            public void onResponse(Call call, final Response response) {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (response.isSuccessful()){
                            Log.d(TAG+"1",response.code()+"");
                            if (response.body() != null && response.code() == 200){
                                try {
                                    String jsonData = response.body().string();
                                    Log.d(TAG,jsonData);

//                                    Intent intent = new Intent(getApplicationContext(), GuideActivity.class);
//                                    intent.putExtra("results",jsonData);
//                                    intent.putExtra("time",startTime);
//
//                                    JSONObject json = new JSONObject(Global.jsonResult);
//                                    JSONObject json1 = json.getJSONObject("depression");
//                                    voiceValue = json1.getString("class");
//                                    intent.putExtra("voiceValue",voiceValue);
//                                    intent.putExtra("tempValue",tempValue);
//                                    intent.putExtra("Type","PM");
//                                    intent.putExtra("testType","PM4");
//                                    Log.d("RECORDPM2",voiceValue+"/"+tempValue);
//                                    startActivity(intent);

                                } catch (Exception e) { //
                                    e.printStackTrace();
                                    customProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "업로드를 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                Log.d(TAG,"body null");
                                customProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "업로드를 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Log.d(TAG+"fail",response.code()+"");
                            Toast.makeText(RecordPM2Activity.this, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        customProgressDialog.dismiss();
                    }
                });
            }
        });

    }
    /* PCM to WAV */
    private void rawToWave(final File rawFile, final File waveFile) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, RECORDER_SAMPLERATE); // sample rate
            writeInt(output, RECORDER_SAMPLERATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {
                bytes.putShort(s);
            }

            output.write(fullyReadFileToBytes(rawFile));
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
    byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis= new FileInputStream(f);
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                    Log.v("rawDataLen",String.valueOf(tmpBuff.length));
                }
            }
        }  catch (IOException e){
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }
    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    private void CombineWaveFile(String file1, String file2) {
        int RECORDER_BPP = 16;
        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
        FileInputStream in1 = null, in2 = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = RECORDER_SAMPLERATE;
        int channels = 1;
//        long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;
        long byteRate = RECORDER_SAMPLERATE * 2;
        byte[] data = new byte[bufferSize];

        try {
            in1 = new FileInputStream(getExternalCacheDir().getAbsolutePath()+"/"+file1);
            in2 = new FileInputStream(getExternalCacheDir().getAbsolutePath()+"/"+file2);

            out = new FileOutputStream(getExternalCacheDir().getAbsolutePath()+"/merge.wav");

            totalAudioLen = in1.getChannel().size() + in2.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);

            while (in1.read(data) != -1) {

                out.write(data);

            }
            while (in2.read(data) != -1) {

                out.write(data);
            }

            out.close();
            in1.close();
            in2.close();
//            Toast.makeText(this, "Done!!", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        int RECORDER_BPP = 16;
        byte[] header = new byte[44];

        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte)(totalDataLen & 0xff);
        header[5] = (byte)((totalDataLen >> 8) & 0xff);
        header[6] = (byte)((totalDataLen >> 16) & 0xff);
        header[7] = (byte)((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte)(longSampleRate & 0xff);
        header[25] = (byte)((longSampleRate >> 8) & 0xff);
        header[26] = (byte)((longSampleRate >> 16) & 0xff);
        header[27] = (byte)((longSampleRate >> 24) & 0xff);
        header[28] = (byte)(byteRate & 0xff);
        header[29] = (byte)((byteRate >> 8) & 0xff);
        header[30] = (byte)((byteRate >> 16) & 0xff);
        header[31] = (byte)((byteRate >> 24) & 0xff);
        header[32] = (byte) 2; // block align
        header[33] = 0;
        header[34] = (byte) RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte)(totalAudioLen & 0xff);
        header[41] = (byte)((totalAudioLen >> 8) & 0xff);
        header[42] = (byte)((totalAudioLen >> 16) & 0xff);
        header[43] = (byte)((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.checkedNetwork(this);
    }

}
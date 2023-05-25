package ai.megaworks.ema.layout;

import static ai.megaworks.ema.util.RecordUtils.BUFFER_ELEMENTS2REC;
import static ai.megaworks.ema.util.RecordUtils.BYTES_PER_ELEMENT;
import static ai.megaworks.ema.util.RecordUtils.RECORDER_AUDIO_ENCODING;
import static ai.megaworks.ema.util.RecordUtils.RECORDER_CHANNELS;
import static ai.megaworks.ema.util.RecordUtils.RECORDER_SAMPLERATE;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;
import ai.megaworks.ema.databinding.FragmentVoiceRecordItemBinding;
import ai.megaworks.ema.dialog.ProgressDialog;
import ai.megaworks.ema.domain.survey.Survey;
import ai.megaworks.ema.domain.survey.SurveyResult;
import ai.megaworks.ema.util.RecordUtils;

public class VoiceRecordItemFragment extends CustomSurveyFragment {

    private final Context context;
    private final Survey survey;
    private Class clazz;
    private String savePath;
    private ProgressDialog customProgressDialog = null;

    private FragmentVoiceRecordItemBinding binding = null;

    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private int recordingTime = 0;

    private List<String> filePaths = new ArrayList<>();

    private int fileCount = 0;
    private String baseFileName = Global.dateToString(Global.DATETIME_FORMATTER3);

    public VoiceRecordItemFragment(Context context, Survey survey) {
        this.context = context;
        this.survey = survey;
        this.surveyResult.setSubSurveyId(survey.getId());
        this.surveyResult.setSurveySubjectId(Global.TOKEN.getSurveySubjectId());
        this.surveyResult.setSurveyAt(Global.defaultDateStr);
        baseFileName = Global.TOKEN.getSubjectId() + "_" + survey.getId() + "_" + baseFileName;
    }

    public VoiceRecordItemFragment(Context context, Survey survey, Class clazz) {
        this(context, survey);
        this.clazz = clazz;
    }

    public VoiceRecordItemFragment(Context context, Survey survey, String savePath) {
        this(context, survey);
        this.savePath = savePath;
    }

    public VoiceRecordItemFragment(Context context, Survey survey, Class clazz, String savePath) {
        this(context, survey, clazz);
        this.savePath = savePath;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        customProgressDialog = new ProgressDialog(context);
//        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        binding = FragmentVoiceRecordItemBinding.inflate(inflater, container, false);

        binding.recordComment.setText(survey.getDescription());

        if (this.clazz != null) {
            binding.root.setOnClickListener(v -> {
                moveToActivity(this.clazz);
            });
        }

        binding.reset.setOnClickListener(view -> {
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
                binding.recodingTime.setText("0:00");
                isRecording = false;
                filePaths.clear();
                dialog.dismiss();
            });
            dialog.findViewById(R.id.cancel).setOnClickListener(v -> {
                dialog.dismiss();
            });

        });

        binding.recordPause.setOnClickListener(view -> {
            isRecording = !isRecording;
            setRecodingButton(isRecording);
        });

        binding.upload.setOnClickListener(v -> {

            File rawPath = RecordUtils.combineRecordFiles(savePath + "/" + baseFileName, filePaths);
            if(rawPath == null) Toast.makeText(context, getString(R.string.warn_no_data_record), Toast.LENGTH_SHORT);
            File newPath = new File(savePath + "/" + baseFileName + ".wav");

            try {
                RecordUtils.rawToWave(rawPath, newPath); // PCM to WAV
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.surveyResult.setFilePath(newPath.getAbsolutePath());

            recordingTime = 0;
        });

        return binding.root;
    }

    private void setRecodingButton(boolean recoding) {
        if (recoding) {
            String filePath = savePath + "/" + baseFileName + "_" + fileCount++;
            startRecording(filePath);
            // 시작할 때마다 파일 생성
            filePaths.add(filePath + ".pcm");
            binding.recordPause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            timeHandler.sendEmptyMessage(0);

        } else {
            stopRecording();
            isRecording = false;
            binding.recordPause.setBackgroundResource(R.drawable.bg_layout_circle_purple_gradient);
            timeHandler.removeMessages(0);
        }
    }

    private void startRecording(String fileName) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, getString(R.string.permission_denied_record), Toast.LENGTH_SHORT).show();
        }

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BUFFER_ELEMENTS2REC * BYTES_PER_ELEMENT);

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(() -> writeAudioDataToFile(fileName), "AudioRecorder Thread");
        recordingThread.start();
    }

    private void stopRecording() {
        // stops the recording activity
        if (recorder != null) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }
    }

    private void writeAudioDataToFile(String fileName) {
        short sData[] = new short[BUFFER_ELEMENTS2REC];
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(fileName + ".pcm");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            recorder.read(sData, 0, BUFFER_ELEMENTS2REC);
            try {
                byte bData[] = RecordUtils.short2byte(sData);
                os.write(bData, 0, BUFFER_ELEMENTS2REC * BYTES_PER_ELEMENT);
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

    Handler timeHandler = new Handler() {
        public void handleMessage(Message msg) {
            recordingTime++;
            int min = recordingTime / 60;
            int sec = recordingTime % 60;
            binding.recodingTime.setText(String.format("%02d:%02d", min, sec));
            Log.v("countTime", String.format("%02d:%02d", min, sec));

            timeHandler.sendEmptyMessageDelayed(0, 1000);
            if (recordingTime == 31) {
                isRecording = !isRecording;
                setRecodingButton(isRecording);

                File rawPath = RecordUtils.combineRecordFiles(savePath + "/" + baseFileName, filePaths);
                if(rawPath == null) Toast.makeText(context, getString(R.string.warn_no_data_record), Toast.LENGTH_SHORT);
                File newPath = new File(savePath + "/" + baseFileName + ".wav");

                try {
                    RecordUtils.rawToWave(rawPath, newPath); // PCM to WAV
                } catch (IOException e) {
                    e.printStackTrace();
                }

                binding.recodingTime.setText("0:00");
                recordingTime = 0;
                timeHandler.removeMessages(0);
            }
        }
    };

    @Override
    public SurveyResult getSurveyResult() {
        return this.surveyResult;
    }

    private void moveToActivity(Class clazz) {
        Intent intent = new Intent(context, clazz);
        startActivity(intent);
    }

}
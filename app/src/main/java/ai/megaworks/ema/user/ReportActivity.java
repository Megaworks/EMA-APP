package ai.megaworks.ema.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;
import ai.megaworks.ema.api.MindcareApi;
import ai.megaworks.ema.api.ReportData;
import ai.megaworks.ema.api.RetrofitClient;
import ai.megaworks.ema.api.UserData;
import ai.megaworks.ema.api.UserPHQ9;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {

    private AppCompatButton btnNext;
    private LinearLayout back;
    private BarChart barChart;
    private PieChart pieChart;
    private ProgressBar progressBar;
    private int phq9;
    private TextView comment1, comment2, comment3, phq9score;
    private int totTempAm = 0;
    private int totTempPm = 0;
    private float avgVoice = 0;
    private float avgTemp = 0;
    private TextView thisWeekBtn;
    private LocalDateTime periodDate = null;


    private MindcareApi mindcareApi = RetrofitClient.getRetrofitInterface();
    private RetrofitClient retrofitClient = RetrofitClient.getInstance();

    // 안드로이드 뒤로가기 버튼 기능
    private  BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        btnNext = findViewById(R.id.btnNext);
        back = findViewById(R.id.back);
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        progressBar = findViewById(R.id.progress);
        comment1 = findViewById(R.id.comment1);
        comment2 = findViewById(R.id.comment2);
        comment3 = findViewById(R.id.comment3);
        phq9score = findViewById(R.id.phq9score);
        thisWeekBtn = findViewById(R.id.thisWeekBtn);

        btnNext.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), FreudFeedbackActivity.class);
            intent.putExtra("totTempAM",totTempAm);
            intent.putExtra("totTempPM",totTempPm);
            intent.putExtra("avgVoice",avgVoice);
            intent.putExtra("avgTemp",avgTemp);
            intent.putExtra("periodDate",periodDate);
            Log.d("TOTALTEMPDATA22",totTempAm+"/"+totTempPm+"/"+avgVoice);
            startActivity(intent);
        });

        thisWeekBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ThisWeekReportActivity.class);
            startActivity(intent);
        });

        getThisWeekData();
        getUSERPHQ();
        getLastWeekValue();
        back.setOnClickListener(view -> {finish();});
    }
//    @Override
//    public void onBackPressed() {
//        backKeyHandler.onBackPressed();
//    }

//    public void getBarChart(){
    public void getBarChart(float[] valOne, float[] valTwo, LocalDate localDate){
        barChart.setDrawBarShadow(false);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.setBackgroundColor(Color.TRANSPARENT);
        // empty labels so that the names are spread evenly
        String[] labels2 = {"","월", "화", "수", "목", "금", "토", "일",""};
        String[] labels = {"","월", "화", "수", "목", "금", "토", "일",""};
        // 라벨을 날짜로 설정
        String lineSep = System.getProperty("line.separator");
        for (int i=1;i<8;i++){
            labels[i] = Global.DATE_FORMATTER4.format(localDate.plusDays(i-1))+" "+labels2[i];
        }
        XAxis xAxis = barChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setTextColor(Color.BLACK);
//        xAxis.setTextSize(12);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisMinimum(1f);
        xAxis.setAxisMaximum(labels.length - 1.1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(12);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularity(1);
        leftAxis.setAxisMaxValue(10);
        leftAxis.setAxisMinValue(0);
        leftAxis.setDrawZeroLine(true);
//        leftAxis.setLabelCount(8, true);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        barChart.getAxisRight().setDrawLabels(false);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawGridBackground(false);

//        float[] valOne = {1, 2, 3, 4, 5, 6, 7};
//        float[] valTwo = {7, 6, 5, 4, 3, 2, 1};
//        float[] valThree = {50, 60, 20, 10, 30};

        ArrayList<BarEntry> barOne = new ArrayList<>();
        ArrayList<BarEntry> barTwo = new ArrayList<>();
//        ArrayList<BarEntry> barThree = new ArrayList<>();
        for (int i = 0; i < valOne.length; i++) {
            Log.d("BARCHART_DATA",valOne[i]+"//"+valTwo[i]);
            barOne.add(new BarEntry(i, valOne[i]));
            barTwo.add(new BarEntry(i, valTwo[i]));
//            barThree.add(new BarEntry(i, valThree[i]));
        }

        BarDataSet set1 = new BarDataSet(barOne, "barOne");
        set1.setColor(Global.REPORT_COLORS[2]);
        BarDataSet set2 = new BarDataSet(barTwo, "barTwo");
        set2.setColor(Global.REPORT_COLORS[3]);
//        BarDataSet set3 = new BarDataSet(barThree, "barTwo");
//        set2.setColor(Color.GREEN);

        set1.setHighlightEnabled(false);
        set2.setHighlightEnabled(false);
//        set3.setHighlightEnabled(false);
        set1.setDrawValues(false);
        set2.setDrawValues(false);
//        set3.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
//        dataSets.add(set3);
        BarData data = new BarData(dataSets);
        float groupSpace = 0.4f;
        float barSpace = 0f;
        float barWidth = 0.3f;
        // (barSpace + barWidth) * 2 + groupSpace = 1
        data.setBarWidth(barWidth);
        // so that the entire chart is shown when scrolled from right to left

        barChart.setData(data);
        barChart.setScaleEnabled(false);
        barChart.setVisibleXRangeMaximum(7f);
        barChart.groupBars(1f, groupSpace, barSpace);
//        barChart.animateX(1000);
        barChart.invalidate();

    }


    private void getPiechart(int value1, int value2, int[] gcolor){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        //initializing data
        ArrayList<Integer> values = new ArrayList<>();
        values.add(value1);
        values.add(value2);

        PieDataSet pieDataSet = new PieDataSet(pieEntries,"");
        if (value1 + value2 == 0){
            for (int i=0;i<2;i++){
                pieEntries.add(new PieEntry(values.get(i)));
            }
            pieDataSet.setColors(Global.NODATA_COLORS);
        }else{
            ArrayList<String> labels = new ArrayList<>();
            Log.v("piechartD",Math.round(((float) value1/(value1+value2))*100) +"");
            labels.add(String.format("%.1f",((float) value1/(value1+value2))*100)+"%");
            labels.add(String.format("%.1f",((float) value2/(value1+value2))*100)+"%");

            for (int i=0;i<2;i++){
                pieEntries.add(new PieEntry(values.get(i),labels.get(i)));
            }

//            pieDataSet.setColors(Global.REPORT_COLORS);
            pieDataSet.setColors(gcolor);
        }




        PieData data = new PieData((pieDataSet));
        data.setDrawValues(false);

        pieChart.setData(data);
//        pieChart.setDrawEntryLabels(false);
        pieChart.invalidate();
        pieChart.getDescription().setEnabled(false);
        pieChart.setTouchEnabled(false);
        pieChart.setDrawSlicesUnderHole(false);
        pieChart.getLegend().setEnabled(false);

    }

    public void getUSERPHQ(){
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        mindcareApi.getUserPHQ9("Bearer "+Global.TOKEN.getToken(), Global.TOKEN.getId()).enqueue(new Callback<List<UserPHQ9>>() {
            @Override
            public void onResponse(Call<List<UserPHQ9>> call, Response<List<UserPHQ9>> response) {
                if(response.isSuccessful()){
                    if(response.body().size() >0){
                        UserPHQ9 userPHQ9 = response.body().get(0);
                        phq9 = userPHQ9.getPhq9();
                        // PHQ-9 점수 설정
                        progressBar.setProgress(phq9);
                        phq9score.setText(phq9+"/27점");
                        Log.d("AAAAAAA",phq9+"");
                        String phq9R = "";
                        if (phq9 <= 4){
                            phq9R = "정상";
                        }else if (phq9 <= 9){
                            phq9R = "가벼운 우울감";
                        }else if (phq9 <= 19){
                            phq9R = "중간 정도의 우울감";
                        }else {
                            phq9R = "심한 우울감";
                        }
                        comment3.setText("설문지로 확인한 회원님의 지난 2주간 기분은\n"+phq9R+"에 해당합니다.");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserPHQ9>> call, Throwable t) {

            }
        });
    }

    public void getLastWeekValue(){
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        mindcareApi.getLastWeekValue("Bearer "+Global.TOKEN.getToken(), Global.TOKEN.getId()).enqueue(new Callback<List<UserData>>() {
            @Override
            public void onResponse(Call<List<UserData>> call, Response<List<UserData>> response) {
                if (response.isSuccessful()){
                    List<UserData> userData;
                    if (response.body().size() != 0){
                        userData = response.body();
                        float[] valOne = {0,0,0,0,0,0,0};
                        float[] valTwo = {0,0,0,0,0,0,0};
                        periodDate = LocalDateTime.parse(userData.get(0).getCreated(), Global.DATETIME_FORMATTER2);
                        ArrayList<Integer> voice0 = new ArrayList<>(); // 보통
                        ArrayList<Integer> voice1 = new ArrayList<>(); // 주의
                        Log.d("SSSSSSSSSSSSSS",userData.size()+"");
                        // 기분온도계이력
                        int start = 0;
                        int seq = 0;

                        for (int i = 0;i<userData.size();i++){ // 데이터 표시
                            String date = userData.get(i).getCreated();
                            LocalDateTime userDate = LocalDateTime.parse(date,Global.DATETIME_FORMATTER2); // 데이터 날짜
                            // 기분 온도계
                            if (userData.get(i).getTesttime().equals("AM")){
                                valOne[userDate.getDayOfWeek().getValue()-1] = userData.get(i).getEmotion();
                                totTempAm += userData.get(i).getEmotion();
                            }else{
                                valTwo[userDate.getDayOfWeek().getValue()-1] = userData.get(i).getEmotion();
                                totTempPm += userData.get(i).getEmotion();
                            }
                            Log.d("TOTALTEMPDATA",totTempAm+"/"+totTempPm);
                            // 음성 테스트
                            if (userData.get(i).getVoice() == 0){
                                voice0.add(userData.get(i).getVoice());
                            }else{
                                voice1.add(userData.get(i).getVoice());
                            }

                        }

                        int totTemp = totTempAm+totTempPm;
                        avgTemp = (float) totTemp/(userData.size());
                        comment1.setText("지난 주의 평균 기분 온도 점수는 "+String.format("%.1f",avgTemp)+"점으로 "+Global.getTempAVG(avgTemp)+"에 해당합니다.");

//                        getBarChart(valOne, valTwo);
//                    if (voice0.size()+voice1.size() >= 14){
//                        comment2.setText("1주간 AI프로이드가 예측한 결과는\n 우울기분 "+voice1.size()+"회로 "+String.format("%.1f",((float) voice1.size()/(voice0.size()+voice1.size()))*100)+"%로 예측하였습니다.");
//                        getPiechart(voice0.size(),voice1.size());
//                    }else{
//                        getPiechart(1,0);
//                    }
                        comment2.setText("지난 주의 AI프로이드가 예측한 결과는\n주의 : "+voice1.size()+"회, 보통 : "+voice0.size()+"회 입니다.");
//                        getPiechart(voice0.size(),voice1.size(),Global.REPORT_COLORS);

                        // 저번주 데이터가 8개가 안될때 (4일)
//                    if (userData.size() < 8){
//                        // 주간 데이터 분석 x
//                        avgVoice = 0;
//                        avgTemp = 0;
//
//                    } else{
                        // 주간 데이터 분석 o
                        avgVoice = ((float) voice0.size()/(voice0.size()+voice1.size()))*10;

//                    }
                        int total_score = (int) ((int) avgTemp*avgVoice);
//                        upDateScore(total_score);
                    }else{
                        float[] valOne = {0,0,0,0,0,0,0};
                        float[] valTwo = {0,0,0,0,0,0,0};
//                        getBarChart(valOne, valTwo);
                        getPiechart(1,0,Global.NODATA_COLORS);
                        comment1.setText("지난 주의 데이터가 없습니다");
                        comment2.setText("지난 주의 데이터가 없습니다");
                    }


                }
            }

            @Override
            public void onFailure(Call<List<UserData>> call, Throwable t) {

            }
        });
    }

    public void upDateScore(int score){
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        UserData userData = new UserData(Global.TOKEN.getId(), score);
        mindcareApi.updateScore("Bearer "+Global.TOKEN.getToken(), userData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Log.d("UPDATESCORE_DATA",response.message()+"");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void getThisWeekData(){
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        mindcareApi.getThisWeekData("Bearer "+Global.TOKEN.getToken(), Global.TOKEN.getId()).enqueue(new Callback<ReportData>() {
            @Override
            public void onResponse(Call<ReportData> call, Response<ReportData> response) {
                if (response.isSuccessful()){
                    float[] valOne = {0,0,0,0,0,0,0};
                    float[] valTwo = {0,0,0,0,0,0,0};
                    ReportData reportData = response.body();
                    String mondayDate = reportData.getMonday();
                    if (reportData.getUserReport().size() != 0){

                        List<UserData> thisWeekData = reportData.getUserReport();
                        ArrayList<Integer> voice0 = new ArrayList<>(); // 정상 -> 보통
                        ArrayList<Integer> voice1 = new ArrayList<>(); // 우울 -> 주의

                        for (int i = 0;i<thisWeekData.size();i++){ // 데이터 표시
                            String date = thisWeekData.get(i).getCreated();
                            LocalDateTime userDate = LocalDateTime.parse(date,Global.DATETIME_FORMATTER2); // 데이터 날짜

                            // 기분 온도계
                            if (thisWeekData.get(i).getTesttime().equals("AM")){
                                valOne[userDate.getDayOfWeek().getValue()-1] = thisWeekData.get(i).getEmotion();
//                                totTempAm += thisWeekData.get(i).getEmotion();
                            }else{
                                valTwo[userDate.getDayOfWeek().getValue()-1] = thisWeekData.get(i).getEmotion();
//                                totTempPm += thisWeekData.get(i).getEmotion();
                            }
//                            Log.d("TOTALTEMPDATA",totTempAm+"/"+totTempPm);
                            // 음성진단
                            if (thisWeekData.get(i).getVoice() == 0){
                                voice0.add(thisWeekData.get(i).getVoice());
                            }else{
                                voice1.add(thisWeekData.get(i).getVoice());
                            }

                        }
                        mondayDate = mondayDate==null?Global.DATE_FORMATTER2.format(Global.today):mondayDate;
                        LocalDate localDate = LocalDate.parse(mondayDate,Global.DATE_FORMATTER2);
                        getBarChart(valOne, valTwo,localDate);
                        getPiechart(voice0.size(),voice1.size(),Global.REPORT_COLORS);
                    }else{
                        LocalDate localDate = Global.getMondayOfWeek(Global.DATE_FORMATTER2.format(Global.today));
                        getBarChart(valOne, valTwo, localDate);
                        getPiechart(1,0,Global.NODATA_COLORS);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReportData> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.checkedNetwork(this);
    }

}

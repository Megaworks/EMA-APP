package ai.megaworks.ema;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import ai.megaworks.ema.api.Token;

public class Global {

    private Global() {}

    public static LocalDateTime today = LocalDateTime.now();

    public static Token TOKEN = new Token();

    public static boolean btnVoice = true;

    //public static String IP_ADDRESS = "133.186.251.245"; // 국가 데이터센터
    public static String IP_ADDRESS = "192.168.0.27"; // 메가웍스 gpu 서버
    public static String PORT_NUMBER = "8081";
    public static String AI_SERVER_URL = "http://"+IP_ADDRESS+":"+PORT_NUMBER+"/";

    public final static DateTimeFormatter DATETIME_FORMATTER2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public final static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh:mm");
    public final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
    public final static DateTimeFormatter DATE_FORMATTER2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter DATE_FORMATTER3 = DateTimeFormatter.ofPattern("yyMMdd");
    public final static DateTimeFormatter DATE_FORMATTER4 = DateTimeFormatter.ofPattern("MM/dd");

    public static int TITLE_TEXT_COLOR = Color.rgb(116,110,216);
    public static int GRAY_COLOR = Color.rgb(85,85,85);

    public static String strDate = Global.DATE_FORMATTER2.format(Global.today); //yyMMdd
    public static String jsonResult = null;

    public final static int[] REPORT_COLORS = {
            Color.parseColor("#f4c238"), //주황색 //report1
            Color.parseColor("#a097f9"), //보라 //report2
            Color.parseColor("#f1a2b2"), //분홍 //report3
            Color.parseColor("#95ceec"), //하늘 //report4


            Color.parseColor("#785dcf"), //보라색
            Color.parseColor("#ff8383"), //분홍색
            Color.parseColor("#88b4bb"), //옥색
            Color.parseColor("#76aeaf"), //옥색2
            Color.parseColor("#2a6d82"), //청록색
    };
    public final static int[] GRAPH_COLORS = {
            //[1. 기분 온도계] 상위에 있는 색 부터 좋음, 평범, 힘듦
            Color.parseColor("#8ad491"), //  // color:temp1
            Color.parseColor("#95ceec"), //    // color:temp2
            Color.parseColor("#f1a2b2"), //    // color:temp3

            Color.parseColor("#f4c238"),    //
            Color.parseColor("#f1a2b2"),    //

            Color.parseColor("#424481"), //진파랑
            Color.parseColor("#e65775"), //진분홍
            Color.parseColor("#f2d541"), //샛노랑
            Color.parseColor("#c6963c"), //겨자색
            Color.parseColor("#4a4949"), //진회색

    };

    public final static int[] GRAPH_COLORS2 = {
            //[2.음성 테스트] 상위에 있는 색 부터 보통, 주의
            Color.parseColor("#f4c238"),    // color:voice1
            Color.parseColor("#a097f9"),   // color:voice2

            Color.parseColor("#424481"), //진파랑
            Color.parseColor("#c6963c"), //겨자색
            Color.parseColor("#f2d541"), //샛노랑
            Color.parseColor("#e65775"), //진분홍
            Color.parseColor("#4a4949"), //진회색

    };

    public final static int[] GRAPH_COLORS3 = {
            //[3.PHQ-9] 상위에 있는 색 부터 정상, 경도, 중증도, 중증
            Color.parseColor("#8ad491"), //
            Color.parseColor("#95ceec"), //
            Color.parseColor("#a097f9"), //
            Color.parseColor("#f1a2b2"), //


            Color.parseColor("#424481"), //진파랑
            Color.parseColor("#c6963c"), //겨자색
            Color.parseColor("#f2d541"), //샛노랑
            Color.parseColor("#e65775"), //진분홍
            Color.parseColor("#4a4949"), //진회색

    };
    public final static int[] GRAPH_COLORS4 = {
            //[4.프로이드분석] 상위에 있는 색 부터 정상, 우울의심, 우울, 심각한우울
            Color.parseColor("#8ad491"), //
            Color.parseColor("#f4c238"), //
            Color.parseColor("#a097f9"), //
            Color.parseColor("#f1a2b2"), //

            Color.rgb(242, 213, 65), //샛노랑 #f2d541
            Color.rgb(66, 68, 129),  //진파랑 #424481
            Color.rgb(230, 87, 117),  //진분홍 #e65775
            Color.rgb(198, 150, 60),   //겨자색 #c6963c
            Color.rgb(74, 73, 73) //진회색 #4a4949
    };
    public final static int[] NODATA_COLORS = {
            // 아닌듯 이거 아닌듯,, 아 이거 노데이터네
            Color.parseColor("#ededed"),
            Color.parseColor("#5c5c5c"),
            Color.parseColor("#9989B4"),
            Color.parseColor("#ff7e02"),
            Color.parseColor("#FEFF98"),

            Color.rgb(200, 200, 200), //연회색 #c8c8c8
            Color.rgb(105, 100, 253), //청보라색 #6964fd
            Color.rgb(136, 180, 187), //옥색 #88b4bb
            Color.rgb(118, 174, 175), //옥색2 #76aeaf
            Color.rgb(42, 109, 130) //청록색 #2a6d82
    };

    public static void setListViewHeightBasedOnChildren(ListView listView, int type) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (type == 1){
            params.height = 90*listAdapter.getCount(); // 아이템 1개 높이 * 갯수
        }else if (type == 2){
//            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()-1)); // 아이템 1개 높이 * 갯수
            params.height = 360*listAdapter.getCount(); // 아이템 1개 높이 * 갯수
        }else if (type == 3){
            params.height = 140*listAdapter.getCount(); // 아이템 1개 높이 * 갯수
        }

        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String getTempAVG(float value){
        if(value>=8){
            return "좋음";
        }else if(value>=5){
            return "평범";
        }else{
            return "힘듦";
        }

    }

    // 해당 일의 주차 구하기
    public static int getWeekValue(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date value = null;
        try {
            value = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        return week;
    }

    // 해당 일의 순서 구하기 월요일 = 1
    public static int getDayValue(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date value = null;
        try {
            value = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        LocalDate localDate = LocalDate.parse(format.format(calendar.getTime()),Global.DATE_FORMATTER2);
        int day = localDate.getDayOfWeek().getValue();
        return day;
    }

    // 해당 달의 일수 구하기
    public static int getDayCount(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date value = null;
        try {
            value = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        int count = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return count;
    }

    // 해당 일의 값 구하기
    public static int getDayNum(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date value = null;
        try {
            value = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        int count = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        if(Global.getMonthNum(date) == Global.getMonthNum(Global.DATE_FORMATTER2.format(Global.today))){
            count = calendar.get(Calendar.DAY_OF_MONTH);
        }else if(Global.getMonthNum(date) > Global.getMonthNum(Global.DATE_FORMATTER2.format(Global.today))){
            count = 0;
        }
        return count;
    }
    // 해당 일의 월 구하기
    public static int getMonthNum(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date value = null;
        try {
            value = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        int count = calendar.get(Calendar.MONTH);
        return count;
    }

    // 해당 주의 월요일 날짜 구하기
    public static LocalDate getMondayOfWeek(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date value = null;
        try {
            value = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
//        Log.d("DAYOFWEEK_MONDAY",calendar.getTime()+"//"+format.format(calendar.getTime()));
        LocalDate localDate = LocalDate.parse(format.format(calendar.getTime()),Global.DATE_FORMATTER2);
        return localDate;
    }

    // 해당 달의 주 갯수 구하기
    public static int getLastWeek(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date value = null;
        try {
            value = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String lastDate = calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+lastDay;
        Log.d("LAST_DATE",lastDate);

        try {
            value = format.parse(lastDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(value);
        int lastWeek = calendar.get(Calendar.WEEK_OF_MONTH);

        return lastWeek;

    }

    public static long getDateValue(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date value = null;
        long dateValue = 0;
        try {
            value = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);
        dateValue = calendar.getTimeInMillis();
//        Log.d("DAYOFWEEK_MONDAY",calendar.getTime()+"//"+format.format(calendar.getTime()));
//        LocalDate localDate = LocalDate.parse(format.format(calendar.getTime()),Global.DATE_FORMATTER2);
        return dateValue;
    }

    public static void setThread(){
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public static void checkedNetwork(AppCompatActivity activity){
        ConnectivityManager manager = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo == null){
            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_network_null);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            dialog.findViewById(R.id.ok).setOnClickListener(v -> {
                dialog.dismiss();
                activity.finishAffinity();
            });

        }

    }
}

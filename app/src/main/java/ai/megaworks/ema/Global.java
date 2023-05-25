package ai.megaworks.ema;

import android.os.Build;
import android.os.StrictMode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import ai.megaworks.ema.domain.Token;

public class Global {

    public static LocalDateTime today = LocalDateTime.now();

    public static Token TOKEN = new Token();
    public static String IP_ADDRESS = "59.0.226.47"; // 메가웍스 gpu 서버
    public static String PORT_NUMBER = "8081";
    public static String API_SERVER_URL = "http://" + IP_ADDRESS + ":" + PORT_NUMBER + "/";
    public final static DateTimeFormatter DATETIME_FORMATTER1 = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh:mm");
    public final static DateTimeFormatter DATETIME_FORMATTER2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public final static DateTimeFormatter DATETIME_FORMATTER3 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");

    public final static DateTimeFormatter DATE_FORMATTER1 = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
    public final static DateTimeFormatter DATE_FORMATTER2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter DATE_FORMATTER3 = DateTimeFormatter.ofPattern("yyMMdd");
    public final static DateTimeFormatter DATE_FORMATTER4 = DateTimeFormatter.ofPattern("MM/dd");

    public static String defaultDateStr = Global.DATE_FORMATTER2.format(Global.today); //yyMMdd
    public static String jsonResult = null;

    public static String dateToString(DateTimeFormatter formatter){
        return formatter.format((today));
    }

    public static void setThread() {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }
}

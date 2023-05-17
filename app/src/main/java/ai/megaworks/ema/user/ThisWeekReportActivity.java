package ai.megaworks.ema.user;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.List;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;
import ai.megaworks.ema.api.MindcareApi;
import ai.megaworks.ema.api.RetrofitClient;
import ai.megaworks.ema.api.UserData;
import ai.megaworks.ema.api.UserDetailData;
import ai.megaworks.ema.api.UserPHQ9;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThisWeekReportActivity extends AppCompatActivity {

    // 안드로이드 뒤로가기 버튼 기능
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    private LinearLayout back;
    private ListView tempList, voiceList, phq9List;
    private AppCompatButton btnNext;
    private TextView userName;
    private ScrollView scrollView;
    private TextView tv_avgEmotion,tv_totVoice0,tv_totVoice1;

    private MindcareApi mindcareApi = RetrofitClient.getRetrofitInterface();
    private RetrofitClient retrofitClient = RetrofitClient.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_this_week);

        back = findViewById(R.id.back);
        scrollView = findViewById(R.id.scrollView);
        userName = findViewById(R.id.btnNext);
        tempList = findViewById(R.id.listTemp);
        voiceList = findViewById(R.id.listVoice);
        phq9List = findViewById(R.id.listPHQ9);
        btnNext = findViewById(R.id.btnNext);
        tv_avgEmotion = findViewById(R.id.tv_avgEmotion);
        tv_totVoice0 = findViewById(R.id.tv_totVoice0);
        tv_totVoice1 = findViewById(R.id.tv_totVoice1);


        tempList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        voiceList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        phq9List.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


        Long userid = Global.TOKEN.getId();

        getUserData(userid);
        getUserPHQ9Data(userid);

        back.setOnClickListener(view -> finish());
        btnNext.setOnClickListener(view -> finish());


    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    // 사용자관리 어댑터
    public class ReportAdapter extends BaseAdapter {
        // 컨텍스트 객체
        private Context context;

        // 어댑터 타입
        private int type;

        // 결과이력 아이템
        private ArrayList<UserDetailData> items;

        // phq9 아이템
        private List<UserData> amdatas, pmdatas;

        public ReportAdapter(Context context, ArrayList<UserDetailData> items, int type) {
            this.context = context;
            this.items = items;
            this.type = type;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = convertView;
            if (type == 1){
                view = inflater.inflate(R.layout.user_info_temp_list_view, null, false);
                TextView tempDate = view.findViewById(R.id.tempDate);
                TextView tempAm = view.findViewById(R.id.tempAm);
                TextView tempPm = view.findViewById(R.id.tempPm);

                UserDetailData item = items.get(position);
                tempDate.setText(item.getCreated());
                tempAm.setText(item.getAmemotion());
                tempPm.setText(item.getPmemotion());
            }else if (type == 2){
                view = inflater.inflate(R.layout.user_info_temp_list_view, null, false);
                TextView tempDate = view.findViewById(R.id.tempDate);
                TextView tempAm = view.findViewById(R.id.tempAm);
                TextView tempPm = view.findViewById(R.id.tempPm);

                UserDetailData item = items.get(position);
                tempDate.setText(item.getCreated());
                tempAm.setText(item.getAmvoice());
                tempPm.setText(item.getPmvoice());
            }else if (type == 3){
                view = inflater.inflate(R.layout.user_info_phq9_list_view, null, false);
                TextView phqDate = view.findViewById(R.id.phq9Date);
                TextView phqValue = view.findViewById(R.id.phq9Score);
                UserDetailData item = items.get(position);
                phqDate.setText(item.getCreated());
                phqValue.setText(item.getPhq9());
            }else if (type == 4){
                view = inflater.inflate(R.layout.user_info_frued_list_view, null, false);
                TextView fruedDate = view.findViewById(R.id.fruedDate);
                TextView fruedComment = view.findViewById(R.id.fruedComment);
                UserDetailData item = items.get(position);
                fruedDate.setText(item.getCreated());
                fruedComment.setText(item.getPhq9());
            }else if (type == 5){
                view = inflater.inflate(R.layout.user_info_counselor_list_view, null, false);
                TextView counselorDate = view.findViewById(R.id.counselorDate);
                TextView counselorComment = view.findViewById(R.id.counselorComment);
                UserDetailData item = items.get(position);
                counselorDate.setText(item.getCreated());
                counselorComment.setText(item.getContent());
            }


            return view;
        }
    }

    // 사용자 상세 정보
    public void getUserData(Long id){
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        mindcareApi.getUserDatabyorder(id).enqueue(new Callback<List<UserData>>() {
            @Override
            public void onResponse(Call<List<UserData>> call, Response<List<UserData>> response) {
                if (response.isSuccessful()){
                    List<UserData> userData = response.body();
//                    String[] dateArr = new String[userData.size()];
                    List<String> distinctArr = new ArrayList<>();

                    for (int i=0;i<7;i++){
                        distinctArr.add(Global.DATE_FORMATTER2.format(Global.today.minusDays(i)));
                    }

//                    for (int i=0;i<userData.size();i++){
//                         dateArr[i] = userData.get(i).getCreated().substring(0,10);
//                    }
                    // 날짜 수집
//                    List<String> distinctArr = Arrays.asList(dateArr).stream().distinct().collect(Collectors.toList());
                    String[][] valuesArr = new String[distinctArr.size()][5]; // date,amT,amV,pmT,pmV
                    for (int i=0;i<distinctArr.size();i++){
                        valuesArr[i][0] = distinctArr.get(i);
                    }

                    for (int i=0;i<userData.size();i++){
                        for(int k=0;k<valuesArr.length;k++){
                            if (userData.get(i).getCreated().substring(0,10).equals(valuesArr[k][0])) {
                                if (userData.get(i).getTesttime().equals("AM")){
                                    valuesArr[k][1] = userData.get(i).getEmotion().toString();
                                    valuesArr[k][2] = userData.get(i).getVoice()==0?"보통":"주의";
                                }else{
                                    valuesArr[k][3] = userData.get(i).getEmotion().toString();
                                    valuesArr[k][4] = userData.get(i).getVoice()==0?"보통":"주의";
                                }
                            }
//                            else{
//                                valuesArr[k][1] = "";
//                                valuesArr[k][2] = "";
//                                valuesArr[k][3] = "";
//                                valuesArr[k][4] = "";
//
//                            }
                        }

                    }
                    int totEmotion = 0;
                    int totVoice0 = 0;
                    int totVoice1 = 0;
                    // date,amT,amV,pmT,pmV
                    for (int i=0;i<valuesArr.length;i++){
                        totEmotion += Integer.parseInt(valuesArr[i][1]==null?"0":valuesArr[i][1]);
                        totEmotion += Integer.parseInt(valuesArr[i][3]==null?"0":valuesArr[i][3]);
                        if ((valuesArr[i][2]==null?"0":valuesArr[i][2]).equals("보통")){
                            totVoice0++;
                        }
                        if ((valuesArr[i][2]==null?"0":valuesArr[i][2]).equals("주의")){
                            totVoice1++;
                        }
                        if ((valuesArr[i][4]==null?"0":valuesArr[i][4]).equals("보통")){
                            totVoice0++;
                        }
                        if ((valuesArr[i][4]==null?"0":valuesArr[i][4]).equals("주의")){
                            totVoice1++;
                        }
                    }
                    Log.d("AVG_DATA",String.format("%.1f",getTot(totEmotion,(totVoice0+totVoice1)))+"/"+totVoice0+"/"+totVoice1);
                    tv_avgEmotion.setText(String.format("%.1f",getTot(totEmotion,(totVoice0+totVoice1)))+"점");
                    tv_totVoice0.setText(totVoice0+"회");
                    tv_totVoice1.setText(totVoice1+"회");

                    ArrayList<UserDetailData> userDatas = new ArrayList<>();
                    if (valuesArr.length != 0){
                        for (int i=0;i<valuesArr.length;i++){
                            UserDetailData userDetailData = new UserDetailData(getValues(valuesArr[i][1]),getValues(valuesArr[i][3]),getValues(valuesArr[i][2]),getValues(valuesArr[i][4]),valuesArr[i][0]);
                            userDatas.add(userDetailData);
                        }
                    }else{
                        UserDetailData userDetailData = new UserDetailData("-","-","-","-","-");
                        userDatas.add(userDetailData);
                    }

                    // 기분온도계
                    ReportAdapter reportAdapter = new ReportAdapter(getApplicationContext(),userDatas,1);
                    tempList.setAdapter(reportAdapter);

                    // 음성진단이력
                    reportAdapter = new ReportAdapter(getApplicationContext(),userDatas,2);
                    voiceList.setAdapter(reportAdapter);


                }
            }

            @Override
            public void onFailure(Call<List<UserData>> call, Throwable t) {

            }
        });
    }

    public void getUserPHQ9Data(Long id){
        retrofitClient = RetrofitClient.getInstance();
        mindcareApi = RetrofitClient.getRetrofitInterface();
        mindcareApi.getUserPHQ9("Bearer "+Global.TOKEN.getToken(), id).enqueue(new Callback<List<UserPHQ9>>() {
            @Override
            public void onResponse(Call<List<UserPHQ9>> call, Response<List<UserPHQ9>> response) {
                if (response.isSuccessful()){
                    Log.d("GETUSERPHQ9_DATA",response.message());
                    List<UserPHQ9> userPHQ9 = response.body();
                    ArrayList<UserDetailData> userphq = new ArrayList<>();
                    if (userPHQ9.size() != 0){
                        for (int i=0;i<userPHQ9.size();i++){
                            UserDetailData userDetailData = new UserDetailData(getValues(userPHQ9.get(i).getCreated().substring(0,10)),getValues(userPHQ9.get(i).getPhq9()==null?"-":userPHQ9.get(i).getPhq9().toString()));
                            userphq.add(userDetailData);
                        }
                    }else{
                        UserDetailData userDetailData = new UserDetailData("분석 이력 없음","-");
                        userphq.add(userDetailData);
                    }


                    // PHQ9 이력
                    ReportAdapter reportAdapter = new ReportAdapter(getApplicationContext(),userphq,3);
                    phq9List.setAdapter(reportAdapter);

                }
            }

            @Override
            public void onFailure(Call<List<UserPHQ9>> call, Throwable t) {

            }
        });
    }

    public String getValues(String val){
        if (val == null || val.equals("")){
            return "-";
        }else{
            return val;
        }
    }

    public float getTot(int value1, int value2){
        if (value2 == 0){
            return 0;
        }else{
            return (float)value1/value2;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.checkedNetwork(this);
    }

}

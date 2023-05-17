package ai.megaworks.ema.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import ai.megaworks.ema.Global;
import ai.megaworks.ema.R;

public class PHQ9Activity extends AppCompatActivity {

    private ListView listView;

    private AppCompatButton setPHQ9;

    private LinearLayout back;

    // 답변 값 리스트
    private String[] answerValues = new String[9];

//    // 질문 리스트
//    private

    // 안드로이드 뒤로가기 버튼 기능
    private  BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phq9);

        listView = findViewById(R.id.listView);
        setPHQ9 = findViewById(R.id.setPHQ9);
        back = findViewById(R.id.back);

        PHQ9Adapder phq9Adapder = new PHQ9Adapder(this);
        listView.setAdapter(phq9Adapder);

        setPHQ9.setOnClickListener(view -> {
            for (int i=0;i<answerValues.length;i++){
                if (answerValues[i] == null){
                    Toast toast = Toast.makeText(this,(i+1)+"번 문항을 답하십시오.",Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                Log.v("results"+i,answerValues[i]+"");
            }
            Intent intent = new Intent(this, PHQ9resultActivity.class);
            intent.putExtra("totalScore",answerValues);
            startActivity(intent);
        });

        back.setOnClickListener(view -> finish());

    }

    public class PHQ9Adapder extends BaseAdapter{
        private Context mcontext;

        // 질문 리스트
        private String[] questions = getResources().getStringArray(R.array.phq9QuestionArray);
        // 답변 리스트
        private String[] answers = getResources().getStringArray(R.array.phq9AnswerArray);
        private LayoutInflater inflater;

        public PHQ9Adapder(Context context){
            this.mcontext = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return questions.length;
        }

        @Override
        public Object getItem(int position) {
            return questions[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
//            ViewHolder holder;
            view = inflater.inflate(R.layout.listview_phq9, parent,false);
            TextView[] textViews = {view.findViewById(R.id.answer0),view.findViewById(R.id.answer1),view.findViewById(R.id.answer2),view.findViewById(R.id.answer3)};
            if(answerValues[position] != null){
                int pos = Integer.parseInt(answerValues[position]);
                textViews[pos].setBackgroundResource(R.drawable.bg_layout_purple_radius1_1);
                textViews[pos].setTextColor(Color.WHITE);
            }
            TextView question = view.findViewById(R.id.question);
            TextView answer0 = view.findViewById(R.id.answer0);
            TextView answer1 = view.findViewById(R.id.answer1);
            TextView answer2 = view.findViewById(R.id.answer2);
            TextView answer3 = view.findViewById(R.id.answer3);

            question.setText(questions[position]);
            answer0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answerValues[position] = "0";
                    Log.v("position",position+"");
                    answer0.setBackgroundResource(R.drawable.bg_layout_purple_radius1_1);
                    answer1.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                    answer2.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                    answer3.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                    answer0.setTextColor(Color.WHITE);
                    answer1.setTextColor(getColor(R.color.title_color_purple3));
                    answer2.setTextColor(getColor(R.color.title_color_purple3));
                    answer3.setTextColor(getColor(R.color.title_color_purple3));
                }
            });

            answer1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answerValues[position] = "1";
                    answer0.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                    answer1.setBackgroundResource(R.drawable.bg_layout_purple_radius1_1);
                    answer2.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                    answer3.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                    answer0.setTextColor(getColor(R.color.title_color_purple3));
                    answer1.setTextColor(Color.WHITE);
                    answer2.setTextColor(getColor(R.color.title_color_purple3));
                    answer3.setTextColor(getColor(R.color.title_color_purple3));
                }
            });

            answer2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answerValues[position] = "2";
                    answer0.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                    answer1.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                    answer2.setBackgroundResource(R.drawable.bg_layout_purple_radius1_1);
                    answer3.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                    answer0.setTextColor(getColor(R.color.title_color_purple3));
                    answer1.setTextColor(getColor(R.color.title_color_purple3));
                    answer2.setTextColor(Color.WHITE);
                    answer3.setTextColor(getColor(R.color.title_color_purple3));
                }
            });

            answer3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answerValues[position] = "3";
                    answer0.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                    answer1.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                    answer2.setBackgroundResource(R.drawable.bg_layout_purple_radius2);
                    answer3.setBackgroundResource(R.drawable.bg_layout_purple_radius1_1);
                    answer0.setTextColor(getColor(R.color.title_color_purple3));
                    answer1.setTextColor(getColor(R.color.title_color_purple3));
                    answer2.setTextColor(getColor(R.color.title_color_purple3));
                    answer3.setTextColor(Color.WHITE);
                }
            });

            return view;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.checkedNetwork(this);
    }
}

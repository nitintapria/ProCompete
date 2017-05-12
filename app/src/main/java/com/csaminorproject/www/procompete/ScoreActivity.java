package com.csaminorproject.www.procompete;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ScoreActivity extends AppCompatActivity {

    TextView mScoreView;
    private static final String TAG = "ScoreActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        mScoreView =(TextView) findViewById(R.id.textView_score);
        String score = QuizActivity.mScore+"\n"+QuizActivity.mNumberOfQuestions;
        mScoreView.setText(score);

        Button back = (Button) findViewById(R.id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG," Return to ProfileActivity");
                Intent intent = new Intent(ScoreActivity.this,ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

package com.csaminorproject.www.procompete;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.csaminorproject.www.procompete.pojo.Question;
import com.csaminorproject.www.procompete.pojo.Questions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    static String mQuizCategory;

    private static final String TAG = "QuizActivity";
    private static final int DELAY_QUESTIONS = 25000;
    private static final int DELAY_OPTION_SELECTED = 2000;

    static Questions mQuestions;
    static List<Question> mQuestionList;
    static String mCorrectOption;
    static boolean oneOptionAlreadyClicked = false;
    static int mCurrentQuestionNumber = 1;
    static int mNumberOfQuestions = 0;
    static int mTime = DELAY_QUESTIONS/1000;
    static String mDisplayTime;
    static int mScore = 0 ;

    TextView mQuizTitle;
    TextView mQuestionStatement;
    TextView mQuizNumber;
    TextView mTimer;
    TextView mOption1;
    TextView mOption2;
    TextView mOption3;
    TextView mOption4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Intent intent = getIntent();
        mQuizCategory = intent.getStringExtra("key");
        Log.d(TAG," taking quiz in "+mQuizCategory);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference questionsRef = rootRef.child(mQuizCategory);

        mQuizTitle = (TextView) findViewById(R.id.textView_quizTitle);
        mQuestionStatement = (TextView) findViewById(R.id.textView_questionStatement);
        mQuizNumber = (TextView) findViewById(R.id.textView_questionNumber);
        mTimer = (TextView) findViewById(R.id.textView_timer);
        mOption1 = (TextView) findViewById(R.id.textView_option1);
        mOption2 = (TextView) findViewById(R.id.textView_option2);
        mOption3 = (TextView) findViewById(R.id.textView_option3);
        mOption4 = (TextView) findViewById(R.id.textView_option4);

        //Retrieving questions with respect to a particular category from the database
        ValueEventListener questionsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mQuestions = dataSnapshot.getValue(Questions.class);
                mQuestionList = new ArrayList<>(mQuestions.getQuestions());
                mNumberOfQuestions = mQuestionList.size();
                mQuizTitle.setText(mQuizCategory);
                runnable.run();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Questions failed, log a message
                Log.d(TAG, "loadQuestions:onCancelled", databaseError.toException());
            }
        };
        questionsRef.addListenerForSingleValueEvent(questionsListener);
    }


    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            nextQuestion();
        }
    };
    Handler handler2 = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            changeTime();
        }
    };

    //To change questions after interval of DELAY_QUESTIONS
    public void nextQuestion() {
        try {
            handler2.post(timerRunnable);
            //Shuffling questions
            Collections.shuffle(mQuestionList);
            if(mQuestionList.size()==mNumberOfQuestions) {
                mScore = 0;
            }
            Question question = mQuestionList.remove(0);
            String displayQuestionNumber = mCurrentQuestionNumber+"/"+mNumberOfQuestions;
            mQuizNumber.setText(displayQuestionNumber);
            mQuestionStatement.setText(question.getQuestionStatement());

            //Shuffling options
            List<String> options = new ArrayList<>();
            options.add(question.getOption1());
            options.add(question.getOption2());
            options.add(question.getOption3());
            options.add(question.getOption4());

            Collections.shuffle(options);
            mOption1.setText(options.get(0));
            mOption2.setText(options.get(1));
            mOption3.setText(options.get(2));
            mOption4.setText(options.get(3));

            mCorrectOption = question.correctOption;

            mOption1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCorrectOption();
                    if(!mOption1.getText().equals(mCorrectOption) && !oneOptionAlreadyClicked) {
                        mOption1.setBackgroundResource(R.drawable.option_background_red);
                    } else if(!oneOptionAlreadyClicked) {
                        mScore++;
                    }
                    if(mQuestionList.isEmpty() && !oneOptionAlreadyClicked) {
                        oneOptionAlreadyClicked = true;
                        showScore();
                    }
                    if(!mQuestionList.isEmpty() && !oneOptionAlreadyClicked) {
                        oneOptionAlreadyClicked = true;
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable,DELAY_OPTION_SELECTED);
                    }
                }
            });
            mOption2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCorrectOption();
                    if(!mOption2.getText().equals(mCorrectOption) && !oneOptionAlreadyClicked) {
                        mOption2.setBackgroundResource(R.drawable.option_background_red);
                    } else if(!oneOptionAlreadyClicked) {
                        mScore++;
                    }
                    if(mQuestionList.isEmpty() && !oneOptionAlreadyClicked) {
                        oneOptionAlreadyClicked = true;
                        showScore();
                    }
                    if(!mQuestionList.isEmpty() && !oneOptionAlreadyClicked) {
                        oneOptionAlreadyClicked = true;
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable,DELAY_OPTION_SELECTED);
                    }
                }
            });
            mOption3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCorrectOption();
                    if(!mOption3.getText().equals(mCorrectOption) && !oneOptionAlreadyClicked) {
                        mOption3.setBackgroundResource(R.drawable.option_background_red);
                    } else if(!oneOptionAlreadyClicked) {
                        mScore++;
                    }
                    if(mQuestionList.isEmpty() && !oneOptionAlreadyClicked) {
                        oneOptionAlreadyClicked = true;
                        showScore();
                    }
                    if(!mQuestionList.isEmpty() && !oneOptionAlreadyClicked) {
                        oneOptionAlreadyClicked = true;
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable,DELAY_OPTION_SELECTED);
                    }
                }
            });
            mOption4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCorrectOption();
                    if(!mOption4.getText().equals(mCorrectOption) && !oneOptionAlreadyClicked) {
                        mOption4.setBackgroundResource(R.drawable.option_background_red);
                    } else if(!oneOptionAlreadyClicked) {
                        mScore++;
                    }
                    if(mQuestionList.isEmpty() && !oneOptionAlreadyClicked) {
                        oneOptionAlreadyClicked = true;
                        showScore();
                    }
                    if(!mQuestionList.isEmpty() && !oneOptionAlreadyClicked) {
                        oneOptionAlreadyClicked = true;
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable,DELAY_OPTION_SELECTED);
                    }
                }
            });
            removeColors();
            if(!mQuestionList.isEmpty())  {
                handler.postDelayed(runnable,DELAY_QUESTIONS);
                oneOptionAlreadyClicked=false;
            } else {
                mCurrentQuestionNumber = 0;
                oneOptionAlreadyClicked=false;
            }
            mCurrentQuestionNumber++;
        } catch(IndexOutOfBoundsException e) {
            Log.e(TAG,"EXTRA QUESTION REMOVE TRY");
        }
    }

    //To show the user a timer which starts from DELAY_QUESTION and decrements to 0
    public void changeTime() {
        if(mTime>=10)
            mDisplayTime = "00:"+mTime;
        else
            mDisplayTime = "00:0"+mTime;
        mTimer.setText(mDisplayTime);
        mTime--;
        if(mTime>0 && !oneOptionAlreadyClicked)
            handler2.postDelayed(timerRunnable,1000);
        else if(mTime>0 && mQuestionList.isEmpty() && !oneOptionAlreadyClicked) {
            handler2.postDelayed(timerRunnable,1000);
        } else if(mTime==0 && mQuestionList.isEmpty() && !oneOptionAlreadyClicked) {
            showScore();
        } else {
            mTime = DELAY_QUESTIONS/1000;
        }
    }

    //To show th score to user. Correct answer adds
    // one point and wrong answer do nt change the score
    public void showScore() {
        mCurrentQuestionNumber = 1;
        mTime = DELAY_QUESTIONS/1000;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG," displaying score");
                Intent intent = new Intent(QuizActivity.this,ScoreActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    //The user should be showed correct score even if he clikcs on the wrong option
    //This method does that
    public void showCorrectOption() {
        if(mOption1.getText().equals(mCorrectOption)) {
            mOption1.setBackgroundResource(R.drawable.option_background_green);
        } else if(mOption2.getText().equals(mCorrectOption)) {
            mOption2.setBackgroundResource(R.drawable.option_background_green);
        } else if(mOption3.getText().equals(mCorrectOption)) {
            mOption3.setBackgroundResource(R.drawable.option_background_green);
        } else if(mOption4.getText().equals(mCorrectOption)) {
            mOption4.setBackgroundResource(R.drawable.option_background_green);
        }
    }

    //It is necessary to set the background of each option to default after each question change
    public void removeColors() {
        mOption1.setBackgroundResource(R.drawable.option_background_default);
        mOption2.setBackgroundResource(R.drawable.option_background_default);
        mOption3.setBackgroundResource(R.drawable.option_background_default);
        mOption4.setBackgroundResource(R.drawable.option_background_default);
    }

    @Override
    public void onBackPressed() {
        //Dialog to ask the user whether they really want ot end the quiz or not
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Do you want to stop the quiz ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                handler.removeCallbacks(runnable);
                handler2.removeCallbacks(timerRunnable);
                mCurrentQuestionNumber = 1;
                mTime = DELAY_QUESTIONS/1000;
                Log.d(TAG," cancelling quiz. Return to profile");
                Intent intent = new Intent(QuizActivity.this,ProfileActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}

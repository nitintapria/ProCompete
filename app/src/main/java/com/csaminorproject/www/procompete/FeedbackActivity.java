package com.csaminorproject.www.procompete;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FeedbackActivity extends AppCompatActivity {

    private DatabaseReference mFeedbackRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        mFeedbackRef = rootRef.child("feedback");

        final EditText editTextFeedback = (EditText) findViewById(R.id.editText_feedback);

        TextView submit = (TextView) findViewById(R.id.textView_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = editTextFeedback.getText().toString();
                if(!feedback.equals(""))
                mFeedbackRef.push().setValue(feedback, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(FeedbackActivity.this,
                                R.string.feedback_toast,
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(FeedbackActivity.this,
                                ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                else {
                    Intent intent = new Intent(FeedbackActivity.this,
                            ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


}

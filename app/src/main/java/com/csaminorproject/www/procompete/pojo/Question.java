package com.csaminorproject.www.procompete.pojo;

/**
 * Created by Nitin on 03/05/2017.
 */

public class Question {
    public String questionStatement;
    public String option1;
    public String option2;
    public String option3;
    public String option4;
    public String correctOption;

    public Question() {
        // Default constructor required for calls to DataSnapshot.getValue(Question.class)
    }

    public Question(String questionStatement, String option1,
                    String option2, String option3,
                    String option4, String correctOption) {
        this.questionStatement = questionStatement;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctOption = correctOption;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public String getQuestionStatement() {
        return questionStatement;
    }
}

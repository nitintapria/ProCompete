package com.csaminorproject.www.procompete.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nitin on 03/05/2017.
 */

public class Questions {
    public List<Question> questions = new ArrayList<>();

    public Questions() {
    }

    public Questions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return  questions;
    }
}

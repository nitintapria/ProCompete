package com.csaminorproject.www.procompete.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nitin on 20/04/2017.
 */

public class Category {
    List<String> categories = new ArrayList<>();
    public Category() {

    }
    public Category(List<String> categories) {
        this.categories = categories;
    }
    public List<String> getCategories() {
        return categories;
    }
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}

package com.csaminorproject.www.procompete;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SelectedCategoryFragment extends Fragment {

    private static final String TAG = "SelectedCategoryFrag";
    static AppBarLayout appBar;
    static LinearLayout selectedCategoryFragment;

    private DatabaseReference mRootRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mUserSelectedCategoriesRef;

    public SelectedCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selected_category, container, false);

        appBar = (AppBarLayout) getActivity().findViewById(R.id.appBar);
        selectedCategoryFragment = (LinearLayout) getActivity().findViewById(R.id.selectedCategoryFragment);

        TextView takeQuiz = (TextView) view.findViewById(R.id.textView_takeQuiz);
        takeQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quizIntent = new Intent(getActivity(),QuizActivity.class);
                quizIntent.putExtra("key",ProfileActivity.mSelectedCategoryName);
                startActivity(quizIntent);
            }
        });

        TextView removeCategory = (TextView) view.findViewById(R.id.textView_removeCategory);
        removeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remove selected category from user database
                mRootRef = FirebaseDatabase.getInstance().getReference();
                FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                DatabaseReference mUserParentRef = mRootRef.child(Constant.USERS);
                mUserRef = mUserParentRef.child(mFirebaseUser.getUid());
                mUserSelectedCategoriesRef = mUserRef.child(Constant.SELECTED_CATEGORIES);
                Map<String,Object> map = new HashMap<>();
                map.put(ProfileActivity.mSelectedCategoryName,null);
                mUserSelectedCategoriesRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Log.d(TAG,"category removed from database");
                    }
                });
                Log.d(TAG," hiding SelectedCategoryFragment");
                appBar.setVisibility(View.VISIBLE);
                selectedCategoryFragment.setVisibility(View.GONE);
                ProfileActivity.mSelectedCategoryFragmentVisible = false;
            }
        });

        ImageView hideSelectedCategoryFragment = (ImageView) view.findViewById(R.id.hideSelectedCategoryFragment);
        hideSelectedCategoryFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG," hiding SelectedCategoryFragment");
                appBar.setVisibility(View.VISIBLE);
                selectedCategoryFragment.setVisibility(View.GONE);
                ProfileActivity.mSelectedCategoryFragmentVisible = false;
            }
        });
        return view;
    }
}

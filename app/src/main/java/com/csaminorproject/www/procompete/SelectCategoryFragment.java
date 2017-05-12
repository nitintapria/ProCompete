package com.csaminorproject.www.procompete;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csaminorproject.www.procompete.pojo.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SelectCategoryFragment extends Fragment {

    private static final String TAG = "SelectCategoryFragment";

    static AppBarLayout appBar;
    static LinearLayout selectCategoryFragment;
    private RecyclerView mCategoryRecyclerView;

    private static List<String> categoryList;

    private DatabaseReference mRootRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mCategoryReference;
    private ValueEventListener mCategoryReferenceListener;

    public SelectCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_select_category, container, false);

        appBar = (AppBarLayout) getActivity().findViewById(R.id.appBar);
        selectCategoryFragment = (LinearLayout) getActivity().findViewById(R.id.selectCategoryFragment);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCategoryRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_selectCategory);
        mCategoryReference = mRootRef.child(Constant.CATEGORIES);

        //reading categories from the database
        mCategoryReferenceListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"getting categories");
                Category categoryRef = dataSnapshot.getValue(Category.class);
                categoryList = new ArrayList<>(categoryRef.getCategories());
                //Inserting items in RecyclerView
                //todo change getContext() to getActivity()
                CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(),categoryList);
                mCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mCategoryRecyclerView.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,"getting categories failed");
            }
        };
        mCategoryReference.addValueEventListener(mCategoryReferenceListener);

        ImageView hideSelectCategoryFragment = (ImageView) view.findViewById(R.id.hideSelectCategoryFragment);
        hideSelectCategoryFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG," hiding SelectCategoryFragment");
                appBar.setVisibility(View.VISIBLE);
                selectCategoryFragment.setVisibility(View.GONE);
                ProfileActivity.mSelectCategoryFragmentVisible = false;
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mCategoryReferenceListener!=null)
        mCategoryReference.removeEventListener(mCategoryReferenceListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mCategoryReference!=null)
        mCategoryReference.addValueEventListener(mCategoryReferenceListener);
    }


    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>{
        LayoutInflater inflater;
        ArrayList<String> categoryList;

        public CategoryAdapter(Context context, List<String> categoryList){
            inflater = LayoutInflater.from(context);
            this.categoryList = new ArrayList<>(categoryList);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view =inflater.inflate(R.layout.category_list_item,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
            DatabaseReference mUserParentRef = mRootRef.child(Constant.USERS);
            mUserRef = mUserParentRef.child(mFirebaseUser.getUid());
            mUserRef = mUserRef.child(Constant.SELECTED_CATEGORIES);

            holder.mCategoryTextView.setText(categoryList.get(position));
            holder.getCategoryItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //When user clicks on a Category then it gets inserted inside the user database
                    Log.d(TAG,"inserting new user category in database");
                    Map<String,Object> map = new HashMap<>();
                    map.put(categoryList.get(position),true);
                    mUserRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                        @Override
                        //Invoked when user selects a category from the list
                        public void onComplete(DatabaseError dbe, DatabaseReference dbr) {
                            appBar.setVisibility(View.VISIBLE);
                            selectCategoryFragment.setVisibility(View.GONE);
                            ProfileActivity.mSelectCategoryFragmentVisible = false;
                            if(dbe != null) {
                                Log.e(TAG,"Error adding new category");
                                dbe.toException().printStackTrace();
                            }
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView mCategoryTextView;
            CardView mCategoryCardView;

            public MyViewHolder(View itemView) {
                super(itemView);
                mCategoryTextView = (TextView) itemView.findViewById(R.id.textView_categoryName);
                mCategoryCardView = (CardView) itemView.findViewById(R.id.category_item_view);
            }

            CardView getCategoryItemView() {
                return mCategoryCardView;
            }
        }
    }
}

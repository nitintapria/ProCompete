package com.csaminorproject.www.procompete;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.csaminorproject.www.procompete.pojo.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "ProfileActivity";
    private String mUsername;
    private String mPhotoUrl;
    private String mUid;
    public static String mSelectedCategoryName;
    public static List<UserCategory> mUserSelectedCategories;
    public static boolean mSelectCategoryFragmentVisible;
    public static boolean mSelectedCategoryFragmentVisible;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mUserParentRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mUserSelectedCategoriesRef;
    private ValueEventListener mUserSelectedCategoriesListener;
    private GoogleApiClient mGoogleApiClient;

    private User mUser;
    private LinearLayout mSelectCategoryFragment;
    private LinearLayout mSelectedCategoryFragment;
    private ProgressBar mProgressBar;
    private RecyclerView mUserCategoriesRecyclerView;
    static UserCategoryAdapter mUserCategoryAdapter;

    class UserCategory {
        private String categoryName;

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
        public String getCategoryName() {
            return  categoryName;
        }
        public boolean equals(Object userCategory) {
            if(userCategory instanceof UserCategory) {
                UserCategory that = (UserCategory)userCategory;
                return this.categoryName.equals(that.getCategoryName());
            }
            return false;
        }
    }

    //to check internet connectivity is there or not
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(!isNetworkAvailable()) {
            Toast.makeText(ProfileActivity.this,
                    "No Internet Connection",
                    Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mFirebaseUser != null) {
                    // User is signed in
                    Log.d(TAG,"inside onAuthStateChanged");

                    //We call the .getInstance() method to access our database and then
                    // write that into a local Object of type FirebaseDatabase.
                    //Then we create a reference by calling getReference() on our database
                    // instance. It now refers to our entire database at the root level.
                    mRootRef = FirebaseDatabase.getInstance().getReference();
                    mUserParentRef = mRootRef.child(Constant.USERS);
                    mUserRef = mUserParentRef.child(mFirebaseUser.getUid());

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mFirebaseUser.getUid());

                    mUsername = mFirebaseUser.getDisplayName();

                    if (mFirebaseUser.getPhotoUrl() != null) {
                        mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
                        mUser = new User(mUsername,mPhotoUrl);
                        Log.d(TAG,mUser.getName()+" "+mUser.getPhotoUrl());

                        // The user's ID, unique to the Firebase project
                        mUid = mFirebaseUser.getUid();
                        //This code is written here rather than outside this function because
                        //outside code runs before completing this function so code needed here
                        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            //Firebase utilizes listeners to watch for changes in a
                            // specified node. It is similar to an event handler in
                            // the sense that a code is triggered based on a certain
                            // circumstance. In our case, whenever changes in that
                            // node's data occur, the listener automatically provides
                            // the application updated data, called a snapshot

                            // addListenerForSingleValueEvent adds a listener for a single change in
                            // the data at this location. This listener will be triggered once with
                            // the value of the data at the location.
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //A DataSnapshot instance contains data from a Firebase
                                // Database location. Any time you read Database data,
                                // you receive the data as a DataSnapshot.
                                mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
                                if (dataSnapshot.exists()) {
                                    Log.d(TAG,"user exists");
                                    // User exists
                                    showProfile();
                                }
                                else {
                                    // User do not exists
                                    addNewUser();
                                    showProfile();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {
                                //If the Event can not be completed,
                                // a second callback method, onCancelled() is called.
                            }
                        });
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* ProfileActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    public void addNewUser() {
        //New user. Update database
        Log.d(TAG,"user do not exists");
        Map<String,Object> map = new HashMap<>();
        map.put(mUid,mUser);
        mUserParentRef.updateChildren(map,new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError dbe, DatabaseReference dbr) {
                if(dbe != null) {
                    Log.e("Error adding user"," ");
                    dbe.toException().printStackTrace();
                }
            }
        });
    }

    public void showProfile() {
        //When using toolbar when setting it up as supportActionBar is important
        mUserSelectedCategories = new ArrayList<>();
        mProgressBar.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Hi, "+mUsername);

        setSupportActionBar(toolbar);

        ImageView imageViewUser = (ImageView) findViewById(R.id.imageView_user);

        Glide.with(imageViewUser.getContext())
                .load(mPhotoUrl)
                .into(imageViewUser);

        //To show categories selected by user
        mUserCategoriesRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_userCategories);
        mUserSelectedCategoriesRef = mUserRef.child(Constant.SELECTED_CATEGORIES);
        //reading user categories from the database
        mUserSelectedCategoriesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!mUserSelectedCategories.isEmpty()) {
                    //if not done then each time we add new category or delete
                    // one then list append the change
                    mUserSelectedCategories.clear();
                    mUserCategoryAdapter.notifyDataSetChanged();
                }
                Log.d(TAG,"getting user categories");
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    UserCategory userCategory = new UserCategory();
                    userCategory.categoryName = childSnapshot.getKey();
                    mUserSelectedCategories.add(userCategory);
                    Log.d(TAG,childSnapshot.getKey()+" category selected by user");
                }
                mUserCategoryAdapter = new UserCategoryAdapter(ProfileActivity.this,mUserSelectedCategories);
                mUserCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                mUserCategoriesRecyclerView.setAdapter(mUserCategoryAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,"getting categories failed");
            }
        };
        mUserSelectedCategoriesRef.addValueEventListener(mUserSelectedCategoriesListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
        if(mUserSelectedCategoriesRef!=null)
        mUserSelectedCategoriesRef.addValueEventListener(mUserSelectedCategoriesListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
        if(mUserSelectedCategoriesListener!=null) {
            mUserSelectedCategoriesRef.removeEventListener(mUserSelectedCategoriesListener);
        }
    }

    //Invoked when user selects the menu (currently just sign out)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            case R.id.feedback_menu:
                //todo give feedback
                Intent intent = new Intent(ProfileActivity.this,FeedbackActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    //Displays the fragment SelectCategoryFragment from which user can select the
    // category in which they would like to take the quiz
    public void selectCategories(View v) {
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appBar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SelectCategoryFragment selectCategory = new SelectCategoryFragment();
        fragmentTransaction.add(R.id.selectCategoryFragment,selectCategory);
        fragmentTransaction.commit();

        appBar.setVisibility(View.GONE);
        mSelectCategoryFragment = (LinearLayout) findViewById(R.id.selectCategoryFragment);
        mSelectCategoryFragment.setVisibility(View.VISIBLE);
        mSelectCategoryFragmentVisible = true;
    }

    //If user presses back when SelectCategoryFragment is visible then rather than closing the app
    //It should make the fragment invisible so that ProfileFragment is visible
    @Override
    public void onBackPressed() {
        if(mSelectCategoryFragmentVisible) {
            SelectCategoryFragment.appBar.setVisibility(View.VISIBLE);
            mSelectCategoryFragment.setVisibility(View.GONE);
            mSelectCategoryFragmentVisible = false;
        }
        else if (mSelectedCategoryFragmentVisible) {
            try {
                SelectedCategoryFragment.appBar.setVisibility(View.VISIBLE);
                mSelectedCategoryFragment.setVisibility(View.GONE);
                mSelectedCategoryFragmentVisible = false;
            } catch(NullPointerException e) {
                mSelectedCategoryFragmentVisible = false;
                this.finishAffinity();
            }
        }
        else
        super.onBackPressed();
    }







    class UserCategoryAdapter extends RecyclerView.Adapter<ProfileActivity.UserCategoryAdapter.MyViewHolder>{
        LayoutInflater inflater;
        List<UserCategory> userCategoryList;

        public UserCategoryAdapter(Context context, List<UserCategory> userCategoryList){
            inflater = LayoutInflater.from(context);
            this.userCategoryList = new ArrayList<>(userCategoryList);
        }

        @Override
        public ProfileActivity.UserCategoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view =inflater.inflate(R.layout.category_list_item,parent,false);
            return new ProfileActivity.UserCategoryAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.mCategoryTextView.setText(userCategoryList.get(position).getCategoryName());
            holder.getCategoryItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //To display new Fragment to show category specific options
                    AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appBar);

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    SelectedCategoryFragment selectedCategory = new SelectedCategoryFragment();
                    fragmentTransaction.add(R.id.selectedCategoryFragment,selectedCategory);
                    fragmentTransaction.commit();

                    mSelectedCategoryName = userCategoryList.get(position).getCategoryName();
                    TextView selectedCategoryTextView =(TextView) findViewById(R.id.textView_selectedCategory);
                    selectedCategoryTextView.setText(mSelectedCategoryName);

                    appBar.setVisibility(View.GONE);
                    mSelectedCategoryFragment = (LinearLayout) findViewById(R.id.selectedCategoryFragment);
                    mSelectedCategoryFragment.setVisibility(View.VISIBLE);
                    mSelectedCategoryFragmentVisible = true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return userCategoryList.size();
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

package com.qboxus.tictic.activitesfragments.profile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.material.tabs.TabLayoutMediator;
import com.qboxus.tictic.activitesfragments.profile.usersstory.ViewStoryA;
import com.qboxus.tictic.adapters.ViewPagerAdapter;
import com.qboxus.tictic.models.StoryModel;
import com.qboxus.tictic.models.StoryVideoModel;
import com.qboxus.tictic.simpleclasses.AppCompatLocaleActivity;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qboxus.tictic.activitesfragments.accounts.LoginA;
import com.qboxus.tictic.activitesfragments.accounts.ManageAccountsF;
import com.qboxus.tictic.activitesfragments.chat.ChatA;


import com.qboxus.tictic.activitesfragments.profile.followtabs.NotificationPriorityF;
import com.qboxus.tictic.activitesfragments.profile.likedvideos.LikedVideoF;
import com.qboxus.tictic.activitesfragments.profile.uservideos.UserVideoF;
import com.qboxus.tictic.activitesfragments.WebviewA;
import com.qboxus.tictic.adapters.SuggestionAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.simpleclasses.CircleDivisionView;
import com.qboxus.tictic.simpleclasses.DebounceClickHandler;
import com.qboxus.tictic.simpleclasses.PermissionUtils;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.FollowingModel;
import com.qboxus.tictic.models.PrivacyPolicySettingModel;
import com.qboxus.tictic.models.PushNotificationSettingModel;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.Variables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileA extends AppCompatLocaleActivity {


    Context context;
    LinearLayout tabPrivacyLikes;
    RelativeLayout viewTabLikes;
    public TextView username, username2Txt,tvFollowBtn,tvBio,tvLink,tvEditProfile;
    public SimpleDraweeView imageView,suggestionBtn;
    public TextView followCountTxt, fansCountTxt, heartCountTxt;
    ImageView backBtn, messageBtn,unFriendBtn,notificationBtn,favBtn,ivMultipleAccount;
    String userId, userName,fullName,buttonStatus, userPic,totalLikes="";
    RecyclerView rvSugesstion;
    SuggestionAdapter adapterSuggestion;
    protected TabLayout tabLayout;
    protected ViewPager2 pager;
    private ViewPagerAdapter adapter;
    public boolean isdataload = false ,isDirectMessage=false,isLikeVideoShow=false;
    public String picUrl,profileGif,followerCount,followingCount;
    LinearLayout tabSuggestion,tabAllSuggestion,tabLink,tabAccount;
    LinearLayout tabFollowOtherUser,tabFollowSelfUser;

    String notificationType="1";
    String isUserAlreadyBlock = "0";
    String blockByUserId = "0";
    DatabaseReference rootref;
    UserVideoF fragmentUserVides;
    LikedVideoF fragmentLikesVides;


    ArrayList<StoryModel> storyDataList=new ArrayList<>();
    CircleDivisionView circleDivisionView;

    PermissionUtils takePermissionUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Functions.setLocale(Functions.getSharedPreference(ProfileA.this).getString(Variables.APP_LANGUAGE_CODE,Variables.DEFAULT_LANGUAGE_CODE)
                , this, getClass(),false);
        setContentView(R.layout.activity_profile_);
        context = ProfileA.this;

        if (getIntent().hasExtra("user_id") && getIntent().hasExtra("user_name") && getIntent().hasExtra("user_pic"))
        {
            userId = getIntent().getStringExtra("user_id");
            userName = getIntent().getStringExtra("user_name");
            userPic = getIntent().getStringExtra("user_pic");
        }
        else
        {
            userName = getIntent().getStringExtra("user_name");
        }

        init();
    }

    boolean isSuggestion=true;
    private void openSettingScreen() {
        Intent intent=new Intent(context, SettingAndPrivacyA.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    TextView tvBlockUser;
    private void showVideoOption() {
        final Dialog alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.item_report_user_dialog);

        RelativeLayout tabReportUser = alertDialog.findViewById(R.id.tabReportUser);
        RelativeLayout tabBlockUser = alertDialog.findViewById(R.id.tabBlockUser);
        RelativeLayout tabShareProfile=alertDialog.findViewById(R.id.tabShareProfile);
        tvBlockUser = alertDialog.findViewById(R.id.tvBlockUser);

        Log.d(Constants.tag,"blockObj: "+blockByUserId);
        Log.d(Constants.tag,"isUserAlreadyBlock: "+isUserAlreadyBlock);

        if (blockByUserId.equals(Functions.getSharedPreference(context).getString(Variables.U_ID,"")))
        {
            tabBlockUser.setVisibility(View.VISIBLE);
        }
        else
        {
            if (isUserAlreadyBlock.equals("1"))
            {
                tabBlockUser.setVisibility(View.GONE);
            }
            else
            {
                tabBlockUser.setVisibility(View.VISIBLE);
            }

        }

        if (isUserAlreadyBlock.equals("1"))
            tvBlockUser.setText(context.getString(R.string.unblock_user));
        else
            tvBlockUser.setText(context.getString(R.string.block_user));

        tabShareProfile.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (Functions.checkLoginUser(ProfileA.this)) {
                shareProfile();
            }
        });
        tabReportUser.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (Functions.checkLoginUser(ProfileA.this)) {
                openUserReport();
            }
        });


        tabBlockUser.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (Functions.checkLoginUser(ProfileA.this)) {
                openBlockUserDialog();
            }
        });

        alertDialog.show();

    }

    public void openUserReport() {
        Intent intent = new Intent(ProfileA.this, ReportTypeA.class);
        intent.putExtra("user_id", userId);
        intent.putExtra("isFrom", false);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }


    private void showLoadingProgressSuggestionButton() {
        ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(R.raw.ic_progress_animation)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(suggestionBtn.getController())
                .setAutoPlayAnimations(true)
                .build();

        suggestionBtn.setController(controller);
    }

    private void openChatF() {
        Intent intent=new Intent(ProfileA.this,ChatA.class);
        intent.putExtra("user_id", userId);
        intent.putExtra("user_name", userName);
        intent.putExtra("user_pic", userPic);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void openProfileShareTab() {
        boolean isGif=false;
        String mediaURL;
        if (profileGif!=null && !(profileGif.isEmpty())) {
            isGif=true;
            mediaURL=profileGif;
        }
        else {
            isGif=false;
            mediaURL=picUrl;
        }
        final ShareAndViewProfileF fragment = new ShareAndViewProfileF(isGif,mediaURL,userId, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getString("action").equals("profileShareMessage")) {
                    if (Functions.checkLoginUser(ProfileA.this))
                    {
                        // firebase sharing
                    }
                }

            }
        });
        fragment.show(getSupportFragmentManager(), "");
    }

    // open the favourite videos fragment
    private void openFavouriteVideos() {
        Intent intent=new Intent(ProfileA.this, FavouriteMainA.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    ActivityResultLauncher<Intent> resultCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {
                            updateProfile();
                        }

                    }
                }
            });


    private void openEditProfile() {
        Intent intent=new Intent(ProfileA.this,EditProfileA.class);
        resultCallback.launch(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    // place the profile data
    private void updateProfile() {
        username2Txt.setText(Functions.showUsername(Functions.getSharedPreference(context).getString(Variables.U_NAME, "")));
        String firstName = Functions.getSharedPreference(context).getString(Variables.F_NAME, "");
        String lastName = Functions.getSharedPreference(context).getString(Variables.L_NAME, "");
        if (firstName.equalsIgnoreCase("") && lastName.equalsIgnoreCase("")) {
            username.setText(Functions.getSharedPreference(context).getString(Variables.U_NAME, ""));
        } else {
            username.setText(firstName + " " + lastName);
        }
        if (TextUtils.isEmpty(Functions.getSharedPreference(context).getString(Variables.U_BIO, "")))
        {
            tvBio.setVisibility(View.GONE);
        }
        else
        {
            tvBio.setVisibility(View.VISIBLE);
            tvBio.setText(Functions.getSharedPreference(context).getString(Variables.U_BIO, ""));
        }
        if (TextUtils.isEmpty(Functions.getSharedPreference(context).getString(Variables.U_LINK, "")))
        {
            tabLink.setVisibility(View.GONE);
        }
        else
        {
            tabLink.setVisibility(View.VISIBLE);
            tvLink.setText(Functions.getSharedPreference(context).getString(Variables.U_LINK, ""));
        }
        picUrl = Functions.getSharedPreference(context).getString(Variables.U_PIC, "");
        profileGif=Functions.getSharedPreference(context).getString(Variables.U_GIF, "");
        if (profileGif.isEmpty())
        {
            imageView.setController(Functions.frescoImageLoad(picUrl,imageView,false));
        }
        else
        {
            imageView.setController(Functions.frescoImageLoad(profileGif,R.drawable.ic_user_icon,imageView,true));
        }

    }



    private void selectNotificationPriority() {
        boolean isFriend=false;
        if (tvFollowBtn.getText().toString().equalsIgnoreCase(getString(R.string.messenge)))
        {
            isFriend=true;
        }
        else
        {
          isFriend=false;
        }

        NotificationPriorityF f = new NotificationPriorityF(notificationType,isFriend,userName,userId,new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    notificationType=bundle.getString("type");
                    setUpNotificationIcon(notificationType);
                }
                else
                {
                    callApiForGetAllvideos();
                }
            }
        });
        f.show(getSupportFragmentManager(), "");

    }

    private void setUpNotificationIcon(String type) {
        if (type.equalsIgnoreCase("1"))
        {
            notificationBtn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_live_notification));
        }
        else
        if (type.equalsIgnoreCase("0"))
        {
            notificationBtn.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_mute_notification));
        }
    }

    private void showMyLikesCounts() {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.show_likes_alert_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView tvMessage,tvDone;
        tvDone=dialog.findViewById(R.id.tvDone);
        tvMessage=dialog.findViewById(R.id.tvMessage);

        tvMessage.setText(username.getText()+" "+getString(R.string.received_a_total_of)+" "+totalLikes+" "+getString(R.string.likes_across_all_video));
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void OpenSuggestionScreen() {
        Intent intent=new Intent(ProfileA.this, FollowsMainTabA.class);
        intent.putExtra("id", userId);
        intent.putExtra("from_where", "suggestion");
        intent.putExtra("userName", userName);
        intent.putExtra("followingCount",""+followingCount);
        intent.putExtra("followerCount",""+followerCount);
        resultFollowCallback.launch(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    ActivityResultLauncher<Intent> resultFollowCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data.getBooleanExtra("isShow",false))
                        {
                            callApiForGetAllvideos();
                        }

                    }
                }
            });

    private void getSuggestionUserList() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, "0"));
            parameters.put("starting_point", "0");
        } catch (Exception e) {
            e.printStackTrace();
        }


        VolleyRequest.JsonPostRequest(ProfileA.this, ApiLinks.showSuggestedUsers, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(ProfileA.this,resp);

               hideSugestionButtonProgress();

                suggestionList.clear();

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONArray msgArray = jsonObject.getJSONArray("msg");
                        for (int i = 0; i < msgArray.length(); i++) {

                            JSONObject object = msgArray.optJSONObject(i);
                            UserModel userDetailModel=DataParsing.getUserDataModel(object.optJSONObject("User"));

                            FollowingModel item = new FollowingModel();
                            item.fb_id = userDetailModel.getId();
                            item.first_name = userDetailModel.getFirstName();
                            item.last_name =userDetailModel.getLastName();
                            item.bio = userDetailModel.getBio();
                            item.username = userDetailModel.getUsername();

                            item.setProfile_pic(userDetailModel.getProfilePic());

                            item.follow_status_button = "follow";

                            suggestionList.add(item);
                            adapterSuggestion.notifyDataSetChanged();
                        }

                        if (suggestionList.isEmpty())
                        {
                            findViewById(R.id.tvNoSuggestionFound).setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            findViewById(R.id.tvNoSuggestionFound).setVisibility(View.GONE);
                        }

                    } else {
                        findViewById(R.id.tvNoSuggestionFound).setVisibility(View.VISIBLE);
                    }




                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void hideSugestionButtonProgress() {
        ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.ic_arrow_drop_down_black_24dp)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(suggestionBtn.getController())
                .build();
        suggestionBtn.setController(controller);
    }


    public void init() {
        rootref = FirebaseDatabase.getInstance().getReference();
        username = findViewById(R.id.username);
        username2Txt = findViewById(R.id.username2_txt);
        imageView = findViewById(R.id.user_image);
        imageView.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (circleDivisionView.getVisibility()==View.VISIBLE)
                {
                    openStoryView();
                }
                else
                {
                    openProfileShareTab();
                }
            }
        }));
        tabSuggestion=findViewById(R.id.tabSuggestion);
        followCountTxt = findViewById(R.id.follow_count_txt);
        fansCountTxt = findViewById(R.id.fan_count_txt);
        heartCountTxt = findViewById(R.id.heart_count_txt);
        viewTabLikes=findViewById(R.id.viewTabLikes);
        tabPrivacyLikes=findViewById(R.id.tabPrivacyLikes);
        tabPrivacyLikes.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyLikesCounts();
            }
        }));
        circleDivisionView=findViewById(R.id.circleStatusBar);
        circleDivisionView.setStrokeLineColor(ProfileA.this.getColor(R.color.colorAccent));
        tabAllSuggestion=findViewById(R.id.tabAllSuggestion);
        tabAllSuggestion.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenSuggestionScreen();
            }
        }));

        tvBio=findViewById(R.id.tvBio);
        tvLink=findViewById(R.id.tvLink);
        tabLink=findViewById(R.id.tabLink);
        tabLink.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebUrl(getString(R.string.web_browser),""+tvLink.getText().toString());
            }
        }));
        favBtn=findViewById(R.id.favBtn);
        favBtn.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFavouriteVideos();
            }
        }));

        ivMultipleAccount=findViewById(R.id.ivMultipleAccount);
        tvEditProfile=findViewById(R.id.edit_profile_btn);
        tvEditProfile.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditProfile();
            }
        }));
        suggestionBtn=findViewById(R.id.suggestionBtn);
        suggestionBtn.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSuggestion)
                {
                    suggestionBtn.animate().rotation(180).setDuration(300).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            tabSuggestion.setVisibility(View.VISIBLE);
                            if (suggestionList.isEmpty())
                            {
                                showLoadingProgressSuggestionButton();
                                getSuggestionUserList();
                            }
                        }
                    }).start();
                    isSuggestion=false;
                }
                else
                {
                    suggestionBtn.animate().rotation(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            tabSuggestion.setVisibility(View.GONE);
                        }
                    }).start();
                    isSuggestion=true;
                }
            }
        }));
        messageBtn = findViewById(R.id.message_btn);
        messageBtn.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Functions.getSharedPreference(context).getString(Variables.U_ID,"").equals(userId))
                {
                    openSettingScreen();
                }
                else
                {
                    showVideoOption();
                }
            }
        }));
        tabFollowOtherUser = findViewById(R.id.tabFollowOtherUser);
        tabFollowSelfUser = findViewById(R.id.tabFollowSelfUser);
        notificationBtn=findViewById(R.id.notification_btn);
        notificationBtn.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNotificationPriority();
            }
        }));
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        }));
        unFriendBtn = findViewById(R.id.unFriendBtn);
        unFriendBtn.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Functions.checkLoginUser(ProfileA.this))
                    followUnFollowUser();
            }
        }));
        tvFollowBtn=findViewById(R.id.tvFollowBtn);
        tvFollowBtn.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvFollowBtn.getText().toString().equalsIgnoreCase(getString(R.string.messenge)))
                {
                    openChatF();
                }
                else
                {
                    if (Functions.checkLoginUser(ProfileA.this))
                        followUnFollowUser();
                }
            }
        }));
        setUpSuggestionRecyclerview();
        findViewById(R.id.following_layout).setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFollowing();
            }
        }));
        findViewById(R.id.fans_layout).setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFollowers();
            }
        }));

        isdataload = true;


        if (userId != null)
        {
            setupProfileIcon();
        }
        SetTabs();
        callApiForGetAllvideos();

    }

    public void SetTabs() {
        adapter = new ViewPagerAdapter(this);
        pager = (ViewPager2) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        pager.setOffscreenPageLimit(2);
        registerFragmentWithPager();
        pager.setAdapter(adapter);
        addTabs();
        setupTabIcons();
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

    private void addTabs() {
        TabLayoutMediator tabLayoutMediator=new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position==0)
                {
                    tab.setText(context.getString(R.string.my_videos));
                }
                else
                if (position==1)
                {
                    tab.setText(context.getString(R.string.liked_videos));
                }
            }
        });
        tabLayoutMediator.attach();
    }

    private void registerFragmentWithPager() {
        fragmentUserVides=UserVideoF.newInstance(false, userId,userName,isUserAlreadyBlock);
        adapter.addFrag(fragmentUserVides);
        fragmentLikesVides=LikedVideoF.newInstance(false, userId,userName,isLikeVideoShow,isUserAlreadyBlock);
        adapter.addFrag(fragmentLikesVides);
    }


    private void setupProfileIcon() {
        if (userId.equalsIgnoreCase(Functions.getSharedPreference(ProfileA.this).getString(Variables.U_ID,"0")))
        {
            notificationBtn.setVisibility(View.GONE);
            messageBtn.setVisibility(View.GONE);
            tabFollowSelfUser.setVisibility(View.VISIBLE);
            tabFollowOtherUser.setVisibility(View.GONE);
            ivMultipleAccount.setVisibility(View.VISIBLE);
            tabAccount=findViewById(R.id.tabAccount);
            tabAccount.setOnClickListener(new DebounceClickHandler(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openManageMultipleAccounts();
                }
            }));
        }
        else
        {
            notificationBtn.setVisibility(View.VISIBLE);
            messageBtn.setVisibility(View.VISIBLE);
            tabFollowSelfUser.setVisibility(View.GONE);
            notificationBtn.setVisibility(View.VISIBLE);
            tabFollowOtherUser.setVisibility(View.VISIBLE);
            ivMultipleAccount.setVisibility(View.GONE);
        }
    }


    ArrayList<FollowingModel> suggestionList=new ArrayList<>();
    private void setUpSuggestionRecyclerview() {
        rvSugesstion=findViewById(R.id.rvSugesstion);
        LinearLayoutManager layoutManager=new LinearLayoutManager(ProfileA.this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvSugesstion.setLayoutManager(layoutManager);
        adapterSuggestion=new SuggestionAdapter(suggestionList, new SuggestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, FollowingModel item) {


                if (view.getId()==R.id.tvFollowBtn)
                {
                    if (Functions.checkLoginUser(ProfileA.this))
                        followSuggestedUser(item.fb_id,postion);
                }
                else
                if (view.getId()==R.id.user_image)
                {
                    Intent intent=new Intent(view.getContext(), ProfileA.class);
                    intent.putExtra("user_id", item.fb_id);
                    intent.putExtra("user_name", item.username);
                    intent.putExtra("user_pic", item.getProfile_pic());
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
                else
                if (view.getId()==R.id.ivCross)
                {
                    suggestionList.remove(postion);
                    adapterSuggestion.notifyDataSetChanged();
                }
            }
        });
        rvSugesstion.setAdapter(adapterSuggestion);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (isRunFirstTime) {

            callApiForGetAllvideos();

        }

    }


    private void setupTabIcons() {
        View view1 = LayoutInflater.from(context).inflate(R.layout.item_tabs_profile_menu, null);
        ImageView imageView1 = view1.findViewById(R.id.image);
        imageView1.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_my_video_select));
        tabLayout.getTabAt(0).setCustomView(view1);

        View view2 = LayoutInflater.from(context).inflate(R.layout.item_tabs_profile_menu, null);
        ImageView imageView2 = view2.findViewById(R.id.image);
        imageView2.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_liked_video_gray));
        tabLayout.getTabAt(1).setCustomView(view2);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition(),true);
                View v = tab.getCustomView();
                ImageView image = v.findViewById(R.id.image);

                switch (tab.getPosition()) {
                    case 0:

                        image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_my_video_select));
                        break;

                    case 1:
                        image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_liked_video_color));
                        break;
                }
                tab.setCustomView(v);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                ImageView image = v.findViewById(R.id.image);

                switch (tab.getPosition()) {
                    case 0:
                        image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_my_video_gray));
                        break;
                    case 1:
                        image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_liked_video_gray));
                        break;
                }

                tab.setCustomView(v);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


    }




    // get the profile details of user
    boolean isRunFirstTime = false;

    private void callApiForGetAllvideos() {

        if (getIntent() == null) {
            userId = Functions.getSharedPreference(context).getString(Variables.U_ID, "0");
        }

        JSONObject parameters = new JSONObject();
        try {

            if (Functions.getSharedPreference(context).getBoolean(Variables.IS_LOGIN, false) && userId != null) {
                parameters.put("user_id", Functions.getSharedPreference(context).getString(Variables.U_ID, ""));
                parameters.put("other_user_id", userId);
            } else if (userId != null) {
                parameters.put("user_id", userId);
            } else {
                if (Functions.getSharedPreference(context).getBoolean(Variables.IS_LOGIN, false))
                {
                    parameters.put("user_id",  Functions.getSharedPreference(context).getString(Variables.U_ID, ""));
                }
                parameters.put("username", userName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        VolleyRequest.JsonPostRequest(ProfileA.this, ApiLinks.showUserDetail, parameters,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(ProfileA.this,resp);
                isRunFirstTime = true;
                parseData(resp);
            }
        });

    }


    public void parseData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONObject msg = jsonObject.optJSONObject("msg");
                JSONObject userObj=msg.optJSONObject("User");
                UserModel userDetailModel = DataParsing.getUserDataModel(userObj);


                try {
                    JSONArray storyArray=userObj.getJSONArray("story");
                    storyDataList.clear();
                    StoryModel storyItem=new StoryModel();
                    storyItem.setUserModel(userDetailModel);
                    ArrayList<StoryVideoModel> storyVideoList=new ArrayList<>();
                    for (int i=0; i<storyArray.length();i++)
                    {
                        JSONObject itemObj=storyArray.getJSONObject(i);
                        StoryVideoModel storyVideoItem=DataParsing.getVideoDataModel(itemObj.optJSONObject("Video"));
                        storyVideoList.add(storyVideoItem);
                    }
                    storyItem.setVideoList(storyVideoList);
                    if (storyVideoList.size()>0)
                    {
                        storyDataList.add(storyItem);

                        circleDivisionView.setVisibility(View.VISIBLE);
                        circleDivisionView.setCounts(storyVideoList.size());
                    }
                    else
                    {
                        circleDivisionView.setVisibility(View.GONE);
                    }
                }
                catch (Exception e)
                {
                    Log.d(Constants.tag,"Exception: "+e);
                }


                JSONObject push_notification_setting = msg.optJSONObject("PushNotification");
                JSONObject privacy_policy_setting = msg.optJSONObject("PrivacySetting");

                if (userId == null) {
                    userId = userDetailModel.getId();
                    setupTabIcons();
                }

                String first_name = userDetailModel.getFirstName();
                String last_name = userDetailModel.getLastName();

                if (first_name.equalsIgnoreCase("") && last_name.equalsIgnoreCase("")) {
                    username.setText(userDetailModel.getUsername());
                } else {
                    username.setText(first_name + " " + last_name);
                }

                username2Txt.setText(Functions.showUsername(userDetailModel.getUsername()));

                picUrl = userDetailModel.getProfilePic();
                profileGif=userDetailModel.getProfileGif();
                userPic= userDetailModel.getProfilePic();
                fullName=first_name + " " + last_name;
                buttonStatus =userDetailModel.getButton().toLowerCase();

                if (profileGif.isEmpty())
                {
                    imageView.setController(Functions.frescoImageLoad(picUrl,imageView,false));
                }
                else
                {
                    imageView.setController(Functions.frescoImageLoad(profileGif,R.drawable.ic_user_icon,imageView,true));
                }

                if (TextUtils.isEmpty(userDetailModel.getBio()))
                {
                    tvBio.setVisibility(View.GONE);
                }
                else
                {
                    tvBio.setVisibility(View.VISIBLE);
                    tvBio.setText(userDetailModel.getBio());
                }

                if (TextUtils.isEmpty(userDetailModel.getWebsite()))
                {
                    tabLink.setVisibility(View.GONE);
                }
                else
                {
                    tabLink.setVisibility(View.VISIBLE);
                    tvLink.setText(userDetailModel.getWebsite());
                }


                followingCount=userDetailModel.getFollowingCount();
                followerCount=userDetailModel.getFollowersCount();
                totalLikes=userDetailModel.getLikesCount();
                followCountTxt.setText(Functions.getSuffix(followingCount));
                fansCountTxt.setText(Functions.getSuffix(followerCount));
                heartCountTxt.setText(Functions.getSuffix(totalLikes));
                isUserAlreadyBlock = userDetailModel.getBlock();
                blockByUserId = userDetailModel.getBlockByUser();
                notificationType=userDetailModel.getNotification();
                setUpNotificationIcon(notificationType);


                if (fragmentUserVides!=null)
                {
                    if (msg.has("Playlist"))
                    {
                        JSONArray playlistArray = msg.getJSONArray("Playlist");
                        fragmentUserVides.updateUserPlaylist(playlistArray, userDetailModel.getVerified(), new FragmentCallBack() {
                            @Override
                            public void onResponce(Bundle bundle) {
                                if (bundle.getBoolean("isShow",false))
                                {
                                    callApiForGetAllvideos();
                                }
                            }
                        });
                    }
                }


                PushNotificationSettingModel pushNotificationSetting_model = new PushNotificationSettingModel();
                pushNotificationSetting_model.setComments("" + push_notification_setting.optString("comments"));
                pushNotificationSetting_model.setLikes("" + push_notification_setting.optString("likes"));
                pushNotificationSetting_model.setNewfollowers("" + push_notification_setting.optString("new_followers"));
                pushNotificationSetting_model.setMentions("" + push_notification_setting.optString("mentions"));
                pushNotificationSetting_model.setDirectmessage("" + push_notification_setting.optString("direct_messages"));
                pushNotificationSetting_model.setVideoupdates("" + push_notification_setting.optString("video_updates"));


                PrivacyPolicySettingModel privacyPolicySetting_model = new PrivacyPolicySettingModel();
                privacyPolicySetting_model.setVideos_download("" + privacy_policy_setting.optString("videos_download"));
                privacyPolicySetting_model.setDirect_message("" + privacy_policy_setting.optString("direct_message"));
                privacyPolicySetting_model.setDuet("" + privacy_policy_setting.optString("duet"));
                privacyPolicySetting_model.setLiked_videos("" + privacy_policy_setting.optString("liked_videos"));
                privacyPolicySetting_model.setVideo_comment("" + privacy_policy_setting.optString("video_comment"));


                if (privacyPolicySetting_model.getLiked_videos().toLowerCase().equalsIgnoreCase("only_me")) {
                    isLikeVideoShow=false;
                } else {
                    isLikeVideoShow=true;
                }


                //perform block functionality
                if (isUserAlreadyBlock.equals("1"))
                {
                    notificationBtn.setVisibility(View.GONE);
                    tabFollowOtherUser.setVisibility(View.GONE);
                }
                else
                {
                    notificationBtn.setVisibility(View.VISIBLE);
                    tabFollowOtherUser.setVisibility(View.VISIBLE);
                }

                fragmentLikesVides.updateLikeVideoState(isLikeVideoShow);
                fragmentLikesVides.updateUserData(userId,userName,isUserAlreadyBlock);
                fragmentUserVides.updateUserData(userId,userName,isUserAlreadyBlock);



                if (Functions.isShowContentPrivacy(context, privacyPolicySetting_model.getDirect_message(),
                        userDetailModel.getButton().toLowerCase().equalsIgnoreCase("friends"))) {
                    isDirectMessage=true;
                } else {
                    isDirectMessage=false;
                }


                String follow_status = userDetailModel.getButton().toLowerCase();
                if (!userDetailModel.getId().
                        equals(Functions.getSharedPreference(context).getString(Variables.U_ID, ""))) {

                    if (follow_status.equalsIgnoreCase("following")) {
                        unFriendBtn.setVisibility(View.VISIBLE);
                        tvFollowBtn.setVisibility(View.VISIBLE);
                        tvFollowBtn.setText(getString(R.string.messenge));
                        tvFollowBtn.setTextColor(ContextCompat.getColor(context,R.color.black));
                        tvFollowBtn.setBackground(ContextCompat.getDrawable(context,R.drawable.button_rounded_gray_strok_background));
                    } else if (follow_status.equalsIgnoreCase("friends")) {
                        unFriendBtn.setVisibility(View.VISIBLE);
                        tvFollowBtn.setVisibility(View.VISIBLE);
                        tvFollowBtn.setText(getString(R.string.messenge));
                        tvFollowBtn.setTextColor(ContextCompat.getColor(context,R.color.black));
                        tvFollowBtn.setBackground(ContextCompat.getDrawable(context,R.drawable.button_rounded_gray_strok_background));
                    }
                    else if (follow_status.equalsIgnoreCase("follow back")){
                        unFriendBtn.setVisibility(View.GONE);
                        tvFollowBtn.setVisibility(View.VISIBLE);
                        tvFollowBtn.setText(getString(R.string.follow_back));
                        tvFollowBtn.setTextColor(ContextCompat.getColor(context,R.color.whiteColor));
                        tvFollowBtn.setBackground(ContextCompat.getDrawable(context,R.drawable.button_rounded_solid_primary));
                    }
                    else {
                        unFriendBtn.setVisibility(View.GONE);
                        tvFollowBtn.setVisibility(View.VISIBLE);
                        tvFollowBtn.setText(getString(R.string.follow));
                        tvFollowBtn.setTextColor(ContextCompat.getColor(context,R.color.whiteColor));
                        tvFollowBtn.setBackground(ContextCompat.getDrawable(context,R.drawable.button_rounded_solid_primary));

                    }


                }

                String verified = userDetailModel.getVerified();
                if (verified != null && verified.equalsIgnoreCase("1")) {
                    findViewById(R.id.varified_btn).setVisibility(View.VISIBLE);
                }


            } else {
                Functions.showToast(context, jsonObject.optString("msg"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void openBlockUserDialog() {
        JSONObject params = new JSONObject();
        try {
            params.put("user_id",
                    Functions.getSharedPreference(ProfileA.this).getString(Variables.U_ID, ""));
            params.put("block_user_id", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Functions.showLoader(ProfileA.this, false, false);
        VolleyRequest.JsonPostRequest(ProfileA.this, ApiLinks.blockUser, params,Functions.getHeaders(this), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(ProfileA.this,resp);
                Functions.cancelLoader();

                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONObject msgObj=jsonObject.getJSONObject("msg");
                        if(msgObj.has("BlockUser"))
                        {
                            Functions.showToast(ProfileA.this, getString(R.string.user_blocked));
                            tvBlockUser.setText(R.string.unblock_user);
                            isUserAlreadyBlock = "1";
                        }
                        else
                        {
                            isUserAlreadyBlock = "0";
                        }
                    }
                    else
                    {
                        isUserAlreadyBlock = "0";
                    }
                    callApiForGetAllvideos();
                } catch (Exception e) {
                    Log.d(Constants.tag,"Exception: "+e);
                }
            }
        });

    }


    private void shareProfile() {
        boolean fromSetting=false;
        if (userId.equalsIgnoreCase(Functions.getSharedPreference(ProfileA.this).getString(Variables.U_ID,"")))
        {
            fromSetting=true;
        }
        else
        {
            fromSetting=false;
        }

        final ShareUserProfileF fragment = new ShareUserProfileF(userId,userName,fullName,userPic,buttonStatus,isDirectMessage,fromSetting, new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    callApiForGetAllvideos();
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "");

    }






    private void followSuggestedUser(String userId,int position) {
        Functions.callApiForFollowUnFollow(ProfileA.this,
                Functions.getSharedPreference(context).getString(Variables.U_ID, ""),
                userId,
                new APICallBack() {
                    @Override
                    public void arrayData(ArrayList arrayList) {
                    }

                    @Override
                    public void onSuccess(String responce) {

                        suggestionList.remove(position);
                        adapterSuggestion.notifyDataSetChanged();
                        callApiForGetAllvideos();
                    }

                    @Override
                    public void onFail(String responce) {

                    }

                });

    }


    private void followUnFollowUser() {
        Functions.callApiForFollowUnFollow(ProfileA.this,
                Functions.getSharedPreference(context).getString(Variables.U_ID, ""),
                userId,
                new APICallBack() {
                    @Override
                    public void arrayData(ArrayList arrayList) {
                    }

                    @Override
                    public void onSuccess(String responce) {

                        callApiForGetAllvideos();
                    }

                    @Override
                    public void onFail(String responce) {

                    }

                });

    }


    public void openWebUrl(String title, String url) {
        Intent intent=new Intent(ProfileA.this, WebviewA.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void openManageMultipleAccounts() {
        ManageAccountsF f = new ManageAccountsF(new FragmentCallBack() {
            @Override
            public void onResponce(Bundle bundle) {
                if (bundle.getBoolean("isShow",false))
                {
                    Functions.hideSoftKeyboard(ProfileA.this);
                    Intent intent = new Intent(ProfileA.this, LoginA.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                }
            }
        });
        f.show(getSupportFragmentManager(), "");
    }


    // open the following screen
    private void openFollowing() {

        Intent intent=new Intent(ProfileA.this, FollowsMainTabA.class);
        intent.putExtra("id", userId);
        intent.putExtra("from_where", "following");
        intent.putExtra("userName", userName);
        intent.putExtra("followingCount",""+followingCount);
        intent.putExtra("followerCount",""+followerCount);
        resultFollowCallback.launch(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


    }

    // open the followers screen
    private void openFollowers() {

        Intent intent=new Intent(ProfileA.this, FollowsMainTabA.class);
        intent.putExtra("id", userId);
        intent.putExtra("from_where", "fan");
        intent.putExtra("userName", userName);
        intent.putExtra("followingCount",""+followingCount);
        intent.putExtra("followerCount",""+followerCount);
        resultFollowCallback.launch(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }


    public void openStoryView()
    {
        Intent myIntent = new Intent(ProfileA.this, ViewStoryA.class);
        myIntent.putExtra("storyList",storyDataList); //Optional parameters
        myIntent.putExtra("position", 0); //Optional parameters
        startActivity(myIntent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isShow", true);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

}

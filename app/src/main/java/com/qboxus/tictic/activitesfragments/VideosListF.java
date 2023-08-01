package com.qboxus.tictic.activitesfragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.android.material.tabs.TabLayout;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.qboxus.tictic.activitesfragments.profile.ProfileA;
import com.qboxus.tictic.activitesfragments.profile.ReportTypeA;
import com.qboxus.tictic.activitesfragments.soundlists.VideoSoundA;
import com.qboxus.tictic.activitesfragments.profile.videopromotion.VideoPromoteStepsA;
import com.qboxus.tictic.activitesfragments.videorecording.VideoRecoderDuetA;
import com.qboxus.tictic.adapters.ViewPagerStatAdapter;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.qboxus.tictic.mainmenu.MainMenuActivity;
import com.qboxus.tictic.models.UserModel;
import com.qboxus.tictic.simpleclasses.CircleDivisionView;
import com.qboxus.tictic.simpleclasses.DataParsing;
import com.qboxus.tictic.simpleclasses.DebounceClickHandler;
import com.qboxus.tictic.simpleclasses.OnSwipeTouchListener;
import com.qboxus.tictic.simpleclasses.ShowMoreLess;
import com.qboxus.tictic.simpleclasses.VerticalViewPager;
import com.volley.plus.VPackages.VolleyRequest;
import com.qboxus.tictic.Constants;
import com.volley.plus.interfaces.APICallBack;
import com.volley.plus.interfaces.Callback;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.interfaces.FragmentDataSend;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.FriendsTagHelper;
import com.qboxus.tictic.simpleclasses.Functions;
import com.qboxus.tictic.simpleclasses.PermissionUtils;
import com.qboxus.tictic.simpleclasses.Variables;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.facebook.FacebookSdk.getApplicationContext;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */

// this is the main view which is show all  the video in list
public class VideosListF extends Fragment implements Player.Listener {

    View view;
    Context context;
    HomeModel item;


    public VideosListF(HomeModel item) {
        this.item = item;
    }


    public VideosListF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.item_home_layout, container, false);
        context = view.getContext();

        initializePlayer();
        initalize_views();

        return view;
    }

    TextView descTxt;
    TextView username;
    SimpleDraweeView thumb_image;
    StyledPlayerView  playerView;
    ProgressBar pbar;


    public void initalize_views() {
        playerView = view.findViewById(R.id.playerview);
        username = view.findViewById(R.id.username);
        thumb_image=view.findViewById(R.id.thumb_image);
        descTxt = view.findViewById(R.id.desc_txt);
        pbar = view.findViewById(R.id.p_bar);



        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setData();
            }
        },200);

    }



    public void setData() {

        if(view==null )
            return;
        else {

            if (item!=null)
            {


                thumb_image.setController(Functions.frescoImageLoad(item.getThum(),thumb_image,false));

                username.setText(Functions.showUsernameOnVideoSection(item));

            }
        }
    }


    ExoPlayer exoplayer;
    // initlize the player for play video
    private void initializePlayer() {
        if(exoplayer==null && item!=null){
            try {
                exoplayer =new ExoPlayer.Builder(context).
                        setTrackSelector(new DefaultTrackSelector(context)).
                        setLoadControl(Functions.getExoControler()).
                        build();
                Uri videoURI = Uri.parse(item.getVideo_url());
                MediaItem mediaItem = MediaItem.fromUri(videoURI);
                exoplayer.setMediaItem(mediaItem);
                exoplayer.prepare();
                exoplayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                        .build();
                exoplayer.setAudioAttributes(audioAttributes, true);
                exoplayer.addListener(VideosListF.this);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playerView = view.findViewById(R.id.playerview);
                        playerView.findViewById(R.id.exo_play).setVisibility(View.GONE);
                        if(exoplayer!=null) {
                            playerView.setPlayer(exoplayer);
                        }
                    }
                });

            }
            catch (Exception e)
            {
                Log.d(Constants.tag,"Exception : "+e);
            }

        }

    }

    public void setPlayer(boolean isVisibleToUser) {

        if (exoplayer != null) {

            if(exoplayer!=null) {
                if (isVisibleToUser)
                {
                    exoplayer.setPlayWhenReady(true);
                }
                else
                {
                    exoplayer.setPlayWhenReady(false);
                    playerView.findViewById(R.id.exo_play).setAlpha(1);
                }


            }
            playerView.setOnTouchListener(new OnSwipeTouchListener(context) {

                public void onSwipeLeft() {
                }

                @Override
                public void onLongClick() {

                }

                @Override
                public void onSingleClick() {
                    if (!exoplayer.getPlayWhenReady()) {
                        exoplayer.setPlayWhenReady(true);
                        playerView.findViewById(R.id.exo_play).setAlpha(0);
                    } else {
                        exoplayer.setPlayWhenReady(false);
                        playerView.findViewById(R.id.exo_play).setAlpha(1);
                    }
                }

                @Override
                public void onDoubleClick(MotionEvent e) {

                }

            });
        }

    }


    public void updateVideoView(){
        if (Functions.getSharedPreference(context).getBoolean(Variables.IS_LOGIN, false))
        {
            Functions.callApiForUpdateView(getActivity(), item.video_id, new Callback() {
                @Override
                public void onResponce(String resp) {
                    Functions.checkStatus(getActivity(),resp);
                    try {
                        JSONObject jsonObject=new JSONObject(resp);
                        String code=jsonObject.optString("code");
                        if(code!=null && code.equals("200")){
                            JSONObject msgObj=jsonObject.getJSONObject("msg");
                            JSONObject videoObj=msgObj.getJSONObject("Video");
                            String share="0" + videoObj.optInt("share");

                            item.share = share;

                            setData();
                        }
                    } catch (Exception e) {
                        Log.d(Constants.tag,"Exception: "+e);
                    }
                }
            });
        }
//        callApiForSinglevideos();
    }



    boolean isVisibleToUser;
    @Override
    public void setMenuVisibility(final boolean visible) {
        isVisibleToUser = visible;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if (exoplayer != null && visible) {
                    setPlayer(isVisibleToUser);
                    updateVideoView();
                }

            }
        },200);
    }



    public void mainMenuVisibility(boolean isvisible) {

        if (exoplayer != null && isvisible) {
            exoplayer.setPlayWhenReady(true);
        }

        else if (exoplayer != null && !isvisible) {
            exoplayer.setPlayWhenReady(false);
            playerView.findViewById(R.id.exo_play).setAlpha(1);
        }


    }


    // when we swipe for another video this will relaese the privious player

    public void releasePriviousPlayer() {
        if (exoplayer != null) {
            exoplayer.removeListener(this);
            exoplayer.release();
            exoplayer = null;
        }
    }


    @Override
    public void onDestroy() {
        releasePriviousPlayer();
        super.onDestroy();

    }


    @Override
    public void onPause() {
        super.onPause();
        if (exoplayer != null) {
            exoplayer.setPlayWhenReady(false);
            playerView.findViewById(R.id.exo_play).setAlpha(1);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (exoplayer != null) {
            exoplayer.setPlayWhenReady(false);
            playerView.findViewById(R.id.exo_play).setAlpha(1);
        }
    }

    @Override
    public void onPlayerError(PlaybackException error) {
        Player.Listener.super.onPlayerError(error);
        Log.d(Constants.tag,"Exception player: "+error.getMessage());
        Log.d(Constants.tag,"Exception player12: "+error);
    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        Player.Listener.super.onVideoSizeChanged(videoSize);
        if (videoSize.width>videoSize.height)
        {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        }
        else
        {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        }
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            pbar.setVisibility(View.VISIBLE);
        }
        else if (playbackState == Player.STATE_READY) {
            thumb_image.setVisibility(View.GONE);

            pbar.setVisibility(View.GONE);
        }
    }



}

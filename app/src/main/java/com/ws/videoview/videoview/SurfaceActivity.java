package com.ws.videoview.videoview;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.ws.videoview.videoview.view.SurfaceVideoView;

public class SurfaceActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, SurfaceVideoView.OnPlayStateListener, MediaPlayer.OnErrorListener, View.OnClickListener, MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener {

    /**
     * 播放控件
     */
    private SurfaceVideoView mVideoView;

    /**
     * 播放路径
     */
    private String mPath;
    /**
     * 是否需要回复播放
     */
    private boolean mNeedResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);

        // 防止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mPath = getIntent().getStringExtra("path");
        if (TextUtils.isEmpty(mPath)) {
            Toast.makeText(SurfaceActivity.this,"mPath==null",Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mVideoView= (SurfaceVideoView) findViewById(R.id.SurfaceVideoView);

        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnPlayStateListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnClickListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnCompletionListener(this);

        mVideoView.setVideoPath(mPath);
        Toast.makeText(SurfaceActivity.this,"mPath="+mPath,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoView != null && mNeedResume) {
            mNeedResume = false;
            if (mVideoView.isRelease())
                mVideoView.reOpen();
            else
                mVideoView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView != null) {
            if (mVideoView.isPlaying()) {
                mNeedResume = true;
                mVideoView.pause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.release();
            mVideoView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mVideoView.setVolume(SurfaceVideoView.getSystemVolumn(this));
        mVideoView.start();
    }

    @Override
    public void onStateChanged(boolean isPlaying) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        if (!isFinishing()) {

        }
        Toast.makeText(SurfaceActivity.this,"onError",Toast.LENGTH_LONG).show();

        return false;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                // 音频和视频数据不正确
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (!isFinishing())
                    mVideoView.pause();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (!isFinishing())
                    mVideoView.start();
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mVideoView.setBackground(null);
                } else {
                    mVideoView.setBackgroundDrawable(null);
                }
                break;
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (!isFinishing())
            mVideoView.reOpen();
    }
}

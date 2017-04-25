package com.ws.videoview.videoview;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.ws.videoview.videoview.view.TextureVideoView;

public class TextureActivity extends AppCompatActivity {

    private TextureVideoView textureVideoView;
    /**
     * 播放路径
     */
    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture);

        mPath = getIntent().getStringExtra("path");
        if (TextUtils.isEmpty(mPath)) {
            Toast.makeText(TextureActivity.this,"mPath==null",Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        textureVideoView= (TextureVideoView) findViewById(R.id.TextureVideoView);
        //TextureView特性
        textureVideoView.setAlpha(0.5f);
        textureVideoView.setRotation(45.0f);
        //textureVideoView.pause();
        textureVideoView.setVideoPath(mPath);

        textureVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                textureVideoView.setLooping(true);
                textureVideoView.start();
            }
        });

        if(textureVideoView.isPrepared()){
            textureVideoView.setLooping(true);
            textureVideoView.start();
        }
    }
}

package com.ws.videoview.videoview;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button SurfaceBtn,TextureBtn,cameraBtn;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

          initView();

          //videoPath=getSDPath()+"/video.mp4";
          //videoPath一定要对，可以打开图库进入详情看路径
          videoPath="/storage/00A6-8914/video.mp4";
    }

    private void initView() {
        SurfaceBtn= (Button) findViewById(R.id.SurfaceBtn);
        TextureBtn= (Button) findViewById(R.id.TextureBtn);
        cameraBtn= (Button) findViewById(R.id.cameraBtn);

        SurfaceBtn.setOnClickListener(this);
        TextureBtn.setOnClickListener(this);
        cameraBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.SurfaceBtn:
                Intent i=new Intent(MainActivity.this,SurfaceActivity.class);
                i.putExtra("path",videoPath);
                startActivity(i);
                break;

            case R.id.TextureBtn:
                Intent intent=new Intent(MainActivity.this,TextureActivity.class);
                intent.putExtra("path",videoPath);
                startActivity(intent);
                break;

            case R.id.cameraBtn:
                Intent i2=new Intent(MainActivity.this,CameraTextureActivity.class);

                startActivity(i2);
                break;
        }
    }

    public String getSDPath(){
        String sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory().getPath();//获取跟目录
        }
        return sdDir.toString();
    }
}

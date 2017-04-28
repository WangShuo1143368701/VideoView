package com.ws.mediacodecdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;


@SuppressWarnings("deprecation")
public class EncodeActivity extends Activity implements SurfaceHolder.Callback, PreviewCallback
{

	
	AvcEncoder avcCodec;
    public Camera m_camera;
    SurfaceView m_prevewview;
    SurfaceHolder m_surfaceHolder;
    int width = 1280;
    int height = 720;
    int framerate = 15;
    int bitrate = 125000;
    //int bitRate = camera.getFpsRange()[1] * currentSize.width * currentSize.height / 15;
     
    byte[] h264 = new byte[width*height*3/2];
    
    private FileOutputStream file = null;
    private String filename = "camera.h264";
    private int byteOffset = 0;
    private long lastTime = 0;

	private String mImagePath;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mImagePath = Environment.getExternalStorageDirectory().getPath() + "/avcCodec/";
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads()
        .detectDiskWrites()
        .detectAll()   // or .detectAll() for all detectable problems
        .penaltyLog()
        .build());
StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects()
        .detectLeakedClosableObjects()
        .penaltyLog()
        .penaltyDeath()
        .build());
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encode);
		 
		Log.d("ws", "width=" + width+ ", height=" + height + ", framerate=" + framerate + ", bitrate=" + bitrate);
		try {
			//初始化编码器，在有的机器上失败 fuck!
			avcCodec = new AvcEncoder(width,height,framerate,bitrate);
		} catch (IOException e1) {
			Log.d("Fuck", "Fail to AvcEncoder");
		}
		m_prevewview = (SurfaceView) findViewById(R.id.surfaceViewPlay);
		m_surfaceHolder = m_prevewview.getHolder(); 
		m_surfaceHolder.setFixedSize(width, height);
		m_surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		m_surfaceHolder.addCallback((Callback) this);
		

	}


	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
	
	}

	@SuppressLint("NewApi")
	@Override
	public void surfaceCreated(SurfaceHolder arg0)
	{
		try 
		{
			m_camera = Camera.open();
			m_camera.setPreviewDisplay(m_surfaceHolder);
			Camera.Parameters parameters = m_camera.getParameters();
			parameters.setPreviewSize(width, height);
			parameters.setPictureSize(width, height);
			parameters.setPreviewFormat(ImageFormat.YV12);
			parameters.set("rotation", 90);
			//parameters.set("orientation", "portrait");
			m_camera.setParameters(parameters);	
			m_camera.setDisplayOrientation(90);
			m_camera.setPreviewCallback((PreviewCallback) this);
			m_camera.startPreview();
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}	
		

		try {
			File fileFolder = new File(mImagePath);
			if (!fileFolder.exists())
				fileFolder.mkdirs();
			File files = new File(mImagePath, filename);
			if (!files.exists()) {
				Log.e(TAG, "file create success ");
				files.createNewFile();
			}
			file = new FileOutputStream(files);
			Log.e(TAG, "file save success ");
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0)
	{
		m_camera.setPreviewCallback(null);
		m_camera.release();
		m_camera = null; 
		avcCodec.close();
		try {
			file.flush();
			file.close();
		} catch (IOException e) {
			Log.d("Fuck", "File close error");
			e.printStackTrace();
		}
	}

	
	@Override
	public void onPreviewFrame(byte[] data, Camera camera)
	{
		long newTime = System.currentTimeMillis();
		long diff = newTime - lastTime;
		lastTime = newTime;
        //把摄像头的数据传给编码器
		int ret = avcCodec.offerEncoder(data,h264);

		if(ret > 0)
		{
			try {		
				byte[] length_bytes = intToBytes(ret);
				file.write(length_bytes);
				file.write(h264, 0, ret);

			} catch (IOException e)
			{
				Log.d("ws", "exception: " + e.toString());
			}
		}

	}
	
	public static byte[] intToBytes( int value )   
	{   
	    byte[] src = new byte[4];  
	    src[3] =  (byte) ((value>>24) & 0xFF);  
	    src[2] =  (byte) ((value>>16) & 0xFF);  
	    src[1] =  (byte) ((value>>8) & 0xFF);    
	    src[0] =  (byte) (value & 0xFF);                  
	    return src;   
	}
}
package com.yiuhet.camerademo.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.LruCache;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.yiuhet.camerademo.LogUtil;
import com.yiuhet.camerademo.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * 使用Camera类的demo 页
 * Created by yiuhet on 2019/6/24.
 */
public class CameraActivity extends AppCompatActivity implements Camera.PreviewCallback, View.OnClickListener {
    LruCache
    private String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private final int REQUEST_CODE_PERMISSIONS = 10;

    private int mCameraId; //相机ID
    private Camera mCamera;//相机实例
    private MediaRecorder mMediaRecorder; //录屏实例
    private boolean isRecording = false;//是否在录屏

    private View mPreviewDisplayView; //预览视图
    private Button mBtn;

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            startCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            releaseCamera();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtil.e("yiuhet", "surfaceCreated");
            startCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtil.e("yiuhet", "surfaceChanged");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtil.e("yiuhet", "surfaceDestroyed");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera0);
        LogUtil.e("yiuhet", "checkCameraHardware :" + checkCameraHardware(this));
        mPreviewDisplayView = findViewById(R.id.surface);
        mBtn = findViewById(R.id.btn);
        findViewById(R.id.btn2).setOnClickListener(this);
        mBtn.setOnClickListener(this);
        if (mPreviewDisplayView instanceof SurfaceView) {
            ((SurfaceView) mPreviewDisplayView).getHolder().addCallback(mSurfaceCallback);
        } else if (mPreviewDisplayView instanceof TextureView) {
            ((TextureView) mPreviewDisplayView).setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    /**
     * 检摄像头硬件
     */
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * 通过调用相机应用打开相机
     * 返回数据 data.getExtras().get("data")
     *
     * @param requestCode
     */
    private void startCameraByOther(int requestCode) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, requestCode);
    }

    private boolean allPermissionsGranted() {
        Arrays.asList(REQUIRED_PERMISSIONS).stream()
                .allMatch(s -> ContextCompat.checkSelfPermission(getBaseContext(), s) == PackageManager.PERMISSION_GRANTED);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                mPreviewDisplayView.post(this::startCamera);
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * 打开相机
     *
     * @return
     */
    private boolean startCamera() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            return false;
        }
        int cameraNumber = Camera.getNumberOfCameras();
        LogUtil.e("yiuhet", "cameraNumber : " + cameraNumber);
        //没有摄像头
        if (cameraNumber < 0) {
            return false;
        }
        //相机数量为2则打开1,1则打开0,相机ID 1为前置，0为后置
        mCameraId = Camera.getNumberOfCameras() - 2;
        if (mCamera == null) {
            try {
                mCamera = Camera.open(mCameraId);
            } catch (Exception e) {
                LogUtil.e("yiuhet", "open camera error : " + e);
                return false;
            }
        }
        initCameraParams(mCamera, 16 / 9f);
        try {
            if (mPreviewDisplayView instanceof TextureView) {
                mCamera.setPreviewTexture(((TextureView) mPreviewDisplayView).getSurfaceTexture());
            } else {
                mCamera.setPreviewDisplay(((SurfaceView) mPreviewDisplayView).getHolder());
            }
        } catch (IOException e) {
            LogUtil.e("yiuhet", "camera set holder error : " + e);
            return false;
        }
        mCamera.setPreviewCallback(this);
        mCamera.startPreview();
        LogUtil.e("yiuhet", "startCamera end");
        return true;
    }

    /**
     * 设置相机参数
     *
     * @param camera
     */
    private void initCameraParams(Camera camera, float ratio) {
        //设置角度 只对预览视图有效
        int displayOrientation = getCameraOri();
        camera.setDisplayOrientation(displayOrientation);
        Camera.Parameters parameters = camera.getParameters();
        LogUtil.e("yiuhet", "parameters ：\n" + parameters.flatten().replaceAll(";", ";\n"));
        parameters.setPreviewFormat(ImageFormat.NV21);
        //预览大小设置
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = chooseOptimalSize(supportedPreviewSizes, ratio);        //获取效果最好的预览尺寸
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        //对焦模式设置
        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.size() > 0) {
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
        }
        camera.setParameters(parameters);
    }

    /**
     * 选择显示效果最好的预览尺寸
     *
     * @param sizes            支持的尺寸  一般三种比例 16:9 4:3 18:9
     * @param previewViewRatio 高宽比
     * @return
     */
    private Camera.Size chooseOptimalSize(List<Camera.Size> sizes, float previewViewRatio) {
        if (sizes == null || sizes.size() == 0) {
            return mCamera.getParameters().getPreviewSize();
        }
        Camera.Size optimalSize = sizes.get(0);
        // 计算预览尺寸的高宽比

        if (previewViewRatio > 1) {
            previewViewRatio = 1 / previewViewRatio;
        }

        for (Camera.Size s : sizes) {
            if (Math.abs((s.height / (float) s.width) - previewViewRatio) < Math.abs(optimalSize.height / (float) optimalSize.width - previewViewRatio)) {
                optimalSize = s;
            }
        }
        return optimalSize;
    }

    /**
     * 获取相机旋转的角度
     *
     * @return
     */
    private int getCameraOri() {
        int degrees = 0;
        //手机方向
        int phoneRotate = getWindowManager().getDefaultDisplay().getOrientation();
        switch (phoneRotate) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                break;
        }
        int result;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        LogUtil.e("yiuhet", "facing:" + info.facing + ", orientation:" + info.orientation + ", canDisableShutterSound:" + info.canDisableShutterSound);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; //镜像 水平反翻转
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    /**
     * 相机分辨率转换为转字符串 eg:640x480
     *
     * @param sizes 相机分辨率
     * @return
     */
    private String cameraSizeToSting(Iterable<Camera.Size> sizes) {
        StringBuilder s = new StringBuilder();
        for (Camera.Size size : sizes) {
            if (s.length() != 0)
                s.append(",");
            s.append(size.width).append('x').append(size.height);
        }
        return s.toString();
    }

    /**
     * 停止相机预览并清除回调
     */
    private void stopCamera() {
        if (mCamera == null) {
            return;
        }
        try {
            mCamera.setPreviewCallback(null);
            mCamera.setPreviewDisplay(null);
            mCamera.stopPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();//停止预览
            mCamera.setPreviewCallback(null);
            try {
                mCamera.setPreviewDisplay(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.release(); // 释放相机资源
            mCamera = null;
            LogUtil.i("yiuhet", "release done");
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                if (isRecording) {
                    isRecording = startVideoRecorder();
                } else {
                    stopVideoRecorder();
                    isRecording = false;
                }
                //根据录屏状态更改文案
                if (isRecording) {
                    mBtn.setText("停止录制");
                } else {
                    mBtn.setText("开始录制");
                }
//                mCamera.takePicture(null, null, null);
                break;
            case R.id.btn2:
                mCamera.startPreview();
                break;
        }
    }

    /**
     * 录制准备
     *
     * @param camera
     * @return
     */
    private boolean prepareVideoRecorder(Camera camera) {
        mMediaRecorder = new MediaRecorder();
        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        mMediaRecorder.setCamera(camera);
        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        mMediaRecorder.setPreviewDisplay(((SurfaceView) mPreviewDisplayView).getHolder().getSurface());
        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            LogUtil.e("yiuhet", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releasetVideoRecorder();
            return false;
        } catch (IOException e) {
            LogUtil.e("yiuhet", "IOException preparing MediaRecorder: " + e.getMessage());
            releasetVideoRecorder();
            return false;
        }
        return true;
    }

    /**
     * 开始录屏
     *
     * @return
     */
    private boolean startVideoRecorder() {
        boolean isSuccess = true;
        if (mMediaRecorder != null) {
            mMediaRecorder.start();
        } else if (prepareVideoRecorder(mCamera)) {
            mMediaRecorder.start();
        } else {
            isSuccess = false;
        }
        return isSuccess;
    }

    /**
     * 停止录屏
     */
    private void stopVideoRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
        }
    }

    /**
     * 释放录屏资源
     */
    private void releasetVideoRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
        }
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "AndroidDemo");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LogUtil.e("yiuhet", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        LogUtil.e("yiuhet", "mediaFile:" + mediaFile.toString());
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ((ImageView) findViewById(R.id.iv)).setImageBitmap(bitmap);
                    break;
            }
        }
    }
}

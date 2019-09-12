package com.yiuhet.camerademo.fotoapparat;

import android.os.Bundle;
import android.widget.ImageView;

import com.yiuhet.camerademo.R;

import org.jetbrains.annotations.NotNull;

import androidx.appcompat.app.AppCompatActivity;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.log.LoggersKt;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.selector.FlashSelectorsKt;
import io.fotoapparat.selector.FocusModeSelectorsKt;
import io.fotoapparat.selector.LensPositionSelectorsKt;
import io.fotoapparat.selector.ResolutionSelectorsKt;
import io.fotoapparat.selector.SelectorsKt;
import io.fotoapparat.view.CameraView;

public class FotoCameraActivity extends AppCompatActivity {

    private CameraView mCameraView;
    private ImageView mResult;
    private Fotoapparat mFotoapparat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_camera);
        mCameraView = findViewById(R.id.camera_view);
        mResult = findViewById(R.id.picture_redult);
        findViewById(R.id.take_picture).setOnClickListener(v ->
                mFotoapparat.takePicture().toBitmap().whenAvailable(bitmapPhoto -> {
                    mResult.setImageBitmap(bitmapPhoto.bitmap);
                    mResult.setRotation(-bitmapPhoto.rotationDegrees);
                    return null;
                }));
        initFoto();
    }

    private void initFoto() {
        mFotoapparat = Fotoapparat
                .with(getThemedContext())
                .into(mCameraView)           // view which will draw the camera preview
                .previewScaleType(ScaleType.CenterCrop)  // we want the preview to fill the view
                .photoResolution(ResolutionSelectorsKt.highestResolution())   // we want to have the biggest photo possible
                .lensPosition(LensPositionSelectorsKt.back())       // we want back camera
                .focusMode(SelectorsKt.firstAvailable(  // (optional) use the first focus mode which is supported by device
                        FocusModeSelectorsKt.continuousFocusPicture(),
                        FocusModeSelectorsKt.autoFocus(),        // in case if continuous focus is not available on device, auto focus will be used
                        FocusModeSelectorsKt.fixed()             // if even auto focus is not available - fixed focus mode will be used
                ))
                .flash(SelectorsKt.firstAvailable(      // (optional) similar to how it is done for focus mode, this time for flash
                        FlashSelectorsKt.autoRedEye(),
                        FlashSelectorsKt.autoFlash(),
                        FlashSelectorsKt.torch()
                ))
                .frameProcessor(new FrameProcessor() {
                    @Override
                    public void process(@NotNull Frame frame) {

                    }
                })   // (optional) receives each frame from preview stream
                .logger(LoggersKt.loggers(            // (optional) we want to log camera events in 2 places at once
                        LoggersKt.logcat(),           // ... in logcat
                        LoggersKt.fileLogger(getThemedContext())    // ... and to file
                ))
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFotoapparat.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFotoapparat.stop();
    }
}

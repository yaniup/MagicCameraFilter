package com.jx.livestream;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.jx.livestream.camera.Camera2Loader;
import com.jx.livestream.camera.CameraLoader;
import com.jx.livestream.databinding.LivePageBinding;
import com.jx.livestream.util.GPUImageFilterTools;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBilateralBlurFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.util.Rotation;


public class LivePage extends AppCompatActivity implements View.OnClickListener{
    private GPUImageView mGPUImageView;
    private SeekBar skinWhiteSeekbar,skinBlurSeekbar;

    private GPUImageFilter mNoImageFilter = new GPUImageFilter();
    private GPUImageFilter mCurrentImageFilter = mNoImageFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;

    private CameraLoader mCameraLoader;

    private SharedPreferences sharedPreferences;
    private float brightValue,saturationValue,contrastValue,gammaValue,exposureValue,gaussianValue;

    private RecyclerView liveChatRecyclerView;
    private LiveChatAdapter liveChatAdapter;

    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;

    private int lastPosition, currentPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LivePageBinding livePageBinding = LivePageBinding.inflate(getLayoutInflater());
        setContentView(livePageBinding.getRoot());

        sharedPreferences = getSharedPreferences("skin_white_value",MODE_PRIVATE);

        mGPUImageView = livePageBinding.surfaceView;
        initCamera();

        liveChatRecyclerView = livePageBinding.liveRecyclerView;
        liveChatAdapter = new LiveChatAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView,RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(LivePage.this) {
                    private static final float SPEED = 4000f;// Change this value (default=25f)
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }

        };

        liveChatRecyclerView.setLayoutManager(layoutManager);
        liveChatRecyclerView.setAdapter(liveChatAdapter);

        lastPosition = liveChatAdapter.getItemCount() - 1;
        currentPosition = -1;

        autoScrollHandler = new Handler(Looper.getMainLooper());
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentPosition <= lastPosition)
                {
                    currentPosition ++;
                    liveChatRecyclerView.smoothScrollToPosition(currentPosition);
                    autoScrollHandler.postDelayed(autoScrollRunnable,500);
                }
                else
                {
                    currentPosition = 0;
                    liveChatRecyclerView.scrollToPosition(currentPosition);
                    autoScrollHandler.post(autoScrollRunnable);
                }
            }
        };

        autoScrollHandler.post(autoScrollRunnable);

        livePageBinding.filterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                brightValue = sharedPreferences.getFloat("brightness_value",0.0f);
                saturationValue = sharedPreferences.getFloat("saturation_value",1.0f);
                contrastValue = sharedPreferences.getFloat("contrast_value",1.0f);
                gammaValue = sharedPreferences.getFloat("gamma_value",1.0f);
                gaussianValue = sharedPreferences.getFloat("gaussian_value",1.0f);

                FilterDialog filterDialog = new FilterDialog(LivePage.this);
                filterDialog.setContentView(R.layout.filter_bottom_dialog);

                skinWhiteSeekbar = filterDialog.findViewById(R.id.skin_white_seekbar);
                assert skinWhiteSeekbar != null;
                skinWhiteSeekbar.setProgress((int) (brightValue * 100));

                skinBlurSeekbar = filterDialog.findViewById(R.id.skin_blur_seekbar);
                /*assert skinBlurSeekbar != null;
                skinBlurSeekbar.setProgress((int) (gaussianValue * 100));*/

                skinWhiteSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (b)
                        {
                            //brightness -1.0 to 1.0, with 0.0 as default
                            //saturation 0.0 - 2.0, with 1.0 as default
                            //contrast value ranges from 0.0 to 4.0, with 1.0 as the normal level
                            //gamma value ranges from 0.0 to 3.0, with 1.0 as the normal level
                            //exposure: The adjusted exposure (-10.0 - 10.0, with 0.0 as the default)

                            List<GPUImageFilter> gpuImageFilterList = new ArrayList<>();
                            float brightValue = remap(i,0,100,0,1);
                            GPUImageFilter brightnessFilter = new GPUImageBrightnessFilter(brightValue);
                            float saturationValue = remap(i,0,100,1,2);
                            GPUImageFilter saturationFile = new GPUImageSaturationFilter(saturationValue);
                            float contrastValue = remap(i,0,100,1,4);
                            GPUImageFilter contrastFile = new GPUImageContrastFilter(contrastValue);
                            float gammaValue = remap(i,0,100,1,3);
                            GPUImageFilter gammaFilter = new GPUImageGammaFilter(gammaValue);

                            /*gaussianValue = sharedPreferences.getFloat("gaussian_value",1.0f);
                            GPUImageFilter gaussianFilter = new GPUImageGaussianBlurFilter(gaussianValue);
                            gpuImageFilterList.add(gaussianFilter);*/

                            sharedPreferences.edit().putFloat("brightness_value",brightValue).apply();
                            sharedPreferences.edit().putFloat("saturation_value",saturationValue).apply();
                            sharedPreferences.edit().putFloat("contrast_value",contrastValue).apply();
                            sharedPreferences.edit().putFloat("gamma_value",gammaValue).apply();
                            //sharedPreferences.edit().putFloat("exposure_value",exposureValue).apply();

                            gpuImageFilterList.add(brightnessFilter);
                            gpuImageFilterList.add(saturationFile);
                            gpuImageFilterList.add(contrastFile);
                            gpuImageFilterList.add(gammaFilter);
                            //gpuImageFilterList.add(exposureFilter);

                            GPUImageFilterGroup gpuImageFilterGroup = new GPUImageFilterGroup(gpuImageFilterList);
                            mGPUImageView.setFilter(gpuImageFilterGroup);

                            mGPUImageView.requestRender();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                skinBlurSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        List<GPUImageFilter> gpuImageFilterList = new ArrayList<>();

                        brightValue = sharedPreferences.getFloat("brightness_value",0.0f);
                        saturationValue = sharedPreferences.getFloat("saturation_value",1.0f);
                        contrastValue = sharedPreferences.getFloat("contrast_value",1.0f);
                        gammaValue = sharedPreferences.getFloat("gamma_value",1.0f);

                        GPUImageFilter brightnessFilter = new GPUImageBrightnessFilter(brightValue);
                        GPUImageFilter saturationFile = new GPUImageSaturationFilter(saturationValue);
                        GPUImageFilter contrastFile = new GPUImageContrastFilter(contrastValue);
                        GPUImageFilter gammaFilter = new GPUImageGammaFilter(gammaValue);

                        float bilateralValue = remap(i,0,100,1,10);
                        GPUImageFilter bilateralBlurFilter = new GPUImageBilateralBlurFilter(bilateralValue);

                        float gaussianValue = remap(i,0,100,1,10);
                        GPUImageFilter gaussianFilter = new GPUImageGaussianBlurFilter(gaussianValue);

                        gpuImageFilterList.add(bilateralBlurFilter);
                        gpuImageFilterList.add(gaussianFilter);
                        gpuImageFilterList.add(brightnessFilter);
                        gpuImageFilterList.add(saturationFile);
                        gpuImageFilterList.add(contrastFile);
                        gpuImageFilterList.add(gammaFilter);

                        sharedPreferences.edit().putFloat("gaussian_value",gaussianValue).apply();
                        sharedPreferences.edit().putFloat("bilateral_value",bilateralValue).apply();

                        GPUImageFilterGroup gpuImageFilterGroup = new GPUImageFilterGroup(gpuImageFilterList);
                        mGPUImageView.setFilter(gpuImageFilterGroup);

                        mGPUImageView.requestRender();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                filterDialog.show();
            }
        });

        livePageBinding.closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private GPUImageFilterTools.OnGpuImageFilterChosenListener mOnGpuImageFilterChosenListener = new GPUImageFilterTools.OnGpuImageFilterChosenListener() {
        @Override
        public void onGpuImageFilterChosenListener(GPUImageFilter filter, String filterName) {
            switchFilterTo(filter);

        }
    };

    private void switchFilterTo(GPUImageFilter filter) {
        if (mCurrentImageFilter == null
                || (filter != null && !mCurrentImageFilter.getClass().equals(filter.getClass()))) {
            mCurrentImageFilter = filter;
            mGPUImageView.setFilter(mCurrentImageFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mCurrentImageFilter);
        }
    }

    private void initCamera() {
        mCameraLoader = new Camera2Loader(this);

        mCameraLoader.setOnPreviewFrameListener(new CameraLoader.OnPreviewFrameListener() {
            @Override
            public void onPreviewFrame(byte[] data, int width, int height) {
                mGPUImageView.updatePreviewFrame(data, width, height);
            }
        });
        //mGPUImageView.setRatio(1f); // 固定使用 4:3 的尺寸
        updateGPUImageRotate();
        //实机会卡顿
        mGPUImageView.setRenderMode(GPUImageView.RENDERMODE_WHEN_DIRTY);
    }

    private void updateGPUImageRotate() {
        Rotation rotation = getRotation(mCameraLoader.getCameraOrientation());
        boolean flipHorizontal = false;
        boolean flipVertical = false;
        if (mCameraLoader.isFrontCamera()) { // 前置摄像头需要镜像
            if (rotation == Rotation.NORMAL || rotation == Rotation.ROTATION_180) {
                flipHorizontal = true;
            } else {
                flipVertical = true;
            }
        }
        mGPUImageView.getGPUImage().setRotation(rotation, flipHorizontal, flipVertical);
    }

    private Rotation getRotation(int orientation) {
        switch (orientation) {
            case 90:
                return Rotation.ROTATION_90;
            case 180:
                return Rotation.ROTATION_180;
            case 270:
                return Rotation.ROTATION_270;
            default:
                return Rotation.NORMAL;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ViewCompat.isLaidOut(mGPUImageView) && !mGPUImageView.isLayoutRequested()) {
            mCameraLoader.onResume(mGPUImageView.getWidth(), mGPUImageView.getHeight());
        } else {
            mGPUImageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                           int oldRight, int oldBottom) {
                    mGPUImageView.removeOnLayoutChangeListener(this);
                    mCameraLoader.onResume(mGPUImageView.getWidth(), mGPUImageView.getHeight());
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraLoader.onPause();
    }


    @Override
    public void onClick(View view) {

    }

    private float remap(int p, int Amin, int Amax, int Bmin, int Bmax ) {

        float deltaA = Amax - Amin;
        float deltaB = Bmax - Bmin;
        float scale  = deltaB / deltaA;
        float negA   = -1 * Amin;
        float offset = (negA * scale) + Bmin;
        float q      = (p * scale) + offset;
        return q;

    }
}

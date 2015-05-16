package cz.skaut.warehousemanager.scanner;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.widget.FrameLayout;

@SuppressWarnings("deprecation")
public abstract class BarcodeScannerView extends FrameLayout implements Camera.PreviewCallback {
    private Camera mCamera;
    private CameraPreview mPreview;

    public BarcodeScannerView(Context context) {
        super(context);
        if(!isInEditMode()) {
            setupLayout();
        }
    }

    public BarcodeScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if(!isInEditMode()) {
            setupLayout();
        }
    }

    private void setupLayout() {
        mPreview = new CameraPreview(getContext());
        addView(mPreview);
        //mPreview.setViewSize()
    }

    public void startCamera() {
        mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        if (mCamera != null) {
            mPreview.setCamera(mCamera, this);
            mPreview.initCameraPreview();
        }
    }

    public void stopCamera() {
        if (mCamera != null) {
            mPreview.stopCameraPreview();
            mPreview.setCamera(null, null);
            mCamera.release();
            mCamera = null;
        }
    }
}
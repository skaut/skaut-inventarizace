package cz.skaut.warehousemanager.scanner;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Copyright 2015 Dushyanth Maguluru
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@SuppressWarnings("deprecation")
public abstract class BarcodeScannerView extends FrameLayout implements Camera.PreviewCallback {
    private Camera mCamera;
    private CameraPreview mPreview;

    public BarcodeScannerView(Context context) {
        super(context);
        if (!isInEditMode()) {
            setupLayout();
        }
    }

    public BarcodeScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (!isInEditMode()) {
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
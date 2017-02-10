package cz.skaut.warehousemanager.scanner;


import android.content.Context;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

/**
 * Copyright 2015 Dushyanth Maguluru
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * >
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@SuppressWarnings("deprecation")
public class ZBarScannerView extends FrameLayout implements Camera.PreviewCallback {

	static {
		System.loadLibrary("iconv");
	}

	private Camera camera;
	private CameraPreview cameraPreview;

	private ImageScanner scanner;

	private PublishSubject<String> publishSubject;

	private int imageWidth;
	private int imageHeight;

	public ZBarScannerView(Context context) {
		super(context);
		if (!isInEditMode()) {
			setupLayout();
			setupScanner();
		}
	}

	public ZBarScannerView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		if (!isInEditMode()) {
			setupLayout();
			setupScanner();
		}
	}

	public Observable<String> getBarcodes() {
		// send each barcode only once
		return publishSubject.distinct();
	}

	private void setupLayout() {
		cameraPreview = new CameraPreview(getContext());
		addView(cameraPreview);
	}

	private void setupScanner() {
		publishSubject = PublishSubject.create();

		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);

		for (BarcodeFormat format : BarcodeFormat.ALL_FORMATS) {
			scanner.setConfig(format.getId(), Config.ENABLE, 1);
		}
	}

	public void startCamera() {
		camera = null;
		try {
			camera = Camera.open();
		} catch (RuntimeException e) {
			Timber.e(e, "Camera open failed");
		}
		if (camera != null) {
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = parameters.getPreviewSize();
			imageWidth = size.width;
			imageHeight = size.height;

			cameraPreview.setCamera(camera, this);
			cameraPreview.initCameraPreview();
		}
	}

	public void stopCamera() {
		if (camera != null) {
			cameraPreview.stopCameraPreview();
			cameraPreview.setCamera(null, null);
			camera.release();
			camera = null;
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Image barcode = new Image(imageWidth, imageHeight, "Y800");
		barcode.setData(data);

		int result = scanner.scanImage(barcode);

		if (result != 0) {
			SymbolSet symbols = scanner.getResults();
			String resultCode = "";
			for (Symbol sym : symbols) {
				String symData = sym.getData();
				if (!TextUtils.isEmpty(symData)) {
					resultCode = symData;
					break;
				}
			}
			publishSubject.onNext(resultCode);
		}
		camera.setOneShotPreviewCallback(this);
	}
}
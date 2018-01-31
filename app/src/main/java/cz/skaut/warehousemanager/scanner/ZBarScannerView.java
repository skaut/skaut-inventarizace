package cz.skaut.warehousemanager.scanner;


import android.content.Context;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.AttributeSet;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import rx.Observable;
import rx.subjects.PublishSubject;

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
public class ZBarScannerView extends BarcodeScannerView {

    static {
        System.loadLibrary("iconv");
    }

    private ImageScanner mScanner;

    private PublishSubject<String> publishSubject;

    public ZBarScannerView(Context context) {
        super(context);
        if (!isInEditMode()) {
            setupScanner();
        }
    }

    public ZBarScannerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (!isInEditMode()) {
            setupScanner();
        }
    }

    public Observable<String> getBarcodes() {
        // send each barcode only once
        return publishSubject.distinct();
    }

    private void setupScanner() {
        publishSubject = PublishSubject.create();

        mScanner = new ImageScanner();
        mScanner.setConfig(0, Config.X_DENSITY, 3);
        mScanner.setConfig(0, Config.Y_DENSITY, 3);

        mScanner.setConfig(Symbol.NONE, Config.ENABLE, 0);
        for (BarcodeFormat format : BarcodeFormat.ALL_FORMATS) {
            mScanner.setConfig(format.getId(), Config.ENABLE, 1);
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        int width = size.width;
        int height = size.height;

        Image barcode = new Image(width, height, "Y800");
        barcode.setData(data);

        int result = mScanner.scanImage(barcode);

        if (result != 0) {
            SymbolSet symbols = mScanner.getResults();
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
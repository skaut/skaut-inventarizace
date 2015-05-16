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
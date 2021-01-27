package com.monscanner;

import android.os.Environment;


public abstract class ScanConstants {

    public static final int OPEN_GALERIE = 1;
    public static final int OPEN_CAMERA = 2;
    public static final String OPEN_INTENT_PREFERENCE = "open_intent_preference";
    static final String PHOTO_RESULT = "photo_result";
    static final int PICKFILE_REQUEST_CODE = 3;
    static final int START_CAMERA_REQUEST_CODE = 4;
    final static String IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/scantemp";
    final static String SELECTED_BITMAP = "selectedBitmap";
    final static String SCANNED_RESULT = "scannedResult";

}

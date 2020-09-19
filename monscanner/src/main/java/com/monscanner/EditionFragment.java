package com.monscanner;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import static org.opencv.core.Core.ROTATE_90_CLOCKWISE;
import static org.opencv.core.Core.ROTATE_90_COUNTERCLOCKWISE;
import static org.opencv.core.Core.rotate;
/**
 * CAUTION: in order to increase performance, modifications are only applied to
 * the image resized so that the user quickly has a visual of them.
 * The current state (color + rotation) of the resized image is saved, only the
 * Final changes are applied to the original image during checkout.
 */
public class EditionFragment extends Fragment {

    private static final String TAG = "EditionFragmentDebug";

    private final int ORIGINAL = 0;
    private final int MAGIC = 1;
    private final int GRAY = 2;
    private final int BW = 3;
    private int couleur;
    private int angleFinal;
    private ImageView imageView;
    private View view;
    private static ProgressDialogFragment progressDialogFragment;
    private Mat edited;
    private Mat scaled;
    Bitmap original;
    Bitmap bmScaled;
    private ScanActivity scanner;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        scanner = (ScanActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edition, null);
        init();
        return view;
    }

    private void init() {
        angleFinal = 0;
        couleur = ORIGINAL;

        Button originalButton = view.findViewById(R.id.originalButton);
        originalButton.setOnClickListener(new OriginalListener());
        Button magicButton = view.findViewById(R.id.magicButton);
        magicButton.setOnClickListener(new MagicListener());
        Button grayButton = view.findViewById(R.id.grayButton);
        grayButton.setOnClickListener(new GrayListener());
        Button bwButton = view.findViewById(R.id.bwButton);
        bwButton.setOnClickListener(new BWListener());
        ImageButton rgaucheButton = view.findViewById(R.id.rgaucheButton);
        rgaucheButton.setOnClickListener(new RotationListener(ROTATE_90_COUNTERCLOCKWISE));
        ImageButton rdroiteButton = view.findViewById(R.id.rdroiteButton);
        rdroiteButton.setOnClickListener(new RotationListener(ROTATE_90_CLOCKWISE));
        Button validationButton = view.findViewById(R.id.validationButton);
        validationButton.setOnClickListener(new ValidationListener());

        try {
            imageView = view.findViewById(R.id.rognedImageView);
            File file = new File(((Uri) Objects.requireNonNull(getArguments().getParcelable(ScanConstants.SCANNED_RESULT))).getPath());
            Uri uri = Uri.fromFile(file);
            original = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

            // resize the image
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    scaled = new Mat();
                    bmScaled = scanner.scaledBitmap(original,imageView.getWidth(), imageView.getHeight());
                    imageView.setImageBitmap(bmScaled);
                    Utils.bitmapToMat(bmScaled, scaled);
                    edited = scaled.clone();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // update display
    private void updateImage() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.matToBitmap(edited, bmScaled);
                imageView.setImageBitmap(bmScaled);
            }
        });
    }

    // display of the wait dialog during a task
    protected synchronized void showProgressDialog(String message) {
        if (progressDialogFragment != null && progressDialogFragment.isVisible()) {
            // Before creating another loading dialog, close all opened loading dialogs (if any)
            progressDialogFragment.dismissAllowingStateLoss();
        }
        progressDialogFragment = null;
        progressDialogFragment = new ProgressDialogFragment(message);
        FragmentManager fm = getFragmentManager();
        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    // closing the wait dialog
    protected synchronized void dismissDialog() {
        progressDialogFragment.dismissAllowingStateLoss();
    }

    // recording of the image in the media and return of the Uri thereof
    private Uri getUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    // set the picture in original color
    private class OriginalListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showProgressDialog(getResources().getString(R.string.application));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    edited = scaled.clone();
                    updateImage();
                    couleur = ORIGINAL;
                }
            });
            dismissDialog();
        }
    }

    //set the image in enhanced color
    private class MagicListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showProgressDialog(getResources().getString(R.string.application));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    float alpha = 1.9f;
                    float beta = -80;
                    scaled.convertTo(edited, -1, alpha, beta);
                    updateImage();
                    couleur = MAGIC;
                }
            });
            dismissDialog();
        }
    }

    // set the image in "grayscale" color
    private class GrayListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showProgressDialog(getResources().getString(R.string.application));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Imgproc.cvtColor(scaled, edited, Imgproc.COLOR_BGR2GRAY);
                    updateImage();
                    couleur = GRAY;
                }
            });
            dismissDialog();
        }
    }

    // set the picture in black & white color
    private class BWListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showProgressDialog(getResources().getString(R.string.application));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Imgproc.cvtColor(scaled, edited, Imgproc.COLOR_BGR2GRAY);
                    Imgproc.threshold(edited,edited,0,255,Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
                    updateImage();
                    couleur = BW;
                }
            });
            dismissDialog();
        }
    }

    // rotate the image 90 ° or -90 °
    private class RotationListener implements View.OnClickListener {
        private int angle;

        RotationListener(int i) {
            this.angle = i;
        }

        @Override
        public void onClick(View v) {
            showProgressDialog(getResources().getString(R.string.rotation));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    if (angle == ROTATE_90_CLOCKWISE)
                        angleFinal+=90;
                    else
                        angleFinal-=90;
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    bmScaled = Bitmap.createBitmap(bmScaled, 0, 0, bmScaled.getWidth(), bmScaled.getHeight(), matrix, true);
                    rotate(edited, edited, angle);
                    rotate(scaled,scaled,angle);
                    updateImage();
                }
            });
            dismissDialog();
        }
    }

    // validate the changes and apply the changes to the original image
    private class ValidationListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showProgressDialog(getResources().getString(R.string.validation));
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    bmScaled.recycle();
                    Matrix matrix = new Matrix();
                    matrix.postRotate(angleFinal);
                    original = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
                    Mat finie = new Mat();
                    Utils.bitmapToMat(original,finie);
                    switch(couleur) {
                        case ORIGINAL:
                            break;

                        case MAGIC:
                            float alpha = 1.9f;
                            float beta = -80f;
                            finie.convertTo(finie, -1, alpha, beta);
                            Utils.matToBitmap(finie, original);
                            break;

                        case GRAY:
                            Imgproc.cvtColor(finie, finie, Imgproc.COLOR_BGR2GRAY);
                            Utils.matToBitmap(finie, original);
                            break;

                        case BW:
                            Imgproc.cvtColor(finie, finie, Imgproc.COLOR_BGR2GRAY);
                            Imgproc.threshold(finie,finie,0,255,Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
                            Utils.matToBitmap(finie, original);
                            break;
                    }

                    try {
                        Intent data = new Intent();
                        Uri uri = getUri(getActivity().getApplicationContext(), original);
                        data.putExtra(ScanActivity.SCAN_RESULT, uri);
                        getActivity().setResult(Activity.RESULT_OK, data);
                        original.recycle();
                        scanner.clearTempImages();
                        System.gc();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissDialog();
                                getActivity().finish();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}

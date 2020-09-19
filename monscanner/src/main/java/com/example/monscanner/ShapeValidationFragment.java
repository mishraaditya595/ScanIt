package com.example.monscanner;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.core.CvType.CV_8UC3;

/**
 * Cette classe est un fragment permettant de selectionner le document dans
 * l'image sélectionnée.
 * Le but est de détecter le document dans l'imaage. C'est à dire le plus grand
 * rectangle dans la plupart des cas. Cette détection se fait grace à la librairie
 * openCV. La mise en place d'une rapide hiérarchie des rectangles obtenus permet une
 * meilleure détection.
 * Il est possible de changer la forme détectée manuellement ou en appuyant sur le bouton de
 * parcours des contours
 */
public class ShapeValidationFragment extends Fragment {

    private final String TAG = "ShapeValidationDebug";

    private ImageView imageView;
    private Mat srcGray;
    private Mat src;
    private Mat scaled;
    private File folder;
    private PolygonView polygonView;
    private float rapport;
    private static ProgressDialogFragment progressDialogFragment;
    private View view;
    private ScanActivity scanner;
    private FrameLayout sourceframe;
    private List<MatOfPoint> squares;
    private List<Integer> indices;
    private Bitmap scaledBmp;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        scanner = (ScanActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shape_validation, null);
        init();
        return view;
    }

    private void init() {

        // Set des OnClickListeners sur les boutons
        ImageButton infosButton = view.findViewById(R.id.shapeInfoButton);
        infosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInfos(v);
            }
        });
        Button scanButton = view.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new ScanButtonListener());
        ImageButton switchButton = view.findViewById(R.id.switchButton);
        switchButton.setOnClickListener(new SwitchListener());

        polygonView = view.findViewById(R.id.polygonView);
        imageView = view.findViewById(R.id.scanImageView);
        sourceframe = view.findViewById(R.id.sourceframe);

        srcGray = new Mat();
        src = new Mat();

        // création du dossier des images temporaires
        folder = Environment.getExternalStorageDirectory();
        folder = new File(ScanConstants.IMAGE_PATH);
        if (!folder.exists()) {
            if(!folder.mkdir())
                Log.d(TAG, "onCreate: impossible de créer le dossier temp");
        }

        // lancement de la recherche du document
        showProgressDialog(getString(R.string.analyse_image));
        final Uri uri = getArguments().getParcelable(ScanConstants.SELECTED_BITMAP);
        sourceframe.post(new Runnable() {
            @Override
            public void run() {
                assert uri != null;
                shapeDetection(uri);
            }
        });
    }

    private void openInfos(View v) {
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.popup_shape_infos,null);
        PopupWindow popupInfos = new PopupWindow(content, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupInfos.showAsDropDown(v);
    }

    // generation du bitmap redimentionné et lancement de la suite de la recherche du document
    private void shapeDetection(final Uri uri) {
        try {
            File file = new File(uri.getPath());
            Uri fileUri = Uri.fromFile(file);
            Bitmap original = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileUri);
            scaledBmp = scanner.scaledBitmap(original, sourceframe.getWidth(), sourceframe.getHeight());
            scaled = new Mat();
            Utils.bitmapToMat(scaledBmp,scaled);
            Utils.bitmapToMat(original, src);
            Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2RGB);
            rapport  = (float) (src.size().height / scaled.size().height);
            MatOfPoint points = getPoints();
            setPoints(points);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dismissDialog();
    }

    // recherche des points correspondants aux coins du document
    private MatOfPoint getPoints() {
        Mat blurred = new Mat();

        // valeur à modifier pour ajustements de la recherche de rectangles
        Imgproc.medianBlur(scaled, blurred, 9);
        Mat threshOutput = new Mat(blurred.size(), CV_8U);
        squares = new ArrayList<>();
        List<MatOfPoint> threshSquares = new ArrayList<>();
        SparseArray<MatOfPoint> cannySquares = new SparseArray<>();
        indices = new ArrayList<>();


        // valeur à modifier pour ajustements de la recherche de rectangles

        for (int c = 0; c < 3; c++) {

            int[] ch = {c, 0};
            MatOfInt cmat = new MatOfInt(ch);
            List<Mat> lblurred = new ArrayList<>();
            lblurred.add(blurred);
            List<Mat> loutput = new ArrayList<>();
            loutput.add(threshOutput);
            Core.mixChannels(lblurred,loutput,cmat);
//            Imgcodecs.imwrite(ScanConstants.IMAGE_PATH+"/"+c+".jpg",threshOutput);

            // tests de differents threshold
            int threshold_level = 3;
            for (int l = 0; l < threshold_level; l++) {
                if (l == 0) {

                    for (int t=10; t<=60; t+=10) {
                        Imgproc.Canny(threshOutput, srcGray, t, t * 2);
                        Imgproc.dilate(srcGray, srcGray, new Mat(), new Point(-1, -1), 2);
//                        Imgcodecs.imwrite(ScanConstants.IMAGE_PATH + "/" + c + "_" + l + "_" + t + ".jpg", srcGray);

                        findCannySquares(srcGray,cannySquares,c+t);
                    }

                } else {

                    // valeurs et calcul à modifier pour ajustements de la recherche de rectangles
                    Imgproc.threshold(threshOutput, srcGray, 200 - 175 / (l + 2f), 256, Imgproc.THRESH_BINARY);
//                    Imgcodecs.imwrite(ScanConstants.IMAGE_PATH + "/" + c + "" + l + ".jpg", srcGray);

                    findThreshSquares(srcGray, threshSquares);
                }
            }
        }

        int indiceMax = maxi(indices);
        if (indiceMax != -1)
            squares.add(cannySquares.get(indiceMax));

        // Suppression des quadrilatères peu probables c'est à dire ceux qui touchent les bords
        List<MatOfPoint> squaresProba = new ArrayList<>();
        MatOfPoint pointsProba;
        List<Point> pointsList;
        int marge = (int) (srcGray.size().width*0.01f);
        boolean probable;
        for(int i=0;i<threshSquares.size();i++) {
            probable = true;
            pointsProba = threshSquares.get(i);
            pointsList = pointsProba.toList();
            for (Point p : pointsList) {
                if (p.x<marge || p.x>=srcGray.size().width-marge || p.y<marge || p.y>=srcGray.size().height-marge) {
                    probable = false;
                    break;
                }
            }
            if(probable) {
                squaresProba.add(pointsProba);
            }
        }

        // selection du quadrilatère le plus grand
        int largest_contour_index = 0;
        MatOfPoint points = new MatOfPoint();
        if (squaresProba.size()!=0) {
            double largest_area = -1;
            for (int i = 0; i < squaresProba.size(); i++) {
                double a = Imgproc.contourArea(squaresProba.get(i), false);
                if (a > largest_area && a < srcGray.size().height * srcGray.size().width) {
                    largest_area = a;
                    largest_contour_index = i;
                }
            }

            if (squaresProba.size() > 0) {
                points = squaresProba.get(largest_contour_index);
            } else {
                List<Point> pts = new ArrayList<>();
                pts.add(new Point(0, 0));
                pts.add(new Point(scaled.size().width, 0));
                pts.add(new Point(0, scaled.size().height));
                pts.add(new Point(scaled.size().width, scaled.size().height));
                points.fromList(pts);
            }
        }
        else {
            double largest_area = -1;
            for (int i = 0; i < threshSquares.size(); i++) {
                double a = Imgproc.contourArea(threshSquares.get(i), false);
                if (a > largest_area && a < srcGray.size().height * srcGray.size().width) {
                    largest_area = a;
                    largest_contour_index = i;
                }
            }

            if (threshSquares.size() > 0) {
                points = threshSquares.get(largest_contour_index);
            } else {
                List<Point> pts = new ArrayList<>();
                pts.add(new Point(0, 0));
                pts.add(new Point(scaled.size().width, 0));
                pts.add(new Point(0, scaled.size().height));
                pts.add(new Point(scaled.size().width, scaled.size().height));
                points.fromList(pts);
            }
        }
        squares.add(points);

        // Ajout des autres contours après les 2 plus importants
        for (int id : indices) {
            if (id!=indiceMax)
                squares.add(cannySquares.get(id));
        }
        for (int id=0; id<threshSquares.size(); id++) {
            if (id!=largest_contour_index) {
                squares.add(threshSquares.get(id));
            }
        }

        return squares.get(0);
    }

    private int maxi(List<Integer> indices) {
        int max = -1;
        for (int i : indices) {
            if (i>max)
                max = i;
        }
        return max;
    }


    private void findCannySquares(Mat srcGray, SparseArray<MatOfPoint> cannySquares, int indice) {
        // recherche des contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(srcGray, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint2f approx = new MatOfPoint2f();
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint2f contour = new MatOfPoint2f();
            contour.fromArray(contours.get(i).toArray());
            // detection des formes géometriques
            Imgproc.approxPolyDP(contour, approx, Imgproc.arcLength(contour, true) * 0.03, true);
            MatOfPoint approx1f = new MatOfPoint();
            approx1f.fromArray(approx.toArray());
            // détection des quadrilatères parmis les formes géométriques
            if (approx.total() == 4 && abs(Imgproc.contourArea(approx)) > (scaled.size().width / 5) * (scaled.size().height / 5) && Imgproc.isContourConvex(approx1f)) {
                double maxCosine = 0;

                for (int j = 2; j < 5; j++) {
                    double cosine = abs(angle(approx.toArray()[j % 4], approx.toArray()[j - 2], approx.toArray()[j - 1]));
                    maxCosine = max(maxCosine, cosine);
                }
                // selection des quadrilatères ayant des angles assez grands
                if (maxCosine < 0.5) {
                    cannySquares.put(indice,approx1f);
                    indices.add(indice);
                }
            }
        }
    }

    private void findThreshSquares(Mat srcGray, List<MatOfPoint> threshSquares) {
        // recherche des contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(srcGray, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint2f approx = new MatOfPoint2f();
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint2f contour = new MatOfPoint2f();
            contour.fromArray(contours.get(i).toArray());
            // detection des formes géometriques
            Imgproc.approxPolyDP(contour, approx, Imgproc.arcLength(contour, true) * 0.03, true);
            MatOfPoint approx1f = new MatOfPoint();
            approx1f.fromArray(approx.toArray());
            // détection des quadrilatères parmis les formes géométriques
            if (approx.total() == 4 && abs(Imgproc.contourArea(approx)) > (scaled.size().width / 5) * (scaled.size().height / 5) && Imgproc.isContourConvex(approx1f)) {
                double maxCosine = 0;

                for (int j = 2; j < 5; j++) {
                    double cosine = abs(angle(approx.toArray()[j % 4], approx.toArray()[j - 2], approx.toArray()[j - 1]));
                    maxCosine = max(maxCosine, cosine);
                }
                // selection des quadrilatères ayant des angles assez grands
                if (maxCosine < 0.5) {
                    threshSquares.add(approx1f);
                }
            }
        }
    }

    // Set des points du PolygonView servant à délimiter les contours du document
    private void setPoints(MatOfPoint points) {
        Point[] pts = points.toArray();
        List<PointF> pointsf = new ArrayList<>();

        pointsf.add(new PointF((float) pts[0].x, (float) pts[0].y));
        pointsf.add(new PointF((float) pts[1].x, (float) pts[1].y));
        pointsf.add(new PointF((float) pts[2].x, (float) pts[2].y));
        pointsf.add(new PointF((float) pts[3].x, (float) pts[3].y));
        SparseArray<PointF> orderedPoints = polygonView.getOrderedPoints(pointsf);
        polygonView.setPoints(orderedPoints);
        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(scaledBmp.getWidth() + 2 * padding, scaledBmp.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                afficheimage(scaledBmp);
                polygonView.setLayoutParams(layoutParams);
            }
        });
    }

    // calcul d'une valeur d'angle en fonction de 3 points
    private double angle( Point pt1, Point pt2, Point pt0 ) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1*dx2 + dy1*dy2)/sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
    }

    // affichage de l'image
    private void afficheimage(Bitmap bm) {
        imageView.setImageBitmap(bm);
    }

    // après validation, recadrage du document afin d'en faire un rectangle
    private void scan(Mat src, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        double w1 = sqrt( pow(x4 - x3 , 2) + pow(x4 - x3, 2));
        double w2 = sqrt( pow(x2 - x1 , 2) + pow(x2-x1, 2));
        double h1 = sqrt( pow(y2 - y4 , 2) + pow(y2 - y4, 2));
        double h2 = sqrt( pow(y1 - y3 , 2) + pow(y1-y3, 2));

        int maxWidth = (int) ((w1 < w2) ? w1 : w2);
        int maxHeight = (int) ((h1 < h2) ? h1 : h2);

        Mat dst = Mat.zeros(maxHeight, maxWidth, CV_8UC3);

        // corners of destination image with the sequence [tl, tr, bl, br]
        List<Point> dst_pts = new ArrayList<>();
        List<Point> img_pts = new ArrayList<>();
        dst_pts.add(new Point(0, 0));
        dst_pts.add(new Point(maxWidth - 1, 0));
        dst_pts.add(new Point(0, maxHeight - 1));
        dst_pts.add(new Point(maxWidth - 1, maxHeight - 1));

        img_pts.add(new Point(x1,y1));
        img_pts.add(new Point(x2,y2));
        img_pts.add(new Point(x3,y3));
        img_pts.add(new Point(x4,y4));

        MatOfPoint2f mdst = new MatOfPoint2f();
        mdst.fromList(dst_pts);
        MatOfPoint2f mimg = new MatOfPoint2f();
        mimg.fromList(img_pts);
        // get transformation matrix
        Mat transmtx = Imgproc.getPerspectiveTransform(mimg, mdst);
        // apply perspective transformation
        Imgproc.warpPerspective(src, dst, transmtx, dst.size());

        Imgcodecs.imwrite(folder.getAbsolutePath()+"/scanned.jpg", dst);
    }

    // fait apparaitre le dialogue d'attente
    protected synchronized void showProgressDialog(String message) {
        if (progressDialogFragment != null && progressDialogFragment.isVisible()) {
            // Before creating another loading dialog, close all opened loading dialogs (if any)
            progressDialogFragment.dismissAllowingStateLoss();
        }
        progressDialogFragment = null;
        progressDialogFragment = new ProgressDialogFragment(message);
        FragmentManager fm = getActivity().getFragmentManager();
        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    // ferme le dialogue d'attente
    protected synchronized void dismissDialog() {
        progressDialogFragment.dismissAllowingStateLoss();
    }

    // lance le scan du document dont les contours sont définis par le PolygonView
    private class ScanButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (polygonView.isValidShape(polygonView.getPoints())) {
                showProgressDialog(getResources().getString(R.string.scanning));
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        SparseArray<PointF> pts = polygonView.getPoints();
                        float x1, x2, x3, x4, y1, y2, y3, y4;
                        x1 = Objects.requireNonNull(pts.get(0)).x * rapport;
                        x2 = Objects.requireNonNull(pts.get(1)).x * rapport;
                        x3 = Objects.requireNonNull(pts.get(2)).x * rapport;
                        x4 = Objects.requireNonNull(pts.get(3)).x * rapport;

                        y1 = Objects.requireNonNull(pts.get(0)).y * rapport;
                        y2 = Objects.requireNonNull(pts.get(1)).y * rapport;
                        y3 = Objects.requireNonNull(pts.get(2)).y * rapport;
                        y4 = Objects.requireNonNull(pts.get(3)).y * rapport;
                        scan(src, x1, y1, x2, y2, x3, y3, x4, y4);
                        scanner.onScanFinish(Uri.parse(folder.getPath() + "/scanned.jpg"));
                        dismissDialog();
                    }
                });
            }
        }
    }

    // Parcours la liste des quadrilatères trouvé et met à jour le polygonView
    private class SwitchListener implements View.OnClickListener {
        private int id;

        SwitchListener() {
            this.id = 0;
        }

        @Override
        public void onClick(View v) {
            if (squares.size() > 0) {
                showProgressDialog(getResources().getString(R.string.new_contours));
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        id++;
                        if (id >= squares.size()) {
                            id = 0;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(scanner, getResources().getString(R.string.parcours_contours), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        setPoints(squares.get(id));
                        dismissDialog();
                    }
                });
            }
        }
    }
}

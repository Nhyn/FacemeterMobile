package training.facemetermobile.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.FaceDetector;
import android.os.Bundle;
import android.view.View;

import training.facemetermobile.R;

/**
 * Created by Samwi on 23/09/2016.
 */

public class AwesomenessRateActivity extends Activity {
    public View part1, part2;
    int viewHeight, viewWidth;
    float myEyesDistance;
    private FaceDetector myFaceDetect;
    private FaceDetector.Face[] myFace;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.awesomeness_activity);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            location = bundle.getString("location", "");
        }

        part1 = findViewById(R.id.part1);
        part2 = findViewById(R.id.part2);
        part1.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                viewHeight = part1.getMeasuredHeight();
                viewWidth = part1.getMeasuredWidth();
                try {

                    Paint paint = new Paint();
                    paint.setFilterBitmap(true);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmapOrg = BitmapFactory.decodeFile(location, options);

                    int targetWidth = bitmapOrg.getWidth();
                    int targetHeight = bitmapOrg.getHeight();

                    Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                            targetHeight, Bitmap.Config.ARGB_8888);

                    RectF rectf = new RectF(0, 0, viewWidth, viewHeight);

                    Canvas canvas = new Canvas(targetBitmap);
                    Path path = new Path();

                    path.addRect(rectf, Path.Direction.CW);
                    canvas.clipPath(path);

                    canvas.drawBitmap(
                            bitmapOrg,
                            new Rect(0, 0, bitmapOrg.getWidth(), bitmapOrg
                                    .getHeight()), new Rect(0, 0, targetWidth,
                                    targetHeight), paint);

                    Matrix matrix = new Matrix();
                    matrix.postScale(1f, 1f);

                    BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
                    bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;

                    bitmapOrg = BitmapFactory.decodeFile(location, bitmapFatoryOptions);

                    myFace = new FaceDetector.Face[5];
                    myFaceDetect = new FaceDetector(targetWidth, targetHeight,
                            5);
                    int numberOfFaceDetected = myFaceDetect.findFaces(
                            bitmapOrg, myFace);
                    Bitmap resizedBitmap = null;
                    if (numberOfFaceDetected > 0) {
                        PointF myMidPoint = null;
                        FaceDetector.Face face = myFace[0];
                        myMidPoint = new PointF();
                        face.getMidPoint(myMidPoint);
                        myEyesDistance = face.eyesDistance();

                        if (myMidPoint.x + viewWidth > targetWidth) {
                            while (myMidPoint.x + viewWidth > targetWidth) {
                                myMidPoint.x--;
                            }
                        }
                        if (myMidPoint.y + viewHeight > targetHeight) {
                            while (myMidPoint.y + viewHeight > targetHeight) {
                                myMidPoint.y--;
                            }
                        }
                        resizedBitmap = Bitmap.createBitmap(bitmapOrg,
                                (int) (myMidPoint.x - myEyesDistance),
                                (int) (myMidPoint.y - myEyesDistance),
                                viewWidth, viewHeight, matrix, true);
                    } else {
                        resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
                                viewWidth, viewHeight, matrix, true);
                    }

                    BitmapDrawable bd = new BitmapDrawable(resizedBitmap);

                    part1.setBackgroundDrawable(bd);

                } catch (Exception e) {
                    System.out.println("Error1 : " + e.getMessage()
                            + e.toString());
                }
            }
        });
        part2.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                viewHeight = part2.getMeasuredHeight();
                viewWidth = part2.getMeasuredWidth();
                try {

                    Paint paint = new Paint();
                    paint.setFilterBitmap(true);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmapOrg = BitmapFactory.decodeFile(location, options);

                    int targetWidth = bitmapOrg.getWidth();
                    int targetHeight = bitmapOrg.getHeight();

                    Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                            targetHeight, Bitmap.Config.ARGB_8888);

                    RectF rectf = new RectF(0, 0, viewWidth, viewHeight);

                    Canvas canvas = new Canvas(targetBitmap);
                    Path path = new Path();

                    path.addRect(rectf, Path.Direction.CW);
                    canvas.clipPath(path);

                    canvas.drawBitmap(
                            bitmapOrg,
                            new Rect(0, 0, bitmapOrg.getWidth(), bitmapOrg
                                    .getHeight()), new Rect(0, 0, targetWidth,
                                    targetHeight), paint);

                    Matrix matrix = new Matrix();
                    matrix.postScale(1f, 1f);

                    BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
                    bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;

                    bitmapOrg = BitmapFactory.decodeFile(location, bitmapFatoryOptions);

                    myFace = new FaceDetector.Face[5];
                    myFaceDetect = new FaceDetector(targetWidth, targetHeight,
                            5);
                    int numberOfFaceDetected = myFaceDetect.findFaces(
                            bitmapOrg, myFace);
                    Bitmap resizedBitmap = null;
                    if (numberOfFaceDetected > 0) {
                        PointF myMidPoint = null;
                        FaceDetector.Face face = myFace[0];
                        myMidPoint = new PointF();
                        face.getMidPoint(myMidPoint);
                        myEyesDistance = face.eyesDistance() + 20;

                        if (myMidPoint.x + viewWidth > targetWidth) {
                            while (myMidPoint.x + viewWidth > targetWidth) {
                                myMidPoint.x--;
                            }
                        }
                        if (myMidPoint.y + viewHeight > targetHeight) {
                            while (myMidPoint.y + viewHeight > targetHeight) {
                                myMidPoint.y--;
                            }
                        }
                        resizedBitmap = Bitmap.createBitmap(bitmapOrg,
                                (int) (myMidPoint.x - myEyesDistance),
                                (int) (myMidPoint.y - myEyesDistance),
                                viewWidth, viewHeight, matrix, true);
                    } else {
                        resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
                                viewWidth, viewHeight, matrix, true);
                    }
                    BitmapDrawable bd = new BitmapDrawable(resizedBitmap);

                    part2.setBackground(new BitmapDrawable(
                            getCroppedBitmap(bd.getBitmap())));
/*
// <<<<<------------------------TRAINING--------------------->>>>>>>>>>>
                    Mat traindata=null;

                    Mat img;
                    int[] myint =null;
                    for (int i = 0; i < 10; i++) {
                        String path5 = Environment.getExternalStorageDirectory().toString() + "/Pictures/ocr/" + i + ".png";
                        img = imread(path5);
                        img.convertTo(img, CvType.CV_32FC1);
                        traindata.push_back(img);

                        myint[i] = i;
                    }

                    Mat trainResponses = new Mat();
                    trainResponses.put(0,0,myint);

                    ANN_MLP knn = new ANN_MLP(0);
                    knn.train(traindata,1, trainResponses);

// <<<<<---------------------END---TRAINING--------------------->>>>>>>>>>>
*/
                } catch (Exception e) {
                    System.out.println("Error1 : " + e.getMessage()
                            + e.toString());
                }
            }
        });
    }


    public Bitmap getCroppedBitmap(Bitmap bitmap) {

        int targetWidth = bitmap.getWidth();
        int targetHeight = bitmap.getHeight();
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();

        /* path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
                Path.Direction.CCW);
        */

        float xcircle = (targetWidth - 1) / 2;
        float ycircle = (targetHeight - 1) / 2;
        float rcircle = (Math.min(((float) targetWidth), ((float) targetHeight)) / 2);

        path.addRect(xcircle - rcircle, ycircle - rcircle, xcircle + rcircle, ycircle + rcircle, Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = bitmap;
        canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
                sourceBitmap.getHeight()), new Rect(0, 0, targetWidth,
                targetHeight), null);
        return targetBitmap;

    }

}
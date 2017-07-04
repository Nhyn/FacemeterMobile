/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package training.facemetermobile.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import training.facemetermobile.Detection.CameraSourcePreview;
import training.facemetermobile.Detection.FaceGraphic;
import training.facemetermobile.Detection.GraphicOverlay;
import training.facemetermobile.R;


public final class FaceTrackerActivity extends AppCompatActivity {
    private static final String TAG = "FaceTracker";
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private GraphicOverlay mOverlay;
    private FaceGraphic mFaceGraphic;
    private volatile Face mFace;
    private String location;
    private float f1, f2, f3, f4;
    private int f5, f6, f7, f8;

    private FaceDetector detector;
    private FaceDetector.Detections faces;

    Bitmap loadedImage = null;

    @BindView(R.id.capture)
    Button btnCapture;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_facetracker);

        ButterKnife.bind(this);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                    private File imageFile;

                    @Override
                    public void onPictureTaken(byte[] bytes) {
                        try {
                            Log.e(TAG, "mau di save");
                            loadedImage = BitmapFactory.decodeByteArray(bytes, 0,
                                    bytes.length);

                            if (mFaceGraphic != null) {
                                f1 = mFaceGraphic.getLeft();
                                f2 = mFaceGraphic.getTop();
                                f3 = mFaceGraphic.getRight();
                                f4 = mFaceGraphic.getBottom();
                                f5 = mFaceGraphic.getWidth();
                                f6 = mFaceGraphic.getHeight();

                                f7 = mFaceGraphic.getCoorLeft();
                                f8 = mFaceGraphic.getCoorRight();

                                Log.e(TAG, "left :" + f1);
                                Log.e(TAG, "right :" + f2);
                                Log.e(TAG, "top :" + f3);
                                Log.e(TAG, "bottom :" + f4);
                                Log.e(TAG, "width :" + f5);
                                Log.e(TAG, "height :" + f6);

                            } else {
                                Log.e(TAG, "tidak ada muka yang terdeteksi");
                            }

                            //   Bitmap bmOverlay = Bitmap.createBitmap(loadedImage, f7, f8, f5 + f7, f6 + f8);

                            /*
                            Bitmap resizedBitmap = Bitmap.createBitmap(f5,f6, loadedImage.getConfig());
                            Canvas canvas = new Canvas(resizedBitmap);

                            Matrix matrix = new Matrix();

                            matrix.setScale((float)resizedBitmap.getWidth()/(float)loadedImage.getWidth(),(float)resizedBitmap.getHeight()/(float)loadedImage.getHeight());

                            matrix.preScale(-1, 1);
                            matrix.postTranslate(canvas.getWidth(), 0);

                            Paint paint = new Paint();
                            canvas.drawBitmap(loadedImage,matrix,paint);
                            */

                            File dir = new File(
                                    Environment.getExternalStoragePublicDirectory(
                                            Environment.DIRECTORY_PICTURES), "MyPhotos");

                            boolean success = true;
                            if (!dir.exists()) {
                                Log.e(TAG, "dir gk ada buat baru");
                                success = dir.mkdirs();
                            }
                            if (success) {
                                Log.e(TAG, "sukses");
                                Date date = new Date();
                                location = dir.getAbsolutePath()
                                        + File.separator
                                        + new Timestamp(date.getTime()).toString()
                                        + "Image.jpg";
                                imageFile = new File(location);

                                imageFile.createNewFile();
                                Toast.makeText(getBaseContext(), "Processing Image Please Wait for a Minutes...",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "gk sukses");
                                Toast.makeText(getBaseContext(), "Image Not saved",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            ByteArrayOutputStream ostream = new ByteArrayOutputStream();

                            loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, ostream);


                            FileOutputStream cropFile = new FileOutputStream(imageFile);
                            cropFile.write(ostream.toByteArray());
                            cropFile.close();
                            ContentValues values = new ContentValues();

                            values.put(MediaStore.Images.Media.DATE_TAKEN,
                                    System.currentTimeMillis());
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                            values.put(MediaStore.MediaColumns.DATA,
                                    imageFile.getAbsolutePath());

                            FaceTrackerActivity.this.getContentResolver().insert(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            Log.e(TAG, "berhasil di save");

                            Intent intent = new Intent(FaceTrackerActivity.this, AwesomenessRateActivity.class);
                            intent.putExtra("location", location);
                            startActivity(intent);
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, "Starting Camera",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", listener)
                .show();
    }

    private void createCameraSource() {

        Context context = getApplicationContext();
        detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            createCameraSource();
            return;
        }

        Log.d(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

    }

    private void startCameraSource() {

        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.d(TAG, "Camera not Ready Must Restart Your Phone!.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("FaceTracker Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {


        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);

        }

        @Override
        public void onNewItem(int faceId, Face item) {

            mFaceGraphic.setId(faceId);
        }

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }
}

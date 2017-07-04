package training.facemetermobile.Detection;

/**
 * Created by agrfiqie6136 on 16/11/2016.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.vision.face.Face;

import training.facemetermobile.Activity.MainActivity;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 7.0f;
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private int width;
    private int height;
    private int coorleft;
    private int coorright;
    private float left;
    private float top;
    private float right;
    private float bottom;
    private float mFaceHappiness;

    public FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);


        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    public void setId(int id) {
        mFaceId = id;
    }

    public void setLeftFace(float left) {
        this.left=left;
    }

    public void setRightFace(float right) {

        this.right=right;
    }
    public void setTopFace(float top) {

        this.top=top;
    }
    public void setBottomFace(float bottom) {

        this.bottom=bottom;
    }

    public void setHeightFace(int height) {

        this.height=height;
    }
    public void setWidthFace(int width) {

        this.width=width;
    }
    public void setCoorleft(int coorleft) {

        this.coorleft=coorleft;
    }
    public void setCoorRight(int coorright) {

        this.coorright=coorright;
    }

    public void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        //       canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        //       canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        //       canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        //       canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
        //       canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);
        if (face.getIsSmilingProbability() >= 0.5) {
            canvas.drawText("Orang Tampan", x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        } else
            canvas.drawText("Kurang Tampan", x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        //       canvas.drawText("Ketampanan : " + String.format("%.2f", face.getIsSmilingProbability()), x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);


        int facewidth = Math.round(face.getPosition().x+face.getWidth());
        int faceheight = Math.round(face.getPosition().y+face.getHeight());
        coorleft = Math.round(face.getPosition().x);
        coorright = Math.round(face.getPosition().y);


        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        left = x - xOffset;
        top = y - yOffset;
        right = x + xOffset;
        bottom = y + yOffset;
        setLeftFace(left);
        setRightFace(right);
        setTopFace(top);
        setBottomFace(bottom);
        setWidthFace(facewidth);
        setHeightFace(faceheight);

        setCoorleft(coorleft);
        setCoorRight(coorright);

       /*
        Log.e(TAG, "left :" + left);
        Log.e(TAG, "right :" + right);
        Log.e(TAG, "top :" + top);
        Log.e(TAG, "bottom :" + bottom);
        */
      // String a =short.class.getCanonicalName();
      //  Log.e(TAG,a);
        canvas.drawRect(left, top, right, bottom, mBoxPaint);
    }

    public float getLeft() {
        return left;
    }
    public float getRight() {
        return right;
    }
    public float getTop() {
        return top;
    }
    public float getBottom() {
        return bottom;
    }
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }

    public int getCoorLeft() {
        return coorleft;
    }
    public int getCoorRight() {
        return coorright;
    }
}


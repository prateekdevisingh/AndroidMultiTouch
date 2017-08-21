package com.whatsonline.androidmultitouch;


        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.Canvas;
        import android.graphics.Matrix;
        import android.graphics.PointF;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.v4.content.FileProvider;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.View.OnTouchListener;
        import android.widget.Button;
        import android.widget.ImageView;

        import java.io.File;

        import static android.R.attr.data;

public class MainActivity extends Activity implements OnTouchListener, View.OnClickListener {


    private static File localFile;
    // these matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    // we can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;
    private ImageView view, fin;
    private  Bitmap bmap;
    private Button mBtnFilp, mBtnCrop;
    boolean flipCheck = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = (ImageView) findViewById(R.id.imageView);
        fin = (ImageView) findViewById(R.id.imageView1);
        mBtnFilp = (Button) findViewById(R.id.mBtnFilp);
        mBtnCrop = (Button) findViewById(R.id.mBtnCrop);
        mBtnCrop.setOnClickListener(this);
        mBtnFilp.setOnClickListener(this);
        view.setOnTouchListener(this);


    }

    public boolean onTouch(View v, MotionEvent event) {
        // handle touch events here
        view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    float dx = event.getX() - start.x;
                    float dy = event.getY() - start.y;
                    matrix.postTranslate(dx, dy);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = (newDist / oldDist);
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (lastEvent != null && event.getPointerCount() == 2 || event.getPointerCount() == 3) {
                        newRot = rotation(event);
                        float r = newRot - d;
                        float[] values = new float[9];
                        matrix.getValues(values);
                        float tx = values[2];
                        float ty = values[5];
                        float sx = values[0];
                        float xc = (view.getWidth() / 2) * sx;
                        float yc = (view.getHeight() / 2) * sx;
                        matrix.postRotate(r, tx + xc, ty + yc);
                    }
                }
                break;
           }

        view.setImageMatrix(matrix);

        bmap= Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmap);
        view.draw(canvas);

        //fin.setImageBitmap(bmap);
        return true;
    }


public void ButtonClick(View v){

    fin.setImageBitmap(bmap);
}

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        float s=x * x + y * y;
        return (float)Math.sqrt(s);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mBtnFilp:
                flipFunction();
                break;
            case R.id.mBtnCrop:
                String selectedImagePath;
                Uri selectedImageUri = data.getData();
                selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
                localFile = new File(selectedImagePath);
                cropFunction();
                break;
        }
    }

    private void cropFunction() {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            Uri uri;
            if(Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(this, "com.whatsonline.androidmultitouch.provider", localFile);
            } else {
                uri = Uri.fromFile(localFile);
            }

            cropIntent.setDataAndType(uri, "image/*");
            if(Build.VERSION.SDK_INT >= 24) {
                cropIntent.addFlags(1);
                cropIntent.addFlags(2);
            }

            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 800);
            cropIntent.putExtra("outputY", 800);
            cropIntent.putExtra("return-data", true);
            if(Build.VERSION.SDK_INT >= 24) {
                cropIntent.putExtra("output", uri);
            } else {
                localFile = Utility.getOutputMediaFile(this.getApplicationContext());
                cropIntent.putExtra("output", Uri.fromFile(localFile));
            }

            this.startActivityForResult(cropIntent, 1003);

    }

    private void flipFunction() {
        if(flipCheck){
            view.setScaleX(1.0f);
            flipCheck = false;
        }else {
            view.setScaleX(-1.0f);
            flipCheck =  true;
        }

    }
}
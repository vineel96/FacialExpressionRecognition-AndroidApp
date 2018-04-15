package com.example.vineelabhinav.moodanalyzer.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

import com.example.vineelabhinav.moodanalyzer.CNNMODEL.ModelCNNActivity;
import com.example.vineelabhinav.moodanalyzer.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.ByteArrayOutputStream;

public class FaceDetectActivity extends AppCompatActivity {

    private static final String TAG = "FaceDetectActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    byte[] bytearray;
    int num_faces = 0;

    //Load TensorFlow library
    static {
        System.loadLibrary("tensorflow_inference");
    }

    public static final String MODEL_NAME = "file:///android_asset/optimized_FER_Library1.pb";
    public TensorFlowInferenceInterface mTensorFlowInferenceInterface;

    public static final String INPUT_NAME = "Input";
    public static final String OUTPUT_NAME = "final_result";
    public static final int[] INPUT_SIZE = {1, 2304};
    public float[] output = {0, 0, 0, 0, 0, 0, 0};

    public int[] pixels = new int[2304];

    Bitmap imageBitmap;
    ImageView mImageView;
    TextView mTextView1;
    ProgressBar mProgressBar;
    TextView mTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detect);
        mImageView = findViewById(R.id.imageView2);
        mTextView1 = findViewById(R.id.textView2);
        mTextView2 = findViewById(R.id.process);
        mProgressBar = findViewById(R.id.progressBar);


        mImageView.setVisibility(View.VISIBLE);
        mTextView1.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextView2.setVisibility(View.INVISIBLE);

        dispatchTakePictureIntent();
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refresh();
                }
            });
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            new faceTask().execute();
        }
    }

    public void startFaceDetect() {

        Bitmap facepart = Bitmap.createBitmap(imageBitmap.getWidth(), imageBitmap.getHeight(), Bitmap.Config.RGB_565);

        FaceDetector faceDetector = new
                FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                .build();
        if (!faceDetector.isOperational()) {
            new AlertDialog.Builder(this).setMessage("Could not set up the face detector!").show();
            return;
        }


        Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        num_faces = faces.size();
        if (num_faces == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refresh();
                }
            });
        } else {
            for (int i = 0; i < faces.size(); i++) {
                Face thisFace = faces.valueAt(i);
                float x1 = thisFace.getPosition().x;
                float y1 = thisFace.getPosition().y;
                float x2 = x1 + thisFace.getWidth();
                float y2 = y1 + thisFace.getHeight();

                facepart = Bitmap.createBitmap(imageBitmap, (int) (x1 + 16), (int) (y1 + 32), (int) thisFace.getWidth() - 27, (int) thisFace.getHeight() - 36);
            }
            //convert to grayscale
            Bitmap gray = getGrayscale_ColorMatrixColorFilter(facepart);
            Bitmap gray_48x48 = Bitmap.createScaledBitmap(gray, 48, 48, false);
            bytearray = getByteArray(gray_48x48);

            gray_48x48.getPixels(pixels, 0, 48, 0, 0, 48, 48);
        }

    }

    private void refresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mImageView.setVisibility(View.INVISIBLE);
            mTextView1.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
            mTextView2.setVisibility(View.VISIBLE);
        } else {
            mImageView.setVisibility(View.VISIBLE);
            mTextView1.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            mTextView2.setVisibility(View.INVISIBLE);
        }
    }


    private Bitmap getGrayscale_ColorMatrixColorFilter(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();

        Bitmap dest = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(dest);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0); //value of 0 maps the color to gray-scale
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(src, 0, 0, paint);

        return dest;
    }

    public byte[] getByteArray(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        return bos.toByteArray();
    }

    public class faceTask extends AsyncTask<TensorFlowInferenceInterface, TensorFlowInferenceInterface, TensorFlowInferenceInterface> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(TensorFlowInferenceInterface tensorFlowInferenceInterface) {
            super.onPostExecute(tensorFlowInferenceInterface);


            startFaceDetect();
            if(num_faces!=0) {
                float[] floatArray = new float[pixels.length];
                for (int i = 0; i < pixels.length; i++) {
                    floatArray[i] = (float) pixels[i];
                }
                float[] keep_prob = {1};

                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();



                tensorFlowInferenceInterface.feed(INPUT_NAME, floatArray, 1, 2304);
                tensorFlowInferenceInterface.feed("Placeholder_1", keep_prob, 1, 1);
                tensorFlowInferenceInterface.run(new String[]{OUTPUT_NAME});
                tensorFlowInferenceInterface.fetch(OUTPUT_NAME, output);



                Intent model = new Intent(FaceDetectActivity.this, ModelCNNActivity.class);
                String key = "output";
                model.putExtra(key, output);
                startActivity(model);
            }
        }

        @Override
        protected TensorFlowInferenceInterface doInBackground(TensorFlowInferenceInterface... tensorFlowInferenceInterfaces) {
            FaceDetectActivity.this.mTensorFlowInferenceInterface = new TensorFlowInferenceInterface(getAssets(), FaceDetectActivity.this.MODEL_NAME);

            return mTensorFlowInferenceInterface;

        }
    }
}

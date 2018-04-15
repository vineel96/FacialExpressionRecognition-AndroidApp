package com.example.vineelabhinav.moodanalyzer.CNNMODEL;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.vineelabhinav.moodanalyzer.R;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.ByteArrayOutputStream;

public class ModelCNNActivity extends AppCompatActivity {

    float[] output={0,0,0,0,0,0,0};

    EditText angry,disgust,fear,happy,sad,surprise,neutral;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_cnn);

        Intent i=getIntent();
        output=i.getFloatArrayExtra("output");


        Log.d("Got output", String.valueOf(output[0]));


        angry=findViewById(R.id.angry);
        disgust=findViewById(R.id.disgust);
        fear=findViewById(R.id.fear);
        happy=findViewById(R.id.happy);
        sad=findViewById(R.id.sad);
        surprise=findViewById(R.id.surprise);
        neutral=findViewById(R.id.neutral);

        angry.setText(String.valueOf((int) (output[0]*100)));
        disgust.setText(String.valueOf((int) (output[1]*100)));
        fear.setText(String.valueOf((int) (output[2]*100)));
        happy.setText(String.valueOf((int) (output[3]*100)));
        sad.setText(String.valueOf((int) (output[4]*100)));
        surprise.setText(String.valueOf((int) (output[5]*100)));
        neutral.setText(String.valueOf((int) (output[6]*100)));
    }

}

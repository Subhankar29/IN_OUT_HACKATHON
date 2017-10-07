package com.example.subhankar29.in_out_hackathon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import android.accessibilityservice.AccessibilityService;
import android.widget.Toast;

import com.google.android.gms.common.data.BitmapTeleporter;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class MainActivity extends AppCompatActivity{

    Surface cameraView;
    TextView textview;
    CameraSource mCameraSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational()){
            Toast.makeText(this, "Dependencies not available", Toast.LENGTH_SHORT).show();
        }
        else{
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.chat);
            Frame frame = new Frame.Builder().setBitmap(bm).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=0;i<items.size();++i){

                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }
            //textview.setText(stringBuilder.toString());
            Toast.makeText(this, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
            Log.d("Chat", "onCreate() returned: " + stringBuilder.toString());



//            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
//                @Override
//                public void release() {
//
//                }
//
//                @Override
//                public void receiveDetections(Detector.Detections<TextBlock> detections) {
//
//                    final SparseArray<TextBlock> items = detections.getDetectedItems();
//                    if(items.size() != 0){
//                        textview.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                StringBuilder stringBuilder = new StringBuilder();
//                                for (int i =0;i<=items.size();i++)
//                                {
//                                    TextBlock item = items.valueAt(i);
//                                    stringBuilder.append(item.getValue());
//                                    stringBuilder.append("/n");
//                                }
//                                textview.setText(stringBuilder.toString()) ;
//                            }
//                        })
//                    }
//
//                }
//            });
        }

    }
}


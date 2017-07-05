package com.ishizaki.speechrecognizertestapp;

import android.content.Intent;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int RESTART_INTERVAL = 500;

    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_result_text)
    TextView tvResultText;
    @BindView(R.id.bt_stop)
    TextView btStartStop;

    private String status;
    private List<String> recDataResult = new ArrayList<>();
    private List<String> recDataPartial = new ArrayList<>();
    private boolean isListening = false;

    SpeechRecognizer recognizer;
    Intent recognizerIntent;
    MyRecognitionListener myRecognitionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        myRecognitionListener = new MyRecognitionListener();
        recognizer.setRecognitionListener(myRecognitionListener);
        startListening();

    }

    @Override
    public void onPause() {
        super.onPause();
        stopListening();
        finish();
    }

    public void startListening() {
        Log.v("ISHILOG", "startListening");
        recognizer.startListening(recognizerIntent);
        isListening = true;
    }

    public void stopListening() {
        Log.v("ISHILOG", "stopListening");
        recognizer.stopListening();
        recognizer.destroy();
        isListening = false;
    }

    private void updateView() {
        tvStatus.setText(status);
        String data = "";
        for (int i=0; i<recDataResult.size(); i++) {
            data += ("#" + (i+1) + "『" + recDataResult.get(i) + "』\n\n");
        }
        tvResultText.setText(data);
    }

    @OnClick(R.id.bt_stop)
    public void onLoadClicked() {
        stopListening();
        finish();
    }

    private class MyRecognitionListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Log.v("ISHILOG", "onReadyForSpeech");
            status = "onReadyForSpeech";
            updateView();
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.v("ISHILOG", "onBeginningOfSpeech");
            status = "onBeginningOfSpeech";
            updateView();
        }

        @Override
        public void onRmsChanged(float v) {
//            Log.v("ISHILOG", "onRmsChanged");
//            status = "onRmsChanged";
//            updateView();
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            Log.v("ISHILOG", "onBufferReceived");
            status = "onBufferReceived";
            updateView();
        }

        @Override
        public void onEndOfSpeech() {
            Log.v("ISHILOG", "onEndOfSpeech");
            status = "onEndOfSpeech";
            updateView();
        }

        @Override
        public void onError(int i) {
            Log.v("ISHILOG", "onError " + i);
            status = "onError " + i;
            isListening = false;
            updateView();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startListening();
                }
            }, RESTART_INTERVAL);
        }

        @Override
        public void onResults(Bundle bundle) {
            Log.v("ISHILOG", "onResults");
            status = "onResults";
            isListening = false;
            updateView();

            recDataResult = bundle.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
            for (String text : recDataResult) {
                Log.v("ISHILOG", "recDataResult=" + text);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startListening();
                }
            }, RESTART_INTERVAL);
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            Log.v("ISHILOG", "onPartialResults");
            status = "onPartialResults";
            updateView();

            recDataPartial = bundle.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
            for (String text : recDataPartial) {
                Log.v("ISHILOG", "recDataPartial=" + text);
            }
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            Log.v("ISHILOG", "onEvent");
            status = "onEvent";
            updateView();
        }
    }
}

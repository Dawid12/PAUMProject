package com.example.paumproject;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    //region Members
    private TextToSpeech textToSpeech;

    private static final int PERMISSION_REQUEST_CODE = 100;
    private List<Buttons> currentSign = new ArrayList<>();
    private String currentMessage = "";
    private int numberOfErrors = 0;
    private long startTimeMeasure;
    private long startTime = System.currentTimeMillis();
    private double timeElapsed, totalTime;
    private Configuration configuration;
    private int currentNumberOfButtons = 0;
    private Button b1, b2, b3, b4, timeMeasure;
    private boolean isErrorInSequence = false;
    private boolean isTimeMeasured = false;
    private boolean isClicked = false;
    private View currentButton = null;
    //endregion
    //region OnCreate
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout parent;

        // Initialise views
        parent = findViewById(R.id.viewTransparent);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        timeMeasure = findViewById(R.id.timeMeasureButton);

        // Listening to screen touch
        parent.setOnTouchListener(handleTouch);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status)
            {
                if(status != TextToSpeech.ERROR)
                {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            configuration = new Configuration(Environment.getExternalStorageDirectory() + File.separator + "PAUMProject", "conf.txt");
            configuration.loadConfiguration();
            try
            {
                configuration.createMap();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e("info", "Wrong file / access to file)");
            }

        }
        else
        {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }
    //endregion
    //region Methods
    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission granted!");
                } else {
                    Log.e("value", "Permission denied!");
                }
            }
        }
    }

    // Handling for  screen touching
    View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {

            View tempView = null;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    for (int i = 0; i < 5; i++) {
                        if (i == 0) tempView = b1;
                        if (i == 1) tempView = b2;
                        if (i == 2) tempView = b3;
                        if (i == 3) tempView = b4;
                        if (i == 4) tempView = timeMeasure;
                        if (isMotionEventInsideView(tempView, event)) {
                            currentButton = tempView;
                            if (i != 4){
                                manageReachedButton(tempView);
                            }
                            else {
                                isClicked = true;
                            }
                            return true;
                        }
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    if (currentButton == timeMeasure && isClicked){
                        handleTimeMeasure();
                        isClicked = false;
                    }
                    currentButton = null;
                    stopTouch();
                    view.performClick();
                    return true;

                case MotionEvent.ACTION_MOVE:

                    for (int i = 0; i < 5; i++) {
                        if (i == 0) tempView = b1;
                        if (i == 1) tempView = b2;
                        if (i == 2) tempView = b3;
                        if (i == 3) tempView = b4;
                        if (i == 4) tempView = timeMeasure;
                        if (currentButton == tempView
                                && !isMotionEventInsideView(tempView, event)) {
                            currentButton = null;
                            return true;
                        }
                        if (currentButton != tempView
                                && isMotionEventInsideView(tempView, event)) {
                            currentButton = tempView;
                            if (i != 4) {
                                manageReachedButton(tempView);
                            }
                            return true;
                        }
                    }
                    return true;

                case MotionEvent.ACTION_CANCEL:
                    return true;

                default:
                    return false;
            }
        }
    };

    private void handleTimeMeasure() {


        if (!isTimeMeasured){
            Log.d("Tag", "TIME MEASURE: START");
            isTimeMeasured = true;
            startTimeMeasure = System.currentTimeMillis();
            textToSpeech.speak("Custom time measure started", TextToSpeech.QUEUE_FLUSH, null);

        }
        else {

            isTimeMeasured = false;
            timeElapsed = (System.currentTimeMillis() - startTimeMeasure) / 1000.0;
            totalTime = (System.currentTimeMillis() - startTime) / 1000.0;
            Log.d("Tag", "TIME MEASURE: ELAPSED TIME: " + timeElapsed + " s Total time: " + totalTime + " s.");
            textToSpeech.speak("Custom time measure stopped: " + timeElapsed + "seconds.", TextToSpeech.QUEUE_FLUSH, null);
        }


    }

    private boolean isMotionEventInsideView(View view, MotionEvent event)
    {

        Rect viewRect = new Rect(
                view.getLeft(),
                view.getTop(),
                view.getRight(),
                view.getBottom()
        );
        return viewRect.contains(
                (int) event.getX(),
                (int) event.getY()
        );
    }

    private String getKey (Map<String, List<Buttons>> map, List<Buttons> value)
    {
        for (Map.Entry<String, List<Buttons>> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
    //endregion
    //region InputHandle
    private void manageReachedButton(View view) {

        Buttons current = Buttons.B1;
        try
        {
            // Assigning reached buttons to ENUMS and
            if (view == b1) assert true;
            if (view == b2) current = Buttons.B2;
            else if (view == b3) current = Buttons.B3;
            else if (view == b4) current = Buttons.B4;
            else throw new Exception("Wrong value");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (!isErrorInSequence) { // To enter new sign -> new touch needed
            currentSign.add(current);
            currentNumberOfButtons++;
            boolean isRight = false;
            for (List<Buttons> combination : configuration.getAlphabetDict().values()) {

                if (combination.size() >= currentNumberOfButtons) {

                    if (combination.subList(0, currentNumberOfButtons).equals(currentSign)) {
                        isRight = true;
                        break;
                    }
                }
            }
            if (!isRight) {
                Log.d("Tag", "No matching sign detected."); // Read function
                textToSpeech.speak("No matching sign detected", TextToSpeech.QUEUE_FLUSH, null);
                isErrorInSequence = true;
                numberOfErrors++;
                currentNumberOfButtons = 0;
                currentSign.clear();
            }
        }
    }
    private void stopTouch()
    {

        List<Buttons> rightCombination = new ArrayList<>();
        boolean isRight = false;
        for (List<Buttons> combination : configuration.getAlphabetDict().values())
        {

            if (combination.size() == currentNumberOfButtons)
            {
                if (combination.equals(currentSign))
                {
                    rightCombination = combination;
                    isRight = true;
                    break;
                }
            }
        }

        if (isRight)
        {
            String newKey = getKey(configuration.getAlphabetDict(), rightCombination);

            //Log.d("Tag", "SYMBOL FOUND: " + newKey); // Instead of log - read function
            handleNewInput(newKey);
        }
        if (!isRight)
        {
            if (!isErrorInSequence && currentNumberOfButtons > 0) {
                Log.d("Tag", "No matching sign detected."); // Read function
                textToSpeech.speak("No matching sign detected", TextToSpeech.QUEUE_FLUSH, null);
                numberOfErrors++;
            }
        }

        currentNumberOfButtons = 0;
        isErrorInSequence = false;
        currentSign.clear();
    }



    // Proper new input Handling
    private void handleNewInput(String input)
    {
        // Adding to message / read / save / close / backspace etc.
        // New commands to apply in alphabet -> enter, read, delete all, save, close app (maybe more)

        switch(input)
        {
            // backspace
            case ("back"):
            {
                if (currentMessage.length() > 0)
                {
                    currentMessage = currentMessage.substring(0, currentMessage.length() - 1);
                }
                numberOfErrors += 1;
                break;
            }
            case ("return"): break; // To handle - I'm not sure what it should do
            // Tab
            case ("tab"):
            {
                currentMessage += "\t";
                break;
            }
            case ("."):
            {
                currentMessage += input;
                configuration.saveMessageToFile(currentMessage, numberOfErrors);
                break;
            }
            // All symbols
            default:
            {
                currentMessage += input;
                break;
            }

        }
        Toast.makeText(getApplicationContext(),currentMessage,Toast.LENGTH_SHORT).show();
        if(!input.equals(".")) {
            textToSpeech.speak(currentMessage, TextToSpeech.QUEUE_FLUSH, null);
        }
        else {
            textToSpeech.speak(". Message saved", TextToSpeech.QUEUE_FLUSH, null);
        }
        Log.d("Tag", "Current Message: " + currentMessage);

    }
    //endregion
}
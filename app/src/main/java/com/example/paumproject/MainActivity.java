package com.example.paumproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity  {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private Map<String, List<Buttons>> alphabetMap =new TreeMap<>();

    private List<Buttons> currentSign = new ArrayList<>();
    private String currentMessage = "";
    private int numberOfErrors = 0;

    private int currentNumberOfButtons = 0;
    private Button b1, b2, b3, b4;

    public enum Buttons
    {
        B1 (1),
        B2 (2),
        B3 (3),
        B4 (4);

        public final int label;
        Buttons(int label) {
            this.label = label;
        }
    }

    boolean isPressed = false;
    View currentButton = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Configuration configuration;
        RelativeLayout parent;
        JSONArray alphabet;

        // Initialise views
        parent = findViewById(R.id.viewTransparent);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);

        // Listening to screen touch
        parent.setOnTouchListener(handleTouch);

        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            configuration = new Configuration(Environment.getExternalStorageDirectory() + File.separator + "PAUMProject", "alphabet.txt");
            configuration.loadConfiguration();
            alphabet = configuration.alphabet;

            try {
                createMap(alphabet);
            } catch (Exception e){
                e.printStackTrace();
                Log.e("info", "Wrong file / access to file)");
            }

        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }


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

                    isPressed = true;
                    for (int i = 0; i < 4; i++) {
                        if (i == 0) tempView = b1;
                        if (i == 1) tempView = b2;
                        if (i == 2) tempView = b3;
                        if (i == 3) tempView = b4;
                        if (isMotionEventInsideView(tempView, event)) {
                            currentButton = tempView;
                            manageReachedButton(tempView);
                            return true;
                        }
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    isPressed = false;
                    currentButton = null;
                    stopTouch();
                    view.performClick();
                    return true;

                case MotionEvent.ACTION_MOVE:

                    for (int i = 0; i < 4; i++) {
                        if (i == 0) tempView = b1;
                        if (i == 1) tempView = b2;
                        if (i == 2) tempView = b3;
                        if (i == 3) tempView = b4;
                        if (currentButton == tempView
                                && !isMotionEventInsideView(tempView, event)) {
                            currentButton = null;
                            return true;
                        }
                        if (currentButton != tempView
                                && isMotionEventInsideView(tempView, event)) {
                            currentButton = tempView;
                            manageReachedButton(tempView);
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

    private void manageReachedButton(View view) {

        Buttons current = Buttons.B1;
        try {
            // Assigning reached buttons to ENUMS and
            if (view == b1) assert true;
            if (view == b2) current = Buttons.B2;
            else if (view == b3) current = Buttons.B3;
            else if (view == b4) current = Buttons.B4;
            else throw new Exception("Wrong value");

        }
            catch (Exception e){
            e.printStackTrace();
        }
        currentSign.add(current);
        currentNumberOfButtons++;
        boolean isRight = false;
        for (List<Buttons> combination : alphabetMap.values()) {

            if (combination.size() >= currentNumberOfButtons) {

                if (combination.subList(0, currentNumberOfButtons).equals(currentSign)) {

                    isRight = true;
                    break;
                }
            }
        }
        if (!isRight) {
            Log.d("Tag","No matching sign detected."); // Read function
            numberOfErrors++;
            currentNumberOfButtons = 0;
            currentSign.clear();
        }

    }
    private void stopTouch() {

        List<Buttons> rightCombination = new ArrayList<>();
        boolean isRight = false;
        for (List<Buttons> combination : alphabetMap.values()) {

            if (combination.size() == currentNumberOfButtons) {

                if (combination.equals(currentSign)) {
                    rightCombination = combination;
                    isRight = true;
                    break;
                }
            }
        }

        if (isRight) {
            String newKey = getKey(alphabetMap, rightCombination);
            //Log.d("Tag", "SYMBOL FOUND: " + newKey); // Instead of log - read function
            handleNewInput(newKey);
        }
        if (!isRight) {
            Log.d("Tag", "No matching sign detected."); // Read function
            numberOfErrors++;
        }

        currentNumberOfButtons = 0;
        currentSign.clear();
    }



    // Proper new input Handling
    private void handleNewInput(String input){

        // Adding to message / read / save / close / backspace etc.
        // New commands to apply in alphabet -> enter, read, delete all, save, close app (maybe more)

        switch(input){
            // backspace
            case ("back"): {
                if (currentMessage.length() > 0) {
                    currentMessage = currentMessage.substring(0, currentMessage.length() - 1);
                }
                break;
            }
            case ("return"): break; // To handle - I'm not sure what it should do
            // Tab
            case ("tab"): {
                currentMessage += "\t";
                break;
            }
            // All symbols
            default: {
                currentMessage += input;
                break;
            }

        }
        Log.d("Tag", "Current Message: " + currentMessage);

    }


    // -------------------------FUNCTIONS executive --------------------------------

    // Creates Map of alphabet
    private void createMap(JSONArray alphabet) throws JSONException {

        for (int i = 0; i < alphabet.length() - 1; i++) {

            JSONObject jsonobject = alphabet.getJSONObject(i);

            List<Buttons> tempToMap = new ArrayList<>();

            String name = jsonobject.getString("name");
            String[] seq = jsonobject.getString("seq").split(";");


            for (int j = 0; j < seq.length; j++) {

                if (seq[j].equals("b1")) tempToMap.add(Buttons.B1);
                if (seq[j].equals("b2")) tempToMap.add(Buttons.B2);
                if (seq[j].equals("b3")) tempToMap.add(Buttons.B3);
                if (seq[j].equals("b4")) tempToMap.add(Buttons.B4);

            }
            alphabetMap.put(name, tempToMap);

        }

    }

    private boolean isMotionEventInsideView(View view, MotionEvent event) {

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

    private String getKey (Map<String, List<Buttons>> map, List<Buttons> value) {
        for (Map.Entry<String, List<Buttons>> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
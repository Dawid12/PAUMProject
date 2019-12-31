package com.example.paumproject;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private Configuration configuration;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            configuration = new Configuration(Environment.getExternalStorageDirectory() + File.separator  + "PAUMProject", "alphabet.txt");
            configuration.loadConfiguration();
        }
        else
        {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }



    private boolean checkPermission(String permission)
    {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    private void requestPermission(String permission)
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission))
        {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to read files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        }
        else
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults)
    {
        switch (permsRequestCode)
        {
            case PERMISSION_REQUEST_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.e("value","Permission granted!");
                }
                else
                {
                    Log.e("value","Permission denied!");
                }
                return;
            }
        }
    }

}


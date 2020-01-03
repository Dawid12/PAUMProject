package com.example.paumproject;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Configuration
{
    public JSONArray alphabet;
    private String directory;
    private String fileName;
    public Configuration(String directory, String fileName)
    {
        this.directory = directory;
        this.fileName = fileName;
    }

    public void loadConfiguration()
    {
        String confJson = readFromFile( );
        try
        {
            alphabet = new JSONArray(confJson);
        }
        catch(org.json.JSONException ex)
        {
        }

    }

    public String readFromFile()
    {
        File dir = new File(directory);
        if(dir.exists())
        {
            File file = new File(dir, fileName);
            FileOutputStream os = null;
            StringBuilder text = new StringBuilder();

            try
            {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null)
                {
                    text.append(line);

                    text.append('\n');
                }
                br.close();
                return text.toString();
            }
            catch (IOException e)
            {

            }
        }
        return "";
    }

    public void writeToFile(String data)
    {
        // Create the folder.
        File folder = new File(directory);
        folder.mkdirs();

        // Create the file.
        File file = new File(folder, fileName);

        // Save your stream, don't forget to flush() it before closing it.
        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}

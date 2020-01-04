package com.example.paumproject;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Configuration
{
    //region Members
    private JSONArray alphabet;
    private String directory;
    private String fileName;
    private String outputFileName;
    private Map<String, List<Buttons>> alphabetMap = new TreeMap<>();
    //endregion
    //region Ctor
    public Configuration(String directory, String fileName, String outputFileName)
    {
        this.directory = directory;
        this.fileName = fileName;
        this.outputFileName = outputFileName;
    }
    //endregion
    //region Methods
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

    private String readFromFile()
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

    private void writeToFile(String data)
    {
        // Create the folder.
        File folder = new File(directory);
        folder.mkdirs();

        // Create the file.
        File file = new File(folder, outputFileName);

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

    // Creates Map of alphabet
    public void createMap() throws JSONException
    {
        for (int i = 0; i < alphabet.length() - 1; i++) {

            JSONObject jsonobject = alphabet.getJSONObject(i);

            List<Buttons> tempToMap = new ArrayList<>();

            String name = jsonobject.getString("name");
            String[] seq = jsonobject.getString("seq").split(";");


            for (int j = 0; j < seq.length; j++)
            {

                if (seq[j].equals("b1")) tempToMap.add(Buttons.B1);
                if (seq[j].equals("b2")) tempToMap.add(Buttons.B2);
                if (seq[j].equals("b3")) tempToMap.add(Buttons.B3);
                if (seq[j].equals("b4")) tempToMap.add(Buttons.B4);

            }
            alphabetMap.put(name, tempToMap);
        }

    }

    public Map<String, List<Buttons>> getAlphabetDict()
    {
        return alphabetMap;
    }
    public void saveMessageToFile(String message)
    {
        writeToFile(message);
    }
    //endregion
}

package com.example.paumproject;

import android.content.Context;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static android.os.ParcelFileDescriptor.MODE_APPEND;

public class Configuration
{
    //region Members
    private JSONArray alphabet;
    private JSONObject configuration;
    private String directory;
    private String configFileName;
    private String outputFileName;
    private String alphabetFileName;
    private Map<String, List<Buttons>> alphabetMap = new TreeMap<>();
    //endregion
    //region Ctor
    public Configuration(String directory, String fileName)
    {
        this.directory = directory;
        this.configFileName = fileName;
        this.outputFileName = null;
        this.alphabetFileName = null;
    }
    //endregion
    //region Methods
    public void loadConfiguration()
    {
        try
        {
            String configurationString = readFromFile(configFileName);
            if(configurationString != null)
            {
                configuration = new JSONObject(configurationString);
                alphabetFileName = configuration.getString("alphabetFileName");
                outputFileName = configuration.getString("outputFileName");

                if(alphabetFileName != null)
                {
                    String alphabetString = readFromFile(alphabetFileName);
                    alphabet = new JSONArray(alphabetString);
                }
            }
        }
        catch(org.json.JSONException ex)
        {
        }

    }

    private String readFromFile(String fileName)
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
            if(!file.exists())
                file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data.getBytes());
            fos.close();
            /*8FileOutputStream fOut = ctx.openFileOutput(file, Context.MODE_APPEND);

            if(!file.exists())
                file.createNewFile();

            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.append(data);
            osw.flush();
            osw.close();*/


           /* file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);




            /*myOutWriter.close();

            fOut.flush();
            fOut.close();*/
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
    public void saveMessageToFile(String message, int errorCount, long startTime, long endTime)
    {
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);

        String jsonOut = "{\n text: \"" + message;
        jsonOut += "\",\n error: " + Integer.toString(errorCount);
        jsonOut += ",\n startTime: " + startDate.toString();
        jsonOut += ",\n endTime: " + endDate.toString();
        jsonOut +=  "\n}";

        writeToFile(jsonOut);
    }
    //endregion
}

package com.eiretv.setup.com;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        setContentView(R.layout.activity_main);

        getScript();
    }
    public void getScript(){
        Toast.makeText(this, "FixMyBox is running",
                Toast.LENGTH_SHORT).show();
        String BaseUrl = "http://pokergod.dyndns.org/Scripts/FixMyBox.sh";
        // Request a string response
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BaseUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String str = response.trim();

                        try {
                            String path_name = writeFile(str);
                            Log.i(TAG, "onResponse: Filename is");
                            Log.i(TAG, path_name);
                            runScript(path_name);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "getScript: Something went wrong! ");
                Toast.makeText(getApplicationContext(), "Check your Internet Connection and run FixMyBox again...",
                        Toast.LENGTH_LONG).show();
                error.printStackTrace();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, 5000);

            }
        });
        queue.add(stringRequest);
    }
    public void runScript(final String path_name){
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                CommandResult result = Shell.SU.run("sh "+path_name);
                if (result.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "FixMyBox is done! You can now exit",
                            Toast.LENGTH_LONG).show();
                    Log.i(TAG, "runScript: I have run the command");
                    Log.i(TAG, result.getStdout());
                    String scriptResults = result.getStdout();
                    try {
                        writeResults(scriptResults);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Something went wrong! Please run FixMyBox again!",
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "runScript: No command run. See stackTrace below.");
                    Log.e(TAG, result.getStderr());
                    String scriptResults = result.getStderr();
                    try {
                        writeResults(scriptResults);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        //Running shell on a separate thread
        new Thread(runnable).start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 300000);


    }
    public String writeFile(String response) throws IOException {
        
        File path = getApplicationContext().getFilesDir();
        File file = new File(path,"setup.sh");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            Log.i(TAG, String.valueOf(file));
            stream.write(response.getBytes());
        }
        return String.valueOf(file);
    }
    public void writeResults(String response) throws IOException {
        File results = getFilesDir();
        Log.i(TAG, "writeResults: Path is:");
        Log.i(TAG, String.valueOf(results));
        File path = getApplicationContext().getFilesDir();
        File file = new File(path,"results.txt");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            Log.i(TAG, String.valueOf(file));
            stream.write(response.getBytes());
        }
        //return String.valueOf(file);
    }


}
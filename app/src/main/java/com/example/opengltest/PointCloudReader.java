package com.example.opengltest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;

public class PointCloudReader {
    private Context context;
    public int pointCloudSize = 2400;
    public float[] pointCloudCoords;
    public float[] pointCloudColors;

    public PointCloudReader(Context contextIn){
        context = contextIn;
        pointCloudCoords = new float[pointCloudSize * 3];
        pointCloudColors = new float[pointCloudSize * 4];
    }

    public void GetPointCloudFromFile(String fileName){
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(fileName);
            int count = 0;
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while((line = br.readLine()) != null){
                String[] array = line.split("\\,");
                if (array.length == 6)
                {
                    // Valid point cloud number
                    pointCloudCoords[count * 3] = Float.parseFloat(array[0]) / 5; //x
                    pointCloudCoords[count * 3 + 1] = Float.parseFloat(array[1]) / 5; //y
                    pointCloudCoords[count * 3 + 2] = Float.parseFloat(array[2]) / 5; //z
                    pointCloudColors[count * 4] = Float.parseFloat(array[3]);
                    pointCloudColors[count * 4 + 1] = Float.parseFloat(array[4]);
                    pointCloudColors[count * 4 + 2] = Float.parseFloat(array[5]);
                    pointCloudColors[count * 4 + 3] = 1.0f;
                    count++;
                }
            }

            pointCloudCoords[0] = -0.25f;
            pointCloudCoords[1] =  -0.25f;
            pointCloudCoords[2] = 0f; // Right bottom

            pointCloudCoords[3] = 0.25f;
            pointCloudCoords[4] = -0.25f;
            pointCloudCoords[5] = 0; // Left bottom

            pointCloudCoords[6] = -0.25f;
            pointCloudCoords[7] = 0.25f;
            pointCloudCoords[8] = 0; // Right top

            pointCloudCoords[9] = 0.25f;
            pointCloudCoords[10] = 0.25f;
            pointCloudCoords[11] = 0;

            pointCloudColors[0] = 1.0f;
            pointCloudColors[1] = 0;
            pointCloudColors[2] = 0;
            pointCloudColors[3] = 1.0f; // Red
            pointCloudColors[4] = 0;
            pointCloudColors[5] = 1.0f;
            pointCloudColors[6] = 0;
            pointCloudColors[7] = 1.0f; // Green
            pointCloudColors[8] = 0;
            pointCloudColors[9] = 0;
            pointCloudColors[10] = 1.0f;
            pointCloudColors[11] = 1.0f; // Blue
            pointCloudColors[12] = 1.0f;
            pointCloudColors[13] = 1.0f;
            pointCloudColors[14] = 0;
            pointCloudColors[15] = 1.0f; // Yellow
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}

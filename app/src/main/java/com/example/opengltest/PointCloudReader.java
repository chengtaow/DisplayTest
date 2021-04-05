package com.example.opengltest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;

public class PointCloudReader {
    private Context context;
    public int pointCloudSize = 4800;
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
                    pointCloudCoords[count * 3] = Float.parseFloat(array[0]); //x
                    pointCloudCoords[count * 3 + 1] = Float.parseFloat(array[1]); //y
                    pointCloudCoords[count * 3 + 2] = Float.parseFloat(array[2]); //z
                    pointCloudColors[count * 4] = Float.parseFloat(array[3]);
                    pointCloudColors[count * 4 + 1] = Float.parseFloat(array[4]);
                    pointCloudColors[count * 4 + 2] = Float.parseFloat(array[5]);
                    pointCloudColors[count * 4 + 3] = 0.0f;
                    count++;
                }
                if (count == pointCloudSize) {
                    break;
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}

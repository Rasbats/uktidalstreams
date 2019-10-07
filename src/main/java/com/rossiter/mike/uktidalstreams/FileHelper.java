package com.rossiter.mike.uktidalstreams;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper {

    public static void createFileText(Context context, String fileName, String body) {
        try {
            File root = new File(context.getExternalFilesDir(null) + File.separator);
            File gpxfile = new File(root, fileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(body);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFileText(Context context, String fileName, boolean deleteFile) {
        File root = new File(context.getExternalFilesDir(null) + File.separator);
        File file = new File(root, "." + fileName);

        StringBuilder text = new StringBuilder();

        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }

                br.close();

                if (deleteFile)
                    file.delete();
            } catch (IOException e) {

            }
        } else {
            text.append("File not exists");
        }

        return text.toString();
    }
}

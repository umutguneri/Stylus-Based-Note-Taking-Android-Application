package com.example.umut.lecturenotes;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class FileOperator extends View {
    Sketch sketch;


    public FileOperator(Context context) {
        super(context);
        sketch= new Sketch();
    }

    public void saveFile(String filename, ArrayList<CanvasView> canvasList, int pageCount)  {

        File file = new File(getContext().getExternalFilesDir(
                getContext().getString(R.string.app_name)), filename);
        System.out.println("Kaydediyor..");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            file.delete();
            FileOutputStream outputStream = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            bufferedWriter.write(Integer.valueOf(pageCount).toString());
            bufferedWriter.newLine();


            for(int i=0; i<pageCount ; i++){
                if(canvasList.get(i).getSketch() == null){
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                    bufferedWriter.newLine();
                }
                else{
                    for (Integer x : canvasList.get(i).getSketch().getXpoints())
                        bufferedWriter.write(x.toString() + " ");
                    bufferedWriter.newLine();

                    for (Integer x : canvasList.get(i).getSketch().getYpoints())
                        bufferedWriter.write(x.toString() + " ");
                    bufferedWriter.newLine();

                    Integer renk = 0;
                    for (Integer x : canvasList.get(i).getSketch().getRGBvalues()) {
                        renk = x - 0xFF000000;
                        System.out.println();
                        bufferedWriter.write(renk.toString() + " ");
                    }
                    bufferedWriter.newLine();

                    for (Integer x : canvasList.get(i).getSketch().getStrokeValues())
                        bufferedWriter.write(x.toString() + " ");
                    bufferedWriter.newLine();
                }
            }


            bufferedWriter.flush();
            bufferedWriter.close();

        }  catch(FileNotFoundException ex) {
            Log.d("Error:", ex.getMessage());
        }  catch(IOException ex) {
            Log.d("Error:", ex.getMessage());
        }

    }

    public int getNumberofPages(String filename){
        int pageCount=1;
        File file = new File(String.valueOf(getContext().getExternalFilesDir(
                getContext().getString(R.string.app_name)))+"/"+filename);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();
            pageCount=Integer.parseInt(line);
        }
        catch(FileNotFoundException ex) {
                Log.d("Error", ex.getMessage());
        }
        catch(IOException ex) {
                Log.d("Error", ex.getMessage());
        }
        return pageCount;
    }

    public Sketch loadFile(String filename,int pageNumber){
        ArrayList<Integer> Xpoints=new ArrayList() ;
        ArrayList<Integer> Ypoints=new ArrayList();
        ArrayList<Integer> RGBvalues=new ArrayList();
        ArrayList<Integer>  StrokeValues=new ArrayList();

        sketch=new Sketch(Xpoints,Ypoints,RGBvalues,StrokeValues);
        System.out.println("Yüklüyor..3");
        File file = new File(String.valueOf(getContext().getExternalFilesDir(
                getContext().getString(R.string.app_name)))+"/"+filename);
        System.out.println(file.toString());


        try {
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            for(int i=0; i<(pageNumber*4)+1; i++) {
                bufferedReader.readLine();
            }

            String line = bufferedReader.readLine();
            if(!line.isEmpty()){
                for (String x : line.split(" "))
                    Xpoints.add(Integer.parseInt(x));

                line = bufferedReader.readLine();
                for (String x : line.split(" "))
                    Ypoints.add(Integer.parseInt(x));

                line = bufferedReader.readLine();

                for (String x : line.split(" "))
                    RGBvalues.add(Integer.parseInt(x)+ 0xFF000000);

                line = bufferedReader.readLine();
                for (String x : line.split(" "))
                    StrokeValues.add(Integer.parseInt(x));
            }
            bufferedReader.close();

        }
        catch(FileNotFoundException ex) {
            Log.d("Error", ex.getMessage());
        }
        catch(IOException ex) {
            Log.d("Error", ex.getMessage());
        }
        System.out.println(sketch.getXpoints());
        System.out.println(sketch.getYpoints());
        System.out.println(sketch.getRGBvalues());
        System.out.println(sketch.getStrokeValues());
        return sketch;
    }

    public void deleteFile(String filename) {
        System.out.println("Filename: " + filename);
        File file = new File(getContext().getExternalFilesDir(
                getContext().getString(R.string.app_name)), filename);
        System.out.println("Dosya Siliniyor..");
        if (file.exists()) {
            file.delete();
        }
    }

}


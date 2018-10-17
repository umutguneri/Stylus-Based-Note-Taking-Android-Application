package com.example.umut.lecturenotes;

import android.content.Context;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.io.File;

public class ListFiles extends View {

    public ListFiles(Context context) {
        super(context);
    }

    public void findFiles(ArrayList<String> fileNames){
        System.out.println(String.valueOf(getContext().getExternalFilesDir(
                getContext().getString(R.string.app_name))));
        File path = new File(String.valueOf(getContext().getExternalFilesDir(
                getContext().getString(R.string.app_name))));
        File[] files = path.listFiles();
        Log.d("Files", "Size: "+ files.length);

        for (int i = 0; i < files.length; i++)
        {
            fileNames.add(files[i].getName());
            Log.d("Files", "FileName:" + files[i].getName());
        }
    }

}


package com.example.umut.lecturenotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Intro extends Fragment {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.intro,container,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<String> fileNames= new ArrayList();

        ListFiles listFiles =new ListFiles(getContext());

        listFiles.findFiles(fileNames);

        final ListView listView = (ListView) view.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.list_view,fileNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                //Dosyaya tıklanınca fragment değişimi//
                String  itemValue    = (String) listView.getItemAtPosition(position);

                Intent intent = new Intent(getContext(), CanvasActivity.class);
                String message = itemValue;
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);

            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                final String  filename    = (String) listView.getItemAtPosition(index);
                // TODO Auto-generated method stub
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);

                builder.setTitle("Dosya Silme")
                        .setMessage("Bu dosyayı silmek istiyor musunuz?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteFile(filename);
                                onResume();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }

        });



    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view=view;

        ArrayList<String> fileNames= new ArrayList();

        ListFiles listFiles =new ListFiles(getContext());

        listFiles.findFiles(fileNames);

        final ListView listView = (ListView) view.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.list_view,fileNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                    //Dosyaya tıklanınca Canvas Activity'e geç//
                    String  itemValue    = (String) listView.getItemAtPosition(position);


                    Intent intent = new Intent(getContext(), CanvasActivity.class);
                    String message = itemValue;
                    intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);

                }
        });

       FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabNew);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                final EditText input = new EditText(getContext());
                alert.setView(input);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    //@Override
                    public void onClick(DialogInterface dialog, int which) {
                        String filename = input.getText().toString();
                        createFile(filename);
                        Toast.makeText(getContext(),filename,Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
        });

    }
    private void createFile(String filename){
        Intent intent = new Intent(getContext(), CanvasActivity.class);
        intent.putExtra(EXTRA_MESSAGE, filename);
        startActivity(intent);
    }
    private void deleteFile(String filename){
        FileOperator fileOperator= new FileOperator(getContext());
        fileOperator.deleteFile(filename);
    }



}


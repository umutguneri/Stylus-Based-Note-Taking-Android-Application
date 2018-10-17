package com.example.umut.lecturenotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;

public class CanvasActivity extends AppCompatActivity {
    public static CanvasView canvasView;
    public static ArrayList<CanvasView> canvasList;
    public ArrayList<View> gaps;
    public MyScrollView myScrollView;
    public LinearLayout linearLayout;
    FloatingActionButton fabEdit,fabShare,fabStroke,fabPalette,fabEraser,fabSave,fabClear,fabUndo;
    FloatingActionButton fabBlack,fabYellow,fabGray,fabBlue,fabOrange,fabRed,fabGreen;
    FloatingActionButton fabLine1,fabLine2,fabLine3,fabLine4,fabLine5,fabLine6;
    FloatingActionButton fabNew,fabHold;
    ArrayList<FloatingActionButton> fabMenu, fabColors, fabLines;
    Animation fabOpen,fabClose,rotateForward,rotateBackward;
    boolean menuOpen=false;
    boolean paletteOpen=false;
    boolean strokeOpen=false;
    public static String filename;
    public boolean enable=true;
    public boolean isSaved=false;

    public FileOperator fileOperator;

    public final int pageWidth=1500;
    public final int pageHeigth=2500;
    public final int pageLimit=100;
    public int pageCount=1;
    public int i;

    @Override
    protected void onStart()
    {
        super.onStart();
        draw();
    }

    public void draw(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0; i<pageCount; i++) {
                            CanvasActivity.canvasList.get(i).loadValues();
                        }
                    }
                });

            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById((R.id.activity_canvas));

        this.myScrollView = new MyScrollView(getApplicationContext());
        myScrollView = (MyScrollView) relativeLayout.findViewById(R.id.myScroll);
        myScrollView.setEnableScrolling(false);

        this.linearLayout = (LinearLayout) myScrollView.findViewById(R.id.linearLayout2);

        fabMenu = new ArrayList<>();
        fabColors = new ArrayList<>();
        fabLines = new ArrayList<>();

        addFabs(relativeLayout);
        clickListener();

        canvasView = new CanvasView(getApplicationContext());
        canvasView = (CanvasView) relativeLayout.findViewById(R.id.canvas);

        this.canvasList= new ArrayList<>();
        this.canvasList.add(canvasView);
        this.gaps= new ArrayList<>();

        for(int i=0 ; i<pageLimit; i++  ){
            this.canvasList.add(new CanvasView(getApplicationContext()));
            this.gaps.add(new View(getApplicationContext()));
            this.gaps.get(i).setBackgroundColor(Color.GRAY);
        }

        this.fileOperator = new FileOperator(getApplicationContext()) ;

        Intent intent = getIntent();
        filename = intent.getStringExtra(Intro.EXTRA_MESSAGE);

        pageCount=fileOperator.getNumberofPages(filename);

        for(int i=0; i<pageCount; i++){
            canvasList.get(i).loadFile(filename,i);
        }

        for(int i=1; i<pageCount; i++){
            linearLayout.addView(canvasList.get(i),pageWidth,pageHeigth);
            linearLayout.addView(gaps.get(i),pageWidth*2,10);
        }

    }

    public void onDestroy() {
        super.onDestroy();
        fileOperator.saveFile(filename, canvasList , pageCount);
    }

    public void onBackPressed() {
        if(isSaved==false) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            builder.setTitle("Dosyayı Kaydet")
                    .setMessage("Bu dosyayı çıkmadan önce kaydetmek ister misiniz?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            fileOperator.saveFile(filename, canvasList, pageCount);
                            onResume();
                            CanvasActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            CanvasActivity.super.onBackPressed();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else{
            CanvasActivity.super.onBackPressed();
        }
    }


    private void clickListener() {
        //HOLDER LISTENER//
        fabHold.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                myScrollView.setEnableScrolling(true);
                for(i=0; i<pageCount; i++ )
                    canvasList.get(i).enableDrawing(false);

                Toast.makeText(getApplicationContext(), "Kaydır", Toast.LENGTH_SHORT).show();
            }
        });

        //NEW PAGE LISTENER//
        fabNew.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                canvasList.get(pageCount).setColor(canvasList.get(pageCount-1).getColor());
                canvasList.get(pageCount).setStroke(canvasList.get(pageCount-1).getStroke());

                linearLayout.addView(canvasList.get(pageCount),pageWidth,pageHeigth);
                linearLayout.addView(gaps.get(pageCount),pageWidth*3,10);
                pageCount++;
                isSaved=false;
                Toast.makeText(getApplicationContext(), "Yeni Sayfa", Toast.LENGTH_SHORT).show();

            }
        });

        //MENU CLICK LISTENER//
        fabEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                for(i=0; i<pageCount; i++ )
                    canvasList.get(i).enableDrawing(true);
                animateMenu();
            }
        });

        fabStroke.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                for(i=0; i<pageCount; i++ )
                    canvasList.get(i).enableDrawing(true);
                Toast.makeText(getApplicationContext(), "Kalınlık", Toast.LENGTH_SHORT).show();
                animateStroke();
            }
        });
        fabShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Paylaş", Toast.LENGTH_SHORT).show();
                File file = new File(getApplicationContext().getExternalFilesDir(
                                getApplicationContext().getString(R.string.app_name)), filename);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("*/txt");
                Uri u = Uri.fromFile(file);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, u);
                startActivity(Intent.createChooser(sharingIntent, "share file with"));

            }
        });
        fabPalette.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                for(i=0; i<pageCount; i++ )
                    canvasList.get(i).enableDrawing(true);
                Toast.makeText(getApplicationContext(), "Palet", Toast.LENGTH_SHORT).show();
                animateColor();
            }
        });

        fabUndo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                canvasView.undo();
                Toast.makeText(getApplicationContext(), "Geri Alındı", Toast.LENGTH_SHORT).show();
            }
        });

        fabEraser.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                for(i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setColor(0xFFFFFFFF);
                }
                Toast.makeText(getApplicationContext(), "Silgi", Toast.LENGTH_SHORT).show();
            }
        });

        fabSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Dosya Kaydedildi", Toast.LENGTH_SHORT).show();
                fileOperator.saveFile(filename, canvasList , pageCount);
                isSaved=true;
            }
        });
        fabClear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Sayfa temizlendi", Toast.LENGTH_SHORT).show();
                for(i=0; i<pageCount; i++ ){
                    canvasList.get(i).clearCanvas();
                }
                isSaved=false;
            }
        });

        //THICKNESS CLICK LISTENER//
        fabLine1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                Toast.makeText(getApplicationContext(), "Line1", Toast.LENGTH_SHORT).show();
                for(i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setStroke(1);
                }
            }
        });
        fabLine2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                Toast.makeText(getApplicationContext(), "Line2", Toast.LENGTH_SHORT).show();
                for(i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setStroke(3);
                }
            }
        });
        fabLine3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                Toast.makeText(getApplicationContext(), "Line3", Toast.LENGTH_SHORT).show();
                for(i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setStroke(6);
                }
            }
        });
        fabLine4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                Toast.makeText(getApplicationContext(), "Line4", Toast.LENGTH_SHORT).show();
                for(i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setStroke(10);
                }
            }
        });
        fabLine5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                Toast.makeText(getApplicationContext(), "Line5", Toast.LENGTH_SHORT).show();
                for(i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setStroke(15);
                }
            }
        });
        fabLine6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                Toast.makeText(getApplicationContext(), "Line6", Toast.LENGTH_SHORT).show();
                for(i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setStroke(20);
                }
            }
        });

        //COLORS CLICK LISTENER//
        fabBlack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                Toast.makeText(getApplicationContext(), "Siyah", Toast.LENGTH_SHORT).show();
                for(i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setColor(Color.BLACK);
                }
            }
        });
        fabYellow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                Toast.makeText(getApplicationContext(), "Sarı", Toast.LENGTH_SHORT).show();
                for(i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setColor(Color.YELLOW);
                }

            }
        });
        fabGray.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                Toast.makeText(getApplicationContext(), "Gri", Toast.LENGTH_SHORT).show();
                for(i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setColor(Color.GRAY);
                }
            }
        });
        fabBlue.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                Toast.makeText(getApplicationContext(), "Mavi", Toast.LENGTH_SHORT).show();
                for(i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setColor(0xFF009ACD);
                }
            }
        });
        fabOrange.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                Toast.makeText(getApplicationContext(), "Turuncu", Toast.LENGTH_SHORT).show();
                for(i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setColor(0xFFFF8000);
                }
            }
        });
        fabRed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                Toast.makeText(getApplicationContext(), "Kırmızı", Toast.LENGTH_SHORT).show();

                for(int i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setColor(0xFFFF0000);
                }
            }
        });
        fabGreen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myScrollView.setEnableScrolling(false);
                Toast.makeText(getApplicationContext(), "Yeşil", Toast.LENGTH_SHORT).show();
                for(i=0; i<pageCount; i++ ) {
                    canvasList.get(i).enableDrawing(true);
                    canvasList.get(i).setColor(0xFF008000);
                }
            }
        });

    }

    private void animateMenu(){
        if(menuOpen){
            fabEdit.startAnimation(rotateForward);
            for (FloatingActionButton fab : fabMenu) {
                fab.startAnimation(fabClose);
                fab.setClickable(false);
            }

            for (FloatingActionButton fab : fabLines) {
                fab.startAnimation(fabClose);
                fab.setClickable(false);
            }

            for (FloatingActionButton fab : fabColors) {
                fab.startAnimation(fabClose);
                fab.setClickable(false);
            }
            menuOpen=false;
            paletteOpen=false;
            strokeOpen=false;
        }
        else{
            fabEdit.startAnimation(rotateBackward);
            for (FloatingActionButton fab : fabMenu) {
                fab.startAnimation(fabOpen);
                fab.setClickable(true);
            }
            menuOpen=true;
        }
    }
    private void animateStroke(){
        if(strokeOpen){
            fabStroke.startAnimation(rotateBackward);
            for (FloatingActionButton fab : fabLines) {
                fab.startAnimation(fabClose);
                fab.setClickable(false);
            }
            strokeOpen=false;
        }
        else{
            fabStroke.startAnimation(rotateForward);
            for (FloatingActionButton fab : fabLines) {
                fab.startAnimation(fabOpen);
                fab.setClickable(true);
            }
            strokeOpen=true;
        }
    }
    private void animateColor(){
        if(paletteOpen){
            fabPalette.startAnimation(rotateForward);
            for (FloatingActionButton fab : fabColors) {
                fab.startAnimation(fabClose);
                fab.setClickable(false);
            }
            paletteOpen=false;
        }
        else{
            fabPalette.startAnimation(rotateBackward);
            for (FloatingActionButton fab : fabColors) {
                fab.startAnimation(fabOpen);
                fab.setClickable(true);
            }
            paletteOpen=true;
        }
    }

    private void addFabs(RelativeLayout relativeLayout) {
        fabNew =(FloatingActionButton) relativeLayout.findViewById(R.id.fabNew);
        fabHold =(FloatingActionButton) relativeLayout.findViewById(R.id.fabHold);

        fabEdit=(FloatingActionButton) relativeLayout.findViewById(R.id.fabEdit);

        fabMenu.add(fabShare=(FloatingActionButton) relativeLayout.findViewById(R.id.fabShare));
        fabMenu.add(fabStroke=(FloatingActionButton) relativeLayout.findViewById(R.id.fabStroke));
        fabMenu.add(fabPalette=(FloatingActionButton) relativeLayout.findViewById(R.id.fabPalette));
        fabMenu.add(fabEraser=(FloatingActionButton) relativeLayout.findViewById(R.id.fabEraser));
        fabMenu.add(fabSave=(FloatingActionButton) relativeLayout.findViewById(R.id.fabSave));
        fabMenu.add(fabClear=(FloatingActionButton) relativeLayout.findViewById(R.id.fabClear));
        fabMenu.add(fabUndo=(FloatingActionButton) relativeLayout.findViewById(R.id.fabUndo));

        fabLines.add(fabLine1=(FloatingActionButton) relativeLayout.findViewById(R.id.fabLine1));
        fabLines.add(fabLine2=(FloatingActionButton) relativeLayout.findViewById(R.id.fabLine2));
        fabLines.add(fabLine3=(FloatingActionButton) relativeLayout.findViewById(R.id.fabLine3));
        fabLines.add(fabLine4=(FloatingActionButton) relativeLayout.findViewById(R.id.fabLine4));
        fabLines.add(fabLine5=(FloatingActionButton) relativeLayout.findViewById(R.id.fabLine5));
        fabLines.add(fabLine6=(FloatingActionButton) relativeLayout.findViewById(R.id.fabLine6));

        fabColors.add(fabBlack=(FloatingActionButton) relativeLayout.findViewById(R.id.fabBlack));
        fabColors.add(fabYellow=(FloatingActionButton) relativeLayout.findViewById(R.id.fabYellow));
        fabColors.add(fabGray=(FloatingActionButton) relativeLayout.findViewById(R.id.fabGray));
        fabColors.add(fabBlue=(FloatingActionButton) relativeLayout.findViewById(R.id.fabBlue));
        fabColors.add(fabOrange=(FloatingActionButton) relativeLayout.findViewById(R.id.fabOrange));
        fabColors.add(fabRed=(FloatingActionButton) relativeLayout.findViewById(R.id.fabRed));
        fabColors.add(fabGreen=(FloatingActionButton) relativeLayout.findViewById(R.id.fabGreen));

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
    }


}

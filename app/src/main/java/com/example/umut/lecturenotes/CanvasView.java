package com.example.umut.lecturenotes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.io.IOException;


public class CanvasView extends View {
    private boolean drawEnabled=true;
    private Paint paint;
    private ArrayList<Integer> Xpoints=new ArrayList() ;
    private ArrayList<Integer> Ypoints=new ArrayList();
    private ArrayList<Integer> RGBvalues=new ArrayList();;
    private ArrayList<Integer>  StrokeValues=new ArrayList();;

    private Path path;
    private Paint canvasPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private Sketch sketch;

    public CanvasView(Context context ) {
        super(context);
        this.init();

        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(4f);
        this.paint.setColor(Color.BLACK);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(4f);
        this.paint.setColor(Color.BLACK);
        System.out.println("Renk:" +Color.BLACK / 0xFF00000);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void init() {
        Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        this.drawCanvas= new Canvas(b);
        this.paint = new Paint();
        this.path = new Path();
        this.canvasPaint = new Paint(Paint.DITHER_FLAG);
    }


    public void undo(){
        int index= Xpoints.size()-1;
        if(index>0) {
            System.out.println("index: " + index);
            Xpoints.remove(index);
            Ypoints.remove(index);
            RGBvalues.remove(index);
            StrokeValues.remove(index);
            index--;
            System.out.println("index: " + index);
            while (!Xpoints.get(index).equals(Integer.valueOf(-1)) && index >0) {
                Xpoints.remove(index);
                Ypoints.remove(index);
                RGBvalues.remove(index);
                StrokeValues.remove(index);
                index--;
                System.out.println("index: " + index);
            }
            if(index==0){
                Xpoints.remove(index);
                Ypoints.remove(index);
                RGBvalues.remove(index);
                StrokeValues.remove(index);
            }

            this.drawCanvas.drawColor(0, Mode.CLEAR);
            loadValues();
        }
    }

    public void enableDrawing(boolean bool){
        this.drawEnabled=bool;
    }
    public boolean isEnable(){
        return drawEnabled;
    }

    public void setStroke(Integer width) {
        this.paint.setStrokeWidth(width);
        if(!StrokeValues.isEmpty()){
            StrokeValues.set(StrokeValues.size()-1,(int)width);
        }
    }

    public void setColor(int color) {
        this.paint.setColor(color);
        if(!RGBvalues.isEmpty()){
            RGBvalues.set(RGBvalues.size()-1,color);
        }
    }

    public int getColor() {
        return this.paint.getColor();
    }
    public int getStroke() {
        return (int)this.paint.getStrokeWidth();
    }

    public void clearCanvas() {
        this.drawCanvas.drawColor(0, Mode.CLEAR);
        sketch.clear();
        Xpoints.clear();
        Ypoints.clear();
        RGBvalues.clear();
        StrokeValues.clear();
        invalidate();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.drawCanvas = new Canvas(this.canvasBitmap);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(drawEnabled==true){
            canvas.drawBitmap(this.canvasBitmap, 0, 0, this.canvasPaint);
            canvas.drawPath(this.path, this.paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(drawEnabled==true){
            Integer eventX = (int)event.getX();
            Integer eventY = (int)event.getY();
            System.out.println("dokundun");

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    this.path.moveTo(eventX, eventY);
                    System.out.println("X: "+ eventX);
                    System.out.println("Y: "+ eventY);
                    break;

                case MotionEvent.ACTION_MOVE:
                    this.path.lineTo(eventX, eventY);
                    saveValues(eventX, eventY);
                    break;

                case MotionEvent.ACTION_UP:
                    this.drawCanvas.drawPath(this.path, this.paint);
                    Xpoints.add(Integer.valueOf(-1));
                    Ypoints.add(Integer.valueOf(-1));
                    RGBvalues.add(this.paint.getColor());
                    StrokeValues.add((int) this.paint.getStrokeWidth());

                    this.path.reset();
                    break;

                default:
                    return false;
            }
            invalidate();
            return true;
        }
        return true;
    }

    public void saveValues(Integer eventX,Integer eventY){
        RGBvalues.add(this.paint.getColor());
        StrokeValues.add((int)this.paint.getStrokeWidth());
        Xpoints.add(eventX);
        Ypoints.add(eventY);
        this.sketch = new Sketch(Xpoints,Ypoints,RGBvalues,StrokeValues);
    }

    public void loadValues(){
        int i;
        System.out.println("Test");
        if(sketch.getRGBvalues().size()==0){
            System.out.println("Boş");
            return;
        }

        Path path2 = new Path();
        path2.moveTo(sketch.getXpoints().get(0), sketch.getYpoints().get(0));
        System.out.println("X: " + sketch.getXpoints().get(0) + "Y: " + sketch.getYpoints().get(0));
        for(i=1 ;i<sketch.getXpoints().size()-1;i++){
            if(sketch.getXpoints().get(i).equals(Integer.valueOf(-1))){
                path2.reset();
                path2.moveTo(sketch.getXpoints().get(i+1), sketch.getYpoints().get(i+1));
            }
            else{
                path2.lineTo(sketch.getXpoints().get(i), sketch.getYpoints().get(i));
                this.paint.setColor(sketch.getRGBvalues().get(i));
                this.paint.setStrokeWidth(sketch.getStrokeValues().get(i));
            }
            this.drawCanvas.drawPath(path2, this.paint);
        }

        invalidate();
    }

    public void loadFile(String filename,int pageNumber){
        sketch= new Sketch(Xpoints,Ypoints,RGBvalues,StrokeValues);
        FileOperator fileOperator=new FileOperator(getContext());
        this.sketch=fileOperator.loadFile(filename,pageNumber);
        Xpoints=this.sketch.getXpoints();
        Ypoints=this.sketch.getYpoints();
        RGBvalues=this.sketch.getRGBvalues();
        StrokeValues=this.sketch.getStrokeValues();
    }

    public Sketch getSketch(){
        return this.sketch;
    }


}
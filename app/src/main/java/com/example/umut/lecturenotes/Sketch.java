package com.example.umut.lecturenotes;

import java.util.ArrayList;

public class Sketch {
    private ArrayList<Integer> Xpoints=new ArrayList() ;
    private ArrayList<Integer> Ypoints=new ArrayList();
    private ArrayList<Integer> RGBvalues=new ArrayList();
    private ArrayList<Integer>  StrokeValues=new ArrayList();

    public Sketch() {

    }
    public Sketch(ArrayList<Integer> xpoints, ArrayList<Integer> ypoints, ArrayList<Integer> RGBvalues, ArrayList<Integer> strokeValues) {
        this.Xpoints = xpoints;
        this.Ypoints = ypoints;
        this.RGBvalues = RGBvalues;
        this.StrokeValues = strokeValues;
    }

    public ArrayList<Integer> getXpoints() {
        return Xpoints;
    }

    public ArrayList<Integer> getYpoints() {
        return Ypoints;
    }

    public ArrayList<Integer> getRGBvalues() {
        return RGBvalues;
    }

    public ArrayList<Integer> getStrokeValues() {
        return StrokeValues;
    }

    public void setXpoints(ArrayList<Integer> xpoints) {
        this.Xpoints = xpoints;
    }

    public void setYpoints(ArrayList<Integer> ypoints) {
        this.Ypoints = ypoints;
    }

    public void setRGBvalues(ArrayList<Integer> RGBvalues) {
        this.RGBvalues = RGBvalues;
    }

    public void setStrokeValues(ArrayList<Integer> strokeValues) {
        this.StrokeValues = strokeValues;
    }
    public void clear() {
        this.Xpoints.clear();
        this.Ypoints.clear();
        this.RGBvalues.clear();
        this.StrokeValues.clear();
    }
}

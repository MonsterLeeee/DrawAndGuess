package com.example.drawandguess;

import android.graphics.Paint;
import android.graphics.Path;

public class DrawPathList {

    private Paint paint;

    private Path path;

    public DrawPathList(Paint paint,Path path)
    {
        this.path = path;
        this.paint = paint;
    }

    public Paint getPaint() {
        return paint;
    }


    public Path getPath() {
        return path;
    }

}

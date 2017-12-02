package com.example.zju.markmark;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * Created by 本 on 2017-12-02.
 */

public class MarkedTextView extends AppCompatTextView {

    private Paint raPaint;
    private Paint rbPaint;
    private Paint eaPaint;
    private Paint ebPaint;
    private Paint ecPaint;

    private ArrayList<Integer> start = new ArrayList<>();
    private ArrayList<Integer> end = new ArrayList<>();
    private ArrayList<String> label = new ArrayList<>();
    private ArrayList<Rect> rects = new ArrayList<>();

    public void setStart(int start) {
        this.start.add(start);
    }
    public void setEnd(int end) {
        this.end.add(end);
    }
    public void setLabel(String label) {
        this.label.add(label);
    }
    public void cl() {
        this.start.clear();
        this.end.clear();
        this.label.clear();
    }

    public MarkedTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        final int StrokeWidth = 5;
        raPaint = new Paint();
        raPaint.setStyle(Paint.Style.STROKE);
        raPaint.setStrokeWidth(StrokeWidth);
        raPaint.setColor(0xffD05A6E); // IMAYOH
        raPaint.setAntiAlias(true);
        rbPaint = new Paint();
        rbPaint.setStyle(Paint.Style.STROKE);
        rbPaint.setStrokeWidth(StrokeWidth);
        rbPaint.setColor(0xff7DB9DE); // WASURENAGUSA
        rbPaint.setAntiAlias(true);
        eaPaint = new Paint();
        eaPaint.setStyle(Paint.Style.FILL);
        eaPaint.setColor(0x66FFB11B); // YAMABUKI
        eaPaint.setAntiAlias(true);
        ebPaint = new Paint();
        ebPaint.setStyle(Paint.Style.FILL);
        ebPaint.setColor(0x668F77B5); // SHION
        ebPaint.setAntiAlias(true);
        ecPaint = new Paint();
        ecPaint.setStyle(Paint.Style.FILL);
        ecPaint.setColor(0x66CAAD5F); //KARASHI
        ecPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int horizontialOffset = 20;
        int vertivalOffset = 10;
        Layout layout = getLayout();
        for (int i = 0; i < start.size(); i++) {
            int s = start.get(i);
            int e = end.get(i);
            for (int j = s; j <= e; j++) {
                Rect bound = new Rect();
                int line = layout.getLineForOffset(j);
                layout.getLineBounds(line, bound);
                float left = layout.getPrimaryHorizontal(j) + horizontialOffset;
                float right = left + getTextSize();
                rects.add(new Rect((int)left, bound.top + vertivalOffset, (int)right, bound.bottom + vertivalOffset));
            }
            /*int Left = rect.left;
            int Top = rect.top;
            int Right = rect.right;
            int bottom = rect.bottom;*/
            switch (label.get(i)) {
                case "Location->Country":
                    for (Rect rect : rects) {
                        canvas.drawRect(rect, eaPaint);
                    }
                    break;
                case "Location->City":
                    for (Rect rect : rects) {
                        canvas.drawRect(rect, ebPaint);
                    }
                    break;
                case "Person":
                    for (Rect rect : rects) {
                        canvas.drawRect(rect, ecPaint);
                    }
                    break;
                default:
                    for (Rect rect : rects) {
                        canvas.drawRect(rect, eaPaint);
                    }
            }
            rects.clear();
        }
    }
}

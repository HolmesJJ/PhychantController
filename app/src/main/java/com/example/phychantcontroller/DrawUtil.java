package com.example.phychantcontroller;

import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawUtil {

    public static void drawCrossLine(Canvas canvas, Paint paint, int x, int y) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        canvas.drawLine(0, y, canvasWidth, y, paint);// 绘制x轴
        canvas.drawLine(x, 0, x, canvasHeight, paint); // 绘制y轴
        canvas.drawCircle(x, y, 10, paint);
    }
}

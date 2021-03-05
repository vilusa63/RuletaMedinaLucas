package com.example.ruletalucasmedina;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class Flecha extends  View {

    private float x;
    private float y;
    private String cadena;

    private int color;
    public Flecha(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Flecha,
                0, 0);

        try {
            x = a.getFloat(R.styleable.Flecha_cadena, 0);
            cadena = a.getString(R.styleable.Flecha_cadena);

        } finally {
            a.recycle();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        //paint.setColor(android.graphics.Color.RED);
        //canvas.drawPaint(paint);

        paint.setStrokeWidth(4);
        paint.setColor(android.graphics.Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        int intx=0;
        int inty=0;
        System.out.println(intx);
        System.out.println(inty);
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(intx,inty);
        path.lineTo(intx+this.getWidth(),inty);
        path.lineTo(intx+this.getWidth()/2,inty+this.getHeight());
        path.close();
        canvas.drawPath(path, paint);

    }

    public String getCadena() {
        return cadena;
    }

    public void setCadena(String cadena) {
        this.cadena = cadena;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}

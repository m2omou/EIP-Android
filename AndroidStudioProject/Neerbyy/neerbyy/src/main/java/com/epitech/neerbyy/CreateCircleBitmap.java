package com.epitech.neerbyy;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

public class CreateCircleBitmap extends Drawable  
{  
    private static final int DIMENSION = 100;  
    private int radius;  
    private RectF rect;  
    Paint paint;  
  
    public CreateCircleBitmap (Bitmap bitmap, int radius)  
    {  
        this.radius = radius;  
        bitmap = Bitmap.createScaledBitmap(bitmap, DIMENSION, DIMENSION, true);  
        BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);  
  
        paint = new Paint();  
        paint.setAntiAlias(true);  
        paint.setShader(shader);  
  
        rect = new RectF(0f, 0f, DIMENSION, DIMENSION);  
    }  
  
    @Override  
    public void draw(Canvas canvas)  
    {  
        canvas.drawRoundRect(rect, radius, radius, paint);  
    }

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAlpha(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColorFilter(ColorFilter arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {


        Bitmap outputTmp;
        if (bitmap.getWidth() >= bitmap.getHeight())
            outputTmp = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2 - bitmap.getHeight() / 2, 0, bitmap.getHeight(), bitmap.getHeight());
        else
            outputTmp = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth(), bitmap.getWidth());

        Bitmap output = Bitmap.createBitmap(outputTmp.getWidth(), outputTmp    //  verif si nul ??
              .getHeight(), Config.ARGB_8888);


        if (output == null)
        	return null;

        
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, outputTmp.getWidth(), outputTmp.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(outputTmp, rect, rect, paint);

        return output;
    }
}  
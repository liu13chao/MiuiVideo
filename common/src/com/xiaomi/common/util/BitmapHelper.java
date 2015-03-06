package com.xiaomi.common.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class BitmapHelper {

    public static Bitmap clipRoundCornerBitmap(Bitmap bitmap, float radius, int borderColor) {
        if(bitmap == null){
            return null;
        }
        final int h = bitmap.getHeight();
        final int w = bitmap.getWidth();

        final Bitmap output = Bitmap.createBitmap(w, h , Config.ARGB_8888);

        final Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(borderColor);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static void recycleImageView(ImageView view, Drawable exclude) {
        if (view == null) {
            return;
        }

        final Bitmap bm = exclude instanceof BitmapDrawable ? ((BitmapDrawable) exclude).getBitmap() : null;
        final Drawable d = view.getDrawable();
        view.setImageDrawable(null);
        if (d instanceof BitmapDrawable) {
            Bitmap recycleBitmap = ((BitmapDrawable) d).getBitmap();
            if (recycleBitmap != null && recycleBitmap != bm) {
                recycleBitmap.recycle();
            }
        }
    }

    /**
     * use xfermode to src
     * @param src
     * @param mask
     * @param xfermode
     * @return 返回与mask尺寸相同的mask后的图片,如果src尺寸大于mask，按fitCenter处理
     */
    public static Bitmap transferMode(Bitmap src, Bitmap mask, Xfermode xfermode) {
        final int width = mask.getWidth();
        final int height = mask.getHeight();
        final Bitmap dst = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        final Canvas canvas = new Canvas(dst);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        final int srcWidth = src.getWidth();
        final int srcHeight = src.getHeight();

        canvas.save();

        // Scale down the image first if required.
        if ((width < srcWidth) || (height < srcHeight)) {
            float radioX = (float) width / srcWidth;
            float radioY = (float) height / srcHeight;
            float radio = 1f;
            float dx = 0f;
            float dy = 0f;

            if (radioX > radioY) {
                radio = radioX;
                dy = (height/radioX - srcHeight)/2.0f;
            } else {
                radio = radioY;
                dx = (width/radioX - srcWidth)/2.0f;
            }

            canvas.scale(radio, radio);
            canvas.translate(dx, dy);
        }
        canvas.drawBitmap(src, 0, 0, paint);
        canvas.restore();

        if (xfermode != null) {
            paint.setXfermode(xfermode);
            canvas.drawBitmap(mask, 0, 0, paint);
        }

        return dst;
    }

    /**
     * 加载并裁剪图片，生成的图片尺寸小于或等于(suggestWidth, suggestHeight)，且width/height=suggestWidth/suggestHeight
     * @param loader
     * @param suggestWidth
     * @param suggestHeight
     * @param force
     * @return
     */
    //by tfling
    /*
    public static Bitmap decode(InputStreamLoader loader, int suggestWidth, int suggestHeight, boolean force) {
        if (force) {
            return ImageUtils.getBitmap(loader, suggestWidth, suggestHeight);
        }

        final BitmapFactory.Options opt = ImageUtils.getBitmapSize(loader);
        int w, h;
        if (suggestWidth < opt.outWidth && suggestHeight < opt.outHeight) {
            w = suggestWidth;
            h = suggestHeight;
        } else {
            w = opt.outWidth;
            h = opt.outHeight;
            final float fw = ((float) w) / suggestWidth;
            final float fh = ((float) h) / suggestHeight;
            if (fw < fh) {
                h = Math.round(suggestHeight * fw);
            } else {
                w = Math.round(suggestWidth * fh);
            }
        }
        return ImageUtils.getBitmap(loader, w, h);
    }
    */
}

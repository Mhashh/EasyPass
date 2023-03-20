package com.maheshtiria.easypass.utility;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.Image;

import androidx.annotation.ColorInt;

import java.nio.ByteBuffer;

public class ImageUtils {
    //private val CHANNEL_RANGE = 0 until (1 shl 18)
    static final int CHANNEL_RANGE_START = 0;
    static final int  CHANNEL_RANGE_END = (1 << 18);
    static Bitmap convertYuv420888ImageToBitmap(Image image) {
        if(image.getFormat() != ImageFormat.YUV_420_888) {
            throw new IllegalArgumentException("Unsupported image format $(image.format)");
        }

        Image.Plane[] planes = image.getPlanes();

        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        ByteBuffer yuvBytes [] = new ByteBuffer[3];
        for(int i = 0;i<3;i++){
            ByteBuffer buffer = planes[i].getBuffer();
            yuvBytes[i] = ByteBuffer.allocate(buffer.capacity());
            buffer.put(yuvBytes[i]);
            buffer.rewind();  // Be kind…
        }

        int yRowStride = planes[0].getRowStride();
        int uvRowStride = planes[1].getRowStride();
        int uvPixelStride = planes[1].getPixelStride();
        int width = image.getWidth();
        int height = image.getHeight();
        @ColorInt int[] argb8888 = new int[width * height];
        int i = 0;
        for (int y= 0;y<height;y++) {
            int pY = yRowStride * y;
            int uvRowStart = uvRowStride * (y >> 1);
            for (int x=0;x<width;x++) {
                int uvOffset = (x >> 1) * uvPixelStride;
                argb8888[i++] =
                        yuvToRgb(
                                yuvBytes[0].get(pY + x),
                                yuvBytes[1].get(uvRowStart + uvOffset),
                                yuvBytes[2].get(uvRowStart + uvOffset)
                        );
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(argb8888, 0, width, 0, 0, width, height);
        return bitmap;
    }

    static Bitmap rotateAndCrop(
            Bitmap bitmap,
            int imageRotationDegrees,
            Rect cropRect
    ){
        Matrix matrix = new Matrix();
        matrix.preRotate((float) imageRotationDegrees);
        return Bitmap.createBitmap(
                bitmap,
                cropRect.left,
                cropRect.top,
                cropRect.width(),
                cropRect.height(),
                matrix,
                true
        );
    }

    @ColorInt
    static int yuvToRgb(int nY,int nU,int nV) {

        nY -= 16;
        nU -= 128;
        nV -= 128;
        nY = nY<0?0:nY;

        // This is the floating point equivalent. We do the conversion in integer
        // because some Android devices do not have floating point in hardware.
        // nR = (int)(1.164 * nY + 2.018 * nU);
        // nG = (int)(1.164 * nY - 0.813 * nV - 0.391 * nU);
        // nB = (int)(1.164 * nY + 1.596 * nV);
        int nR = 1192 * nY + 1634 * nV;
        int nG = 1192 * nY - 833 * nV - 400 * nU;
        int nB = 1192 * nY + 2066 * nU;

        // Clamp the values before normalizing them to 8 bits.
        nR = nR<CHANNEL_RANGE_START?CHANNEL_RANGE_START:nR;
        nG = nG<CHANNEL_RANGE_START?CHANNEL_RANGE_START:nG;
        nB = nB<CHANNEL_RANGE_START?CHANNEL_RANGE_START:nB;

        nR = nR>CHANNEL_RANGE_END?CHANNEL_RANGE_END:nR;
        nG = nG>CHANNEL_RANGE_END?CHANNEL_RANGE_END:nG;
        nB = nB>CHANNEL_RANGE_END?CHANNEL_RANGE_END:nB;

        nR = (nR>> 10) & 0xff;
        nG = (nG>> 10) & 0xff;
        nB = (nB>> 10) & 0xff;
        return -0x1000000 | (nR << 16) | (nG << 8) | nB;
    }
}

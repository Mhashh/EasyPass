package com.maheshtiria.easypass.imageutils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;

import java.nio.ByteBuffer;

public class ImageUtil {
  @OptIn(markerClass = ExperimentalGetImage.class)
  public static InputImage  crop(ImageProxy image){
    Image inp = image.getImage();
    Rect cropRect = image.getCropRect();
    //bitmap image
    ByteBuffer buffer = (inp != null ? inp.getPlanes() : new Image.Plane[0])[0].getBuffer();//first plane contain image data
    byte[] bytes = new byte[buffer.remaining()];//length of data
    buffer.get(bytes);//transfer data into []
    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

    //rotation
    int deg = image.getImageInfo().getRotationDegrees();


    //finally crop
    bitmap = Bitmap.createBitmap(bitmap,cropRect.left,cropRect.top,cropRect.width(),cropRect.height());

    return InputImage.fromBitmap(bitmap,deg);
  }
}

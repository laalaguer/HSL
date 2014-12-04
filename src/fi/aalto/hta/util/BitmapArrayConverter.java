package fi.aalto.hta.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

/**
 * Convert between Bitmaps and byte array
 * @author Xiqing Chu
 *
 */
public class BitmapArrayConverter {
	
	// Convert the image into byte array
	public static byte[] convertBitmapToByteArray(Bitmap image){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		image.compress(CompressFormat.PNG, 0, bos); // number of 0-100 doesn't harm the quality
		return bos.toByteArray();
	}
	
	// Convert the byte array back to Bitmap
	public static Bitmap convertByteArrayToBitmap(byte[] image_byte){
		// 0 means offset
		return BitmapFactory.decodeByteArray(image_byte , 0, image_byte.length);
	}
}

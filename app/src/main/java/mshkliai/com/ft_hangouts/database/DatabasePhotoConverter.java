package mshkliai.com.ft_hangouts.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;


public class DatabasePhotoConverter {
    public static byte[] bitmapToBytes(Bitmap image) {
        ByteArrayOutputStream streamWithBytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 25, streamWithBytes);

        return streamWithBytes.toByteArray();
    }

    public static Bitmap bytesToBitmap(byte[] decodedByte) {
        if (decodedByte != null) {
            return BitmapFactory.decodeByteArray(decodedByte,0, decodedByte.length);
        }
        return null;
    }
}

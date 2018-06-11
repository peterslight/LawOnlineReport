package com.peterstev.lawonlinereportnigeria.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Peterstev on 11/11/2017.
 */

public class ImageCompression {

    public static byte[] convertBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap convertByteToBitmap(byte[] imageByte) {
        return BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
    }


    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //get raw h and w of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = (height / 2);
            final int halfWidth = (width / 2);

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

//    private static Bitmap decodeBitmapFromResource(String uri){
//        Observable observable = new Observable();
//
//    }


    public static Bitmap compressBitmapFromIS(InputStream stream, int reqHeight, int reqWidth) {
        // first decode with inJustDecodeVounds = true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);

        //calc insample size
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        //decode bitmap with insample Set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(stream, null, options);
    }

    public static Bitmap compressBitmapFromByte(byte[] bitByte, int reqHeight, int reqWidth) {
        // first decode with inJustDecodeVounds = true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bitByte, 0, bitByte.length, options);
        //BitmapFactory.decodeResource(context.getResources(), resID, options);

        //calc insample size
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        //decode bitmap with insample Set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bitByte, 0, bitByte.length, options);
    }


    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public static byte[] getBytesFromUri(Context context, Uri uri) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] byteResult = null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            int bufferSize = 1024;
            byte[] byteBuffer = new byte[bufferSize];
            int length;
            if (inputStream != null) {
                while ((length = inputStream.read(byteBuffer)) != -1) {
                    byteArrayOutputStream.write(byteBuffer, 0, length);
                }
                byteResult = byteArrayOutputStream.toByteArray();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteResult;
    }

    public static String getRealPathFromUri(Context context, String imageUri) {
        Uri contentURI = Uri.parse(imageUri);
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int position = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(position);
        }
    }

    ///////////////////////////////////////compresion lib///////////////////////////////////////////////////////////


    //max width and height values of the compressed image is taken as 612x816
    private int maxWidth = 612;
    private int maxHeight = 816;
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
    private int quality = 80;
    private String destinationDirectoryPath;

    public ImageCompression(Context context) {
        destinationDirectoryPath = context.getCacheDir().getPath() + File.separator + "images";
    }

    public ImageCompression setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public ImageCompression setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public ImageCompression setCompressFormat(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
        return this;
    }

    public ImageCompression setQuality(int quality) {
        this.quality = quality;
        return this;
    }

    public ImageCompression setDestinationDirectoryPath(String destinationDirectoryPath) {
        this.destinationDirectoryPath = destinationDirectoryPath;
        return this;
    }

    public File compressToFile(String imagePath) throws IOException {
        return compressToFile(imagePath, imagePath);
    }

    public File compressToFile(String imagePath, String compressedFileName) throws IOException {
        return ImageUtil.compressImage(imagePath, maxWidth, maxHeight, compressFormat, quality,
                destinationDirectoryPath + File.separator + compressedFileName);
    }

    public Bitmap compressToBitmap(File imageFile) throws IOException {
        return ImageUtil.decodeSampledBitmapFromFile(imageFile, maxWidth, maxHeight);
    }


    public static class ImageUtil {

        static File compressImage(String imagePath, int reqWidth, int reqHeight, Bitmap.CompressFormat compressFormat, int quality, String destinationPath) throws IOException {
            File imageFile = new File(imagePath);
            FileOutputStream fileOutputStream = null;
            File file = new File(destinationPath).getParentFile();
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                fileOutputStream = new FileOutputStream(destinationPath);
                // write the compressed bitmap at the destination specified by destinationPath.
                decodeSampledBitmapFromFile(imageFile, reqWidth, reqHeight).compress(compressFormat, quality, fileOutputStream);
            } finally {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
            return new File(destinationPath);
        }

        static Bitmap decodeSampledBitmapFromFile(File imageFile, int reqWidth, int reqHeight) throws IOException {
            // First decode with inJustDecodeBounds=true to check dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            Bitmap scaledBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            //check the rotation of the image and display it properly
            ExifInterface exif;
            exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            return scaledBitmap;
        }
    }
}
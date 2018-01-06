package com.teknokrait.tomatoclassification.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.soundcloud.android.crop.Crop;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class GalleryCameraInvoker {
    private static final String[] WORDING_GALLERY_OR_CAMERA = new String[]{
            "From Gallery", "Take Photo"
    };
    private static final String WORDING_NO_IMAGE_SELECTED = "No image selected";
    private static final boolean DEBUG = false;
    private static final int REQ_CODE_GALLERY_WITH_CROP = 784;
    private static final int REQ_CODE_CAMERA_WITH_CROP = 785;
    private Activity activity;
    private android.support.v4.app.Fragment fragmentSupport;
    private android.app.Fragment fragment;
    private File cameraFile;
    private Callback callback;
    private int reqCodeCamera;
    private int reqCodeGallery;
    private boolean cropImage;
    private File croppedFile;

    public void invokeCamera() {
        Object instance = activity;
        if (instance == null) {
            instance = fragment;
        }
        if (instance == null) {
            instance = fragmentSupport;
        }
        invokeCamera(instance);
    }

    public void invokeGallery() {
        Object instance = activity;
        if (instance == null) {
            instance = fragment;
        }
        if (instance == null) {
            instance = fragmentSupport;
        }
        invokeGallery(instance);
    }

    public File invokeGalleryAndCamera(
            android.support.v4.app.Fragment fragment, Callback callback, int requestCodeCamera, int requestCodeGallery, boolean cropImage) {
        this.fragmentSupport = fragment;
        this.callback = callback;
        this.reqCodeCamera = requestCodeCamera;
        this.reqCodeGallery = requestCodeGallery;
        this.cropImage = cropImage;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.fragmentSupport.getActivity()
                .getExternalCacheDir();
        try {
            cameraFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        onShowOptionList(fragment.getActivity());
        return cameraFile;
    }

    public File invokeGalleryAndCamera(
            Activity activity, Callback callback, int requestCodeCamera, int requestCodeGallery, boolean cropImage) {
        this.activity = activity;
        this.callback = callback;
        this.reqCodeCamera = requestCodeCamera;
        this.reqCodeGallery = requestCodeGallery;
        this.cropImage = cropImage;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity
                .getExternalCacheDir();
        try {
            cameraFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        onShowOptionList(activity);
        return cameraFile;
    }

    public File invokeGalleryAndCamera(
            android.support.v4.app.Fragment fragment, Callback callback, int requestCodeCamera, int requestCodeGallery) {
        return invokeGalleryAndCamera(fragment, callback, requestCodeCamera, requestCodeGallery, false);
    }

//    public void invokeGalleryAndCamera(Activity activity, DialogListener callback, int requestCodeCamera, int requestCodeGallery) {
//        this.activity = activity;
//        this.callback = callback;
//        this.reqCodeCamera = requestCodeCamera;
//        this.reqCodeGallery = requestCodeGallery;
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
//                .format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = activity.getExternalCacheDir();
//        try {
//            cameraFile = File.createTempFile(imageFileName, ".jpg", storageDir);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        onShowOptionList(activity);
//    }
//
//    public File invokeGalleryAndCamera(android.app.Fragment fragment,
//                                       DialogListener callback, int requestCodeCamera, int requestCodeGallery) {
//        this.fragment = fragment;
//        this.callback = callback;
//        this.reqCodeCamera = requestCodeCamera;
//        this.reqCodeGallery = requestCodeGallery;
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
//                .format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = this.fragment.getActivity().getExternalCacheDir();
//        try {
//            cameraFile = File.createTempFile(imageFileName, ".jpg", storageDir);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        onShowOptionList(fragment.getActivity());
//        return cameraFile;
//    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_GALLERY_WITH_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                if(Build.VERSION.SDK_INT < 24) {
                    croppedFile = tryCropImage(data.getData(), this.reqCodeGallery);
                } else {
                    croppedFile = tryCropImageNougat(data.getData(), this.reqCodeGallery);
                }
            }
        } else if (requestCode == REQ_CODE_CAMERA_WITH_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                if(Build.VERSION.SDK_INT < 24) {
                    croppedFile = tryCropImage(Uri.fromFile(cameraFile), this.reqCodeCamera);
                } else {
                    Uri uri = FileProvider.getUriForFile(activity != null? activity : (fragment != null ? fragment.getActivity(): fragmentSupport.getActivity()), BuildConfig.APPLICATION_ID + ".provider", cameraFile);
                    croppedFile = tryCropImageNougat(uri, this.reqCodeCamera);
                }
            }
        } else if (requestCode == this.reqCodeGallery) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage;
                if (!cropImage) {
                    selectedImage = data.getData();
                } else {
                    if(Build.VERSION.SDK_INT < 24) {
                        selectedImage = Uri.fromFile(croppedFile);
                    } else {
                        selectedImage = FileProvider.getUriForFile(activity != null ? activity : this.fragmentSupport.getActivity(), BuildConfig.APPLICATION_ID + ".provider",
                                croppedFile);
                    }
                }

                if (selectedImage == null) {
                    if (DEBUG) Log.e("danzoye", "Selected Image Uri == null");
                    Toast.makeText(activity, WORDING_NO_IMAGE_SELECTED,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                AsyncTask<Uri, Void, File> processImgTask = new AsyncTask<Uri, Void, File>() {

                    @Override
                    protected File doInBackground(Uri... params) {
                        Context context = activity;
                        if (context == null) {
                            if (fragment != null) {
                                context = fragment.getActivity();
                            } else if (fragmentSupport != null) {
                                context = fragmentSupport.getActivity();
                            }
                        }

                        try {
                            return onProcessingImageFromGallery(context.getContentResolver().openInputStream(params[0]));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            return null;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(File result) {
                        super.onPostExecute(result);
                        callback.onBitmapResult(result);
                    }
                };
                processImgTask.execute(selectedImage);
                if (callback instanceof CallbackWithProcessing) {
                    ((CallbackWithProcessing) callback).onProcessing();
                }

            } else {
                // TODO kalo cancel harus nya di kirim ke fragment deh
            }
        } else if (requestCode == this.reqCodeCamera) {
            if (resultCode == Activity.RESULT_OK) {
                if (DEBUG) {
                    Log.d("danzoye",
                            "starting process image file = "
                                    + cameraFile.toString());
                }

                AsyncTask<String, Void, File> processImgTask = new AsyncTask<String, Void, File>() {

                    @Override
                    protected File doInBackground(String... params) {
                        return onProcessingImageFromCamera(params[0]);
                    }

                    @Override
                    protected void onPostExecute(File result) {
                        super.onPostExecute(result);
                        callback.onBitmapResult(result);
                    }
                };
                processImgTask.execute(
                        (cropImage ? croppedFile : cameraFile).getAbsolutePath()
                );
                if (callback instanceof CallbackWithProcessing) {
                    ((CallbackWithProcessing) callback).onProcessing();
                }

            } else {
                // TODO kalo cancel harus nya di kirim ke fragment deh
            }
        }
    }

    protected ListAdapter getOptionListAdapter(Context context) {
        return new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, WORDING_GALLERY_OR_CAMERA);
    }

    protected int maxImageWidth() {
        return -1;
    }

    protected File onProcessingImageFromCamera(String filePath) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e1) {
            e1.printStackTrace();
            Log.e("danzoye", e1.toString());
        }

        if (DEBUG) Log.d("danzoye", "processing image = " + filePath);

        Bitmap bitmap = null;
        int targetW = maxImageWidth();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // only scale down if larger than max width
        if (targetW > 0 && photoW > targetW) {
            int targetH = (photoH * targetW) / photoW;

            if (DEBUG) Log.d("danzoye", "resizing image, target = " + targetW + ", " + targetH);

            // Determine how much to scale down the image
            int scaleFactor = photoW / targetW;

            // Decode the image file into a Bitmap sized to fill the
            // View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            bitmap = BitmapFactory.decodeFile(filePath, bmOptions);
        } else {
            bitmap = BitmapFactory.decodeFile(filePath);
        }

        int orientation = 0;
        if (exif != null) {
            if (DEBUG) Log.d("danzoye", "EXIF available!");
            int temp = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (temp) {
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;
                    break;
                default:
                    orientation = 0;
                    break;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(CameraInfo.CAMERA_FACING_BACK, info);

            orientation = info.orientation;
        }

        if (DEBUG) Log.d("danzoye", "orientation = " + orientation);

        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);

        if (DEBUG) Log.d("danzoye", "bitmap = " + bitmap);

        Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);

        if (bitmap2 != bitmap) {
            bitmap.recycle();
        }

        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        bitmap2.compress(Bitmap.CompressFormat.JPEG, 80, imageStream);
        bitmap2.recycle();

        System.gc();

        if (DEBUG)
            Log.d("danzoye", "bitmap final size = " + bitmap.getWidth() + "," + bitmap.getHeight());

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        String imageFileName = timeStamp + "_JPEG.jpg";
        File storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File pictureFile = new File(storageDir, imageFileName);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(imageStream.toByteArray());
            fos.close();

            return pictureFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.gc();
        }
        return null;
    }

    protected File onProcessingImageFromGallery(InputStream inputStream) throws IOException {
        byte[] data = IOUtils.toByteArray(inputStream);

        if (DEBUG) Log.d("danzoye", "processing image");

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        int targetW = maxImageWidth();

        // Get the dimensions of the bitmap
        int photoW = bitmap.getWidth();
        int photoH = bitmap.getHeight();

        // only scale down if larger than max width
        if (targetW > 0 && photoW > targetW) {
            int targetH = (photoH * targetW) / photoW;

            if (DEBUG) Log.d("danzoye", "resizing image, target = " + targetW + ", " + targetH);

            // Determine how much to scale down the image
            int scaleFactor = photoW / targetW;

            // Decode the image file into a Bitmap sized to fill the
            // View
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, bmOptions);
        }


        Matrix matrix = null;
        ExifIFD0Directory exif = null;
        try {
            if (DEBUG) Log.d("danzoye", "try read image metadata");

            Metadata metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(new ByteArrayInputStream(data)), false);

            if (DEBUG) Log.d("danzoye", "try read image exif");

            exif = metadata.getDirectory(ExifIFD0Directory.class);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ImageProcessingException e1) {
            e1.printStackTrace();
        }

        if (exif != null) {
            if (DEBUG) Log.d("danzoye", "image exif available!");

            int orientation = -1;
            try {
                orientation = exif.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            } catch (MetadataException e) {
                e.printStackTrace();
            }

            if (DEBUG) Log.d("danzoye", "image orientation = " + orientation);

            matrix = new Matrix();
            switch (orientation) {
                case 1:
                    break;  // top left
                case 2:
                    matrix.postScale(-1, 1);
                    break;  // top right
                case 3:
                    matrix.postRotate(180);
                    break;  // bottom right
                case 4:
                    matrix.postRotate(180);
                    matrix.postScale(-1, 1);
                    break;  // bottom left
                case 5:
                    matrix.postRotate(90);
                    matrix.postScale(-1, 1);
                    break;  // left top
                case 6:
                    matrix.postRotate(90);
                    break;  // right top
                case 7:
                    matrix.postRotate(270);
                    matrix.postScale(-1, 1);
                    break;  // right bottom
                case 8:
                    matrix.postRotate(270);
                    break;  // left bottom
                default:
                    break;  // Unknown
            }
        } else {
            if (DEBUG) Log.d("danzoye", "exif not available.");
        }
        if (DEBUG) Log.d("danzoye", "bitmap = " + bitmap);

        if (matrix != null) {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
        }

        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, imageStream);

        if (DEBUG)
            Log.d("danzoye", "bitmap final size = " + bitmap.getWidth() + "," + bitmap.getHeight());

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        String imageFileName = timeStamp + "_JPEG.jpg";
        File storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File pictureFile = new File(storageDir, imageFileName);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(imageStream.toByteArray());
            fos.close();
            bitmap.recycle();

            return pictureFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.gc();
        }
        return null;
    }

    protected void onShowOptionList(final Context context) {
        AlertDialog.Builder build = new AlertDialog.Builder(context);
        build.setItems(WORDING_GALLERY_OR_CAMERA,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Object instance = activity;
                        if (instance == null) {
                            instance = fragment;
                        }
                        if (instance == null) {
                            instance = fragmentSupport;
                        }
                        switch (which) {
                            case 0:
                                invokeGallery(instance);
                                break;
                            case 1:
                                invokeCamera(instance);
                                break;
                        }
                    }
                });
        build.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                GalleryCameraInvoker.this.callback.onCancelGalleryCameraInvoker();
            }
        });
        build.create().show();
    }

    protected File tryCropImageNougat(Uri selectedImage, int nextReqCode) {
        Activity context = activity;
        if (context == null) {
            if (fragment != null) {
                context = fragment.getActivity();
            } else if (fragmentSupport != null) {
                context = fragmentSupport.getActivity();
            }
        }

        String captureTime = String.valueOf(System
                .currentTimeMillis());
        String filename = new StringBuffer("raw_")
                .append(captureTime).append(".jpg").toString();
        File root = context.getExternalCacheDir();
        File media = new File(new StringBuffer(
                root.getAbsolutePath()).append("/draft/")
                .toString());

        File oututFile = null;
        if (!media.exists() && !media.mkdirs()) {
        } else {
            oututFile = new File(media, filename);
        }
        Object instance = activity;
        if (instance == null) {
            instance = fragment;
        }
        if (instance == null) {
            instance = fragmentSupport;
        }
        if (instance instanceof Activity) {
            Uri uri = FileProvider.getUriForFile((Activity)instance, BuildConfig.APPLICATION_ID + ".provider", oututFile);
            Crop.of(selectedImage, uri).start((Activity)instance, nextReqCode);
        } else if (instance instanceof android.support.v4.app.Fragment) {
            Uri uri = FileProvider.getUriForFile(((android.support.v4.app.Fragment) instance).getActivity(), BuildConfig.APPLICATION_ID + ".provider", oututFile);
            Crop.of(selectedImage, uri).start(((android.support.v4.app.Fragment) instance).getActivity(), (android.support.v4.app.Fragment) instance, nextReqCode);
        } else if (instance instanceof android.app.Fragment) {
            Uri uri = FileProvider.getUriForFile(((android.app.Fragment) instance).getActivity(), BuildConfig.APPLICATION_ID + ".provider", oututFile);
            Crop.of(selectedImage, uri).start(((android.app.Fragment) instance).getActivity(), (android.app.Fragment) instance, nextReqCode);
        }
        return oututFile;
    }

    protected File tryCropImage(Uri selectedImage, int nextReqCode) {
        Activity context = activity;
        if (context == null) {
            if (fragment != null) {
                context = fragment.getActivity();
            } else if (fragmentSupport != null) {
                context = fragmentSupport.getActivity();
            }
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = context.getPackageManager()
                .queryIntentActivities(intent, 0);
        int size = list.size();

        if (size > 0) {
            String captureTime = String.valueOf(System
                    .currentTimeMillis());
            String filename = new StringBuffer("raw_")
                    .append(captureTime).append(".jpg").toString();
            File root = context.getExternalCacheDir();
            File media = new File(new StringBuffer(
                    root.getAbsolutePath()).append("/draft/")
                    .toString());

            File oututFile = null;
            if (!media.exists() && !media.mkdirs()) {
            } else {
                oututFile = new File(media, filename);
            }

            intent.setData(selectedImage);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("output", Uri.fromFile(oututFile));

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);

            i.setComponent(new ComponentName(
                    res.activityInfo.packageName, res.activityInfo.name));

            Object instance = activity;
            if (instance == null) {
                instance = fragment;
            }
            if (instance == null) {
                instance = fragmentSupport;
            }
            if (instance instanceof Activity) {
                ((Activity) instance).startActivityForResult(i, nextReqCode);
            } else if (instance instanceof android.support.v4.app.Fragment) {
                ((android.support.v4.app.Fragment) instance)
                        .startActivityForResult(i, nextReqCode);
            } else if (instance instanceof android.app.Fragment) {
                ((android.app.Fragment) instance).startActivityForResult(i, nextReqCode);
            }
            return oututFile;

        }
        return null;
    }

    private void invokeCamera(Object instance) {
        Context context = activity;
        if (context == null) {
            if (fragment != null) {
                context = fragment.getActivity();
            } else if (fragmentSupport != null) {
                context = fragmentSupport.getActivity();
            }
        }
        int reqCode = cropImage ? REQ_CODE_CAMERA_WITH_CROP : this.reqCodeCamera;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            if (cameraFile != null) {
                if(Build.VERSION.SDK_INT < 24) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(cameraFile));
                } else {
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    if (instance instanceof Activity) {
                        Uri uri = FileProvider.getUriForFile((Activity) instance, BuildConfig.APPLICATION_ID + ".provider", cameraFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    } else if (instance instanceof android.support.v4.app.Fragment) {
                        Uri uri = FileProvider.getUriForFile(((android.support.v4.app.Fragment) instance).getActivity(), BuildConfig.APPLICATION_ID + ".provider", cameraFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    } else if (instance instanceof android.app.Fragment) {
                        Uri uri = FileProvider.getUriForFile(((android.app.Fragment) instance).getActivity(), BuildConfig.APPLICATION_ID + ".provider", cameraFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    }
                }
                if (instance instanceof Activity) {
                    ((Activity) instance).startActivityForResult(
                            takePictureIntent, reqCode);
                } else if (instance instanceof android.support.v4.app.Fragment) {
                    ((android.support.v4.app.Fragment) instance)
                            .startActivityForResult(takePictureIntent,
                                    reqCode);
                } else if (instance instanceof android.app.Fragment) {
                    ((android.app.Fragment) instance).startActivityForResult(
                            takePictureIntent, reqCode);
                }
            }
        }

    }

    private void invokeGallery(Object instance) {
        int reqCode = cropImage ? REQ_CODE_GALLERY_WITH_CROP : this.reqCodeGallery;

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        pickIntent.putExtra("return-data", true);
        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        if (instance instanceof Activity) {
            ((Activity) instance).startActivityForResult(chooserIntent,
                    reqCode);
        } else if (instance instanceof android.support.v4.app.Fragment) {
            ((android.support.v4.app.Fragment) instance)
                    .startActivityForResult(chooserIntent,
                            reqCode);
        } else if (instance instanceof android.app.Fragment) {
            ((android.app.Fragment) instance).startActivityForResult(
                    chooserIntent, reqCode);
        }
    }

    public static interface Callback {
        public void onBitmapResult(File file);

        public void onCancelGalleryCameraInvoker();
    }

    /**
     * Interface to get event callback when the UI has back to your application. But the image still
     * processed.
     */
    public static interface CallbackWithProcessing extends Callback {
        public void onProcessing();
    }
}

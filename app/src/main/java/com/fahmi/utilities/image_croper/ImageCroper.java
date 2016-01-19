package com.fahmi.utilities.image_croper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fahmi on 6/11/15.
 */
public class ImageCroper {
    public static final String IS_CROP = "IS_CROP";
    public static final String CURRENT_ID = "CURRENT_ID";
    public static final String FILE_CAMERA_PATH = "FILE_CAMERA_PATH";
    public static final String DEFAULT_CROP_DIR = "DEFAULT_CROP_DIR";
    public static final String FILE_IMAGE_CROP = "FILE_IMAGE_CROP";

    public static final String TAG = "image croper";
    public static final int RC_CAMERA = 1231;
    public static final int RC_GALERY = 1232;
    public static final int RC_CROP_IMAGE = 1233;
    private int currentId = 0;
    private boolean isCrop;
    private ImageCroperCallback callBackImageCropper;
    private String fileCameraPath;
    private String defaultCropDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    // need to improve
    private String fileImageCropName = "temp_image.jpg";


    public void onSaveInstanceState(Bundle outState){
        outState.putString(FILE_IMAGE_CROP, fileImageCropName);
        outState.putString(DEFAULT_CROP_DIR, defaultCropDir);
        outState.putString(FILE_CAMERA_PATH, fileCameraPath);
        outState.putBoolean(IS_CROP, isCrop);
        outState.putInt(CURRENT_ID, currentId);
    }

    public void onRestoreInstanceState(Bundle saveInstanceState){
        if(saveInstanceState != null){
            fileImageCropName = saveInstanceState.getString(FILE_IMAGE_CROP);
            defaultCropDir = saveInstanceState.getString(DEFAULT_CROP_DIR);
            fileCameraPath = saveInstanceState.getString(FILE_CAMERA_PATH);
            isCrop = saveInstanceState.getBoolean(IS_CROP);
            currentId = saveInstanceState.getInt(CURRENT_ID);
        }
    }

    public void setCallBackImageCropper(ImageCroperCallback callBackImageCropper) {
        this.callBackImageCropper = callBackImageCropper;
    }

    public void setIsCrop(boolean isCrop) {
        this.isCrop = isCrop;
    }

    public void showCamera(ImageCroperDelegate imageCroperDelegate, int id){
        try {
            imageCroperDelegate.startActivityForResultDelegate(createCameraIntent(id), RC_CAMERA);
        } catch (IOException e) {
            Log.d(TAG, "IOException Camera ");
        }
    }

    private Intent createCameraIntent(int id) throws IOException {
        this.currentId = id;
        File tempCameraFile = createImageFile();
        callBackImageCropper.onPrepareFile(tempCameraFile.getAbsolutePath(), currentId);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempCameraFile));
        return intent;
    }

    public void showGalery(ImageCroperDelegate imageCroperDelegate, int id){
        imageCroperDelegate.startActivityForResultDelegate(createGaleryIntent(id), RC_GALERY);
    }

    public Intent createGaleryIntent(int id){
        this.currentId = id;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        return intent;
    }

    public Intent createCropImageIntent(String sourcePath, String destinationPath){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        File src = new File(sourcePath);
        File dst = new File(destinationPath);
        System.out.println("source : " + sourcePath);
        System.out.println("destination : " + destinationPath);

        cropIntent.setDataAndType(Uri.fromFile(src), "image/*");
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(dst));

        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 1024);
        cropIntent.putExtra("outputY", 1024);
        cropIntent.putExtra("return-data", true);

        return cropIntent;
    }

    public void showCropImage(ImageCroperDelegate imageCroperDelegate, String sourcePath, String destinationPath){
        try {
            imageCroperDelegate.startActivityForResultDelegate(createCropImageIntent(sourcePath, destinationPath), RC_CROP_IMAGE);
        }
        catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            System.out.println(errorMessage);
        }
    }

    public void onActivityResult(ImageCroperDelegate imageCroperDelegate, int requestCode, int resultCode, Intent data){
        System.out.println("result code is " + resultCode + " request code is " + requestCode);
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case RC_CAMERA :
                    Log.d(TAG, "camera : " + fileCameraPath);
                    if(isCrop){
                        showCropImage(imageCroperDelegate, fileCameraPath, getFileStringPath(fileImageCropName));
                    }
                    else {
                        if(callBackImageCropper != null){
                            callBackImageCropper.onSuccess(fileCameraPath, currentId);
                            fileCameraPath = "";
                        }
                    }
                    break;
                case RC_GALERY :
                    Uri uri = data.getData();
                    String filePathGalery = getRealPathFromURI(imageCroperDelegate.getContext(), uri);
                    Log.d(TAG, "galery : " + filePathGalery);
                    if(isCrop){
                        showCropImage(imageCroperDelegate, filePathGalery, getFileStringPath(fileImageCropName));
                    }
                    else {
                        if(callBackImageCropper != null){
                            callBackImageCropper.onSuccess(filePathGalery, currentId);
                            fileCameraPath = "";
                        }
                    }
                    break;
                case RC_CROP_IMAGE:
                    callBackImageCropper.onSuccess(getFileStringPath(fileImageCropName), currentId);
                    break;
            }
        }
        else {
            System.out.println("execute delete file ");
            File file = new File(fileCameraPath);
            if(file.exists()){
                if(file.length() == 0){
                    file.delete();
                }
            }


        }
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        fileCameraPath = image.getAbsolutePath();
        Log.d(TAG, "camera " + fileCameraPath);
        return image;
    }


    public String getFileStringPath(String file){
        return new File(defaultCropDir, file).getAbsolutePath();
    }

    public void deleteCameraImage(){
//        String fileCameraPath = Prefs.getString(FILE_CAMERA_PATH, null);
        if(fileCameraPath != null){
            File file = new File(fileCameraPath);
            if(file.exists()){
                file.delete();
                fileCameraPath = "";
            }
        }
    }

    public boolean isCrop() {
        return isCrop;
    }
}

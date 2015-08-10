package com.fahmi.utilities.image_croper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by fahmi on 6/11/15.
 */
public class ImageCroper {
    public static int PICK_FROM_GALERY = 100;
    public static int PICK_FROM_CAMERA = 200;
    public static int CROP_IMAGE = 300;

    private boolean isFromCamera = false;
    private String fileImageCropFromGalery;
    private String resultCropImageFile = "";
    private File folderResutlImage = Environment.getExternalStorageDirectory();
    private ImageCroperCallback imageCroperCallback;
    private Activity context;

    private boolean crop;

    public ImageCroper(Activity context) {
        this.context = context;
//        resultCropImageFile = new File(Environment.getExternalStorageDirectory(), "temp_file").getAbsolutePath();
    }

    public void showGalery(){
        isFromCamera = false;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        context.startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FROM_GALERY);
    }

    public void showCamera(){
        isFromCamera = true;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
        context.startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void showCropImage(){
//
//        File source, destination ;
////
//            if(isFromCamera){
//                destination = new File(resultCropImageFile);
//                source = new File(resultCropImageFile);
//            }
//            else {
//                destination = new File(folderResutlImage, "temp_image.JPEG");
//                resultCropImageFile = destination.getAbsolutePath();
//                source = new File(fileImageCropFromGalery);
//            }
//        Crop.of(Uri.fromFile(source), Uri.fromFile(destination)).withMaxSize(1024,1024).withAspect(2,2).asSquare().start(context);
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            File source, destination ;

            if(isFromCamera){
                destination = new File(resultCropImageFile);
                source = new File(resultCropImageFile);
            }
            else {
                destination = new File(folderResutlImage, "temp_image.JPEG");
                resultCropImageFile = destination.getAbsolutePath();
                source = new File(fileImageCropFromGalery);
            }
            System.out.println("source : " + source.getAbsolutePath());
            System.out.println("destination : " + destination.getAbsolutePath());

            cropIntent.setDataAndType(Uri.fromFile(source), "image/*");
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));

            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 1024);
            cropIntent.putExtra("outputY", 1024);
            cropIntent.putExtra("return-data", true);

            context.startActivityForResult(cropIntent, CROP_IMAGE);
        }
        catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void extractFile(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PICK_FROM_CAMERA || requestCode == PICK_FROM_GALERY){
                if(requestCode == PICK_FROM_GALERY){
                    Uri dataPhoto = data.getData();
                    fileImageCropFromGalery  = getRealPathFromURI(dataPhoto);
                    System.out.println("file galery " + fileImageCropFromGalery);
                }

                if(isCrop()){
                    showCropImage();
                }
                else {
                    callImageCroperCallback(true);
                }

            }
            else if(requestCode == CROP_IMAGE){
                callImageCroperCallback(true);
            }
        }

    }


    private String getRealPathFromURI(Uri contentUri) {
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

    private File createImageFile() {
        File image = null;
        try {
            image = File.createTempFile("TEMP_", ".JPEG", folderResutlImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        resultCropImageFile = image.getAbsolutePath();
        System.out.println("file temp : " + resultCropImageFile);
        return image;
    }


    public String getFileImage() {
        return resultCropImageFile;
    }

    public boolean isCrop() {
        return crop;
    }

    public void setCrop(boolean crop) {
        this.crop = crop;
    }

    public void deleteFile(){
        File file = new File(resultCropImageFile);
        if(file.exists()){
            file.delete();
        }
    }

    public void setImageCroperCallback(ImageCroperCallback imageCroperCallback){
        this.imageCroperCallback = imageCroperCallback;
    }

    private void callImageCroperCallback(boolean success){
        if(imageCroperCallback != null){
            if(success){
                this.imageCroperCallback.onSuccess(resultCropImageFile);
            }
            else {
                this.imageCroperCallback.onFailure("Cancel cal");
            }

        }
    }

}

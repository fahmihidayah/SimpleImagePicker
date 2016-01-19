package com.fahmi.simpleimagepicker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fahmi.utilities.image_croper.ImageCroper;
import com.fahmi.utilities.image_croper.ImageCroperCallback;
import com.fahmi.utilities.image_croper.ImageCroperDelegate;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ImagePickerActivity extends AppCompatActivity implements ImageCroperDelegate {


    @InjectView(R.id.imageView)
    ImageView imageView;

    @OnClick(R.id.button_camera)
    public void onClickButtonCamera(View v){
        imageCroper.showCamera(this, 1);
    }

    @OnClick(R.id.button_galery)
    public void onClickGalery(View view){
        imageCroper.showGalery(this, 1);
    }

    ImageCroper imageCroper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        ButterKnife.inject(this);
        System.out.println("execute onCreate");
        imageCroper = new ImageCroper();
        imageCroper.setIsCrop(true);
        imageCroper.setCallBackImageCropper(new ImageCroperCallback() {

            @Override
            public void onPrepareFile(String path, int id) {

            }

            @Override
            public void onSuccess(String path, int id) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(String message) {
                Log.d("image cropper error", message);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("execute onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("execute onRestart");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("execute onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        System.out.println("execute onRestoreInstanceState");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("execute onActivityResult");
        imageCroper.onActivityResult(this, requestCode,resultCode,data);
    }

    @Override
    public void startActivityForResultDelegate(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public Context getContext() {
        return this;
    }
}

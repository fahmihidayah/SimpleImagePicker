package com.fahmi.simpleimagepicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fahmi.utilities.image_croper.ImageCroper;
import com.fahmi.utilities.image_croper.ImageCroperCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ImagePickerActivity extends AppCompatActivity {


    @InjectView(R.id.imageView)
    ImageView imageView;

    @OnClick(R.id.button_camera)
    public void onClickButtonCamera(View v){
        imageCroper.showCamera();
    }

    @OnClick(R.id.button_galery)
    public void onClickGalery(View view){
        imageCroper.showGalery();
    }

    ImageCroper imageCroper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        ButterKnife.inject(this);
        imageCroper = new ImageCroper(this);
        imageCroper.setCrop(true);
        imageCroper.setImageCroperCallback(new ImageCroperCallback() {
            @Override
            public void onSuccess(String path) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                imageView.setImageBitmap(bitmap);
                imageCroper.deleteFile();
            }

            @Override
            public void onFailure(String message) {
                Log.d("image cropper error", message);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageCroper.extractFile(requestCode,resultCode,data);
    }
}

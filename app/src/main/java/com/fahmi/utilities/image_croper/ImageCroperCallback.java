package com.fahmi.utilities.image_croper;

/**
 * Created by fahmi on 7/27/15.
 */
public interface ImageCroperCallback {
    public void onPrepareFile(String path, int id);
    public void onSuccess(String path, int id);
    public void onFailure(String message);
}

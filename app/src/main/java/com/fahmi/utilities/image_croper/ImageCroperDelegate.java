package com.fahmi.utilities.image_croper;

import android.content.Context;
import android.content.Intent;

/**
 * Created by fahmi on 1/19/16.
 */
public interface ImageCroperDelegate {
    public void startActivityForResultDelegate(Intent intent, int requestCode);
    public Context getContext();
}

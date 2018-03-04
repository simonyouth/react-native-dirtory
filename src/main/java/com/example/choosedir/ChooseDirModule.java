package com.example.choosedir;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.ArrayList;

/**
 * Created by zyx on 2018/2/3.
 */

public class ChooseDirModule extends ReactContextBaseJavaModule {
    private Context context;
    public static final int PATHREQUESTCODE = 44;
    private static String mpath;
    private Promise mPickerPromise;

    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String E_PICKER_CANCELLED = "E_PICKER_CANCELLED";
    private static final String E_FAILED_TO_SHOW_PICKER = "E_FAILED_TO_SHOW_PICKER";
    private static final String E_NO_IMAGE_DATA_FOUND = "E_NO_IMAGE_DATA_FOUND";

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener(){
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

            if(mPickerPromise != null) {
                if (requestCode == PATHREQUESTCODE && resultCode == ChooseFileActivity.RESULTCODE) {
                    ArrayList<String> resPath = data.getStringArrayListExtra(ChooseFileActivity.SELECTPATH);
                    mpath = resPath.toString().substring(1,resPath.toString().length()-1);
                    mPickerPromise.resolve(mpath);

                }
                mPickerPromise = null;
            }
        }
    };

    public ChooseDirModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "PathSetting";
    }

    @ReactMethod
    public void choose(final Promise promise){
        Activity activity = getCurrentActivity();
        if(activity == null){
            promise.reject( E_ACTIVITY_DOES_NOT_EXIST,"没有activity");
            return;
        }
        mPickerPromise = promise;
        try {
            ChooseFileActivity.enterActivityForResult(getCurrentActivity(), PATHREQUESTCODE);
        }catch (Exception e){
            mPickerPromise.reject(E_FAILED_TO_SHOW_PICKER,e);
            mPickerPromise = null;
        }

    }

}

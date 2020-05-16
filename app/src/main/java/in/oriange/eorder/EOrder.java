package in.oriange.eorder;

import android.app.Application;

import in.oriange.eorder.utilities.TypefaceUtil;

public class EOrder extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/ebrima.ttf"); // font from assets
    }


}

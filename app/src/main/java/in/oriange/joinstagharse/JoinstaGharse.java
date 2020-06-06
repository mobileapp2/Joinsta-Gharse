package in.oriange.joinstagharse;

import android.app.Application;

import in.oriange.joinstagharse.utilities.TypefaceUtil;

public class JoinstaGharse extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/ebrima.ttf"); // font from assets
    }


}

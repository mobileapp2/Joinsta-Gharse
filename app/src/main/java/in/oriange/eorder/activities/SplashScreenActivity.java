package in.oriange.eorder.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import in.oriange.eorder.R;
import in.oriange.eorder.utilities.UserSessionManager;

public class SplashScreenActivity extends AppCompatActivity {

    private Context context;
    private UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        init();
    }

    private void init() {
        context = SplashScreenActivity.this;
        session = new UserSessionManager(context);

        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (session.isUserLoggedIn()) {
                    startActivity(new Intent(context, MainDrawerActivity.class)
                            .putExtra("startOrigin", 0));
                } else {
                    startActivity(new Intent(context, LoginActivity.class));
                }
                finish();
            }
        }, secondsDelayed * 500);
    }

}

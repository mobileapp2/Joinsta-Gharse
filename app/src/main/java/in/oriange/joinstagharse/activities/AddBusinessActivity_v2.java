package in.oriange.joinstagharse.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.google.android.material.button.MaterialButton;
import com.kofigyan.stateprogressbar.StateProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.adapters.AddBusinessViewPagerAdapter;
import in.oriange.joinstagharse.adapters.BotNavViewPagerAdapter;
import in.oriange.joinstagharse.utilities.Utilities;

import static in.oriange.joinstagharse.utilities.Utilities.changeStatusBar;

public class AddBusinessActivity_v2 extends AppCompatActivity {

    @BindView(R.id.btn_save)
    MaterialButton btnSave;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    AppCompatEditText toolbarTitle;
    @BindView(R.id.status_progress_bar)
    StateProgressBar statusProgressBar;
    @BindView(R.id.view_pager)
    AHBottomNavigationViewPager viewPager;

    private Context context;
    private ProgressDialog pd;

    private Fragment currentFragment;
    private AddBusinessViewPagerAdapter adapter;

    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business_v2);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventListner();
        setUpToolbar();

    }

    private void init() {
        context = AddBusinessActivity_v2.this;
        changeStatusBar(context, getWindow());
        adapter = new AddBusinessViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
    }

    private void getSessionDetails() {
    }

    private void setDefault() {

        String[] descriptionData = {"Business", "Contact", "Other"};
        statusProgressBar.setStateDescriptionData(descriptionData);
        updatePosition();
    }

    private void setEventListner() {
        btnSave.setOnClickListener(v -> {
            if (currentPosition < 2) {
                currentPosition = currentPosition + 1;
                updatePosition();
            }
        });
    }

    private void updatePosition() {
        if (currentFragment == null) {
            currentFragment = adapter.getCurrentFragment();
        }

        viewPager.setCurrentItem(currentPosition, true);

        if (currentFragment == null) {
            currentFragment = adapter.getCurrentFragment();
        }

        switch (currentPosition) {
            case 0:
                btnSave.setText("Next");
                statusProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                break;
            case 1:
                btnSave.setText("Next");
                statusProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                break;
            case 2:
                btnSave.setText("Save");
                statusProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                break;
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow_black);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    public void onBackPressed() {
        if (currentPosition == 0) {
            finish();
        } else {
            currentPosition = currentPosition - 1;
            updatePosition();
        }
    }
}
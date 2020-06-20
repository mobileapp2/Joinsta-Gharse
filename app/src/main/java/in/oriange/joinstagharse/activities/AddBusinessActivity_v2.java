package in.oriange.joinstagharse.activities;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anton46.stepsview.StepsView;
import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.joinstagharse.R;

public class AddBusinessActivity_v2 extends AppCompatActivity {

    @BindView(R.id.stepsView)
    StepsView stepsView;
    @BindView(R.id.btn_save)
    MaterialButton btnSave;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business_v2);
        ButterKnife.bind(this);
        final String[] labels = {"Step 1", "Step 2", "Step 3"};

        Context context = AddBusinessActivity_v2.this;
        stepsView.setLabels(labels)
                .setBarColorIndicator(getResources().getColor(R.color.mediumGray))
                .setProgressColorIndicator(getResources().getColor(R.color.colorPrimary))
                .setLabelColorIndicator(getResources().getColor(R.color.colorPrimary))
                .drawView();
        stepsView.setCompletedPosition(0);
    }
}
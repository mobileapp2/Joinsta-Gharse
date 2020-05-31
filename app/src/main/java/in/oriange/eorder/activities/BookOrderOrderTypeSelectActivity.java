package in.oriange.eorder.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.oriange.eorder.R;

public class BookOrderOrderTypeSelectActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cv_text)
    CardView cvText;
    @BindView(R.id.cv_image)
    CardView cvImage;
    @BindView(R.id.cv_products)
    CardView cvProducts;

    private Context context;
    private String businessOwnerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_type_select);
        ButterKnife.bind(this);

        init();
        getSessionDetails();
        setDefault();
        setEventHandler();
        setUpToolbar();
    }

    private void init() {
        context = BookOrderOrderTypeSelectActivity.this;
    }

    private void getSessionDetails() {

    }

    private void setDefault() {
        businessOwnerId = getIntent().getStringExtra("businessOwnerId");
    }

    private void setEventHandler() {
//        cvText.setOnClickListener(v -> startActivity(new Intent(context, BookOrderOrderTypeTextActivity.class)
//                .putExtra("businessOwnerId", businessOwnerId)));

//        cvImage.setOnClickListener(v -> startActivity(new Intent(context, BookOrderOrderTypeImageActivity.class)
//                .putExtra("businessOwnerId", businessOwnerId)));

        cvProducts.setOnClickListener(v -> startActivity(new Intent(context, BookOrderProductsListActivity.class)
                .putExtra("businessOwnerId", businessOwnerId)));
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.icon_backarrow);
        toolbar.setNavigationOnClickListener(view -> finish());
    }
}

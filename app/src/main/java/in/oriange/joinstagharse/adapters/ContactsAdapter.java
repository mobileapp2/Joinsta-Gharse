package in.oriange.joinstagharse.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.AddCustomerActivity;
import in.oriange.joinstagharse.activities.AddVendorActivity;
import in.oriange.joinstagharse.models.ContactsModel;

import static android.Manifest.permission.CALL_PHONE;
import static in.oriange.joinstagharse.utilities.Utilities.provideCallPremission;
import static in.oriange.joinstagharse.utilities.Utilities.showCallDialog;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    private Context context;
    private List<ContactsModel> list;

    public ContactsAdapter(Context context, List<ContactsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_contact, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        ContactsModel contactsModel = list.get(position);

        holder.tv_initial.setText(contactsModel.getInitLetter());
        holder.tv_name.setText(contactsModel.getName());
        holder.tv_mobile.setText(contactsModel.getPhoneNo());

        holder.ib_call.setOnClickListener(v -> {
            showCallDialog(context,  holder.tv_mobile.getText().toString().trim());
        });

        holder.imv_add.setOnClickListener(v -> showContextMenu(v, contactsModel));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_main_layout;
        private TextView tv_initial, tv_name, tv_mobile;
        private ImageButton imv_add, ib_call;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imv_add = itemView.findViewById(R.id.imv_add);
            cv_main_layout = itemView.findViewById(R.id.cv_main_layout);
            tv_initial = itemView.findViewById(R.id.tv_initial);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_mobile = itemView.findViewById(R.id.tv_mobile);
            ib_call = itemView.findViewById(R.id.ib_call);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @SuppressLint("RestrictedApi")
    private void showContextMenu(View view, ContactsModel contactsModel) {
        String mobile = "";
        String countryCode = "";

        try {
            if (contactsModel.getPhoneNo().length() > 10) {
                mobile = contactsModel.getPhoneNo().substring(contactsModel.getPhoneNo().length() - 10);
                countryCode = contactsModel.getPhoneNo().substring(0, contactsModel.getPhoneNo().length() - mobile.length());
            } else {
                mobile = contactsModel.getPhoneNo();
                countryCode = "+91";
            }
        } catch (Exception e) {
            mobile = "";
            countryCode = "+91";
        }

        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.menu_vendor_cudtomer);
        String finalCountryCode = countryCode;
        String finalMobile = mobile;
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_vendor:
                    context.startActivity(new Intent(context, AddVendorActivity.class)
                            .putExtra("businessCode", "")
                            .putExtra("businessName", "")
                            .putExtra("name", contactsModel.getName())
                            .putExtra("countryCode", finalCountryCode)
                            .putExtra("mobile", finalMobile)
                            .putExtra("email", "")
                            .putExtra("city", ""));
                    break;
                case R.id.menu_customer:
                    context.startActivity(new Intent(context, AddCustomerActivity.class)
                            .putExtra("businessCode", "")
                            .putExtra("businessName", "")
                            .putExtra("name", contactsModel.getName())
                            .putExtra("countryCode", finalCountryCode)
                            .putExtra("mobile", finalMobile)
                            .putExtra("email", "")
                            .putExtra("city", ""));
                    break;
            }
            return false;
        });

        MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) popup.getMenu(), view);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }
}

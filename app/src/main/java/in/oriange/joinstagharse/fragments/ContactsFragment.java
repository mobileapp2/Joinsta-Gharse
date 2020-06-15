package in.oriange.joinstagharse.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.adapters.ContactsAdapter;
import in.oriange.joinstagharse.models.ContactsModel;

import static android.Manifest.permission.READ_CONTACTS;
import static in.oriange.joinstagharse.utilities.Utilities.provideReadContactsPremission;

public class ContactsFragment extends Fragment {

    private Context context;

    private EditText edt_search;
    private ImageButton imb_refresh;
    private RecyclerView rv_contacts;
    private AppCompatTextView tv_no_review;
    private SpinKitView progressBar;
    private LinearLayout ll_nopreview;
    private List<ContactsModel> contactList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        context = getActivity();
        init(rootView);
        setDefault();
        getSessionDetails();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        edt_search = rootView.findViewById(R.id.edt_search);
        imb_refresh = rootView.findViewById(R.id.imb_refresh);
        tv_no_review = rootView.findViewById(R.id.tv_no_review);
        rv_contacts = rootView.findViewById(R.id.rv_contacts);
        progressBar = rootView.findViewById(R.id.progressBar);
        ll_nopreview = rootView.findViewById(R.id.ll_nopreview);

        rv_contacts.setLayoutManager(new LinearLayoutManager(context));
        contactList = new ArrayList<>();
    }

    private void setDefault() {
        if (ActivityCompat.checkSelfPermission(context, READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            rv_contacts.setVisibility(View.GONE);
            ll_nopreview.setVisibility(View.VISIBLE);
            tv_no_review.setText("Please provide premission to read contacts");
        } else {
            new GetPhoneBookContacts().execute();
        }
    }

    private void getSessionDetails() {
    }

    private void setEventHandler() {
        imb_refresh.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(context, READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                provideReadContactsPremission(context);
            } else {
                new GetPhoneBookContacts().execute();
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                if (query.toString().isEmpty()) {
                    rv_contacts.setAdapter(new ContactsAdapter(context, contactList));
                    return;
                }

                if (contactList.size() == 0) {
                    rv_contacts.setVisibility(View.GONE);
                    return;
                }

                if (!query.toString().equals("")) {
                    ArrayList<ContactsModel> contactSearchedList = new ArrayList<>();
                    for (ContactsModel orderDetails : contactList) {

                        String orderToBeSearched = orderDetails.getName().toLowerCase() +
                                orderDetails.getPhoneNo().toLowerCase();

                        if (orderToBeSearched.contains(query.toString().toLowerCase())) {
                            contactSearchedList.add(orderDetails);
                        }
                    }
                    rv_contacts.setAdapter(new ContactsAdapter(context, contactSearchedList));
                } else {
                    rv_contacts.setAdapter(new ContactsAdapter(context, contactList));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private class GetPhoneBookContacts extends AsyncTask<Void, Void, List<ContactsModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            rv_contacts.setVisibility(View.GONE);
        }

        @Override
        protected List<ContactsModel> doInBackground(Void... voids) {
            return getContactList();
        }

        @Override
        protected void onPostExecute(List<ContactsModel> list) {
            super.onPostExecute(list);
            progressBar.setVisibility(View.GONE);
            if (list.size() != 0) {
                rv_contacts.setVisibility(View.VISIBLE);
                ll_nopreview.setVisibility(View.GONE);
                rv_contacts.setAdapter(new ContactsAdapter(context, list));
            } else {
                rv_contacts.setVisibility(View.GONE);
                ll_nopreview.setVisibility(View.VISIBLE);
            }
        }
    }

    private List<ContactsModel> getContactList() {
        contactList = new ArrayList<>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        assert phones != null;
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactList.add(new ContactsModel(String.valueOf(name.charAt(0)).toUpperCase(), name, phoneNumber.replace("-", "").replace(" ", "")));
        }
        phones.close();

        Set<ContactsModel> s = new HashSet<>(contactList);
        contactList.clear();
        contactList.addAll(s);

        Collections.sort(contactList, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        return contactList;
    }


}

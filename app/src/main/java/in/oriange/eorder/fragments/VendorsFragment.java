package in.oriange.eorder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import in.oriange.eorder.R;

public class VendorsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vendors, container, false);
//        context = getActivity();
//        init(rootView);
//        getSessionDetails();
//        setDefault();
//        setEventHandler();
        return rootView;
    }


}

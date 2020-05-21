package in.oriange.eorder.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import in.oriange.eorder.R;
import in.oriange.eorder.utilities.NonSwipeableViewPager;

public class MyOrdersFragment extends Fragment {

    private Context context;
    private SmartTabLayout tabs;
    private NonSwipeableViewPager viewpager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_orders, container, false);
        context = getActivity();
        init(rootView);
        setDefault();
        setEventHandler();
        return rootView;
    }

    private void init(View rootView) {
        context = getActivity();
        tabs = rootView.findViewById(R.id.tabs);
        viewpager = rootView.findViewById(R.id.viewpager);
    }

    private void setDefault() {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new ReceivedOrdersFragment(), "Received Orders");
        adapter.addFrag(new SentOrdersFragment(), "Sent Orders");

        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(2);
        tabs.setViewPager(viewpager);
    }

    private void setEventHandler() {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}

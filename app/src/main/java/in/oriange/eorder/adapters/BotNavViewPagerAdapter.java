package in.oriange.eorder.adapters;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

import in.oriange.eorder.fragments.HomeFragment;
import in.oriange.eorder.fragments.MoreFragment;
import in.oriange.eorder.fragments.MyOrdersFragment;
import in.oriange.eorder.fragments.SearchFragment;
import in.oriange.eorder.fragments.VendorCustomerContactsFragment;

public class BotNavViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Fragment currentFragment;

    public BotNavViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.clear();
        fragments.add(new HomeFragment());
        fragments.add(new SearchFragment());
        fragments.add(new VendorCustomerContactsFragment());
        fragments.add(new MyOrdersFragment());
        fragments.add(new MoreFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }
}
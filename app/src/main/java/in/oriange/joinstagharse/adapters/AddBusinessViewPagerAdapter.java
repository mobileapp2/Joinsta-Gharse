package in.oriange.joinstagharse.adapters;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

import in.oriange.joinstagharse.fragments.AddBusinessContactDetails;
import in.oriange.joinstagharse.fragments.AddBusinessGeneralDetails;
import in.oriange.joinstagharse.fragments.AddBusinessOtherDetails;
import in.oriange.joinstagharse.fragments.HomeFragment;
import in.oriange.joinstagharse.fragments.MoreFragment;
import in.oriange.joinstagharse.fragments.MyOrdersFragment;
import in.oriange.joinstagharse.fragments.SearchFragment;
import in.oriange.joinstagharse.fragments.VendorCustomerContactsFragment;

public class AddBusinessViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Fragment currentFragment;

    public AddBusinessViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.clear();
        fragments.add(new AddBusinessGeneralDetails());
        fragments.add(new AddBusinessContactDetails());
        fragments.add(new AddBusinessOtherDetails());
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
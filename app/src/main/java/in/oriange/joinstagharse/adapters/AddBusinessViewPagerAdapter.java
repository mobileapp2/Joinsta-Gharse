package in.oriange.joinstagharse.adapters;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

import in.oriange.joinstagharse.fragments.AddBusinessContactDetailsFragment;
import in.oriange.joinstagharse.fragments.AddBusinessGeneralDetailsFragment;
import in.oriange.joinstagharse.fragments.AddBusinessOtherDetailsFragment;

public class AddBusinessViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private Fragment currentFragment;

    public AddBusinessViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments.clear();
        fragments.add(new AddBusinessGeneralDetailsFragment());
        fragments.add(new AddBusinessContactDetailsFragment());
        fragments.add(new AddBusinessOtherDetailsFragment());
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
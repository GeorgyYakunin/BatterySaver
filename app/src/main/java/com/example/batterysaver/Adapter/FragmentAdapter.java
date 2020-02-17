package com.example.batterysaver.Adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments; //Fragment List
    private List<String> namePage; // Fragment Name List

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        namePage = new ArrayList<>();
    }

    public void add(Fragment Frag, String Title) {
        fragments.add(Frag);
        namePage.add(Title);
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return namePage.get(position);
    }
}

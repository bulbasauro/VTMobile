package com.bulbasauro.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bulbasauro.fragments.CrieiTopicosFragment;
import com.bulbasauro.fragments.ParticipeiTopicosFragment;

/**
 * Created on 09/02/2016.
 */
public class MeusTopicosFragment extends FragmentPagerAdapter {

    public MeusTopicosFragment(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new CrieiTopicosFragment();
                break;
            case 1:
                fragment = new ParticipeiTopicosFragment();
                break;
            default:
                fragment = new CrieiTopicosFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Criei";
                break;
            case 1:
                title = "Participei";
                break;
            default:
                title = "Erro!";
                break;
        }
        return title;
    }
}

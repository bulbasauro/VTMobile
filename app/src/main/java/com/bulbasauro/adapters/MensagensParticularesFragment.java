package com.bulbasauro.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bulbasauro.fragments.MpInboxFragment;
import com.bulbasauro.fragments.MpOutboxFragment;

/**
 * Created on 28/01/2016.
 */
public class MensagensParticularesFragment extends FragmentPagerAdapter {

    public MensagensParticularesFragment(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new MpInboxFragment();
                break;
            case 1:
                fragment = new MpOutboxFragment();
                break;
            default:
                fragment = new MpInboxFragment();
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
                title = "Recebidas";
                break;
            case 1:
                title = "Enviadas";
                break;
            default:
                title = "Erro!";
                break;
        }
        return title;
    }
}

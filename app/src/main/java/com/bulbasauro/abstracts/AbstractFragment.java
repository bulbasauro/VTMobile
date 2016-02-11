package com.bulbasauro.abstracts;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ListView;

import com.bulbasauro.async.jsoup.JsoupMpInbox;
import com.bulbasauro.async.jsoup.JsoupMpOutbox;

import org.jsoup.nodes.Document;

/**
 * Created on 09/02/2016.
 */
public abstract class AbstractFragment extends AbstractMenu {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private ListView listViewInbox;

    private ListView listViewOutbox;
    private JsoupMpInbox inbox;
    private JsoupMpOutbox outbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        getViewPager().setAdapter(getFragmentPagerAdapter());
        getTabLayout().setupWithViewPager(getViewPager());
        getViewPager().addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(getTabLayout()));
        getTabLayout().setTabsFromPagerAdapter(getFragmentPagerAdapter());

        String bemVindo = getIntent().getExtras().getString("userName");
        getSupportActionBar().setTitle(bemVindo);
    }

    public abstract void setInfo(Document document);

    // Getters e Setters

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    public void setTabLayout(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    public FragmentPagerAdapter getFragmentPagerAdapter() {
        return fragmentPagerAdapter;
    }

    public void setFragmentPagerAdapter(FragmentPagerAdapter fragmentPagerAdapter) {
        this.fragmentPagerAdapter = fragmentPagerAdapter;
    }
}

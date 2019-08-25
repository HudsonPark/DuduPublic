package org.palpitat.dudu.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import org.palpitat.dudu.fragment.AlbumFolderFragment;
import org.palpitat.dudu.fragment.AlbumStoryFragment;

public class AlbumTabPagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;
    private Fragment fragment;

    public AlbumTabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;

    }

    // 2019.06.23 탭 포지션 값에 따라 각각 다른 프래그먼트 리턴 by Hudson
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            // 앨범 탭 클릭 시
            case 0:
                fragment = new AlbumFolderFragment();
                break;
            // 스토리 탭 클릭 시
            case 1:
                fragment = new AlbumStoryFragment();
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}

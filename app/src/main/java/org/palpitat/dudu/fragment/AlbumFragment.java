package org.palpitat.dudu.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.palpitat.dudu.Adapter.AlbumTabPagerAdapter;
import org.palpitat.dudu.R;

public class AlbumFragment extends Fragment {

    private ViewPager vp_album;
    private TabLayout tl_album;
    private AlbumTabPagerAdapter adapter_tabPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_album,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tl_album = view.findViewById(R.id.tl_album);
        vp_album = view.findViewById(R.id.vp_album);

        tl_album.addTab(tl_album.newTab().setText("앨범"));
        tl_album.addTab(tl_album.newTab().setText("스토리"));

        adapter_tabPager = new AlbumTabPagerAdapter(getFragmentManager(), tl_album.getTabCount());
        vp_album.setAdapter(adapter_tabPager);

        vp_album.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tl_album));

        tl_album.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_album.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}

package org.palpitat.dudu.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import org.palpitat.dudu.R;
import org.palpitat.dudu.fragment.AlbumFragment;
import org.palpitat.dudu.fragment.HomeFragment;
import org.palpitat.dudu.fragment.ScheduleFragment;
import org.palpitat.dudu.fragment.SettingFragment;

public class HomeActivity extends AppCompatActivity {

    String coupleKey = "";
    String myEmail = "";
    String nicname = "";

    BottomNavigationView bottomNavigationView;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 커플키, 나의 이메일 주소, 닉네임 가져옴
        getCoupleKey();

        /** BottomNavigationView **/
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);

        fragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_fragment_layout, fragment);
        ft.commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_album:
                        fragment = new AlbumFragment();
                        break;
                    case R.id.action_record:
                        //fragment = new RecordFragment();
                        break;
                    case R.id.action_schedule:
                        fragment = new ScheduleFragment();
                        break;
                    case R.id.action_setting:
                        fragment = new SettingFragment();
                        break;
                    default :
                        break;
                }
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_fragment_layout, fragment);
                ft.commit();

                return true;
            }
        });
    }

    private void getCoupleKey() {
        coupleKey = getIntent().getStringExtra("USERKEY");
        myEmail = getIntent().getStringExtra("MYEMAIL");
        nicname = getIntent().getStringExtra("NICNAME");
    }
}

package kr.tento.activity;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import kr.tento.PlaylistActionBar;
import kr.tento.R;

public class TentoFragmentActivity extends FragmentActivity {

    // ActionBar의 탭이 가리키고있는 인덱스
    protected int tabPosition = 0;

    protected PlaylistActionBar actionBar;

    /**
     * ActionBar.TabListner 의 onTabSelected, onTabReSelected때 작동하는 콜백 메소드
     *
     * @param i 선택된 탭의 인덱스
     */
    protected void tabSelected(int i) {
    }

    /**
     * ActionBar.TabListner 의 onTabUnSelected 때 작동하는 콜백 메소드
     *
     * @param i 선택해제된 탭의 인덱스
     */
    protected void tabUnselected(int i) {
    }

    @Override
    public void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        actionBar = new PlaylistActionBar(this);

            }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_actionbar,menu);
        actionBar.setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                tabPosition = tab.getPosition();
                tabSelected(tabPosition);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                tabUnselected(tabPosition);
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                tabPosition = tab.getPosition();
                tabSelected(tabPosition);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, PlayingActivity.class);
        startActivity(intent);
        return true;
    }
}

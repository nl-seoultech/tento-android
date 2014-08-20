package kr.tento;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.view.ViewPager;
public class PlaylistActionBar {

    /** tento 프로토타입에 쓰이는 `ActionBar`를 정의하는 클래스
     *
     */

        // ActionBar 를 활성화시킬 activity
        private Activity activity;

        // activity로부터 가져온 ActionBar
        private ActionBar actionBar;

        // AllSong Tab
        private ActionBar.Tab AllSong;

        // Artist Tab
        private ActionBar.Tab Artist;

        // Albumt Tab
        private ActionBar.Tab Album;

        // Mood Tab
        private ActionBar.Tab Mood;

        /**
         *
         * @param act ActionBar를 활성화 시킬 Activity activity.getActionBar()로 actionBar 를
         *            사용하기위해서 받아옵니다.
         */
        public PlaylistActionBar(Activity act) {
            activity = act;
            actionBar = activity.getActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            initTabs();
        }


        public void initTabs() {
            AllSong = actionBar.newTab().setText("AllSong");
            Artist = actionBar.newTab().setText("Artist");
            Album = actionBar.newTab().setText("Album");
            Mood = actionBar.newTab().setText("Mood");
        }


        public void setTabListener(ActionBar.TabListener tabListener) {
            AllSong.setTabListener(tabListener);
            Artist.setTabListener(tabListener);
            Album.setTabListener(tabListener);
            Mood.setTabListener(tabListener);

            actionBar.addTab(AllSong);
            actionBar.addTab(Artist);
            actionBar.addTab(Album);
            actionBar.addTab(Mood);
        }

        /**
         * Fragment에서 스와이프로 탭을 옮겨가면 탭 선택자도 바꾸기위해서 ViewPager를 받아옵니다.
         *
         * @param pager Fragment가 사용하는 ViewPager
         */
        public void setViewPager(ViewPager pager) {
            pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    actionBar.setSelectedNavigationItem(position);
                }
            });
        }
    }

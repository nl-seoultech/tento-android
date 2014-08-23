package kr.tento.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import java.util.ArrayList;

import kr.tento.PlayService;
import kr.tento.R;
import kr.tento.activity.fragement.AlbumFragment;
import kr.tento.activity.fragement.AllSongFragment;
import kr.tento.activity.fragement.ArtistFragment;
import kr.tento.activity.fragement.MoodFragment;

public class PlaylistFragmentActivity extends TentoFragmentActivity implements AllSongFragment.PlaylistInterface{

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    AllSongFragment allsongf;

    Intent intent = new Intent("tento.PlaySongService");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmentactivity_playlist);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        actionBar.setViewPager(mViewPager);
        allsongf = (AllSongFragment) mSectionsPagerAdapter.fragments.get(0);


    }

    @Override
    protected void tabSelected(int i) {
        mViewPager.setCurrentItem(i);
    }

    @Override
    public void startSong(String path) {

        intent.putExtra("func", PlayService.START);
        intent.putExtra("path", path);
        startService(intent);

        Intent intent1 = new Intent(this, PlayingActivity.class);
        startActivity(intent1);
    }
}



/**
 * Fragment를 ViewPager에 적용시키기위한 아답터.
 */
class SectionsPagerAdapter extends FragmentPagerAdapter {

    // Tab에 들어가는 Fragment를 담는 ArrayList
    ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        initFragments();
    }

    private void initFragments() {
        fragments.add(new AllSongFragment());
        fragments.add(new ArtistFragment());
        fragments.add(new AlbumFragment());
        fragments.add(new MoodFragment());
    }

    /**
     *
     * @param position tab이 선택된 위치
     * @return 선택된 Fragment
     */
    @Override
    public Fragment getItem(int position) {
        // 만약에 이상한숫자가 들어온다면 0으로 position을 바꿈. 이거없으면 ArrayIndexOutOfBound 날수있음
        if(position > fragments.size()) {
            position = 0;
        }

        Fragment fragment = fragments.get(position);
        Bundle args = new Bundle();
        args.putInt("position", position + 1); // tab의 인덱스는 항상 position으로 Bundle에 넘김.
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}


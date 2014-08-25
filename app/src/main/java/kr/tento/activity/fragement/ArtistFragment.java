package kr.tento.activity.fragement;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import kr.tento.MusicFinder;
import kr.tento.PlayService;
import kr.tento.R;
import kr.tento.model.Artist;
import kr.tento.model.Music;

public class ArtistFragment extends Fragment {

    private MusicFinder musicFinder;

    private AllSongFragment.PlaylistInterface pi;

    ArrayAdapter<String> listadapter;

    ListView listview;

    Activity activity;

    SlidingUpPanelLayout slidingUpPanelLayout;

    ListView listviewDetail;

    Button btnBackArtist;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);
        Bundle args = getArguments();
        activity = getActivity();
        musicFinder = new MusicFinder(activity);
        musicFinder.findArtist(false);
        listview = (ListView)rootView.findViewById(R.id.listArtist);
        listviewDetail = (ListView)rootView.findViewById(R.id.listArtistDetail);
        slidingUpPanelLayout = (SlidingUpPanelLayout)rootView.findViewById(R.id.layoutSlideArtist);
        slidingUpPanelLayout.setEnabled(false);
        btnBackArtist = (Button)rootView.findViewById(R.id.btnBackArtist);
        btnBackArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.hidePanel();
            }
        });

        listadapter = new ArrayAdapter<String>(
                activity,
                android.R.layout.simple_list_item_1,
                musicFinder.getArtistNames());
        listview.setAdapter(listadapter);
        listview.setOnItemClickListener(new ListViewItemClickListener()); //리스트 클릭 이벤트

        return rootView;
    }


    private class ListViewItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
            if(arg0.equals(listview)) {
                Artist at = musicFinder.findArtistByIndex(arg2);
                musicFinder.findMusic(true, musicFinder.ARTIST, at.getArtist());
                listadapter = new ArrayAdapter<String>(
                        activity,
                        android.R.layout.simple_list_item_1,
                        musicFinder.getMusicNames());
                listviewDetail.setAdapter(listadapter);
                listviewDetail.setOnItemClickListener(new ListViewItemClickListener());
                slidingUpPanelLayout.expandPanel();
            }else if(arg0.equals(listviewDetail)){
                Music m = musicFinder.findMusicByIndex(arg2);
                String path = m.getPath();
                PlayService.Title = path;
                PlayService.SongId = m.getId();
                pi.startSong(path); //재생 메서드에 선택된 음악 파일 경로 넘김.
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        pi = (AllSongFragment.PlaylistInterface)activity; //MainFragmentActivity와 인터페이스 연결
    }
}

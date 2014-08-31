package kr.tento.activity;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import kr.tento.MusicFinder;
import kr.tento.PlayService;
import kr.tento.R;
import kr.tento.SwipeGestureListener;
import kr.tento.model.Music;


public class PlayingActivity extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener  {


    /**
     * seekbarSong 의 progress 값을 500ms 마다 바꾸기위해서 필요한 클래스
     */
    private class UpdateProgressTime implements Runnable {

        @Override
        public void run() {
            long totalDuration = PlayService.mp.getDuration();
            long currentDuration = PlayService.mp.getCurrentPosition();
            int pos = (int) ((currentDuration / (double) totalDuration) * 100);
            seekbarSong.setProgress(pos);
            handler.postDelayed(this, 500);
        }
    }

    private SlidingUpPanelLayout layoutSlide;

    private TextView txtTitle;

    private CheckBox checkboxRepeat;

    private CheckBox checkboxRandom;

    private CheckBox checkboxRepeatAll;

    private ImageView imgAlbumArt;

    private SeekBar seekbarSong;

    private Button btnBackPlaying;

    private ListView listview;

    private Button btnGotoList;

    private long totalDuration;

    // UpdateProgressTime 를 관리하기위해서 사용하는 핸들러
    private Handler handler = new Handler();

    private MusicFinder musicFinder;

    private UpdateProgressTime updateProgressTime = new UpdateProgressTime();


    Intent intent = new Intent("tento.PlaySongService");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_playing);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);



        txtTitle =  (TextView) this.findViewById(R.id.txtTitle);
        checkboxRepeat = (CheckBox) this.findViewById(R.id.checkboxRepeat);
        checkboxRandom = (CheckBox) this.findViewById(R.id.checkboxRandom);
        checkboxRepeatAll = (CheckBox) this.findViewById(R.id.checkboxRepeatAll);
        imgAlbumArt = (ImageView) this.findViewById(R.id.imgAlbumArt);
        seekbarSong = (SeekBar) this.findViewById(R.id.seekbarSong);
        musicFinder = new MusicFinder(this);

        btnGotoList = (Button)this.findViewById(R.id.btnGotoList);
        checkboxRepeat.setOnClickListener(this);
        checkboxRepeatAll.setOnClickListener(this);
        checkboxRandom.setOnClickListener(this);
        seekbarSong.setOnSeekBarChangeListener(this);
        btnGotoList.setOnClickListener(this);
        imgAlbumArt.setOnClickListener(this);
        imgAlbumArt.setOnTouchListener(gestureListener);

        btnBackPlaying = (Button)findViewById(R.id.btnBackPlaying);


        layoutSlide = (SlidingUpPanelLayout)findViewById(R.id.layoutSlide);
        layoutSlide.setDragView(btnBackPlaying);
        musicFinder.findMusic(true, 0, null);
        ArrayAdapter<String> listadapter;
        listview = (ListView)findViewById(R.id.listViewPlaying);
        listadapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                musicFinder.getMusicNames());
        listview.setAdapter(listadapter);
        listview.setOnItemClickListener(new ListViewItemClickListener());

        setSongInfo();
        if (PlayService.mp.isPlaying()) {
            connectService();
        }
    }
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // TODO Auto-generated method stub
            Music m = musicFinder.findMusicByIndex(arg2);
            String path = m.getPath();
            PlayService.Title = path;
            PlayService.SongId = m.getId();
            intent.putExtra("func", PlayService.START);
            intent.putExtra("path", path);
            startService(intent);
            setSongInfo();
            layoutSlide.hidePanel();
        }
    }


    View.OnTouchListener gestureListener = new SwipeGestureListener() {
        public boolean onSwipeRight() {
            if(PlayService.SongId != null) {
                changeNextSong();
            }
            return true;
        }

        public boolean onSwipeLeft() {
            if(PlayService.SongId != null) {
                Music music = musicFinder.findPrevMusicById(PlayService.SongId);
                intent.putExtra("id", music.getId());
                intent.putExtra("path", music.getPath());
                intent.putExtra("func", PlayService.CHANGE);
                startService(intent);
            }
            return true;
        }

        public boolean onSwipeTop() {
            layoutSlide.expandPanel(); //열려라 참깨

            return true;
        }

        public boolean onSwipeBottom() {
            layoutSlide.hidePanel();
            return true;
        }
    };

    /**
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        if(PlayService.SongId == null){
            return;
        }
        switch (view.getId()) {
            case R.id.checkboxRepeat:
                intent.putExtra("func", PlayService.LOOPCONTROL);
                intent.putExtra("state", checkboxRepeat.isChecked());
                startService(intent);
                break;
            case R.id.checkboxRandom:
            {
                musicFinder.setRandom(checkboxRandom.isChecked());
                if(PlayService.SongId != null) {
                    musicFinder.setRandomFirstMusic(PlayService.SongId);
                }
            }
            break;
            case R.id.btnGotoList:
            {
                onBackPressed();
            }
            break;
            case R.id.imgAlbumArt:
            {
                if(!PlayService.Title.isEmpty()){
                    intent.putExtra("func", PlayService.PLAYPAUSE);
                    startService(intent);
                }
            }
        }
    }

    /**
     * Service에서 상태가 변화되었을 때 발생하는 메서드
     * status 는 다음과 같은 상태로 정의됩니다.
     *  - StatusChanged.PLAY : 재생중
     *  - StatusChanged.PAUSE : 일시 정지중
     *  - StatusChanged.CHANGE : 노래 변경
     *
     * @param status 다음 값중 하나를 가집니다. StatusChanged.PLAY, StatusChanged.PAUSE,
     *               StatusChanged.CHANGE.
     */
    public void Changed(int status) {
        switch (status) {
            case PlayService.StatusChanged.PAUSE: {
                // 노래가 재생중이면 버튼은 "재생"이 되야함
                stopProgressBar();
            }
            break;
            case PlayService.StatusChanged.PLAY: {
                // 노래가 재생중이면 버튼은 "일시정지"가 되야함
                final Music music = musicFinder.findMusicById(PlayService.SongId);
                updateProgressBar();
            }
            break;
            case PlayService.StatusChanged.CHANGE: {
                setSongInfo();
            }
            break;
            case PlayService.StatusChanged.AUTO_NEXT: {
                changeNextSong();
            }
            break;
        }
    }


    /**
     * onClick과 statusChanged에서 쓰이는 부분의 중복 제거를 위해 만듦
     */
    public void changeNextSong() {
        Music music = musicFinder.findNextMusicById(PlayService.SongId);
        if (!musicFinder.isLastMusicById(PlayService.SongId) || checkboxRepeatAll.isChecked()) {
            intent.putExtra("id", music.getId());
            intent.putExtra("path", music.getPath());
            intent.putExtra("func", PlayService.CHANGE);
            startService(intent);
        }
    }

    /**
     * 음악 정보(앨범사진, 타이틀, 등등) 및 버튼 설정
     */
    public void setSongInfo() {
        if(PlayService.SongId != null) {
            Music music = musicFinder.findMusicById(PlayService.SongId);
            imgAlbumArt.setImageBitmap(music.getArtwork());
            txtTitle.setText(music.getTitle());
            totalDuration = PlayService.mp.getDuration();
            int nowPosition = PlayService.mp.getCurrentPosition();
            int pos = nowPosition/((int)totalDuration);
            seekbarSong.setProgress(pos);
            updateProgressBar();
        }
    }

    /**
     * UpdateProgressTime 을 500ms 후에 작동하게합니다.
     * UpdateProgressTime 내부에서도 handler.postDelayed(this, 500) 을 실행하기때문에 handler 에서
     * 콜백 함수들이 삭제되기전까진 계속 실행하게됩니다.
     */
    public void updateProgressBar() {
        handler.postDelayed(updateProgressTime, 500);
    }

    /**
     * UpdateProgressTime 을 handler 의 콜백함수에서 제외함으로써 seekbarSong 의 업데이트가 중지됩니다.
     */
    public void stopProgressBar() {
        handler.removeCallbacks(updateProgressTime);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }


    /**
     * 유저가 SeekBar 를 누르기시작하면 위치를 바꾸고싶어하는 것이므로 일단 SeekBar 의 진행을 멈춥니다.
     *
     * @param seekBar 연결시킨 SeekBar, 이 클래스에서 선언된 seekbarSong
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        stopProgressBar();
    }

    /**
     * 유저가 SeekBar 로 위치를 찾다가 놓으면 그 위치부터 재생하고싶어하는 것이므로 노래를 그 위치부터 재생하도록 합니다.
     *
     * @param seekBar 연결시킨 SeekBar, 이 클래스에서 선언된 seekbarSong
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        stopProgressBar();
        totalDuration = PlayService.mp.getDuration();
        int pos = (int) (totalDuration * (seekBar.getProgress() / 100.0));
        intent.putExtra("func", PlayService.SEEKTO);
        intent.putExtra("seekTo", pos);
        startService(intent);
        updateProgressBar();
    }


    /**
     * 서비스와 연결하는 메서드
     * 서비스에 연결해 놓고 서비스에 정의된 인터페이스를 여기서 구현하여 통신
     */
    public void connectService(){
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
    public PlayService ps;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayService.ServiceBinder binder = (PlayService.ServiceBinder)iBinder;
            ps = binder.getService();
            ps.registerInterface(sc);
            PlayService.connect = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            PlayService.connect = false;
            sc = null;
        }

        private PlayService.StatusChanged sc = new PlayService.StatusChanged() {
            @Override
            public void statusChanged(int status) {
                Changed(status);
            }
        };
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(PlayService.SongId!=null){
            unbindService(connection);
        }
    }
}
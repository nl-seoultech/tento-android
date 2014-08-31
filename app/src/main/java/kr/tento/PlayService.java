package kr.tento;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class PlayService extends Service{

    public static MediaPlayer mp = new MediaPlayer();
    public static String Title = ""; // 음악 파일 이름
    public static String SongId; // 음악 파일에 부여되는 고유 ID  MediaStore.Audio.Media._ID
    public static Boolean connect = false;
    private StatusChanged sc;
    private ServiceTimeTask stimetask;
    private Timer timer;
    final static public int START = 0;
    final static public int PLAYPAUSE = 1;
    final static public int LOOPCONTROL = 2;
    final static public int SEEKTO = 3;
    final static public int CHANGE = 4;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                startTimer();
                sc.statusChanged(StatusChanged.AUTO_NEXT);
            }
        });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = new Notification(R.drawable.ic_launcher, "서비스 실행됨", System.currentTimeMillis());
        notification.setLatestEventInfo(getApplicationContext(), "Tento Service", "Foreground로 실행됨", null);
        startForeground(1, notification);

        switch (intent.getIntExtra("func", 0)){
            case START:
                startSong(intent.getStringExtra("path"));
                break;
            case PLAYPAUSE:
                playpauseSong();
                break;
            case LOOPCONTROL:
                loopControl(intent.getBooleanExtra("state", false));
                break;
            case SEEKTO:
                seekTo(intent.getIntExtra("seekTo", 0));
                break;
            case CHANGE:
                changeSong(intent.getStringExtra("id"), intent.getStringExtra("path"));
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void startSong(String path){
        try{
            mp.reset(); //플레이어 초기화
            mp.setDataSource(path); //경로 설정
            mp.prepare();
            mp.start();
            sc.statusChanged(StatusChanged.PLAY);
            if(timer!=null){
                cancelTimer();
            }
        } catch(Exception e) {
        }
    }

    public void playpauseSong(){
        if(mp.isPlaying()){
            mp.pause();
            sc.statusChanged(StatusChanged.PAUSE);
            startTimer();
        } else {
            mp.start();
            sc.statusChanged(StatusChanged.PLAY);
            cancelTimer();
        }
    }

    /**
     * 음악 정지 혹은 종료시 1분 후 서비스 자동 종료
     */
    private class ServiceTimeTask extends TimerTask{

        @Override
        public void run() {
            stopForeground(true);
        }
    }

    private void startTimer(){

        timer = new Timer();
        stimetask = new ServiceTimeTask();
        timer.schedule(stimetask, 60000);
    }

    private void cancelTimer(){
        timer.cancel();
        timer = null;
        stimetask = null;
    }

    /**
     * 체크 박스를 이용한 재생중인 음악 한곡 반복.
     */
    public void loopControl(boolean state){
        mp.setLooping(state);
    }

    /**
     * 노래 재생위치를 변경합니다
     *
     * @param pos 변경할 노래위치
     */
    public void seekTo(int pos) {
        mp.seekTo(pos);
    }

    public void changeSong(String id, String path) {
        SongId = id;
        Title = path;
        startSong(path);
        sc.statusChanged(StatusChanged.CHANGE);
    }

    public class ServiceBinder extends Binder{
        public PlayService getService(){
            return PlayService.this;
        }
    }

    private final IBinder binder = new ServiceBinder();

    public interface StatusChanged {

        // 재생중인 상태
        public int PLAY = 0;

        // 일시정지중인 상태
        public int PAUSE = 1;

        // 노래가 다음곡이나 이전곡 으로 바뀐 상태.
        public int CHANGE = 2;

        // 자동으로 다음 노래로 넘어가는 상태.
        public int AUTO_NEXT = 3;

        public void statusChanged(int status);
    }
    public void registerInterface(StatusChanged _sc){
        this.sc = _sc;
    }


}
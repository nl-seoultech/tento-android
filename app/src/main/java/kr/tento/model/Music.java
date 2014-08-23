package kr.tento.model;

import android.database.Cursor;
import android.graphics.Bitmap;

public class Music {

    // 안드로이드 MediaStore 에서 mp3의 고유 id
    private String id;

    // mp3의 아티스트
    private String artist;

    // mp3의 제목
    private String title;

    // mp3의 경로
    private String path;

    // mp3 의 display 이름
    private String displayName;

    private Bitmap artwork;

    // mp3의 길이
    private String duration;

    public Music(Cursor cursor) {
        id = cursor.getString(0);
        artist = cursor.getString(1);
        title = cursor.getString(2);
        path = cursor.getString(3);
        displayName = cursor.getString(4);
        duration = cursor.getString(5);
    }

    public String getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDuration() {
        return duration;
    }

    public void setArtwork(Bitmap bm) {
        artwork = bm;
    }

    public Bitmap getArtwork() {
        return artwork;
    }

}

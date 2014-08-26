package kr.tento.model;

import android.database.Cursor;
import android.graphics.Bitmap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.IOException;
import java.util.HashMap;

import kr.tento.api.TentoResponse;;

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

    public String getDisplayName() { return displayName; }

    public String getDuration() { return duration; }

    public void setArtwork(Bitmap bm) {
        artwork = bm;
    }

    public Bitmap getArtwork() {
        return artwork;
    }

    public HashMap<String, String> getId3() {

        try {
            final Mp3File mp3file = new Mp3File(this.path);
            String musicName = null;
            String trackNumber = null;
            String artist = null;
            String album = null;
            String year = null;
            String genre = null;

            if(mp3file.hasId3v1Tag()) {
                ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                musicName = id3v1Tag.getTitle();
                trackNumber = id3v1Tag.getTrack();
                artist = id3v1Tag.getArtist();
                album = id3v1Tag.getAlbum();
                year = id3v1Tag.getYear();
                genre = id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")";
            } else if(mp3file.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                musicName = id3v2Tag.getTitle();
                trackNumber = id3v2Tag.getTrack();
                artist = id3v2Tag.getArtist();
                album = id3v2Tag.getAlbum();
                year = id3v2Tag.getYear();
                genre = id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")";
            }

            HashMap<String, String> id3 = new HashMap<String, String>();
            id3.put("music_name", musicName);
            id3.put("trackNumber", trackNumber);
            id3.put("artist", artist);
            id3.put("album", album);
            id3.put("year", year);
            id3.put("genre", genre);

            return id3;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
        return null;
    }
}

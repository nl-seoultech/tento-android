package kr.tento.model;

import android.database.Cursor;

public class Artist {

    private String artist;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Artist(Cursor cursor) {
        artist = cursor.getString(0);
        }
}

package kr.tento.model;

import android.database.Cursor;

public class Album {

    private String album;

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Album(Cursor cursor) {
        album = cursor.getString(0);
    }
}

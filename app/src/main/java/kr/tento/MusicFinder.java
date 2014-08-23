package kr.tento;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import kr.tento.model.Album;
import kr.tento.model.Artist;
import kr.tento.model.Music;

public class MusicFinder {

    public static ArrayList<Music> musics;

    public static ArrayList<Artist> artists;

    public static ArrayList<Album> albums;

    private static HashMap<String, Integer> songIndexById;

    private static HashMap<String, Integer> songIndexByPath;

    private Activity activity;

    private static boolean random = false;

    // 랜덤 재생 인덱스의 순서를 저장하는 리스트, 실제 songs 리스트는 그대로두고 이 리스트만 섞어서 랜덤 재생곡을 결정합니다.
    private ArrayList<Integer> randomIndecies;

    // findNextOrPrevRandomableIndexById 에서 이전곡을 찾아올때 사용하는 상수
    private int INDEX_PREV = -1;

    // findNextOrPrevRandomableIndexById 에서 다음곡을 찾아올때 사용하는 상수
    private int INDEX_NEXT = 1;

    public MusicFinder(Activity a){
        activity = a;
    }

    public void findMusic(boolean refresh, int func, String select){
        if(refresh || musics == null || musics.isEmpty()) {
            musics = new ArrayList<Music>();
            songIndexById = new HashMap<String, Integer>();
            songIndexByPath = new HashMap<String, Integer>();

            String selection = null;

            switch (func){
                case 0: //전체 곡
                    selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
                    break;
                case 1: //아티스트
                    selection = MediaStore.Audio.Media.ARTIST + " = '"+select+"'";
                    break;
                case 2: //앨범
                    selection = MediaStore.Audio.Media.ALBUM + " = '" + select+"'";
                    break;
            }


            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION
            };

            ContentResolver resolver = activity.getContentResolver();
            Cursor cursor = resolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    null);

            while(cursor.moveToNext()) {
                Music m = new Music(cursor);
                //TODO: 모든 노래 artwork 로딩하고있으므로 메모리 낭비가 있을듯. 필요할때 로딩하는게 괜찮지않을까.
                Uri uri = Uri.parse("content://media/external/audio/media/" + m.getId() + "/albumart");
                ParcelFileDescriptor pfd = null;
                try {
                    pfd = resolver.openFileDescriptor(uri, "r");
                    if (pfd != null) {
                        FileDescriptor fd = pfd.getFileDescriptor();
                        Bitmap bm = BitmapFactory.decodeFileDescriptor(fd);
                        m.setArtwork(bm);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                musics.add(m);
            }

            // 나중에 findSongByPath나 findSongById 같은걸 사용하기위해서 id와 path를 HashMap에 저장해놓습니다.
            for(int i = 0; i < musics.size(); i++) {
                songIndexById.put(musics.get(i).getId(), i);
                songIndexByPath.put(musics.get(i).getPath(), i);
            }
        }
    }

    public void findArtist(boolean refresh){
        if(refresh || artists == null || artists.isEmpty()) {
            artists = new ArrayList<Artist>();

            String[] projection = {
                    "DISTINCT " +
                    MediaStore.Audio.Media.ARTIST,
            };
            String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
            ContentResolver resolver = activity.getContentResolver();
            Cursor cursor = resolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    null);

            while(cursor.moveToNext()) {
                Artist at = new Artist(cursor);
                artists.add(at);
            }
        }
    }

    public void findAlbum(boolean refresh){
        if(refresh || albums == null || albums.isEmpty()) {
            albums = new ArrayList<Album>();
            String[] projection = {
                    "DISTINCT " +
                    MediaStore.Audio.Media.ALBUM,
            };
            String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
            ContentResolver resolver = activity.getContentResolver();
            Cursor cursor = resolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null,
                    null);
            while(cursor.moveToNext()) {
                Album al = new Album(cursor);
                albums.add(al);
            }
        }
    }


    public ArrayList<String> getMusicNames() {
        ArrayList<String> names = new ArrayList<String>();

        for(Music m : musics) {
            names.add(String.format("%s - %s", m.getTitle(), m.getArtist()));
        }

        return names;
    }

    public ArrayList<String> getArtistNames(){
        ArrayList<String> names = new ArrayList<String>();

        for(Artist at : artists) {
            names.add(at.getArtist());
        }

        return names;
    }

    public ArrayList<String> getAlbumNames(){
        ArrayList<String> names = new ArrayList<String>();

        for(Album al : albums) {
            names.add(al.getAlbum());
        }

        return names;
    }

    public Music findMusicByPath(String path) {
        return musics.get(songIndexByPath.get(path));
    }

    public Music findMusicById(String id) {
        return musics.get(songIndexById.get(id));
    }

    public Music findMusicByIndex(int i) {
        return musics.get(i);
    }

    public Music findNextMusicById(String id) {
        int nextSongIndex = findNextOrPrevRandomableIndexById(id, INDEX_NEXT);
        return musics.get(nextSongIndex);
    }

    public Music findPrevMusicById(String id) {
        int prevSongIndex = findNextOrPrevRandomableIndexById(id, INDEX_PREV);
        return musics.get(prevSongIndex);
    }

    public Artist findArtistByIndex(int i){
        return artists.get(i);
    }

    public Album findAlbumByIndex(int i){
        return albums.get(i);
    }


    //아래의 메서드들을 어떡하나..
    //findMusic류를 제외하고 나머지 재생 설정같은 걸 만들어서 넣는건 어땨?
    //모르겠다아아아


    public void setRandom(boolean r) {
        if(r) {
            randomIndecies = new ArrayList<Integer>();

            for(int i = 0; i < musics.size(); i++) {
                randomIndecies.add(i);
            }
            Collections.shuffle(randomIndecies);
        }
        random = r;
    }

    /**
     * 랜덤 재생 누를 시점에 노래를 랜덤 인덱스의 맨앞으로보내는 메소드.
     *
     * @param songId 현재 재생중인 노래 인덱스
     */
    public void setRandomFirstMusic(String musicId) {
        int musicIndex = songIndexById.get(musicId);
        int currentIndex = randomIndecies.indexOf(musicIndex);
        randomIndecies.remove(currentIndex);
        ArrayList<Integer> newRandomIndecies = new ArrayList<Integer>();
        newRandomIndecies.add(musicIndex);
        for(int i : randomIndecies) {
            newRandomIndecies.add(i);
        }
        randomIndecies = newRandomIndecies;
    }



    public int findIndexById(String id) {
        return songIndexById.get(id);
    }

    public int findNextOrPrevRandomableIndexById(String id, int prevOrNext) {
        int prevOrNextIndex;
        int realIndex = findIndexById(id);

        if(random) {
            int currentRandomIndex = randomIndecies.indexOf(realIndex);
            int i = indexCirculationCorrection(currentRandomIndex + prevOrNext);
            prevOrNextIndex = randomIndecies.get(i);
        } else {
            prevOrNextIndex = indexCirculationCorrection(realIndex + prevOrNext);
        }

        return prevOrNextIndex;
    }

    public int indexCirculationCorrection(int index) {
        int size = musics.size();
        if(index >= size) {
            index -= size;
        } else if(index < 0) {
            index += size;
        }
        return index;
    }

    public boolean isLastMusicById(String musicId) {
        Music lastSong;
        if(random) {
            int randomIndex  = randomIndecies.get(randomIndecies.size() - 1);
            lastSong = musics.get(randomIndex);
        } else {
            lastSong = musics.get(musics.size() - 1);
        }
        return lastSong.getId().equals(musicId);
    }


}

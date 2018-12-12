package core;

public class Song {
    private String album;
    private String artist;
    private String title;
    private String time;
    private String track;
    private String genre;
    private String year;

    public Song(String album, String artist, String title, String time, String track, String genre, String year) {
        this.album = album;
        this.artist = artist;
        this.title = title;
        this.time = time;
        this.track = track;
        this.genre = genre;
        this.year = year;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}

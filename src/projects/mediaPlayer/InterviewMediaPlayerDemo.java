package projects.mediaPlayer;

import java.util.ArrayList;
import java.util.List;

// STRATEGY PATTERN - Core requirement
interface AudioPlayer{
    void play(String filePath);
    String getFormatType();
}

class MP3Player implements AudioPlayer{
    @Override
    public void play(String filePath) {
        System.out.println("Playing MP3: " + filePath + " (Using MP3 decoder)");
    }

    @Override
    public String getFormatType() {
        return "MP3";
    }
}

class WAVPlayer implements AudioPlayer{
    @Override
    public void play(String filePath) {
        System.out.println("Playing WAV: " + filePath + " (Using WAV decoder)");
    }

    @Override
    public String getFormatType() {
        return "WAV";
    }
}

class FLACPlayer implements AudioPlayer{
    @Override
    public void play(String filePath) {
        System.out.println("Playing FLAC: " + filePath + " (Using FLAC decoder)");
    }

    @Override
    public String getFormatType() {
        return "FLAC";
    }
}

// FACTORY PATTERN - Simple version
class PlayerFactory {
    public static AudioPlayer createPlayer(String filePath) {
        String extension = getFileExtension(filePath);

        switch (extension.toLowerCase()) {
            case ".mp3":
                return new MP3Player();
            case ".wav":
                return new WAVPlayer();
            case ".flac":
                return new FLACPlayer();
            default:
                throw new UnsupportedOperationException("Unsupported format: " + extension);
        }
    }

    private static String getFileExtension(String filePath) {
        int lastDot = filePath.lastIndexOf('.');
        return lastDot > 0 ? filePath.substring(lastDot) : "";
    }
}

// SIMPLE MEDIA PLAYER
class Song{
    private final String title;
    private final String filePath;

    Song(String title, String filePath) {
        this.title = title;
        this.filePath = filePath;
    }

    public String getTitle() { return title; }
    public String getFilePath() { return filePath; }

    @Override
    public String toString() {
        return title;
    }
}

class MusicPlayer{
    private List<Song> playlist;
    private int currentIndex;
    private int volume;
    private boolean isPlaying;

    public MusicPlayer() {
        this.playlist = new ArrayList<>();
        this.currentIndex = 0;
        this.volume = 50;
        this.isPlaying = false;
    }

    public void addSong(Song song){
        playlist.add(song);
        System.out.println("Added: " + song.getTitle());
    }

    public void play(){
        if (playlist.isEmpty()) {
            System.out.println("Playlist is empty!");
            return;
        }

        Song currentSong = playlist.get(currentIndex);

        AudioPlayer audioPlayer =  PlayerFactory.createPlayer(currentSong.getFilePath());

        audioPlayer.play(currentSong.getFilePath());

        isPlaying = true;
        System.out.println("Volume: " + volume + "%");
    }

    public void next(){
       if (playlist.isEmpty()) return;

       currentIndex = (currentIndex + 1) % playlist.size();

        System.out.println("Next song: " + playlist.get(currentIndex).getTitle());

        if (isPlaying) {
            play();
        }
    }

    public void previous(){
        if (playlist.isEmpty()) return;

        currentIndex = currentIndex > 0 ? currentIndex - 1 : playlist.size()-1;

        System.out.println("Previous song: " + playlist.get(currentIndex).getTitle());

        if(isPlaying){
            play();
        }

    }

    public void setVolume(int volume){
        this.volume = Math.max(0, Math.min(100, volume));
        System.out.println("Volume set to: " + this.volume + "%");
    }

    public void pause() {
        isPlaying = false;
        System.out.println("Paused");
    }

    public Song getCurrentSong() {
        return playlist.isEmpty() ? null : playlist.get(currentIndex);
    }

    public List<Song> getPlaylist() {
        return new ArrayList<>(playlist);
    }
}



public class InterviewMediaPlayerDemo {
    public static void main(String[] args) {

        MusicPlayer player = new MusicPlayer();

        // Add songs
        player.addSong(new Song("Song 1", "track1.mp3"));
        player.addSong(new Song("Song 2", "track2.wav"));
        player.addSong(new Song("Song 3", "track3.flac"));

        // Demo functionality
        System.out.println("\n--- Playing ---");
        player.play();

        System.out.println("\n--- Volume Control ---");
        player.setVolume(75);

        System.out.println("\n--- Navigation ---");
        player.next();
        player.next();
        player.previous();

        System.out.println("\n--- Playlist ---");
        for (Song song : player.getPlaylist()) {
            System.out.println("- " + song);
        }

    }
}

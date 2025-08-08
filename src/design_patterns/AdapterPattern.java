package design_patterns;

interface MediaPlayer {
    void play(String audioType, String fileName);
}

interface LegacyMediaPlayer {
    void playVlc(String fileName);
    void playMp4(String fileName);
    void playMkv(String fileName);
}

class VlcPlayer implements LegacyMediaPlayer{

    @Override
    public void playVlc(String fileName) {
        System.out.println("Playing vlc file: " + fileName);
    }

    @Override
    public void playMp4(String fileName) {

    }

    @Override
    public void playMkv(String fileName) {

    }
}

class Mp4Player implements LegacyMediaPlayer{

    @Override
    public void playVlc(String fileName) {

    }

    @Override
    public void playMp4(String fileName) {
        System.out.println("Playing mp4 file: " + fileName);
    }

    @Override
    public void playMkv(String fileName) {

    }
}

class MkvPlayer implements LegacyMediaPlayer{

    @Override
    public void playVlc(String fileName) {

    }

    @Override
    public void playMp4(String fileName) {

    }

    @Override
    public void playMkv(String fileName) {
        System.out.println("Playing mp4 file: " + fileName);
    }
}

//Adapter
class MediaAdapter implements MediaPlayer {
    private LegacyMediaPlayer legacyMusicPlayer;

    public MediaAdapter(String audioType) {
        if(audioType.equalsIgnoreCase("vlc")){
            legacyMusicPlayer = new VlcPlayer();
        } else if (audioType.equalsIgnoreCase("mp4")) {
            legacyMusicPlayer = new MkvPlayer();
        }else {
            legacyMusicPlayer = new MkvPlayer();
        }
    }

    @Override
    public void play(String audioType, String fileName) {
        if(audioType.equalsIgnoreCase("vlc")){
            legacyMusicPlayer.playVlc(fileName);
        } else if (audioType.equalsIgnoreCase("mp4")) {
            legacyMusicPlayer.playMp4(fileName);
        }else {
            legacyMusicPlayer.playMkv(fileName);
        }
    }
}

//client
class AudioPlayer implements MediaPlayer{
    private MediaAdapter mediaAdapter;

    @Override
    public void play(String audioType, String fileName) {
        // Built-in support for mp3
        if (audioType.equalsIgnoreCase("mp3")) {
            System.out.println("Playing mp3 file: " + fileName);
        }
        // Using adapter for other formats
        else if (audioType.equalsIgnoreCase("vlc") ||
                audioType.equalsIgnoreCase("mp4") ||
                audioType.equalsIgnoreCase("mkv")) {
            mediaAdapter = new MediaAdapter(audioType);
            mediaAdapter.play(audioType, fileName);
        }else {
            System.out.println("Invalid media. " + audioType + " format not supported");
        }

    }
}

public class AdapterPattern {
    public static void main(String[] args) {
        System.out.println("=== Media Player Adapter Pattern ===\n");

        AudioPlayer audioPlayer = new AudioPlayer();

        // Built-in format
        audioPlayer.play("mp3", "beyond_the_horizon.mp3");

        // Adapted formats
        audioPlayer.play("mp4", "alone.mp4");
        audioPlayer.play("vlc", "far_far_away.vlc");
        audioPlayer.play("mkv", "mind_me.mkv");

        // Unsupported format
        audioPlayer.play("avi", "my_video.avi");

        System.out.println("\n=== Direct Adapter Usage ===");

        // Using adapter directly
        MediaAdapter vlcAdapter = new MediaAdapter("vlc");
        vlcAdapter.play("vlc", "test_file.vlc");

        MediaAdapter mp4Adapter = new MediaAdapter("mp4");
        mp4Adapter.play("mp4", "test_file.mp4");

        System.out.println("\n=== Demonstration of Legacy Integration ===");

        // Show how different players work independently
        VlcPlayer vlcPlayer = new VlcPlayer();
        Mp4Player mp4Player = new Mp4Player();
        MkvPlayer mkvPlayer = new MkvPlayer();

        System.out.println("Direct usage of legacy players:");
        vlcPlayer.playVlc("legacy_vlc.vlc");
        mp4Player.playMp4("legacy_mp4.mp4");
        mkvPlayer.playMkv("legacy_mkv.mkv");

        System.out.println("\nSame players through unified interface:");
        MediaPlayer unifiedVlc = new MediaAdapter("vlc");
        MediaPlayer unifiedMp4 = new MediaAdapter("mp4");
        MediaPlayer unifiedMkv = new MediaAdapter("mkv");

        unifiedVlc.play("vlc", "legacy_vlc.vlc");
        unifiedMp4.play("mp4", "legacy_mp4.mp4");
        unifiedMkv.play("mkv", "legacy_mkv.mkv");
    }
}

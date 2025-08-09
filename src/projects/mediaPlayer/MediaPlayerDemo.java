package projects.mediaPlayer;

import java.util.*;
import java.io.File;

// =====================================================
// STRATEGY PATTERN - Audio Format Handlers
// =====================================================

interface AudioFormat {
    byte[] decode(String filePath);
    double getDuration(String filePath);
    Map<String, String> getMetadata(String filePath);
    String getFormatName();
}

class MP3Handler implements AudioFormat {
    @Override
    public byte[] decode(String filePath) {
        System.out.println("Decoding MP3 file: " + filePath);
        // Simulate MP3 decoding
        return "mp3_audio_data".getBytes();
    }

    @Override
    public double getDuration(String filePath) {
        return 180.5; // Simulate MP3 duration in seconds
    }

    @Override
    public Map<String, String> getMetadata(String filePath) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("format", "MP3");
        metadata.put("bitrate", "320kbps");
        metadata.put("sampleRate", "44.1kHz");
        return metadata;
    }

    @Override
    public String getFormatName() {
        return "MP3";
    }
}

class WAVHandler implements AudioFormat {
    @Override
    public byte[] decode(String filePath) {
        System.out.println("Decoding WAV file: " + filePath);
        return "wav_audio_data".getBytes();
    }

    @Override
    public double getDuration(String filePath) {
        return 200.3;
    }

    @Override
    public Map<String, String> getMetadata(String filePath) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("format", "WAV");
        metadata.put("bitrate", "1411kbps");
        metadata.put("sampleRate", "44.1kHz");
        return metadata;
    }

    @Override
    public String getFormatName() {
        return "WAV";
    }
}

class FLACHandler implements AudioFormat {
    @Override
    public byte[] decode(String filePath) {
        System.out.println("Decoding FLAC file: " + filePath);
        return "flac_audio_data".getBytes();
    }

    @Override
    public double getDuration(String filePath) {
        return 195.8;
    }

    @Override
    public Map<String, String> getMetadata(String filePath) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("format", "FLAC");
        metadata.put("bitrate", "Variable");
        metadata.put("sampleRate", "96kHz");
        return metadata;
    }

    @Override
    public String getFormatName() {
        return "FLAC";
    }
}

// =====================================================
// FACTORY PATTERN - Audio Format Factory
// =====================================================

class AudioFormatFactory {
    private static final Map<String, Class<? extends AudioFormat>> handlers = new HashMap<>();

    static {
        handlers.put(".mp3", MP3Handler.class);
        handlers.put(".wav", WAVHandler.class);
        handlers.put(".flac", FLACHandler.class);
    }

    public static AudioFormat createHandler(String filePath) throws Exception {
        String extension = getFileExtension(filePath).toLowerCase();

        Class<? extends AudioFormat> handlerClass = handlers.get(extension);
        if (handlerClass != null) {
            return handlerClass.getDeclaredConstructor().newInstance();
        }

        throw new UnsupportedOperationException("Unsupported audio format: " + extension);
    }

    public static void registerHandler(String extension, Class<? extends AudioFormat> handlerClass) {
        handlers.put(extension.toLowerCase(), handlerClass);
    }

    private static String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        return lastDotIndex > 0 ? filePath.substring(lastDotIndex) : "";
    }
}

// =====================================================
// DECORATOR PATTERN - Audio Effects
// =====================================================

interface AudioStream {
    byte[] process(byte[] audioData);
    String getDescription();
}

class BasicAudioStream implements AudioStream {
    private byte[] audioData;

    public BasicAudioStream(byte[] audioData) {
        this.audioData = audioData;
    }

    @Override
    public byte[] process(byte[] audioData) {
        return audioData;
    }

    @Override
    public String getDescription() {
        return "Basic Audio";
    }
}

abstract class AudioEffectDecorator implements AudioStream {
    protected AudioStream audioStream;

    public AudioEffectDecorator(AudioStream audioStream) {
        this.audioStream = audioStream;
    }

    @Override
    public byte[] process(byte[] audioData) {
        return audioStream.process(audioData);
    }

    @Override
    public String getDescription() {
        return audioStream.getDescription();
    }
}

class VolumeControlDecorator extends AudioEffectDecorator {
    private int volume; // 0-100

    public VolumeControlDecorator(AudioStream audioStream, int volume) {
        super(audioStream);
        this.volume = Math.max(0, Math.min(100, volume));
    }

    @Override
    public byte[] process(byte[] audioData) {
        byte[] processedData = super.process(audioData);
        // Simulate volume adjustment
        System.out.println("Applying volume control: " + volume + "%");
        return processedData;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Volume(" + volume + "%)";
    }

    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
    }

    public int getVolume() {
        return volume;
    }
}

class EqualizerDecorator extends AudioEffectDecorator {
    private int bass, mid, treble; // -10 to +10

    public EqualizerDecorator(AudioStream audioStream, int bass, int mid, int treble) {
        super(audioStream);
        this.bass = Math.max(-10, Math.min(10, bass));
        this.mid = Math.max(-10, Math.min(10, mid));
        this.treble = Math.max(-10, Math.min(10, treble));
    }

    @Override
    public byte[] process(byte[] audioData) {
        byte[] processedData = super.process(audioData);
        System.out.println("Applying equalizer - Bass: " + bass + ", Mid: " + mid + ", Treble: " + treble);
        return processedData;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + EQ(B:" + bass + ",M:" + mid + ",T:" + treble + ")";
    }

    public void setBass(int bass) { this.bass = Math.max(-10, Math.min(10, bass)); }
    public void setMid(int mid) { this.mid = Math.max(-10, Math.min(10, mid)); }
    public void setTreble(int treble) { this.treble = Math.max(-10, Math.min(10, treble)); }

    public int getBass() { return bass; }
    public int getMid() { return mid; }
    public int getTreble() { return treble; }
}

class ReverbDecorator extends AudioEffectDecorator {
    private String reverbType;

    public ReverbDecorator(AudioStream audioStream, String reverbType) {
        super(audioStream);
        this.reverbType = reverbType;
    }

    @Override
    public byte[] process(byte[] audioData) {
        byte[] processedData = super.process(audioData);
        System.out.println("Applying reverb effect: " + reverbType);
        return processedData;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Reverb(" + reverbType + ")";
    }
}

// =====================================================
// OBSERVER PATTERN - UI Updates
// =====================================================

interface MediaPlayerObserver {
    void onTrackChanged(Track currentTrack);
    void onVolumeChanged(int volume);
    void onPlaybackStateChanged(boolean isPlaying);
    void onPlaylistChanged(List<Track> playlist);
    void onRepeatModeChanged(RepeatMode repeatMode);
    void onShuffleModeChanged(boolean shuffleEnabled);
}

class ConsoleDisplayObserver implements MediaPlayerObserver {
    @Override
    public void onTrackChanged(Track currentTrack) {
        if (currentTrack != null) {
            System.out.println("♪ Now Playing: " + currentTrack.getTitle() + " - " + currentTrack.getArtist());
        }
    }

    @Override
    public void onVolumeChanged(int volume) {
        System.out.println("🔊 Volume: " + volume + "%");
    }

    @Override
    public void onPlaybackStateChanged(boolean isPlaying) {
        System.out.println(isPlaying ? "▶️ Playing" : "⏸️ Paused");
    }

    @Override
    public void onPlaylistChanged(List<Track> playlist) {
        System.out.println("📋 Playlist updated: " + playlist.size() + " tracks");
    }

    @Override
    public void onRepeatModeChanged(RepeatMode repeatMode) {
        System.out.println("🔁 Repeat mode: " + repeatMode);
    }

    @Override
    public void onShuffleModeChanged(boolean shuffleEnabled) {
        System.out.println("🔀 Shuffle: " + (shuffleEnabled ? "ON" : "OFF"));
    }
}

class PlaylistDisplayObserver implements MediaPlayerObserver {
    @Override
    public void onTrackChanged(Track currentTrack) {
        // Update playlist UI to highlight current track
    }

    @Override
    public void onVolumeChanged(int volume) {
        // Update volume slider/indicator
    }

    @Override
    public void onPlaybackStateChanged(boolean isPlaying) {
        // Update play/pause button
    }

    @Override
    public void onPlaylistChanged(List<Track> playlist) {
        System.out.println("GUI: Refreshing playlist view with " + playlist.size() + " tracks");
    }

    @Override
    public void onRepeatModeChanged(RepeatMode repeatMode) {
        // Update repeat button state
    }

    @Override
    public void onShuffleModeChanged(boolean shuffleEnabled) {
        // Update shuffle button state
    }
}

// =====================================================
// CORE CLASSES
// =====================================================

class Track {
    private String title;
    private String artist;
    private String album;
    private String filePath;
    private double duration;
    private AudioFormat audioFormat;

    public Track(String title, String artist, String album, String filePath) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.filePath = filePath;

        try {
            this.audioFormat = AudioFormatFactory.createHandler(filePath);
            this.duration = audioFormat.getDuration(filePath);
        } catch (Exception e) {
            System.err.println("Error loading track: " + e.getMessage());
        }
    }

    // Getters
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getFilePath() { return filePath; }
    public double getDuration() { return duration; }
    public AudioFormat getAudioFormat() { return audioFormat; }

    @Override
    public String toString() {
        return title + " - " + artist + " (" + String.format("%.1f", duration) + "s)";
    }
}

enum RepeatMode {
    NONE, SINGLE, ALL
}

class Playlist {
    private List<Track> tracks;
    private List<Integer> shuffledIndices;
    private boolean shuffleMode;

    public Playlist() {
        this.tracks = new ArrayList<>();
        this.shuffledIndices = new ArrayList<>();
        this.shuffleMode = false;
    }

    public void addTrack(Track track) {
        tracks.add(track);
        shuffledIndices.add(shuffledIndices.size());
        if (shuffleMode) {
            reshuffleIndices();
        }
    }

    public void removeTrack(int index) {
        if (index >= 0 && index < tracks.size()) {
            tracks.remove(index);
            updateShuffledIndices();
        }
    }

    public Track getTrack(int index) {
        if (shuffleMode && index >= 0 && index < shuffledIndices.size()) {
            return tracks.get(shuffledIndices.get(index));
        } else if (index >= 0 && index < tracks.size()) {
            return tracks.get(index);
        }
        return null;
    }

    public int size() {
        return tracks.size();
    }

    public List<Track> getAllTracks() {
        return new ArrayList<>(tracks);
    }

    public void setShuffleMode(boolean shuffle) {
        this.shuffleMode = shuffle;
        if (shuffle) {
            reshuffleIndices();
        }
    }

    public boolean isShuffleMode() {
        return shuffleMode;
    }

    private void reshuffleIndices() {
        shuffledIndices.clear();
        for (int i = 0; i < tracks.size(); i++) {
            shuffledIndices.add(i);
        }
        Collections.shuffle(shuffledIndices);
    }

    private void updateShuffledIndices() {
        shuffledIndices.clear();
        for (int i = 0; i < tracks.size(); i++) {
            shuffledIndices.add(i);
        }
        if (shuffleMode) {
            Collections.shuffle(shuffledIndices);
        }
    }
}

// =====================================================
// MAIN MEDIA PLAYER CLASS
// =====================================================

class MediaPlayer {
    private Playlist playlist;
    private int currentTrackIndex;
    private boolean isPlaying;
    private RepeatMode repeatMode;
    private AudioStream audioStream;
    private VolumeControlDecorator volumeControl;
    private EqualizerDecorator equalizer;
    private List<MediaPlayerObserver> observers;

    public MediaPlayer() {
        this.playlist = new Playlist();
        this.currentTrackIndex = 0;
        this.isPlaying = false;
        this.repeatMode = RepeatMode.NONE;
        this.observers = new ArrayList<>();

        // Initialize audio effects chain
        AudioStream basicStream = new BasicAudioStream(new byte[0]);
        this.volumeControl = new VolumeControlDecorator(basicStream, 50);
        this.equalizer = new EqualizerDecorator(volumeControl, 0, 0, 0);
        this.audioStream = equalizer;
    }

    // Observer pattern methods
    public void addObserver(MediaPlayerObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(MediaPlayerObserver observer) {
        observers.remove(observer);
    }

    private void notifyTrackChanged() {
        Track currentTrack = getCurrentTrack();
        for (MediaPlayerObserver observer : observers) {
            observer.onTrackChanged(currentTrack);
        }
    }

    private void notifyVolumeChanged() {
        for (MediaPlayerObserver observer : observers) {
            observer.onVolumeChanged(volumeControl.getVolume());
        }
    }

    private void notifyPlaybackStateChanged() {
        for (MediaPlayerObserver observer : observers) {
            observer.onPlaybackStateChanged(isPlaying);
        }
    }

    private void notifyPlaylistChanged() {
        for (MediaPlayerObserver observer : observers) {
            observer.onPlaylistChanged(playlist.getAllTracks());
        }
    }

    private void notifyRepeatModeChanged() {
        for (MediaPlayerObserver observer : observers) {
            observer.onRepeatModeChanged(repeatMode);
        }
    }

    private void notifyShuffleModeChanged() {
        for (MediaPlayerObserver observer : observers) {
            observer.onShuffleModeChanged(playlist.isShuffleMode());
        }
    }

    // Playlist management
    public void addTrack(Track track) {
        playlist.addTrack(track);
        notifyPlaylistChanged();
    }

    public void removeTrack(int index) {
        playlist.removeTrack(index);
        if (currentTrackIndex >= playlist.size() && playlist.size() > 0) {
            currentTrackIndex = playlist.size() - 1;
        }
        notifyPlaylistChanged();
        notifyTrackChanged();
    }

    // Playback controls
    public void play() {
        if (playlist.size() > 0) {
            isPlaying = true;
            Track currentTrack = getCurrentTrack();
            if (currentTrack != null) {
                System.out.println("Playing: " + currentTrack.getTitle());
                // Process audio through effects chain
                byte[] audioData = currentTrack.getAudioFormat().decode(currentTrack.getFilePath());
                audioStream.process(audioData);
            }
            notifyPlaybackStateChanged();
        }
    }

    public void pause() {
        isPlaying = false;
        System.out.println("Paused");
        notifyPlaybackStateChanged();
    }

    public void stop() {
        isPlaying = false;
        currentTrackIndex = 0;
        System.out.println("Stopped");
        notifyPlaybackStateChanged();
        notifyTrackChanged();
    }

    public void next() {
        if (playlist.size() == 0) return;

        if (repeatMode == RepeatMode.SINGLE) {
            // Stay on current track
            notifyTrackChanged();
            return;
        }

        currentTrackIndex++;
        if (currentTrackIndex >= playlist.size()) {
            if (repeatMode == RepeatMode.ALL) {
                currentTrackIndex = 0;
            } else {
                currentTrackIndex = playlist.size() - 1;
                pause();
            }
        }

        notifyTrackChanged();
        if (isPlaying) {
            play();
        }
    }

    public void previous() {
        if (playlist.size() == 0) return;

        currentTrackIndex--;
        if (currentTrackIndex < 0) {
            if (repeatMode == RepeatMode.ALL) {
                currentTrackIndex = playlist.size() - 1;
            } else {
                currentTrackIndex = 0;
            }
        }

        notifyTrackChanged();
        if (isPlaying) {
            play();
        }
    }

    // Audio controls
    public void setVolume(int volume) {
        volumeControl.setVolume(volume);
        notifyVolumeChanged();
    }

    public int getVolume() {
        return volumeControl.getVolume();
    }

    public void setEqualizer(int bass, int mid, int treble) {
        equalizer.setBass(bass);
        equalizer.setMid(mid);
        equalizer.setTreble(treble);
        System.out.println("Equalizer updated - Bass: " + bass + ", Mid: " + mid + ", Treble: " + treble);
    }

    // Mode controls
    public void setRepeatMode(RepeatMode mode) {
        this.repeatMode = mode;
        notifyRepeatModeChanged();
    }

    public void toggleShuffle() {
        playlist.setShuffleMode(!playlist.isShuffleMode());
        notifyShuffleModeChanged();
    }

    // Getters
    public Track getCurrentTrack() {
        return playlist.getTrack(currentTrackIndex);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public boolean isShuffleEnabled() {
        return playlist.isShuffleMode();
    }

    public List<Track> getPlaylist() {
        return playlist.getAllTracks();
    }
}

// =====================================================
// DEMO APPLICATION
// =====================================================

public class MediaPlayerDemo {
    public static void main(String[] args) {
        // Create media player
        MediaPlayer player = new MediaPlayer();

        // Add observers
        player.addObserver(new ConsoleDisplayObserver());
        player.addObserver(new PlaylistDisplayObserver());

        System.out.println("=== Media Player System Demo ===\n");

        // Create some sample tracks
        Track track1 = new Track("Bohemian Rhapsody", "Queen", "A Night at the Opera", "queen_bohemian.mp3");
        Track track2 = new Track("Hotel California", "Eagles", "Hotel California", "eagles_hotel.wav");
        Track track3 = new Track("Stairway to Heaven", "Led Zeppelin", "Led Zeppelin IV", "zeppelin_stairway.flac");
        Track track4 = new Track("Sweet Child O' Mine", "Guns N' Roses", "Appetite for Destruction", "gnr_sweet.mp3");

        // Add tracks to playlist
        System.out.println("Adding tracks to playlist...");
        player.addTrack(track1);
        player.addTrack(track2);
        player.addTrack(track3);
        player.addTrack(track4);

        // Demonstrate playback
        System.out.println("\n--- Basic Playback ---");
        player.play();

        // Demonstrate volume control
        System.out.println("\n--- Volume Control ---");
        player.setVolume(75);
        player.setVolume(25);

        // Demonstrate equalizer
        System.out.println("\n--- Equalizer Settings ---");
        player.setEqualizer(3, -1, 5); // Boost bass and treble, reduce mid

        // Demonstrate track navigation
        System.out.println("\n--- Track Navigation ---");
        player.next();
        player.next();
        player.previous();

        // Demonstrate shuffle mode
        System.out.println("\n--- Shuffle Mode ---");
        player.toggleShuffle();
        player.next();
        player.next();

        // Demonstrate repeat modes
        System.out.println("\n--- Repeat Modes ---");
        player.setRepeatMode(RepeatMode.SINGLE);
        player.next(); // Should stay on same track

        player.setRepeatMode(RepeatMode.ALL);
        // Simulate reaching end of playlist
        player.stop();

        System.out.println("\n--- Current Playlist ---");
        List<Track> playlist = player.getPlaylist();
        for (int i = 0; i < playlist.size(); i++) {
            System.out.println((i + 1) + ". " + playlist.get(i));
        }

        System.out.println("\n=== Demo Complete ===");
    }
}
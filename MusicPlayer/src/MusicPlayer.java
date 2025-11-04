import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class MusicPlayer extends PlaybackListener {
    //this will be used to update isPaused more in sync
    private static final Object playSignal = new Object();
    private MusicPlayerGUI musicPlayerGUI;
    //here we will store our song's details
    private Song currentSong;
    public Song getCurrentSong() {
        return currentSong;
    }

    private ArrayList<Song> playlist;

    //we will need to keep track of the index we are in the playlist
    private int currentPlaylistIndex;

    //we will juse the JLayer library to create an AdvancedPlayer onj which will handle playing the music
    private AdvancedPlayer advancedPlayer;

    //we will use a pause boolean flag to indicate whether the player has been paused or not
    private boolean isPaused;

    //use to check if the song has finished
    private boolean songFinished;

    private boolean pressedNext, pressedPrev;

    //stores the last frame when the playback is finished (used for pausing and starting from the paused second)
    private int currentFrame;
    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    //track how many milliseconds have passed since playing the song
    private int currentTimeInMillis;
    public void setCurrentTimeInMillis(int timeInMillis) {
        currentTimeInMillis = timeInMillis;
    }

    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
    }

    public void loadSong(Song song) {
        currentSong = song;

        playlist = null;

        //stop the song if possible
        if(!songFinished) {
            stopSong();
        }

        //play the current song if not null
        if(currentSong != null) {
            //reset frame

            currentFrame = 0;

            //reset current time in milli
            currentTimeInMillis = 0;

            //update GUI
            musicPlayerGUI.setPlaybackSliderValue(0);
            playCurrentSong();
        }
    }

    public void loadPlaylist(File playlistFile){
        playlist = new ArrayList<>();

        //store the paths from the text file into the playlist array list
        try{
            FileReader fileReader = new FileReader(playlistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //read each line from the text file and store the text into the songPath variable
            String songPath;
            while((songPath = bufferedReader.readLine()) != null) {
                //create new song object based on the song path
                Song song = new Song(songPath);

                //add to playlist array list
                playlist.add(song);

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(playlist.size() > 0) {
            //reset playback slider
            musicPlayerGUI.setPlaybackSliderValue(0);
            currentTimeInMillis = 0;

            //update current song to the sonf in the playlist
            currentSong = playlist.get(0);

            //start from the beginning frame
            currentFrame = 0;

            //update GUI
            musicPlayerGUI.enablePauseButtonDisablePlayButton();
            musicPlayerGUI.updateSongTitleAndArtist(currentSong);
            musicPlayerGUI.updatePlaybacksSlider(currentSong);

            //start song
            playCurrentSong();
        }
    }

    public void pauseSong(){
        if(advancedPlayer != null){
            //update isPaused flag
            isPaused = true;

            //then we want to stop the player
            stopSong();
        }
    }

    public void stopSong(){
        if(advancedPlayer != null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void nextSong(){
        //no need to go to the next song if there is no playlist
        if(playlist == null){
            return;
        }

        if(currentPlaylistIndex + 1 > playlist.size() - 1) {
            return;
        }

        pressedNext = true;

        //stop the song if possible
        if(!songFinished) {
            stopSong();
        }

        //increase current playlist index
        currentPlaylistIndex = currentPlaylistIndex + 1;

        //update current song
        currentSong = playlist.get(currentPlaylistIndex);

        //reset frame
        currentFrame = 0;

        //reset current time in milli
        currentTimeInMillis = 0;

        //update GUI
        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybacksSlider(currentSong);

        //play song
        playCurrentSong();

    }

    public void prevSong(){
        //no need to go to the next song if there is no playlist
        if(playlist == null){
            return;
        }


        if(currentPlaylistIndex - 1 < 0){
            return;
        }

        pressedPrev = true;

        //stop the song if possible
        if(!songFinished) {
            stopSong();
        }

        //decrease current playlist index
        currentPlaylistIndex = currentPlaylistIndex - 1;

        //update current song
        currentSong = playlist.get(currentPlaylistIndex);

        //reset frame
        currentFrame = 0;

        //reset current time in milli
        currentTimeInMillis = 0;

        //update GUI
        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybacksSlider(currentSong);

        //play song
        playCurrentSong();

    }

    public void playCurrentSong() {
        if (currentSong == null) {
            return;
        }
        try{
            //read the mp3 audio data
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            //create a new advanced player
            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);
            //start music
            startMusicThread();

            //start playback slider thread
            startPlaybackSliderThread();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //we create a thread that handles playing the music
    public void startMusicThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(isPaused) {
                        synchronized (playSignal) {
                            //update flag
                            isPaused = false;

                            //notify the other thread to continue(make sure that isPaused is updated to false properly)
                            playSignal.notify();

                        }

                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    }else{
                        //play music from the beginning
                        advancedPlayer.play();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //create a thread that will handle updating the slider
    private void startPlaybackSliderThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isPaused) {
                    try {
                        //wait till it gets notified by other thread to continue
                        //make sure that isPaused boolean flag updates to false
                        synchronized (playSignal) {
                            playSignal.wait();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //System.out.println("isPaused: " + isPaused);
                while(!isPaused && !songFinished && !pressedNext && !pressedPrev) {
                    try {
                        //increment current time milli
                        currentTimeInMillis++;

                        //System.out.println(currentTimeInMillis * 2.08);
                        //calculate into frame value
                        int calculatedFrame = (int) ((double) currentTimeInMillis * 2.08 * currentSong.getFrameRatePerMilliseconds());

                        //update GUI
                        musicPlayerGUI.setPlaybackSliderValue(calculatedFrame);
                        //mimic 1 millisecond using thread.sleep
                        Thread.sleep(1);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        //this method gets called in the beginning of the song
        System.out.println("Playback started");
        songFinished = false;
        pressedNext = false;
        pressedPrev = false;
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        //this method gets called when the song finishes or the player gets closed
        System.out.println("Playback finished");
        System.out.println("Actual stop: " + evt.getFrame());
        //System.out.println("Stopped at @ " + currentFrame + " frame");

        if(isPaused){
           currentFrame += (int) ((double)evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
        }else{
            //if the user pressed next or prev, we don't want to execute the rest
            if(pressedNext || pressedPrev) {
                return;
            }

            //when the song ends
            songFinished = true;

            if(playlist == null){
                //update gui
                musicPlayerGUI.enablePlayButtonDisablePauseButton();
            }else{
                //last song in the playlist
                if(currentPlaylistIndex == playlist.size() - 1){
                    //update gui
                    musicPlayerGUI.enablePauseButtonDisablePlayButton();
                }else{
                    //go to the next song in the playlist
                    nextSong();
                }
            }
        }

    }
}

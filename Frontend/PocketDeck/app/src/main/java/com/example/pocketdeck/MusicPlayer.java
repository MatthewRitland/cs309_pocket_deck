package com.example.pocketdeck;
import android.content.Context;
import android.media.MediaPlayer;

public class MusicPlayer {
    private static MediaPlayer music;

    //I was doing music in settings activiy but it was starting over and over
    // and was starting multiple songs on each screen for some reason
    // Made this file to just keep music running constantly on all activities
    public static void startMusic(Context context) {
        //if the music player doesnt exist then create it and set the music file
        //set looping so it keeps going forever
        if (music == null) {
            music = MediaPlayer.create(context.getApplicationContext(), R.raw.background_music);
            music.setLooping(true);
        }
        music.start();
    }

    public static void pauseMusic() {
        if(music != null) {
            music.pause();
        }
    }

    public static void Volume(int volume) {
        //convert the slider bar value to float because thats what mediaplayer wants
        //set music volume
        if(music != null) {
            float userVolume = volume / 100f;
            music.setVolume(userVolume, userVolume);
        }
    }
}

/**
 * @author Mack Quinn
 */
package com.example.pocketdeck;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class MusicPlayer {
    private static MediaPlayer music;
    // Made this file to just keep music running constantly on all activities


    // Make music choice saved in the user preference so it doesnt change everytime app closes

    /**
     *
     * @param context
     */
    public static void musicPref(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("userPreferences", context.MODE_PRIVATE);
        boolean musicOnCheck = preferences.getBoolean("musicOn", true);
        int volume = preferences.getInt("volume", 75);

        if(musicOnCheck){
            startMusic(context);
            Volume(volume);
        } else {
            pauseMusic();
        }
    }

    /**
     *
     * @param context
     */
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

    /**
     *
     * @param volume
     */
    public static void Volume(int volume) {
        //convert the slider bar value to float because thats what mediaplayer wants
        //set music volume
        if(music != null) {
            float userVolume = volume / 100f;
            music.setVolume(userVolume, userVolume);
        }
    }
}

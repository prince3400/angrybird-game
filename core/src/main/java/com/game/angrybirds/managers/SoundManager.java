package com.game.angrybirds.managers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.game.angrybirds.assets.GameAssets;

/**
 * Manages sound effects and background music with volume controls.
 */
public class SoundManager {

    private final GameAssets assets;
    private float sfxVolume = 0.8f;
    private float musicVolume = 0.5f;
    private Music currentMusic;

    public SoundManager(GameAssets assets) {
        this.assets = assets;
    }

    public void playLaunch() {
        playSafe(assets.sfxLaunch);
    }

    public void playHit() {
        playSafe(assets.sfxHit);
    }

    public void playBreak() {
        playSafe(assets.sfxBreak);
    }

    public void playPigDeath() {
        playSafe(assets.sfxPig);
    }

    public void playWin() {
        playSafe(assets.sfxWin);
    }

    public void playLose() {
        playSafe(assets.sfxLose);
    }

    public void playMenuMusic() {
        playMusic(assets.musicMenu);
    }

    public void playGameMusic() {
        playMusic(assets.musicGame);
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    private void playMusic(Music music) {
        if (music == null) return;
        stopMusic();
        currentMusic = music;
        currentMusic.setLooping(true);
        currentMusic.setVolume(musicVolume);
        currentMusic.play();
    }

    private void playSafe(Sound sound) {
        if (sound != null) {
            sound.play(sfxVolume);
        }
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = volume;
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
    }

    public void dispose() {
        stopMusic();
    }
}

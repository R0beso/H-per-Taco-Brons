package com.htb.game.core;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class MusicManager {

    private Clip clip;
    private final AudioFormat format = new AudioFormat(44100, 16, 1, true, false);

    public void play() {
        stop();
        try {
            byte[] data = generateMusicPcm();
            clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (LineUnavailableException e) {
            System.err.println("No se pudo iniciar la música: " + e.getMessage());
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }

    public void playCoinSound() {
        playOnce(generateTone(1318.5, 0.08, 0.3));
    }

    public void playBumpSound() {
        playOnce(generateTone(220, 0.12, 0.3));
    }

    public void playPowerUpSound() {
        playOnce(generateTone(659.25, 0.06, 0.3));
    }

    public void playStompSound() {
        playOnce(generateTone(150, 0.1, 0.35));
    }

    private void playOnce(byte[] data) {
        try {
            Clip snd = AudioSystem.getClip();
            snd.open(format, data, 0, data.length);
            snd.start();
            snd.addLineListener(e -> {
                if (javax.sound.sampled.LineEvent.Type.STOP.equals(e.getType())) {
                    snd.close();
                }
            });
        } catch (LineUnavailableException e) {
            System.err.println("Error reproduciendo sonido: " + e.getMessage());
        }
    }

    private byte[] generateMusicPcm() {
        int sampleRate = 44100;
        double lengthSec = 8.0;
        int totalSamples = (int) (sampleRate * lengthSec);

        double[][] notes = {
            {523.25, 0.2}, {587.33, 0.2}, {659.25, 0.2}, {523.25, 0.2},
            {659.25, 0.2}, {783.99, 0.2}, {659.25, 0.2}, {523.25, 0.2},
            {587.33, 0.2}, {659.25, 0.2}, {783.99, 0.2}, {659.25, 0.2},
            {523.25, 0.3}, {0, 0.1},
            {392.00, 0.2}, {440.00, 0.2}, {493.88, 0.2}, {392.00, 0.2},
            {493.88, 0.2}, {587.33, 0.2}, {493.88, 0.2}, {392.00, 0.2},
            {440.00, 0.2}, {493.88, 0.2}, {587.33, 0.2}, {493.88, 0.2},
            {392.00, 0.3}, {0, 0.1},
            {659.25, 0.3}, {659.25, 0.15}, {659.25, 0.15},
            {783.99, 0.4}, {659.25, 0.2}, {783.99, 0.4},
            {1046.50, 0.6}, {0, 0.2},
        };

        return generateNotesPcm(notes, sampleRate, totalSamples, 0.12);
    }

    private byte[] generateTone(double freq, double duration, double volume) {
        int sampleRate = 44100;
        int numSamples = (int) (sampleRate * duration);
        short[] samples = new short[numSamples];
        for (int i = 0; i < numSamples; i++) {
            double t = (double) i / sampleRate;
            double value = Math.sin(2 * Math.PI * freq * t) * 0.5
                         + Math.signum(Math.sin(2 * Math.PI * freq * t)) * 0.3;
            double env = 1.0;
            if (i < 200) env = (double) i / 200;
            if (i > numSamples - 500) env = (double) (numSamples - i) / 500;
            samples[i] = (short) (value * env * Short.MAX_VALUE * volume);
        }
        byte[] data = new byte[samples.length * 2];
        for (int i = 0; i < samples.length; i++) {
            data[i * 2] = (byte) (samples[i] & 0xFF);
            data[i * 2 + 1] = (byte) ((samples[i] >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateNotesPcm(double[][] notes, int sampleRate, int totalSamples, double volume) {
        short[] samples = new short[totalSamples];
        int sampleIndex = 0;
        for (double[] note : notes) {
            double freq = note[0];
            double dur = note[1];
            int numSamples = (int) (sampleRate * dur);
            for (int i = 0; i < numSamples && sampleIndex < totalSamples; i++, sampleIndex++) {
                double t = (double) i / sampleRate;
                double value;
                if (freq > 0) {
                    value = Math.sin(2 * Math.PI * freq * t) * 0.5
                          + Math.signum(Math.sin(2 * Math.PI * freq * t)) * 0.3;
                } else {
                    value = 0;
                }
                double env = 1.0;
                if (i < 100) env = (double) i / 100;
                if (i > numSamples - 100) env = (double) (numSamples - i) / 100;
                samples[sampleIndex] = (short) (value * env * Short.MAX_VALUE * volume);
            }
        }
        byte[] data = new byte[samples.length * 2];
        for (int i = 0; i < samples.length; i++) {
            data[i * 2] = (byte) (samples[i] & 0xFF);
            data[i * 2 + 1] = (byte) ((samples[i] >> 8) & 0xFF);
        }
        return data;
    }
}

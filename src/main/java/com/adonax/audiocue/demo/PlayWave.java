package com.adonax.audiocue.demo;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.adonax.audiocue.AudioCue;

public class PlayWave {

	public static void main(String[] args)
			throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {

		System.out.println("testWavePlay() start");
		URL url = PlayWave.class.getResource("/a3.wav");
		// Polyphony value of 1 is used because we only play the file once. 
		AudioCue cue = AudioCue.makeStereoCue(url, 1);
		cue.open(10_000);
		Thread.sleep(100);

		cue.play(0.8);
		Thread.sleep(7000);
		cue.close();

		System.out.println("testWavePlay() done");
	}

}

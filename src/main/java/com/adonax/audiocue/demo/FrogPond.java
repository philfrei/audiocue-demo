/*
 * This file is part of AudioCueSupportPack, 
 * Copyright 2017 Philip Freihofner.
 *  
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the following 
 * disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above 
 * copyright notice, this list of conditions and the following 
 * disclaimer in the documentation and/or other materials 
 * provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of 
 * its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written 
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.adonax.audiocue.demo;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.adonax.audiocue.AudioCue;
import com.adonax.audiocue.AudioCueInstanceEvent;
import com.adonax.audiocue.AudioCueListener;
/**
 * {@code FrogPond} is part of <em>audiocue-demo</em>, 
 * a collection of classes and assets used to demonstrate the 
 * {@code AudioCue} class. {@code FrogPond} illustrates an 
 * example of creating an ambient, aleatory soundscape from 
 * a single asset.</p>
 * <p> All the frogs heard derive from concurrent instances of
 * a single {@code AudioCue} playing the asset "frog.wav", 
 * a frog croak. Random numbers within carefully defined 
 * ranges are used as volume, panning and frequency arguments 
 * to give the sense that there are many frogs at different 
 * angles and distances relative to the listener. </p>
 * <p> This class implements {@code AudioCueListener}. Implementation 
 * is limited to simply reporting a brief message on the console 
 * when the {@code AudioCue} starts playing, reporting the time and 
 * play parameters: vol, pan, pitch.</p>
 * @author Philip Freihofner
 * @version audiocue-demo 1.0.0
 * @see http://adonax.com/AudioCue/index.html#supportpack
 */
public class FrogPond implements AudioCueListener {

	public static void main(String[] args) throws LineUnavailableException, 
			UnsupportedAudioFileException, IOException, InterruptedException {
		
		FrogPond ts = new FrogPond();
		ts.frogPond();
	}

	private String frogPond() throws UnsupportedAudioFileException,
		IOException, LineUnavailableException, InterruptedException {
		
	    URL url = this.getClass().getResource("/frog.wav");
	    AudioCue cue = AudioCue.makeStereoCue(url, 4);
	    cue.addAudioCueListener(this);
	    cue.open();
		Thread.sleep(100);
		
		// Plays for 15 seconds.
		long futureStop = System.currentTimeMillis() + 15_000;
		
		while (System.currentTimeMillis() < futureStop)
		{
			double pan = 0.8 - Math.random() * 1.6;
			double vol = 0.2 + (Math.random() * 0.8);
			double pitch = 1.09 - Math.random() * 0.18;
			if (Math.random() < 0.1) {
				cue.play(vol, pan, pitch, 1);
			} else {
				cue.play(vol, pan, pitch, 0);
			}
			Thread.sleep((int)(Math.random() * 1000) + 150);
		}
		
		Thread.sleep(1000);
		cue.close();
		
		return "frogPond() done";
	}

	@Override
	public void audioCueOpened(long now, int threadPriority, int bufferSize, AudioCue source) {}

	@Override
	public void audioCueClosed(long now, AudioCue source) {}

	@Override
	public void instanceEventOccurred(AudioCueInstanceEvent event) {
	
		if (event.type == AudioCueInstanceEvent.Type.START_INSTANCE) {
			int id = event.instanceID;
			System.out.println("FrogPond croak at " + event.time + " milliseconds by "	
					+ event.source.getName() + " instance #: " + id);
			System.out.println("\tat volume " + event.source.getVolume(id) 
				+ ", pan: " + event.source.getPan(id)
				+ ", freq: " + event.source.getSpeed(id));
		}
	}
}

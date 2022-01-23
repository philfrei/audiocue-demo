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

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.adonax.audiocue.AudioCue;


/**
 * {@code BattleField} is a class built to demonstrate uses of 
 * the {@code AudioCue} class. {@code BattleField} illustrates an 
 * example of creating an ambient, aleatory soundscape from 
 * a minimum of assets.
 * <p>The bombs and single rifle shots derive from the file 
 * gunshot.wav. The bombs are played at speeds that are much
 * slower than the original recordings, and are set to explode
 * at random times and random locations, near and far. The 
 * rifle shots are given one of two pitches and either play 
 * once or twice in succession when evoked.</p>
 * <p> The machine-gun sfx is a procedurally edited version of 
 * the {@code gunshot.wav} PCM data. The frame length is shortened 
 * in order to have the looping playback create rapid fire.</p>
 * <p>
 * This class makes use of the following wav file: {@code gunshot.wav}
 * </p>
 * 
 * @author Philip Freihofner
 * @version 1.0.0
 * @see http://adonax.com/AudioCue/index.html
 */
public class BattleField
{
	public static void main(String[] args) throws LineUnavailableException, 
			UnsupportedAudioFileException, IOException, InterruptedException {
	
		BattleField ts = new BattleField();
		ts.battleField();
	}

	private void battleField() throws UnsupportedAudioFileException,
			IOException, LineUnavailableException, InterruptedException {
	    /*
	     *  First, we load the .wav resource to a PCM array, rather than
	     *  directly to the AudioCue, because we are going to make dual 
	     *  use of this data. One use will be the main "shot" for rifle
	     *  fire and bombs, the other will edited for the rapidly repeating 
	     *  fire of a machine gun.
	     */
	    URL url = this.getClass().getResource("/gunshot.wav");
	    AudioInputStream ais = AudioSystem.getAudioInputStream(url);
	    int framesCount = (int)ais.getFrameLength();
	    
	    float[] cuePCM = new float[framesCount * 2]; // * 2 because stereo
		
	    int cueIdx = 0;
	    int bytesRead;
	    byte[] buffer = new byte[1024];
	    
	    while((bytesRead = ais.read(buffer, 0, 1024)) != -1) {
	    	int framesRead = bytesRead/2;
	    	for (int i = 0; i < framesRead; i++) {
	    		// assemble the two bytes into a single value (16-bit resolution)
	    		cuePCM[cueIdx] = (buffer[i * 2] & 0xff) | (buffer[i * 2 + 1] << 8) ;
    			// scale into signed normalized values (ranging from -1 to 1)
	    		cuePCM[cueIdx++] /= 32767f;
	    	}
	    }
	    	    
	    AudioCue cueSingleShot = AudioCue.makeStereoCue(cuePCM, "SingleShotSFX", 12);
	    cueSingleShot.open(2048); // a little extra for the buffer 
	    
	    
	    /*
	     *  Make a shorter PCM array for the machine gun. We truncate the reverberant 
	     *  tail of the gunshot.wav file so that looping will be more rapid. Start with
	     *  a full-volume copy from the beginning, then...
	     */
	    int quickShotLen = 11_000;
	    int fadeStart = 6_000;
	    float[] quickShot = new float[quickShotLen];
	    for (int i = 0; i < fadeStart; i++) {
	    	quickShot[i] = cuePCM[i];
	    }
	    
	    // ...fade the values to silence to avoid a click at end.
	    int fadeLen = quickShotLen - fadeStart;
	    for (int i = 0; i < fadeLen; i ++) {
	    	quickShot[i + fadeStart] = cuePCM[i + fadeStart] * ((fadeLen - i)/fadeLen);
	    }
	    
	    AudioCue cueSemiAuto = AudioCue.makeStereoCue(quickShot, "SemiAuto", 3);
	    cueSemiAuto.open();
	    
	    /*
	     * Coding three machine guns, one close, and two
	     * at a distance. For the close one, we will prevent it
	     * from interrupting itself by managing an instance.
	     */
		int machineGun0 = cueSemiAuto.obtainInstance();
		
		long futureStop = System.currentTimeMillis() + 15_000;

		// Slight cheat: this SF/X has a long reverberant tail that helps 
		// establish a first impression of a larger space, so start with it.
		double bvol = 0.95 - (Math.random() * 0.5);
		double bpitch = 0.1 + (Math.random() * 0.2);
		double bpan = 0.8 - Math.random() * 1.6;
		cueSingleShot.play(bvol, bpan, bpitch, 0);

		
		while (System.currentTimeMillis() < futureStop) {

			int switchInt = (int)(Math.random() * 10);
			switch(switchInt) {
			case 0: 
			case 1: // machine gun, nearby location
					
				// do not interrupt if already playing
				if (cueSemiAuto.getIsPlaying(machineGun0)) break;

				cueSemiAuto.setFramePosition(machineGun0, 0);
				cueSemiAuto.setLooping(machineGun0, 
						2 + (int)(Math.random() * 6));
				cueSemiAuto.setVolume(machineGun0, 0.6);
				cueSemiAuto.setSpeed(machineGun0, 1.55);
				cueSemiAuto.start(machineGun0);
				break;
				
			case 2:	
			case 3:  // two machine guns, far locations
				
				if (Math.random() < 0.5) {
					cueSemiAuto.play(0.2, -0.67, 1.6, 
							2 + (int)(Math.random() * 6));
					
				} else {
					cueSemiAuto.play(0.3, 0.75, 1.6, 
							2 + (int)(Math.random() * 6));
				}
				break;
				
			case 4:
			case 5:
			case 6: // bombs and grenades
				bvol = 0.95 - (Math.random() * 0.5);
				bpitch = 0.1 + (Math.random() * 0.2);
				bpan = 0.8 - Math.random() * 1.6;
				cueSingleShot.play(bvol, bpan, bpitch, 0);
				break;
				
			case 7:
			case 8:
			case 9:	// rifle, shoots once or twice
				int rifle = cueSingleShot.obtainInstance();
				if (rifle == -1) break;
				
				double gunvol = 0.6f - (Math.random() * 0.5);
				double gunpitch = 1;
				double gunpan = 0.5 - Math.random();

				if (Math.random() < 0.5) {
					cueSingleShot.setLooping(rifle, 0);
				} else { 
					cueSingleShot.setLooping(rifle, 1);
				}
				
				if (Math.random() < 0.5) { 
					gunpitch = 1.2;
				}
				
				cueSingleShot.setFramePosition(rifle, 0);
				cueSingleShot.setVolume(rifle, gunvol);
				cueSingleShot.setPan(rifle, gunpan);
				cueSingleShot.setSpeed(rifle, gunpitch);
				cueSingleShot.start(rifle);
			}
			
			Thread.sleep((int)(Math.random() * 1000) + 100);	
		}
		
		Thread.sleep(3000);
		
		cueSemiAuto.close();
		cueSingleShot.close();
		
		return;
	}
}

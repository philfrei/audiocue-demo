package com.adonax.audiocue.demo;

import javax.sound.sampled.LineUnavailableException;

import com.adonax.audiocue.AudioCue;

public class PlaySine {

	public static void main(String[] args) {
		System.out.println("testLoadFromArray() start");
		
		float[] data = new float[44100 * 6];
		
		double sinIncr = 2 * Math.PI * 220 / 44100;
		
		int lastFrame = 44100 * 3; // three seconds
		int fadeLen = 256;
		int startFadeOut = lastFrame - fadeLen;
		int fadeOutCtr = fadeLen;
		
		for (int i = 0; i < lastFrame; i++)	{
			float audioVal = (float)(Math.sin(i * sinIncr));
			
			// following is a fade in, to prevent transient click
			if (i < fadeLen) {
				audioVal *= (i /(float)fadeLen);
			}
			// and this is a fade out, also to prevent transient click
			if (i >= startFadeOut) {
				audioVal *= (--fadeOutCtr/(float)fadeLen);
			}
			
			// same PCM value is placed in L & R channels
			data[2 * i] = audioVal;
			data[2 * i + 1] = audioVal;
		}
		
		AudioCue cue = AudioCue.makeStereoCue(data, "sine A3", 2);
	    System.out.println("Cue length in frames:" 
	    		+ cue.getFrameLength());
	    System.out.println("Cue length in microseconds:"
	    		+ cue.getMicrosecondLength());
	    
	    try {
			cue.open(4_000);
			Thread.sleep(100);
		    cue.play(0.75, -1, 1, 0);
		    Thread.sleep(1250);
		    cue.play(0.7, 1, 1.5, 0);
		    Thread.sleep(3000);
		} catch (IllegalStateException | LineUnavailableException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	    cue.close();
	
	    System.out.println("testLoadFromArray() done");
	}	
}
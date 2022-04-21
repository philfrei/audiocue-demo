# audiocue-demo

This project was written to demonstrate a variety of usages of the `AudioCue` class. It contains a number of runnable classes, and a copy of *AudioCue-1.0.0.jar*. The class `AudioCue` is a powerful replacement for the Java `Clip`, enhanced to allow concurrent playback with real time controls for volume, panning and pitch. The source for `AudioCue` is located at [philfrei/AudioCue-maven](https://github.com/philfrei/AudioCue-maven).

## License 
BSD License

<br />

## Contents:

**SinglePlayWav**: The `AudioCue` instance is loaded with a .wav resource file, and played back a single time.

**SinglePlaySine**: An array of signed normalized floats is populated with the output from a sine function, set to a frequency of A3 (220 Hz). The `AudioCue` instance is loaded with this PCM array. For the playback, we first play the sound on the left at pitch, and then on the right 1.5 faster by setting the speed parameter to 1.5, which gives us an E4 (330Hz). Be aware that when pure waves are played simultaneously (as opposed to sound cues with more harmonic complexity), it is common for artifacts to arise (e.g., sum and difference tones), especially when the sounds are on the same channel. Note also, that when played at the higher speed, a file completes in less time, and so, the duration of the tone at 1.5 speed will be shorter than the same data played at normal speed.

**FrogPond**: An ambient soundscape with multiple frogs is constructed from a single croak that was recorded locally with my phone (hence a little noisy). The resource cue is directly loaded into a single `AudioCue` instance, and played back concurrently using various volumes, pans, and variations of speed. This program implements `AudioCueListener`. When the cue starts playing, an event is issued and received by the class. If the event is of type START_INSTANCE, we send the name of the cue, instance ID, time of the start, and the play method parameters volume, pan and frequency to the console output. Listener methods could also launch or end an animation, allowing us a way to tie audio directly to graphics.

**BattleField**: A soundscape for a gaming firefight is generated using the single resource `gunshot.wav`. The resource data, in this example, is first imported into a signed float PCM array instead of being loaded directly into the `AudioCue`. The PCM array is then used as the data source for an `AudioCue` class instance named `singleShot`. By playing the cue at different speeds volumes and pans, it becomes the source of rifle fire, or, at low speeds, bombs. To simulate a machine gun, we first edit the PCM data before loading it into another `AudioCue` class instance, named `cueSemiAuto`. The positions of the rifles and machine guns are set to fixed locations, but the bombs land at random locations by using randomized pan and volume arguments.

**SlidersTest**: Demonstrates the triggering of three concurrent playbacks of a bell cue. Sliders provided can alter the pitch, volume and panning in real time. A playback using Java `Clip` is provided for comparison purposes. The `Clip` and the `AudioCue` are loaded from the same resource file. The GUI was built with JavaFX. To run this program, from the CLI, execute `mvn clean javafx:run`. The *SlidersTest* file is set in the POM's *org.openjfx plugin* as the default entry point for the *javafx:run command*.

## Appreciation
It would be great to hear if *audiocue-demo* has been helpful. Positive feedback is very motivating, and much appreciated!

### Contact Info

Programmer/Sound-Designer/Composer: Phil Freihofner

URL: [http://adonax.com](http://adonax.com)

Email: phil AT adonax.com

Recommended forum: [jvm-gaming.org CATEGORY: JavaSound](https://jvm-gaming.org/c/java-sound-amp-openal/12)

If using StackOverflow for a question about this code, chances are highest
that I will eventually see it if you include the tag "javasound". I also 
follow the audio topic at jvm-gaming.org.

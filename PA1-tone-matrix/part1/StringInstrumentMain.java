import processing.core.PApplet;
import processing.sound.AudioSample;

/**
 * Provided "main" test code for the StringInstrument class. This file builds
 * one StringInstrument, generates a note from it, and plays the result
 * so you can hear your work. It expects your class to provide:
 *
 * public StringInstrument(float frequency)
 * public void pluck()
 * public float nextSample()
 * public float[] generateSoundArray(float seconds)
 *
 * @author Tony Liu
 */
public class StringInstrumentMain extends PApplet {

    // Concert A, which is the A above middle C, 440 Hz.
    private static final float CONCERT_A = 440f;

    private AudioSample sample;

    // A Processing library quirk: size() must live in settings() when running as a
    // library, not in setup().
    public void settings() {
        size(480, 200);
    }

    public void setup() {
        StringInstrument instrument = new StringInstrument(CONCERT_A);
        float[] waveform = instrument.generateSoundArray(1.75f);
        sample = new AudioSample(this, waveform);
    }

    public void draw() {
        background(20);
        fill(235);
        textAlign(CENTER, CENTER);
        text("Click to hear your string: Concert A (440 Hz)", width / 2f, height / 2f);
    }

    public void mousePressed() {
        // Set the amplitude (volume) of the audio sample to 0.001.
        // The float literal needs the 'f' to be a float.
        sample.amp(0.001f);
        sample.play();
    }

    public static void main(String[] args) {
        // The String passed in here is the name of the class to run.
        PApplet.main("StringInstrumentMain");
    }
}

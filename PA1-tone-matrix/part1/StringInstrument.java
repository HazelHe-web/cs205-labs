/**
 * Class representing a string instrument following the Karplus-Strong
 * algorithm, drawing from Keith Schwarz's Tone Matrix assignment.
 * 
 * @author Shiman He
 */
public class StringInstrument {

    // number of audio samples per second for waveSample array initialization
    private final int SAMPLE_RATE = 44100;

    // decay factor for the Karplus-Strong algorithm in nextSample()
    private final float DECAY_FACTOR = 0.995f;

    // TODO: declare waveSample array holding one cycle of the waveform
    private float[]waveSample;
    // TODO: declare waveIndex index of the current sample in the waveSample array
    private int waveIndex;
    /**
     * Constructs a new StringInstrument for a string that vibrates at
     * the given frequency.
     * 
     * Initializes:
     * - waveSample array with the correct number of slots based on the frequency
     * - waveIndex to 0
     * 
     * @param frequency the note frequency in Hz for this string instrument
     */
    public StringInstrument(float frequency) {
        // Assignment instruction asks for explicit int casting (rounding down)
        // Note: Math.round() would be acoustically more accurate for pitch precision!
        int capacity = (int) (SAMPLE_RATE / frequency);
        this.waveSample=new float[capacity];
        this.waveIndex=0;
    }

    /**
     * Plucks the string to play the note.
     * 
     * This method simulates the string note plucked by updating the waveSample
     * array filling the first half of the array with 0.05 and the second half of
     * the array with -0.05.
     * 
     * It also resets waveIndex to 0.
     */
    public void pluck() {
        int length=waveSample.length;
        for (int i=0;i<length;i++) {
            if (i<length/2) {
                waveSample[i]=0.05f;
            }else{
                waveSample[i]=-0.05f;
            }
        }
        this.waveIndex=0;
    }

    /**
     * Reads the next sound sample based on the waveIndex position, and updates the
     * waveSample array based on the Karplus-Strong algorithm.
     * 
     * @return the next sample of the note
     */
    public float nextSample() {
        float currentSample=waveSample[waveIndex];
        int nextIndex=(waveIndex+1)%waveSample.length;
        float newSample=(waveSample[waveIndex]+waveSample[nextIndex])/2.0f*DECAY_FACTOR;
        waveSample[waveIndex]=newSample;
        waveIndex=nextIndex;
        return currentSample;
    }

    /**
     * Returns an array of sound samples for the given number of seconds.
     * 
     * This method:
     * 1. Calls pluck() to start the note
     * 2. Initializes a new array with the correct number of slots based on SAMPLE_RATE and 
     *    the number of seconds
     * 3. Loops over the array, calling nextSample() to fill each slot
     * 
     * @param seconds the number of seconds to generate samples for
     * @return the newly created array of sound samples
     */
    public float[] generateSoundArray(float seconds) {
        pluck();
        int totalSamples=(int)(SAMPLE_RATE*seconds);
        float[]soundArray=new float[totalSamples];
        for (int i=0;i<totalSamples;i++) {
            soundArray[i]=nextSample();
        }
        return soundArray;
    }

    /**
     * Test code for the StringInstrument class. 
     */
    public static void testCode() {
        System.out.println("Testing StringInstrument class...");
        StringInstrument instrument1 = new StringInstrument(11025f);

        instrument1.pluck();

        float sample1 = instrument1.nextSample();
        float sample2 = instrument1.nextSample();

        if (sample1 != 0.05f) {
            System.out.println("Failed test for sample1: want 0.05 but got " + sample1);
        }else {
            System.out.println("Passed test for sample1!");
        }


    }

    public static void main(String[] args) {
        // Tests the StringInstrument class
        testCode();
    }
}

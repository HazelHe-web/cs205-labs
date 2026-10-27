import processing.core.PApplet;
import processing.sound.AudioSample;

public class ToneMatrix extends PApplet {

    // length of each generated note, in seconds
    private static final float NOTE_SECONDS = 1.75f;

    // number of lights in each row and column of the grid
    public static final int GRID_SIZE = 16;

    // size of the window in pixels
    public static final int WINDOW_SIZE = 480;

    // number of seconds for the tone sweep to complete one full cycle
    public static final float SWEEP_SECONDS = 2.0f;

    private boolean[][] lightGrid;

    private AudioSample[][] audioSampleGrid;

    // size() must live in settings() when running Processing as a library.
    public void settings() {
        size(WINDOW_SIZE, WINDOW_SIZE);
    }

    /*
     * Calculates the frequency for a given row index, following a pentatonic scale.
     *
     * @param rowIndex the index of the row
     *
     * @return the frequency for the given row index
     */
    private static float frequencyForRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= GRID_SIZE) {
            return -1f;
        }

        /*
         * Pentatonic note offsets from the base note, in number of
         * half steps. Each row lowers the frequency, so we count
         * down by the number of half steps.
         */
        int[] kPentatonicSteps = new int[] {
                0, -1, -3, -5, -7, -8, -10
        };

        /* High C. */
        final float kBaseFrequency = 110 * (float) Math.pow(2, (30.0 + 9.0) / 12.0);

        /* Convert offset to how many octaves down to shift. */
        int octave = rowIndex / 7;

        /* Determine how many half steps we need to shift down. */
        int halfSteps = (-(12 * octave) + kPentatonicSteps[rowIndex % 7]);

        /*
         * Each half step corresponds to scaling the frequency by the twelfth root of
         * two. Therefore, taking a bunch of half steps down is equivalent to
         * multiplying by some power of the twelfth root of two.
         */
        return kBaseFrequency * (float) Math.pow(2.0, halfSteps / 12.0);
    }

    /*
     * Set up the Tone Matrix, initializing the audio sample grid and the logical
     * grid itself.
     */
    public void setup() {
        lightGrid = new boolean[GRID_SIZE][GRID_SIZE];
        audioSampleGrid = new AudioSample[GRID_SIZE][GRID_SIZE];

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                StringInstrument instrument = new StringInstrument(frequencyForRow(row));
                float[] soundArray = instrument.generateSoundArray(NOTE_SECONDS);
                audioSampleGrid[row][col] = new AudioSample(this, soundArray);
            }
        }
    }

    /**
     * Draws the Tone Matrix by:
     *
     * - Calculating the active column based on the current time
     * - Drawing the grid with lit and unlit cells
     * - Highlighting the active column
     * - Playing the notes for lit cells in the active column
     */
    public void draw() {
        // Sets the background color to dark grey
        background(20);

        // calculate which column is "active" based on the current time
        int activeColumn = (int) ((millis() / (SWEEP_SECONDS * 1000.0f)) * GRID_SIZE) % GRID_SIZE;

        int cellSize = WINDOW_SIZE / GRID_SIZE;

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (lightGrid[row][col]) {
                    fill(200);
                    if (col == activeColumn && !audioSampleGrid[row][col].isPlaying()) {
                        audioSampleGrid[row][col].play();
                    }
                } else if (col == activeColumn) {
                    fill(90);
                } else {
                    fill(50);
                }

                rect(col * cellSize, row * cellSize, cellSize, cellSize);
            }
        }
    }

    /**
     * Toggles the cell at the mouse position when the mouse is pressed.
     *
     * Uses the following Processing PApplet variables:
     * this.mouseX: the x-coordinate of the mouse at the time of the click
     * this.mouseY: the y-coordinate of the mouse at the time of the click
     */
    public void mousePressed() {
        int cellSize = WINDOW_SIZE / GRID_SIZE;
        int row = mouseY / cellSize;
        int col = mouseX / cellSize;
        lightGrid[row][col] = !lightGrid[row][col];
    }

    /**
     * Clears the logical grid by setting all cells to false when the space key is
     * pressed.
     *
     * Uses the following Processing PApplet variable:
     * this.key: the key that was pressed
     */
    public void keyPressed() {
        if (key == ' ') {
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    lightGrid[row][col] = false;
                }
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main("ToneMatrix");
    }
}

import processing.core.PApplet;
import processing.core.PFont;

/**
 * Provided "main" test code for a single Card. CardMain is a PApplet that draws one card,
 * which is a visual test of your Card.render() method. Clicking anywhere in the window
 * gives the card a new target, so it animates toward your mouse as a test of your 
 * Card.update() method.
 *
 * A grid is drawn over the window with one square standing for 10 pixels, so it works
 * the same way as the drawing exercise. You can count squares from the card's corner to 
 * check where each rank and suit symbol landed.
 *
 * @author Tony Liu
 */
public class CardMain extends PApplet {

    // One grid square stands for 10 pixels, with a darker line every 100 pixels.
    private static final int GRID_SPACING = 10;
    private static final int GRID_MAJOR_SPACING = 100;

    private static final int INITIAL_CARD_X = 100;
    private static final int INITIAL_CARD_Y = 100;

    // The single card to draw and animate
    private Card card;

    public void settings() {
        size(500, 300);
    }

    public void setup() {
        // The one card drawn for testing at (100, 100). You can try other ranks and suits here!
        card = new Card("A", "spades", INITIAL_CARD_X, INITIAL_CARD_Y);

        PFont font = createFont("SansSerif", 96);
        textFont(font);
    }

    public void draw() {
        // Make the background green.
        background(50, 110, 70);

        card.update();
        card.render(this);

        // The grid goes down last so it stays visible on top of the card.
        drawGrid();

        // Display the card's initial position
        textSize(16);
        fill(255);
        textAlign(LEFT);
        text("The card's initial upper left corner position is (" + INITIAL_CARD_X + ", " + INITIAL_CARD_Y + ")", 10, 20);
    }

    /**
     * Draws a grid of GRID_SPACING pixel squares, with a darker line every
     * GRID_MAJOR_SPACING pixels so the bigger steps are easier to count.
     */
    private void drawGrid() {
        strokeWeight(1);
        for (int x = 0; x <= width; x += GRID_SPACING) {
            setGridColor(x);
            line(x, 0, x, height);
        }
        for (int y = 0; y <= height; y += GRID_SPACING) {
            setGridColor(y);
            line(0, y, width, y);
        }
    }

    /**
     * Sets the line color for a grid line at the given coordinate: darker gray on the
     * major lines, lighter gray everywhere else.
     *
     * @param coordinate the pixel coordinate of the grid line about to be drawn
     */
    private void setGridColor(int coordinate) {
        if (coordinate % GRID_MAJOR_SPACING == 0) {
            stroke(0, 100);
        } else {
            stroke(0, 40);
        }
    }

    public void mouseClicked() {
        // Center the card on the clicked position
        card.setTargetPosition(mouseX - card.getWidth() / 2, mouseY - card.getHeight() / 2);
    }

    public static void main(String[] args) {
        PApplet.main("CardMain");
    }
}

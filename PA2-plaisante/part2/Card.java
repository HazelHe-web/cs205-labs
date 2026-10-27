import processing.core.PApplet;

/**
 * Class representing a playing card rendered in Processing, including its rank,
 * suit, value, and position.
 *
 * @author Shiman He
 */
public class Card {

    // --------------------------------------------------------
    // Setting up the Card class
    // --------------------------------------------------------

    // preset dimensions of cards in pixels
    private final int CARD_WIDTH = 100;
    private final int CARD_HEIGHT = 140;

    private String rank;

    private String suit;

    private int value;

    private boolean isRed;

    // Declare current x and y coordinates of the card on the screen as floats
    private float x;
    private float y;

    // Declare target x and y coordinates of the card on the screen for
    // animation, as floats
    private float targetX;
    private float targetY;

    /**
     * Constructs a new Card with the given rank, suit, and starting x/y position.
     * 
     * Also initializes:
     * - targetX to the same as the starting x coordinate
     * - targetY to the same as the starting y coordinate
     * - value to the value of the card based on the rank, using the getCardValue()
     * helper method
     * - isRed to true if the suit is hearts or diamonds, false otherwise
     *
     * @param rank one of "2".."10", "J", "Q", "K", "A"
     * @param suit one of "hearts", "diamonds", "clubs", "spades"
     * @param x    starting x pixel coordinate
     * @param y    starting y pixel coordinate
     */
    public Card(String rank, String suit, float x, float y) {
        this.rank = rank;
        this.suit = suit;
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        this.value = getCardValue(rank);
        this.isRed = suit.equals("hearts") || suit.equals("diamonds");
    }

    /**
     * Helper method to get the value of a card based on the rank.
     * 
     * @param rank one of "2".."10", "J", "Q", "K", "A"
     * @return the value of the card
     */
    private static int getCardValue(String rank) {
        // Aces are high in this game.
        if (rank.equals("A")) {
            return 14;
        } else if (rank.equals("K")) {
            return 13;
        } else if (rank.equals("Q")) {
            return 12;
        } else if (rank.equals("J")) {
            return 11;
        } else {
            // if rank is a number, return the integer value of the rank
            return Integer.parseInt(rank);
        }
    }

    // --------------------------------------------------------
    // Getters and setters for the card's properties
    // --------------------------------------------------------
    public int getWidth() {
        return CARD_WIDTH;
    }

    public int getHeight() {
        return CARD_HEIGHT;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public int getValue() {
        return value;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setTargetPosition(float x, float y) {
        this.targetX = x;
        this.targetY = y;
    }

    public String toString() {
        return rank + " of " + suit;
    }

    // --------------------------------------------------------
    // Drawing a Card object
    // --------------------------------------------------------

    /**
     * Draws the whole card at its current position inside the given Processing app.
     *
     * The order of drawBackground and drawRankAndSuit matters. The card background
     * has
     * to go down first, or it would paint over the rank and the suit instead of
     * sitting behind them.
     *
     * @param app the Processing sketch to draw into
     */
    public void render(PApplet app) {
        // Ensures that the text is centered on the card
        app.textAlign(PApplet.CENTER, PApplet.CENTER);

        drawBackground(app);
        drawRankAndSuit(app);
    }

    /**
     * Draws the blank card background: a white rounded rectangle with a black
     * outline, positioned at the card's current x and y.
     *
     * @param app the Processing sketch to draw into
     */
    private void drawBackground(PApplet app) {
        app.fill(255);

        app.stroke(0);

        app.strokeWeight(1);

        app.rect(x, y, CARD_WIDTH, CARD_HEIGHT, 10);

    }

    /**
     * Draws everything printed on the card: the rank in two opposite corners,
     * small, and the suit symbol in the middle, large. All text is the
     * same color based on the isRed instance variable.
     * 
     * @param app the Processing sketch to draw into
     */
    private void drawRankAndSuit(PApplet app) {
        if (isRed) {
            app.fill(255, 0, 0);
        } else {
            app.fill(0);
        }

        app.textSize(24);

        app.text(rank, x + 20, y + 20);

        app.text(rank, x + CARD_WIDTH - 20, y + CARD_HEIGHT - 20);

        app.textSize(48);

        app.text(getSuitSymbol(suit), x + (CARD_WIDTH / 2), y + (CARD_HEIGHT / 2));

    }

    /**
     * Helper method to get the suit symbol for a given suit. Symbols
     * are given in Unicode format so that they render correctly.
     * 
     * @param suit
     * @return the suit symbol
     */
    private static String getSuitSymbol(String suit) {
        if (suit.equals("hearts")) {
            return "\u2665"; // heart
        } else if (suit.equals("diamonds")) {
            return "\u2666"; // diamond
        } else if (suit.equals("clubs")) {
            return "\u2663"; // club
        } else if (suit.equals("spades")) {
            return "\u2660"; // spade
        }
        return "";
    }

    // --------------------------------------------------------
    // Animating the card's position
    // --------------------------------------------------------

    /**
     * Updates the card's position by moving it 10% of the remaining distance toward
     * its target each frame using linear interpolation. Once the card is within 1
     * pixel of its target,
     * it snaps exactly onto the target so it comes fully to rest for atTarget() to
     * return true.
     */
    public void update() {

        float newX = PApplet.lerp(x, targetX, 0.1f);

        float newY = PApplet.lerp(y, targetY, 0.1f);

        setPosition(newX, newY);

        if (Math.abs(x - targetX) < 1 && Math.abs(y - targetY) < 1) {
            setPosition(targetX, targetY);
        }

    }

    /**
     * Returns true if the pixel (x, y) falls inside this card's rectangle.
     * Used to determine if the card is being clicked on inside the Processing app.
     * 
     * @param x the x coordinate to check
     * @param y the y coordinate to check
     * @return true if the pixel (x, y) falls inside this card's rectangle, false
     *         otherwise
     */
    public boolean coordinatesContain(float x, float y) {
        return this.x <= x && x <= this.x + CARD_WIDTH && this.y <= y && y <= this.y + CARD_HEIGHT;
    }

    /**
     * True once the card has finished animating to its target position.
     * Determined by checking if the card's current x/y coordinates are equal to its
     * target x/y coordinates.
     * 
     * @return true if the card has finished animating to its target position, false
     *         otherwise
     */
    public boolean atTarget() {
        return x == targetX && y == targetY;
    }

    /**
     * Test code for the Card class.
     */
    public static void testCode() {
        System.out.println("Testing Card class...");
        Card card = new Card("Q", "hearts", 0, 0);

        if (!card.getRank().equals("Q")) {
            System.out.println("Failed test for getRank: expected Q but got " + card.getRank());
        }
        if (card.getValue() != 12) {
            System.out.println("Failed test for getValue: expected 12 but got " + card.getValue());
        }
        if (!card.toString().equals("Q of hearts")) {
            System.out.println("Failed test for toString: expected Q of hearts but got " + card.toString());
        }

    }

    public static void main(String[] args) {
        testCode();
    }
}

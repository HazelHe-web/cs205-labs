import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import processing.core.PApplet;
import processing.core.PFont;

/**
 * Processing application for the Plaisante card game.
 * 
 * Deal 8 cards, select up to 5 of them, and play them for a score of
 * total value x multiplier. You get 4 hands and 4 discards, and the goal is to
 * score as high as you can over the round.
 *
 * This file contains the game engine: the draw() loop, the animation
 * methods, the buttons, and the UI (user interface).
 *
 * Once your Card, Hand, and Tableau classes work, this runs on top of them.
 *
 * The game logic moves through "phases", which are the different states the game can be in.
 * Recall that the draw() method runs continuously, so we need some way to tell the game
 * what to do at each frame. This is represented by the phase String variable.
 * 
 * Every play goes through the following phases, and both end back at "idle":
 *
 *   press Discard:  idle --> clearing --> idle
 *   press Play:     idle --> playing --> scoring --> clearing --> idle
 *
 * The one exception to both paths is the last hand: when the hands run out, 
 * the game goes to "gameover" instead of returning to "idle".
 *
 *   phase      what it waits for                 where it goes next
 *   --------   -------------------------------   --------------------
 *   idle       a click on Play or Discard        playing or clearing
 *   playing    the cards to reach the play area  scoring
 *   scoring    a few seconds to pass by          clearing
 *   clearing   the cards to slide off screen     idle, or gameover
 *   gameover   nothing as the game is over       (no next phase)
 *
 * "idle" is the only phase that allows player input. The rest are animations
 * or pauses that advance themselves once they've finished.
 *
 * @author 205 instructional staff
 */
public class Plaisante extends PApplet {

    /**
     * The face-down draw pile. This is the one list the game keeps for itself:
     * the tableau holds the held cards, the selection, and the playing area, and
     * takes cards from this deck when it needs to refill.
     */
    private List<Card> deck = new ArrayList<>();

    // The held cards, the selection, and the playing area all live in the Tableau class
    private Tableau tableau = new Tableau();

    // dimensions of cards
    private final int CARD_WIDTH = 100;
    private final int CARD_HEIGHT = 140;

    // where the deck sits, and where cards start their slide from
    private final int DECK_X = 100;
    private final int DECK_Y = 100;

    // locations of the held cards
    private final int HELD_X = 100;
    private final int HELD_Y = 300;

    // locations of the playing area
    private final int PLAYING_AREA_X = 300;
    private final int PLAYING_AREA_Y = 100;

    private final int PLAYING_AREA_WIDTH = 560;
    private final int PLAYING_AREA_HEIGHT = 160;

    // buttons
    private final int BTN_WIDTH = 100;
    private final int BTN_HEIGHT = 50;
    private final int PLAY_BTN_X = 100;
    private final int PLAY_BTN_Y = 500;
    private final int DISCARD_BTN_X = 220;
    private final int DISCARD_BTN_Y = 500;

    // the game over panel, centered on the screen
    private final int PANEL_WIDTH = 440;
    private final int PANEL_HEIGHT = 170;

    // ----- game state -----
    private final int START_HANDS = 4;
    private final int START_DISCARDS = 4;
    private final int SHOW_SCORE_FRAMES = 90; // about 1.5 seconds at 60 frames per second

    private int handsRemaining = START_HANDS;
    private int discardsRemaining = START_DISCARDS;
    private int totalScore = 0;

    // Set when a hand is scored, so the "scoring" phase has something to display.
    private String lastType = null;
    private int lastScore = 0;

    // The current phase of the game.
    private String phase = "idle";

    // The frame number when the current phase began, for phases that end after a
    // set amount of time rather than when something reaches its destination.
    private int phaseStartFrame;

    public void settings() {
        size(1280, 720);
    }

    public void setup() {
        String[] suits = {"hearts", "diamonds", "clubs", "spades"};
        String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        for (int i = 0; i < suits.length; i++) {
            for (int j = 0; j < ranks.length; j++) {
                deck.add(new Card(ranks[j], suits[i], DECK_X, DECK_Y));
            }
        }

        // shuffle the cards
        Collections.shuffle(deck);

        // deal the opening cards (they animate into their slots on the screen)
        deal();

        // set the font for the text
        PFont font = this.createFont("SansSerif", 96);
        this.textFont(font);
        this.textAlign(PApplet.CENTER, PApplet.CENTER); // center the text
    }

    // --------------------------------------------------------
    // The animation and drawing loop
    // --------------------------------------------------------

    public void draw() {
        // clears the screen and resets the background to light grey every frame
        background(200);

        // draw the deck face down, with how many cards are left
        stroke(0);
        strokeWeight(1);
        fill(255);
        rect(DECK_X, DECK_Y, CARD_WIDTH, CARD_HEIGHT, 10);
        fill(0);
        textSize(20);
        text("Deck " + deck.size(), DECK_X + (CARD_WIDTH / 2), DECK_Y + (CARD_HEIGHT / 2));

        // draw the playing area that holds the cards being scored
        fill(150);
        noStroke();
        rect(PLAYING_AREA_X - 10, PLAYING_AREA_Y - 10, PLAYING_AREA_WIDTH, PLAYING_AREA_HEIGHT, 10);

        renderHeldCards();

        // animate and draw the cards currently in play (moving to the area or off screen)
        List<Card> playArea = tableau.getPlayArea();
        for (int i = 0; i < playArea.size(); i++) {
            playArea.get(i).update();
            playArea.get(i).render(this);
        }

        drawButtons();
        drawHUD();

        runPhase(); // advance the game now that this frame's motion has happened
    }

    /**
     * Coordinates the game's phases. Every frame, look at which phase we're in and decide
     * whether it has finished. When it has, perform the action for that phase and
     * transition to the next phase.
     */
    private void runPhase() {
        if (phase.equals("idle")) {
            // Nothing to do here. mousePressed() is what starts the next play.

        } 
        else if (phase.equals("playing")) {
            // The played cards are animated into the playing area.
            if (allAtTarget(tableau.getPlayArea())) {
                Hand played = new Hand(tableau.getPlayArea());
                lastType = played.evaluate();
                lastScore = played.score();
                totalScore += lastScore;
                startScoringPause();
            }

        } 
        else if (phase.equals("scoring")) {
            // The score is shown on screen for a set amount of time, then the cards are cleared.
            if (framesInPhase() > SHOW_SCORE_FRAMES) {
                sendToDiscard(tableau.getPlayArea());
                phase = "clearing";
            }

        } 
        else if (phase.equals("clearing")) {
            // The played cards are animated off the right edge. Once they're gone,
            // empty the playing area, and refill the held cards.
            if (allAtTarget(tableau.getPlayArea())) {
                tableau.clearPlayArea();
                deal();
                if (handsRemaining <= 0) {
                    phase = "gameover";
                } else {
                    phase = "idle";
                }
            }
        }
    }

    /**
     * Coordinates player mouse input: clicking the Play or Discard button, or clicking a card to select it.
     */
    public void mousePressed() {
        if (!phase.equals("idle")) {
            return; // ignore clicks while an animation is running
        }

        if (isOverPlayButton(this.mouseX, this.mouseY)) {
            if (canPlay()) {
                startPlay();
            }
            return;
        }
        if (isOverDiscardButton(this.mouseX, this.mouseY)) {
            if (canDiscard()) {
                startDiscard();
            }
            return;
        }

        Card clicked = tableau.cardAt(this.mouseX, this.mouseY);
        if (clicked != null) {
            tableau.toggleSelection(clicked);
        }
    }

    // --------------------------------------------------------
    // Helper methods
    // --------------------------------------------------------

    /**
     * How many frames we've been sitting in the current phase. 
     * 
     * @return the number of frames we've been sitting in the current phase
     * */
    private int framesInPhase() {
        return frameCount - phaseStartFrame;
    }

    /**
     * Moves into the "scoring" phase and starts its timer. 
     */
    private void startScoringPause() {
        phase = "scoring";
        phaseStartFrame = frameCount;
    }

    /**
     * Checks if every card in the list has finished sliding to its target.
     * 
     * @param list the list of cards to check
     * @return true if every card in the list has finished animating to its target, false otherwise
     * */
    private boolean allAtTarget(List<Card> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).atTarget()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Animates every card in the list off the right edge of the screen.
     * 
     * @param toDiscard the list of cards to animate off the right edge of the screen
     * */
    private void sendToDiscard(List<Card> toDiscard) {
        for (int i = 0; i < toDiscard.size(); i++) {
            toDiscard.get(i).setTargetPosition(width + 200, PLAYING_AREA_Y);
        }
    }

    /**
     * Refills the held cards from the deck, and starts each new card on the deck
     * so it animates into its slot.
     *
     */
    private void deal() {
        List<Card> dealt = tableau.dealFrom(deck);
        for (int i = 0; i < dealt.size(); i++) {
            dealt.get(i).setPosition(DECK_X, DECK_Y);
        }
    }

    /**
     * Starts a scoring play and animates the selected cards into the playing area.
     */
    private void startPlay() {
        handsRemaining--;
        tableau.moveSelectedToPlayArea();
        layOutPlayArea();
        phase = "playing";
    }

    /**
     * Starts a discard: pull the selected cards straight toward the discard pile.
     */
    private void startDiscard() {
        discardsRemaining--;
        tableau.moveSelectedToPlayArea();
        sendToDiscard(tableau.getPlayArea());
        phase = "clearing";
    }

    /** 
     * Spreads the playing area's cards out in a row, left to right.
     */
    private void layOutPlayArea() {
        List<Card> playArea = tableau.getPlayArea();
        for (int i = 0; i < playArea.size(); i++) {
            playArea.get(i).setTargetPosition(
                    PLAYING_AREA_X + i * (CARD_WIDTH + 10), PLAYING_AREA_Y);
        }
    }

    /**
     * Checks if a play is legal only when idle, with a legal-sized selection and hands left.
     * 
     * @return true if a play is legal, false otherwise
     */
    private boolean canPlay() {
        int n = tableau.selectionSize();
        return phase.equals("idle") && handsRemaining > 0 && n >= 1 && n <= Tableau.MAX_SELECTED;
    }

    /** 
     * Checks if a discard is legal only when idle, with a legal-sized selection and discards left.
     * 
     * @return true if a discard is legal, false otherwise
     */
    private boolean canDiscard() {
        int n = tableau.selectionSize();
        return phase.equals("idle") && discardsRemaining > 0 && n >= 1 && n <= Tableau.MAX_SELECTED;
    }

    /**
     * Checks if the mouse is over the Play button.
     * 
     * @param mx the x coordinate of the mouse
     * @param my the y coordinate of the mouse
     * @return true if the mouse is over the Play button, false otherwise
     */
    private boolean isOverPlayButton(int mx, int my) {
        return isInside(mx, my, PLAY_BTN_X, PLAY_BTN_Y, BTN_WIDTH, BTN_HEIGHT);
    }

    /**
     * Checks if the mouse is over the Discard button.
     * 
     * @param mx the x coordinate of the mouse
     * @param my the y coordinate of the mouse
     * @return true if the mouse is over the Discard button, false otherwise
     */
    private boolean isOverDiscardButton(int mx, int my) {
        return isInside(mx, my, DISCARD_BTN_X, DISCARD_BTN_Y, BTN_WIDTH, BTN_HEIGHT);
    }

    /**
     * Helper method to check if the mouse is inside a given rectangle.
     * 
     * @param mx the x coordinate of the mouse
     * @param my the y coordinate of the mouse
     * @param x the x coordinate of the rectangle
     * @param y the y coordinate of the rectangle
     * @param w the width of the rectangle
     * @param h the height of the rectangle
     * @return true if the mouse is inside the rectangle, false otherwise
     */
    private boolean isInside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    // --------------------------------------------------------
    // Drawing methods
    // --------------------------------------------------------

    /**
     * Draws the slot outlines, then animates and draws each held card.
     */
    private void renderHeldCards() {
        // draw the outline of the held card slots
        noFill();
        stroke(0);
        strokeWeight(2);
        for (int i = 0; i < Tableau.HELD_SIZE; i++) {
            rect(HELD_X + i * (CARD_WIDTH + 10), HELD_Y, CARD_WIDTH, CARD_HEIGHT, 10);
        }

        // assign each held card its slot (closing gaps), then animate and draw it
        List<Card> heldCards = tableau.getHeldCards();
        for (int i = 0; i < heldCards.size(); i++) {
            heldCards.get(i).setTargetPosition(HELD_X + i * (CARD_WIDTH + 10), HELD_Y);
            heldCards.get(i).update();
            heldCards.get(i).render(this);
        }

        // draw a thick blue outline over each selected card
        noFill();
        stroke(0, 32, 128); // dark blue color
        strokeWeight(4);
        List<Card> selected = tableau.getSelected();
        for (int i = 0; i < selected.size(); i++) {
            rect(selected.get(i).getX(), selected.get(i).getY(), CARD_WIDTH, CARD_HEIGHT, 10);
        }
    }

    /**
     * Draws the Play and Discard buttons.
     */
    private void drawButtons() {
        drawButton(PLAY_BTN_X, PLAY_BTN_Y, "Play", canPlay());
        drawButton(DISCARD_BTN_X, DISCARD_BTN_Y, "Discard", canDiscard());
    }

    /**
     * Helper method to draw a button with a given label and color.
     * 
     * @param x the x coordinate of the button
     * @param y the y coordinate of the button
     * @param label the label of the button
     * @param enabled true if the button is enabled, false otherwise
     */
    private void drawButton(int x, int y, String label, boolean enabled) {
        stroke(0);
        strokeWeight(1);
        if (enabled) {
            fill(0, 128, 255); // light blue when the move is legal
        } else {
            fill(120);         // greyed out otherwise
        }
        rect(x, y, BTN_WIDTH, BTN_HEIGHT, 10);
        fill(255);
        textSize(24);
        text(label, x + (BTN_WIDTH / 2), y + (BTN_HEIGHT / 2));
    }

    /**
     * Draws the HUD (Heads Up Display) with the current game state and instructions.
     */
    private void drawHUD() {
        fill(0);
        textSize(22);
        text("Hands: " + handsRemaining + "     Discards: " + discardsRemaining
                + "     Score: " + totalScore, width / 2, 30);

        textSize(14);
        text("Select up to " + Tableau.MAX_SELECTED + " cards, then Play them or Discard them.",
                width / 2, 58);

        if (phase.equals("scoring") && lastType != null) {
            fill(0, 96, 0);
            textSize(28);
            text(lastType + "   +" + lastScore, width / 2, 275);
        }

        if (phase.equals("gameover")) {
            // Draws a panel behind the text to separate it from the cards underneath it
            fill(255);
            stroke(0);
            strokeWeight(3);
            rect(width / 2 - (PANEL_WIDTH / 2), height / 2 - (PANEL_HEIGHT / 2),
                    PANEL_WIDTH, PANEL_HEIGHT, 14);

            fill(0);
            textSize(48);
            text("Game Over", width / 2, height / 2 - 25);
            textSize(32);
            text("Final Score: " + totalScore, width / 2, height / 2 + 30);
        }
    }

    public static void main(String[] args) {
        PApplet.main("Plaisante");
    }
}

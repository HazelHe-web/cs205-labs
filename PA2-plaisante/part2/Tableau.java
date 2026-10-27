import java.util.ArrayList;
import java.util.List;

/**
 * "Tableau" is the board game word for the cards laid out in front of you. For
 * Plaisante, it keeps track of three lists of cards:
 *
 * heldCards: the row of up to 8 cards the player can choose from
 * selected: the cards the player has clicked, a subset of the held cards
 * playArea: the cards the player has committed to be played and scored
 *
 * A card moves between these lists throughout the logic of the game. It's dealt
 * from the deck
 * into heldCards, clicked into selected, moved into the playArea when the
 * player
 * presses Play or Discard, and cleared out once the round moves on.
 * 
 * Note that this class contains no drawing or visual logic, as it is all
 * contained in the Plaisante class.
 * This allows us to test this class without having to run the entire program.
 *
 * @author Shiman
 */
public class Tableau {

    // How many cards the player holds when the row is full.
    public static final int HELD_SIZE = 8;

    // The most cards that may be selected at once.
    public static final int MAX_SELECTED = 5;

    // The cards available to be selected
    private List<Card> heldCards;

    // The cards that have been selected
    private List<Card> selected;

    // The cards that have been played and need to be scored
    private List<Card> playArea;

    public Tableau() {
        // Initialize the lists
        heldCards = new ArrayList<>();
        selected = new ArrayList<>();
        playArea = new ArrayList<>();
    }

    // --------------------------------------------------------
    // Moving cards between the lists
    // --------------------------------------------------------

    /**
     * Selects the given card, or deselects it if it was already selected. A card
     * can only be selected while fewer than MAX_SELECTED cards are selected.
     *
     * @param card the card that was clicked
     */
    public void toggleSelection(Card card) {
        if (selected.contains(card)) {
            selected.remove(card);
        } else if (selected.size() < MAX_SELECTED) {
            selected.add(card);
        }
    }

    /**
     * Moves the selected cards to the playing area. Every selected card leaves
     * the held cards, and nothing is left selected afterwards.
     */
    public void moveSelectedToPlayArea() {
        for (Card card : selected) {
            heldCards.remove(card);
            playArea.add(card);
        }
        selected.clear();
    }

    // --------------------------------------------------------
    // Dealing, finding, and clearing cards
    // --------------------------------------------------------

    /**
     * Deals from the given deck until the held cards are back up to HELD_SIZE,
     * or until the deck runs out, whichever comes first.
     *
     * The cards that were dealt are returned so that Plaisante can animate them
     * into place.
     *
     * @param deck the draw pile to take cards from
     * @return a new list holding just the cards that were dealt, oldest first
     */
    public List<Card> dealFrom(List<Card> deck) {
        List<Card> dealt = new ArrayList<>();
        while (heldCards.size() < HELD_SIZE && deck.size() > 0) {
            Card card = deck.remove(0);
            heldCards.add(card);
            dealt.add(card);
        }
        return dealt;
    }

    /**
     * Finds the held card at a given point on the screen, if there is one.
     *
     * @param x the x coordinate of the click
     * @param y the y coordinate of the click
     * @return the card at that point, or null if the click missed all cards
     */
    public Card cardAt(float x, float y) {
        for (int i = heldCards.size() - 1; i >= 0; i--) {
            Card card = heldCards.get(i);
            if (card.coordinatesContain(x, y)) {
                return card;
            }
        }
        return null;
    }

    /**
     * Checks if a card is currently selected.
     *
     * @param card the card to check
     * @return true if the card is currently selected, false otherwise
     */
    public boolean isSelected(Card card) {
        return selected.contains(card);
    }

    /**
     * Returns the number of cards currently selected.
     *
     * @return size of the selected list
     */
    public int selectionSize() {
        return selected.size();
    }

    /**
     * Empties the playing area, once its cards have finished animating away.
     */
    public void clearPlayArea() {
        playArea.clear();
    }

    // --------------------------------------------------------
    // Getters for the three lists
    // --------------------------------------------------------

    public List<Card> getHeldCards() {
        return heldCards;
    }

    public List<Card> getSelected() {
        return selected;
    }

    public List<Card> getPlayArea() {
        return playArea;
    }

    /**
     * Tests the toggleSelection() and moveSelectedToPlayArea() methods.
     */
    public static void testCode() {
        System.out.println("Testing Tableau class...");

        // A small deck 2 through J of hearts to deal from
        List<Card> deck = new ArrayList<>();
        String[] ranks = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "J" };
        for (int i = 0; i < ranks.length; i++) {
            deck.add(new Card(ranks[i], "hearts", 0, 0));
        }

        Tableau tableau = new Tableau();

        // Deal 8 cards from the deck to fill the held row
        tableau.dealFrom(deck);

        // ------------------------------------
        // Test toggleSelection()
        // ------------------------------------
        Card first = tableau.getHeldCards().get(0);

        // Toggle selection on the first card to select it
        tableau.toggleSelection(first);

        if (tableau.selectionSize() != 1) {
            System.out.println("Failed test for toggleSelection() on one card: should be 1 but got "
                    + tableau.selectionSize());
        }
        if (!tableau.isSelected(first)) {
            System.out
                    .println("Failed test for toggleSelection() on one card: the card should be selected but was not");
        }

        // Select four more cards to test the MAX_SELECTED limit
        Card second = tableau.getHeldCards().get(1);
        Card third = tableau.getHeldCards().get(2);
        Card fourth = tableau.getHeldCards().get(3);
        Card fifth = tableau.getHeldCards().get(4);
        tableau.toggleSelection(second);
        tableau.toggleSelection(third);
        tableau.toggleSelection(fourth);
        tableau.toggleSelection(fifth);

        // Toggle a sixth card to test the MAX_SELECTED limit
        Card sixth = tableau.getHeldCards().get(5);
        tableau.toggleSelection(sixth);

        if (tableau.selectionSize() != MAX_SELECTED) {
            System.out.println(
                    "Failed test for toggleSelection() with 5 cards selected: should be " + MAX_SELECTED + " but got "
                            + tableau.selectionSize());
        }

        // Toggle the first card again to deselect it
        tableau.toggleSelection(first);
        if (tableau.selectionSize() != 4) {
            System.out.println("Failed test for toggleSelection() on the same card twice: should be 4 but got "
                    + tableau.selectionSize());
        }

        // Toggle the second card to deselect it
        tableau.toggleSelection(second);

        // -----------------------------
        // Test moveSelectedToPlayArea()
        // -----------------------------
        // Move the selected third, fourth, and fifth cards to the playing area
        tableau.moveSelectedToPlayArea();

        if (tableau.getPlayArea().size() != 3) {
            System.out.println(
                    "Failed test for moveSelectedToPlayArea() with 3 selected cards: the play area should be 3 but got "
                            + tableau.getPlayArea().size());
        }
        if (tableau.getHeldCards().size() != 5) {
            System.out.println(
                    "Failed test for moveSelectedToPlayArea() with 3 selected cards: the held cards should be 5 but got "
                            + tableau.getHeldCards().size());
        }
        if (tableau.selectionSize() != 0) {
            System.out.println(
                    "Failed test for moveSelectedToPlayArea() with 3 selected cards: the selection should be 0 but got "
                            + tableau.selectionSize());
        }
    }

    public static void main(String[] args) {
        testCode();
    }
}

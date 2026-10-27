import java.util.ArrayList;
import java.util.List;

/**
 * A Hand object represents one selection of 1 to 5 cards that the player has
 * put
 * into the playing area. It has methods to name itself ("Two Pair", "Flush")
 * and
 * to turn the selected cards into a score.
 *
 * @author Shiman
 */
public class Hand {

    // The eight hand names, ordered from best to worst. We store these as constants
    // so we can use them without risking a typo in the strings.
    public static final String FOUR_KIND = "Four of a Kind";
    public static final String FULL_HOUSE = "Full House";
    public static final String FLUSH = "Flush";
    public static final String STRAIGHT = "Straight";
    public static final String THREE_KIND = "Three of a Kind";
    public static final String TWO_PAIR = "Two Pair";
    public static final String PAIR = "Pair";
    public static final String HIGH_CARD = "High Card";

    // The cards that make up this hand
    private List<Card> cards;

    /**
     * Builds a hand out of the given cards.
     *
     * Notice that we copy the list into a brand new ArrayList instead of just
     * writing this.cards = cards. This allows the Hand class to maintain its
     * own references to the cards in the hand.
     *
     * @param cards the cards being played, 1 to 5 of them
     */
    public Hand(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    /**
     * How many cards are in this hand.
     * 
     * @return the size of the cards list
     */
    public int size() {
        return cards.size();
    }

    // --------------------------------------------------------
    // Helper method for hand count evaluation
    // --------------------------------------------------------

    /**
     * Counts how many cards of each value this hand holds, returning an array
     * of length 15 with the counts of every card value in the cell indices of 2-14.
     *
     * @return an array where counts[v] is how many cards have value v
     */
    private int[] countValues() {
        int[] counts = new int[15];
        for (Card card : cards) {
            int value = card.getValue();
            counts[value]++;
        }
        return counts;
    }

    // --------------------------------------------------------
    // Hand evaluation methods that read counts
    // --------------------------------------------------------

    /**
     * Checks whether some value appears exactly twice.
     *
     * Note that this means exactly two, not two or more.
     *
     * @return true if the hand has a pair, false otherwise
     */
    public boolean hasPair() {
        int[] counts = countValues();
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] == 2) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether some value appears exactly three times.
     *
     * Like hasPair(), this means exactly three, not three or more.
     *
     * @return true if the hand has a three of a kind, false otherwise
     */
    public boolean hasThreeOfAKind() {
        int[] counts = countValues();
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] == 3) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether some value appears exactly four times.
     *
     * @return true if the hand has a four of a kind, false otherwise
     */
    public boolean hasFourOfAKind() {
        int[] counts = countValues();
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] == 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the hand has two pairs.
     * 
     * @return true if the hand has two pairs, false otherwise
     */
    public boolean hasTwoPair() {
        int[] counts = countValues();
        int pairCount = 0;
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] == 2) {
                pairCount++;
            }
        }
        return pairCount == 2;
    }

    /**
     * Checks whether the hand is a full house: three of one value and two of
     * another.
     *
     * @return true if the hand is a full house, false otherwise.
     */
    public boolean hasFullHouse() {
        return hasPair() && hasThreeOfAKind();
    }

    // --------------------------------------------------------
    // Hand evaluation methods that iterate over the cards
    // --------------------------------------------------------

    /**
     * Checks whether the hand has a flush: five cards that all share one suit.
     *
     * @return true if the hand is a flush, false otherwise.
     */
    public boolean hasFlush() {
        if (cards.size() != 5) {
            return false;
        }
        String firstSuit = cards.get(0).getSuit();
        for (Card card : cards) {
            if (!card.getSuit().equals(firstSuit)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the lowest card value in this hand. The hand must hold at least
     * one card.
     * 
     * @return the lowest card value as an integer
     */
    public int lowestValue() {
        int lowest = cards.get(0).getValue();
        for (int i = 1; i < cards.size(); i++) {
            int value = cards.get(i).getValue();
            if (value < lowest) {
                lowest = value;
            }
        }
        return lowest;
    }

    /**
     * Returns the highest card value in this hand. The hand must hold at least
     * one card.
     * 
     * @return the highest card value as an integer
     */
    public int highestValue() {
        int highest = cards.get(0).getValue();
        for (int i = 1; i < cards.size(); i++) {
            int value = cards.get(i).getValue();
            if (value > highest) {
                highest = value;
            }
        }
        return highest;
    }

    /**
     * Checks whether the hand has a straight, which is five different values
     * in a row like 5-6-7-8-9.
     *
     * Aces are high only in Plaisante, so 10-J-Q-K-A is a straight but A-2-3-4-5
     * is not.
     * 
     * @return true if the hand has a straight, false otherwise
     */
    public boolean hasStraight() {
        if (cards.size() != 5) {
            return false;
        }
        if (highestValue() - lowestValue() != 4) {
            return false;
        }
        int[] counts = countValues();
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > 1) {
                return false;
            }

        }
        return true;
    }

    /**
     * Names the best hand that this selection of cards forms. The checks are
     * asked in order, and the first one that says yes decides the name.
     *
     * @return one of the eight hand names above
     */
    public String evaluate() {
        if (hasFourOfAKind()) {
            return FOUR_KIND;
        } else if (hasFullHouse()) {
            return FULL_HOUSE;
        } else if (hasFlush()) {
            return FLUSH;
        } else if (hasStraight()) {
            return STRAIGHT;
        } else if (hasThreeOfAKind()) {
            return THREE_KIND;
        } else if (hasTwoPair()) {
            return TWO_PAIR;
        } else if (hasPair()) {
            return PAIR;
        } else {
            return HIGH_CARD;
        }
    }

    /**
     * The total value of this hand's cards, before the multiplier is applied.
     *
     * Every card played adds its own value with Ace 14, King 13, Queen 12,
     * and Jack 11.
     *
     * @return the sum of the card values in this hand
     */
    public int totalValue() {
        int total = 0;
        for (int i = 0; i < cards.size(); i++) {
            total += cards.get(i).getValue();
        }
        return total;
    }

    /**
     * Determines the multiplier of a hand: the better the hand, the
     * higher the multiplier.
     * 
     * @return the multiplier of a hand
     */
    public int baseMult() {
        String handType = evaluate();
        if (handType.equals(FOUR_KIND)) {
            return 12;
        } else if (handType.equals(FULL_HOUSE)) {
            return 9;
        } else if (handType.equals(FLUSH)) {
            return 8;
        } else if (handType.equals(STRAIGHT)) {
            return 7;
        } else if (handType.equals(THREE_KIND)) {
            return 6;
        } else if (handType.equals(TWO_PAIR)) {
            return 3;
        } else if (handType.equals(PAIR)) {
            return 2;
        } else {
            return 1; // High card, no multiplier
        }
    }

    /**
     * Scores this hand.
     *
     * The two parts of the score come from the cards you played and the hand they
     * form.
     * The card's values decide the total value, and the hand they form decides
     * the multiplier.
     *
     * @return the score of this hand
     */
    public int score() {
        return totalValue() * baseMult();
    }

    /**
     * Test code for the Hand class. We've provided a few tests for you to get
     * started, along with the tests for hasFlush() and hasStraight().
     */
    public static void testCode() {
        System.out.println("Testing Hand class...");

        // A pair of kings: K-K-3-7-9
        List<Card> pairCards = new ArrayList<>();
        pairCards.add(new Card("K", "hearts", 0, 0));
        pairCards.add(new Card("K", "diamonds", 0, 0));
        pairCards.add(new Card("3", "clubs", 0, 0));
        pairCards.add(new Card("7", "spades", 0, 0));
        pairCards.add(new Card("9", "hearts", 0, 0));
        Hand testHand = new Hand(pairCards);

        int[] counts = testHand.countValues();
        if (counts.length != 15) {
            System.out.println("Failed test for countValues() of K-K-3-7-9: should have 15 indices but got "
                    + counts.length);
        }

        // the two kings should both land in index 13
        if (counts[13] != 2) {
            System.out.println("Failed test for countValues() of K-K-3-7-9: counts[13] should be 2 but got "
                    + counts[13]);
        }
        // a value that appears once
        if (counts[3] != 1) {
            System.out.println("Failed test for countValues() of K-K-3-7-9: counts[3] should be 1 but got "
                    + counts[3]);
        }
        // a value that is not in the hand should stay 0
        if (counts[5] != 0) {
            System.out.println("Failed test for countValues() of K-K-3-7-9: counts[5] should be 0 but got "
                    + counts[5]);
        }
        // an ace should be counted at index 14, the last index
        List<Card> aceCards = new ArrayList<>();
        aceCards.add(new Card("A", "spades", 0, 0));
        int[] aceCounts = new Hand(aceCards).countValues();
        if (aceCounts[14] != 1) {
            System.out.println("Failed test for countValues() of A: counts[14] should be 1 but got "
                    + aceCounts[14]);
        }

        // A three of a kind of aces: A-A-A-3-7
        List<Card> threeOfAKindCards = new ArrayList<>();
        threeOfAKindCards.add(new Card("A", "hearts", 0, 0));
        threeOfAKindCards.add(new Card("A", "diamonds", 0, 0));
        threeOfAKindCards.add(new Card("A", "clubs", 0, 0));
        threeOfAKindCards.add(new Card("3", "spades", 0, 0));
        threeOfAKindCards.add(new Card("7", "hearts", 0, 0));
        Hand threeKindHand = new Hand(threeOfAKindCards);

        // K-K-3-7-9 is a pair and nothing better
        if (!testHand.hasPair()) {
            System.out.println("Failed test for hasPair() of K-K-3-7-9: expected true but got false");
        }
        if (testHand.hasTwoPair()) {
            System.out.println("Failed test for hasTwoPair() of K-K-3-7-9: expected false but got true");
        }
        if (testHand.hasThreeOfAKind()) {
            System.out.println("Failed test for hasThreeOfAKind() of K-K-3-7-9: expected false but got true");
        }

        // A-A-A-3-7 is a three of a kind, and three aces do not count as a pair
        if (!threeKindHand.hasThreeOfAKind()) {
            System.out.println("Failed test for hasThreeOfAKind() of A-A-A-3-7: expected true but got false");
        }
        if (threeKindHand.hasPair()) {
            System.out.println("Failed test for hasPair() of A-A-A-3-7: expected false but got true");
        }
        if (threeKindHand.hasFullHouse()) {
            System.out.println("Failed test for hasFullHouse() of A-A-A-3-7: expected false but got true");
        }

        // A two pair: J-J-4-4-8
        List<Card> twoPairCards = new ArrayList<>();
        twoPairCards.add(new Card("J", "hearts", 0, 0));
        twoPairCards.add(new Card("J", "clubs", 0, 0));
        twoPairCards.add(new Card("4", "spades", 0, 0));
        twoPairCards.add(new Card("4", "diamonds", 0, 0));
        twoPairCards.add(new Card("8", "hearts", 0, 0));
        Hand twoPairHand = new Hand(twoPairCards);

        if (!twoPairHand.hasTwoPair()) {
            System.out.println("Failed test for hasTwoPair() of J-J-4-4-8: expected true but got false");
        }

        // A full house: 4-4-4-2-2
        List<Card> fullHouseCards = new ArrayList<>();
        fullHouseCards.add(new Card("4", "hearts", 0, 0));
        fullHouseCards.add(new Card("4", "clubs", 0, 0));
        fullHouseCards.add(new Card("4", "spades", 0, 0));
        fullHouseCards.add(new Card("2", "diamonds", 0, 0));
        fullHouseCards.add(new Card("2", "hearts", 0, 0));
        Hand fullHouseHand = new Hand(fullHouseCards);

        if (!fullHouseHand.hasFullHouse()) {
            System.out.println("Failed test for hasFullHouse() of 4-4-4-2-2: expected true but got false");
        }
        if (fullHouseHand.hasTwoPair()) {
            System.out.println("Failed test for hasTwoPair() of 4-4-4-2-2: expected false but got true");
        }

        // A four of a kind: 8-8-8-8-3
        List<Card> fourKindCards = new ArrayList<>();
        fourKindCards.add(new Card("8", "hearts", 0, 0));
        fourKindCards.add(new Card("8", "clubs", 0, 0));
        fourKindCards.add(new Card("8", "spades", 0, 0));
        fourKindCards.add(new Card("8", "diamonds", 0, 0));
        fourKindCards.add(new Card("3", "hearts", 0, 0));
        Hand fourKindHand = new Hand(fourKindCards);

        if (!fourKindHand.hasFourOfAKind()) {
            System.out.println("Failed test for hasFourOfAKind() of 8-8-8-8-3: expected true but got false");
        }
        if (fourKindHand.hasThreeOfAKind()) {
            System.out.println("Failed test for hasThreeOfAKind() of 8-8-8-8-3: expected false but got true");
        }
        if (fourKindHand.hasPair()) {
            System.out.println("Failed test for hasPair() of 8-8-8-8-3: expected false but got true");
        }

        // A pair can be played with fewer than five cards: K-K
        List<Card> smallPairCards = new ArrayList<>();
        smallPairCards.add(new Card("K", "hearts", 0, 0));
        smallPairCards.add(new Card("K", "spades", 0, 0));
        Hand smallPairHand = new Hand(smallPairCards);

        if (!smallPairHand.hasPair()) {
            System.out.println("Failed test for hasPair() of K-K: expected true but got false");
        }

        // Provided tests for hasFlush() and hasStraight()

        // Build a hand that is both a straight and a flush: 2-3-4-5-6 hearts
        List<Card> straightFlushCards = new ArrayList<>();
        straightFlushCards.add(new Card("2", "hearts", 0, 0));
        straightFlushCards.add(new Card("3", "hearts", 0, 0));
        straightFlushCards.add(new Card("4", "hearts", 0, 0));
        straightFlushCards.add(new Card("5", "hearts", 0, 0));
        straightFlushCards.add(new Card("6", "hearts", 0, 0));
        Hand straightFlushHand = new Hand(straightFlushCards);

        if (!straightFlushHand.hasFlush()) {
            System.out.println("Failed test for hasFlush() of 2-3-4-5-6 hearts: expected true but got false");
        }
        if (!straightFlushHand.hasStraight()) {
            System.out.println("Failed test for hasStraight() of 2-3-4-5-6 hearts: expected true but got false");
        }

        // Build a hand of only four cards: 2-3-4-5 hearts
        List<Card> fourCards = new ArrayList<>();
        fourCards.add(new Card("2", "hearts", 0, 0));
        fourCards.add(new Card("3", "hearts", 0, 0));
        fourCards.add(new Card("4", "hearts", 0, 0));
        fourCards.add(new Card("5", "hearts", 0, 0));
        Hand fourCardHand = new Hand(fourCards);

        if (fourCardHand.hasFlush()) {
            System.out.println("Failed test for hasFlush() of 2-3-4-5 hearts: expected false but got true");
        }
        if (fourCardHand.hasStraight()) {
            System.out.println("Failed test for hasStraight() of 2-3-4-5 hearts: expected false but got true");
        }
    }

    public static void main(String[] args) {
        testCode();
    }
}

package cribbage;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public final class Utility {

    private Utility() {
        throw new UnsupportedOperationException();
    }

    public static Hand handCopy(Hand h) {
        Hand copy = new Hand(Cribbage.cribbage.getDeck());
        ArrayList<Card> cards = h.getCardList();
        for (Card c : cards) {
            copy.insert(c.clone(), false);
        }
        return copy;
    }

    public static Hand sortHand(Hand hand) {
        Hand sorted = new Hand(Cribbage.cribbage.getDeck());
        ArrayList<Card> list = handCopy(hand).getCardList();
        Collections.sort(list, (c1, c2) -> {
            Cribbage.Rank r1 = (Cribbage.Rank) c1.getRank();
            Cribbage.Rank r2 = (Cribbage.Rank) c2.getRank();
            Cribbage.Suit s1 = (Cribbage.Suit) c1.getSuit();
            Cribbage.Suit s2 = (Cribbage.Suit) c2.getSuit();

            if (r1.order != r2.order) return r1.order - r2.order;
            return s1.ordinal() - s2.ordinal();
        });
        for (Card c: list) {
            sorted.insert(c, false);
        }
        return sorted;
    }

    public static int total(Hand hand) {
        int total = 0;
        for (Card c : hand.getCardList()) total += Cribbage.cardValue(c);
        return total;
    }

    /*
    Canonical String representations of Suit, Rank, Card, and Hand
    */
    private static String canonical(Cribbage.Suit s) {
        return s.toString().substring(0, 1);
    }

    private static String canonical(Cribbage.Rank r) {
        switch (r) {
            case ACE:
            case KING:
            case QUEEN:
            case JACK:
            case TEN:
                return r.toString().substring(0, 1);
            default:
                return String.valueOf(r.value);
        }
    }

    public static String canonical(Card c) {
        return canonical((Cribbage.Rank) c.getRank()) + canonical((Cribbage.Suit) c.getSuit());
    }

    public static String canonical(Hand h) {
        Hand h1 = new Hand(Cribbage.cribbage.getDeck()); // Clone to sort without changing the original hand
        for (Card C : h.getCardList()) h1.insert(C.getSuit(), C.getRank(), false);
        h1.sort(Hand.SortType.POINTPRIORITY, false);
        return "[" + h1.getCardList().stream().map(Utility::canonical).collect(Collectors.joining(",")) + "]";
    }
}

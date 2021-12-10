package cribbage;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class ShowFlushScoringStrategy implements IScoringStrategy {

    @Override
    public void getScore(Hand h) {
        Card starter = Cribbage.cribbage.getStarterCard();
        int player = Cribbage.cribbage.getPlayer();
        int previous_total = Cribbage.cribbage.getScores()[player];
        int score;

        // count the frequency of each suit
        int A[] = new int[Cribbage.Suit.values().length];
        Hand hand = new Hand(Cribbage.cribbage.getDeck());
        for (Card c: h.getCardList()) {
            if (c.getRank() == starter.getRank() && c.getSuit() == starter.getSuit()) continue;
            hand.insert(c.clone(), false);
            Cribbage.Suit s = (Cribbage.Suit) c.getSuit();
            A[s.ordinal()]++;
        }

        // loop through all suits to check for flush
        for (int i = 0; i < Cribbage.Suit.values().length; i++) {
            if (A[i] == 4) {
                // flush5
                if (starter.getSuit() == h.get(0).getSuit()) {
                    score = 5;
                    String s = String.format("score,P%d,%d,%d,flush%d,",player, previous_total+score, score, score);
                    logPrinter.writeLog(s + Utility.canonical(h));
                    Cribbage.cribbage.setScore(score, player);
                    return;
                }

                // flush4
                score = 4;
                String s = String.format("score,P%d,%d,%d,flush%d,",player, previous_total+score, score, score);
                logPrinter.writeLog(s + Utility.canonical(hand));
                Cribbage.cribbage.setScore(score, player);
                return;
            }
        }
    }
}

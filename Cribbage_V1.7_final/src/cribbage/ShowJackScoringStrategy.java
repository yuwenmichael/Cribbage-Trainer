package cribbage;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class ShowJackScoringStrategy implements IScoringStrategy {

    @Override
    public void getScore(Hand h) {
        int player = Cribbage.cribbage.getPlayer();
        int previous_total = Cribbage.cribbage.getScores()[player];
        Card starter = Cribbage.cribbage.getStarterCard();

        for (Card c: h.getCardList()) {
            if (c == starter) continue;
            Cribbage.Rank r = (Cribbage.Rank) c.getRank();
            if (r == Cribbage.Rank.JACK && c.getSuit() == starter.getSuit()) {
                String s = String.format("score,P%d,%d,%d,jack,[", player, previous_total + 1, 1);
                logPrinter.writeLog(s + Utility.canonical(c) + "]");
                Cribbage.cribbage.setScore(1, player);
                return;
            }
        }
    }
}

package cribbage;

import ch.aplu.jcardgame.Hand;

public class StarterScoringStrategy implements IScoringStrategy {

    @Override
    public void getScore(Hand h) {
        Cribbage.Rank r = (Cribbage.Rank) h.get(0).getRank();
        if (r != Cribbage.Rank.JACK) return;

        logPrinter.writeLog("score,P1,2,2,starter,[" + Utility.canonical(h.get(0)) + "]");
        Cribbage.cribbage.setScore(2, 1);
    }
}

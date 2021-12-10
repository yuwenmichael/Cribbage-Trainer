package cribbage;

import ch.aplu.jcardgame.Hand;

public class PlayLastPlayerStrategy implements IScoringStrategy {

    @Override
    public void getScore(Hand h) {
        int lastPlayer = Cribbage.cribbage.getLastPlayer();
        int previous_total = Cribbage.cribbage.getScores()[lastPlayer];

//        if (Cribbage.cribbage.isSegmentFinished()) {
            logPrinter.writeLog(String.format("score,P%d,%d,1,go",lastPlayer,previous_total+1));
            Cribbage.cribbage.setScore(1, lastPlayer);
//        }
    }
}
package cribbage;

import ch.aplu.jcardgame.Hand;

public class PlayThirtyOneScoringStrategy implements IScoringStrategy {

    @Override
    public void getScore(Hand h) {
        int lastPlayer = Cribbage.cribbage.getLastPlayer();
        int previous_total = Cribbage.cribbage.getScores()[lastPlayer];
        if(Utility.total(h) == 31){
            logPrinter.writeLog(String.format("score,P%d,%d,%d,thirtyone",lastPlayer,
                    previous_total+2,2));
            Cribbage.cribbage.setScore(2, lastPlayer);
        }
    }
}

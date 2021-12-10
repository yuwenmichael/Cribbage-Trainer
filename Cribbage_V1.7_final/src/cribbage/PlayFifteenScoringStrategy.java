package cribbage;

import ch.aplu.jcardgame.Hand;


public class PlayFifteenScoringStrategy implements IScoringStrategy {

    @Override
    public void getScore(Hand h) {
        int lastPlayer = Cribbage.cribbage.getLastPlayer();
        int previous_total = Cribbage.cribbage.getScores()[lastPlayer];
        if(Utility.total(h) == 15){
            String s = String.format("score,P%d,%d,%d,fifteen",lastPlayer,
                    previous_total+2,2);
            logPrinter.writeLog(s);
            Cribbage.cribbage.setScore(2, lastPlayer);
        }
    }
}

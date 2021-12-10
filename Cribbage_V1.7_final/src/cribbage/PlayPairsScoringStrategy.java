package cribbage;

import ch.aplu.jcardgame.Hand;

public class PlayPairsScoringStrategy implements IScoringStrategy {

    @Override
    public void getScore(Hand h) {
        int lastPlayer = Cribbage.cribbage.getLastPlayer();
        int previous_total = Cribbage.cribbage.getScores()[lastPlayer];
        // the number of cards is smaller than 2, no pair is possible
        if (h.getNumberOfCards() < 2) {
            return;
        }

        int i = h.getNumberOfCards() - 2;  // get the second last one
        int n = 1;                         // no. of cards (rank == lastCard.rank)

        while (i >= 0) {
            // compare current card with the next one in the hand
            if (h.get(i).getRank() == h.get(i + 1).getRank()) {
                n++;
                i--;
            } else {
                break;
            }
        }

        switch (n) {
            case 2:
                logPrinter.writeLog(String.format("score,P%d,%d,%d,pair%d",lastPlayer,
                        previous_total+2,2,2));
                Cribbage.cribbage.setScore(2, lastPlayer);
                return;
            case 3:
                logPrinter.writeLog(String.format("score,P%d,%d,%d,pair%d",lastPlayer,
                        previous_total+6,6,3));
                Cribbage.cribbage.setScore(6, lastPlayer);
                return;
            case 4:
                logPrinter.writeLog(String.format("score,P%d,%d,%d,pair%d",lastPlayer,
                        previous_total+12,12,4));
                Cribbage.cribbage.setScore(12, lastPlayer);
                return;
            default:
                return;
        }
    }


}

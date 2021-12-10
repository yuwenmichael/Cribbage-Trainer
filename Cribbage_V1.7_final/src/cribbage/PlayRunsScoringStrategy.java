package cribbage;

import ch.aplu.jcardgame.Hand;

public class PlayRunsScoringStrategy implements IScoringStrategy {
    @Override
    public void getScore(Hand h) {
        int lastPlayer = Cribbage.cribbage.getLastPlayer();
        int previous_total = Cribbage.cribbage.getScores()[lastPlayer];
        int score;

        Hand copy = Utility.handCopy(h);

        while (copy.getNumberOfCards() > 2) {
            Hand sorted = Utility.sortHand(copy);

            boolean flag = true;
            for (int i = 0; i < sorted.getNumberOfCards() - 1; i++) {

                Cribbage.Rank r1 = (Cribbage.Rank) sorted.get(i).getRank();
                Cribbage.Rank r2 = (Cribbage.Rank) sorted.get(i + 1).getRank();

                if (r1.order + 1 != r2.order) {
                    flag = false;
                    break;
                }
            }

            if (!flag) {
                copy.removeFirst(false);
            } else {
                score = sorted.getNumberOfCards();
                String s = String.format("score,P%d,%d,%d,run%d", lastPlayer, previous_total + score, score, score);
                logPrinter.writeLog(s);
                Cribbage.cribbage.setScore(score, lastPlayer);
                return;
            }
        }
    }
}

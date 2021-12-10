package cribbage;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.Collections;

public class ShowPairsScoringStrategy implements IScoringStrategy {

    @Override
    public void getScore(Hand h) {
        int player = Cribbage.cribbage.getPlayer();
        int previous_total = Cribbage.cribbage.getScores()[player];
        int score = 0;

        // count the frequency of each rank
        int A[] = new int[Cribbage.Rank.values().length];
        for (Card c: h.getCardList()) {
            Cribbage.Rank r = (Cribbage.Rank) c.getRank();
            A[r.order - 1]++;
        }

        Cribbage.Rank[] sorted = new Cribbage.Rank[13];
        for (int i = 0; i < 13; i++) {
            sorted[Cribbage.Rank.values()[i].order - 1] = Cribbage.Rank.values()[i];
        }

        // loop through all ranks to check for pairs
        for (int i = 0; i < 13; i++) {
            switch (A[i]) {
                case 2:
                    score += 2;
                    Cribbage.Rank rPairs = sorted[i];
                    Hand pair = Utility.sortHand(h.extractCardsWithRank(rPairs));
                    String s2 = String.format("score,P%d,%d,%d,pair2,", player, previous_total + score, 2);
                    logPrinter.writeLog(s2 + Utility.canonical(pair));
                    break;
                case 3:
                    score += 6;
                    Cribbage.Rank rTrips = sorted[i];
                    Hand trip = Utility.sortHand(h.extractCardsWithRank(rTrips));
                    String s3 = String.format("score,P%d,%d,%d,pair3,", player, previous_total + score, 6);
                    logPrinter.writeLog(s3 + Utility.canonical(trip));
                    break;
                case 4:
                    score += 12;
                    Cribbage.Rank rQuads = sorted[i];
                    Hand quad = Utility.sortHand(h.extractCardsWithRank(rQuads));
                    String s4 = String.format("score,P%d,%d,%d,pair4,", player, previous_total + score, 12);
                    logPrinter.writeLog(s4 + Utility.canonical(quad));
                    break;
                default:
                    continue;
            }
        }

        Cribbage.cribbage.setScore(score, player);
    }
}

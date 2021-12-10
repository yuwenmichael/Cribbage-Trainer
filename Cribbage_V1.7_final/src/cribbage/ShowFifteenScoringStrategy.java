package cribbage;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.awt.*;
import java.util.ArrayList;


public class ShowFifteenScoringStrategy implements IScoringStrategy {

    private ArrayList<Hand> outputs;

    @Override
    public void getScore(Hand h) {
        int score;
        outputs = new ArrayList<>();
        int player = Cribbage.cribbage.getPlayer();

        Card subset[] = new Card[h.getNumberOfCards()];
        findSubset(Utility.sortHand(h), 0, 15, subset, 0);

        // no combinations found
        if (outputs.isEmpty()) return;

        // log all combinations
        for (Hand output: outputs) {
            int previous_total = Cribbage.cribbage.getScores()[player];
            score = 2;
            String s = String.format("score,P%d,%d,%d,fifteen,", player, previous_total + score, score);
            logPrinter.writeLog(s + Utility.canonical(output));
            Cribbage.cribbage.setScore(score, player);
        }
    }

    private void findSubset(Hand h, int i, int k, Card subset[], int ssn) {
//        String indent = "# ";
//        for (int j = 0; j < i; j++) {
//            indent += "-->";
//        }

        // subset found, save this subset to list
        if (k == 0) {
            Hand output = new Hand(subset[0].getDeck());
            for (int n = 0; n < ssn; n++) {
                output.insert(subset[n], false);
            }
            outputs.add(output);
            return;
        }

        // no subset found, return
        if (i == h.getNumberOfCards()) return;

        // current is in the subset
        subset[ssn] = h.get(i);
        Cribbage.Rank r = (Cribbage.Rank) h.get(i).getRank();
        findSubset(h, i + 1, k - r.value, subset, ssn + 1);

        // current is not in the subset
        findSubset(h, i + 1, k, subset, ssn);
    }
}

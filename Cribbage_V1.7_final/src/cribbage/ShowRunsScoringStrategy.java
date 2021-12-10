package cribbage;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;

public class ShowRunsScoringStrategy implements IScoringStrategy {

    ArrayList<Hand> sequences;

    @Override
    public void getScore(Hand h) {
        int score;
        sequences = new ArrayList<>();
        int player = Cribbage.cribbage.getPlayer();

        ArrayList<Card>[] a = new ArrayList[13];
        Hand sorted = Utility.sortHand(h);

        for (int i = 0; i < 13; i++) {
            a[i] = new ArrayList<>();
        }

        // save the card to an array of arraylists according to its rank
        for (Card c: sorted.getCardList()) {
            Cribbage.Rank r = (Cribbage.Rank) c.getRank();
            a[r.order - 1].add(c);
        }

        // try to find runs with the longer length first
        for (int n = 5; n >= 3; n--) {
            boolean found = false;
            for (int i = 0; i <= 13 - n; i++) {
                boolean flag = true;
                for (int j = i; j < i + n; j++) {
                    if (a[j].size() == 0) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    Card[] sequence = new Card[n];
                    comb(sequence, a, i, 0, n);
                    found = true;
                }
            }
            if (found) break;
        }

        if (sequences.size() != 0) {
            for (Hand seq: sequences) {
                score = seq.getNumberOfCards();
                int previous_total = Cribbage.cribbage.getScores()[player];
                String s = String.format("score,P%d,%d,%d,run%d,", player, previous_total + score, score,score);
                logPrinter.writeLog(s + Utility.canonical(seq));
                Cribbage.cribbage.setScore(score, player);
            }
        }
    }

    private void comb(Card[] seq, ArrayList<Card>[] cards, int i, int j, int n) {
        if (j == n) {
            Hand h = new Hand(seq[0].getDeck());
            for (Card c: seq) {
                h.insert(c, false);
            }
            sequences.add(h);
            return;
        }

        for (Card c: cards[i]) {
            Card[] copy = copySeq(seq);
            copy[j] = c;
            comb(copy, cards, i + 1, j + 1, n);
        }
    }

    private Card[] copySeq(Card[] seq) {
        Card[] copy = new Card[seq.length];
        for (int i = 0; i < seq.length; i++) {
            if (seq[i] == null) break;
            copy[i] = seq[i].clone();
        }
        return copy;
    }
}

package cribbage;

import ch.aplu.jcardgame.Hand;

public interface IScoringStrategy {
    LogPrinter logPrinter = LogPrinter.getInstance();
    void getScore(Hand h);
}

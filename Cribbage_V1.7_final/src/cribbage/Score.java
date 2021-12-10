package cribbage;

import ch.aplu.jcardgame.Hand;

public class Score {
    IScoringStrategy scoringStrategy;

    public Score(IScoringStrategy scoringStrategy) {
        this.scoringStrategy = scoringStrategy;
    }
    public void getScore(Hand h) {
        scoringStrategy.getScore(h);
    }
}

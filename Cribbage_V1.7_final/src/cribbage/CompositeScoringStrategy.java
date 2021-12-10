package cribbage;

import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;

public class CompositeScoringStrategy implements IScoringStrategy{
    protected ArrayList<IScoringStrategy> scoringStrategies = new ArrayList<>();

    // add the strategy to the arraylist
    public void add(IScoringStrategy scoringStrategy){
        scoringStrategies.add(scoringStrategy);
    }

    public void getScore(Hand h) {
        for (IScoringStrategy scoringStrategy : this.scoringStrategies) {
            scoringStrategy.getScore(h);
        }
    }
}

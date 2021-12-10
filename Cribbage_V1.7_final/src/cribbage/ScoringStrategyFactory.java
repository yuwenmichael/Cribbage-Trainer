package cribbage;

public class ScoringStrategyFactory {
    private static ScoringStrategyFactory instance;

    public static ScoringStrategyFactory getInstance() {
        if (instance == null) instance = new ScoringStrategyFactory();
        return instance;
    }

    public IScoringStrategy getStrategy(String type) {
        switch (type) {
            case "starter":
                return new StarterScoringStrategy();
            case "play":
                CompositeScoringStrategy playScoringStrategy = new CompositeScoringStrategy();
                    playScoringStrategy.add(new PlayFifteenScoringStrategy());
                    playScoringStrategy.add(new PlayThirtyOneScoringStrategy());
                    playScoringStrategy.add(new PlayRunsScoringStrategy());
                    playScoringStrategy.add(new PlayPairsScoringStrategy());
                return playScoringStrategy;
            case "show":
                CompositeScoringStrategy showScoringStrategy = new CompositeScoringStrategy();
                showScoringStrategy.add(new ShowFifteenScoringStrategy());
                showScoringStrategy.add(new ShowRunsScoringStrategy());
                showScoringStrategy.add(new ShowPairsScoringStrategy());
                showScoringStrategy.add(new ShowFlushScoringStrategy());
                showScoringStrategy.add(new ShowJackScoringStrategy());
                return showScoringStrategy;
            case "lastPlayer":
                return new PlayLastPlayerStrategy();
            default:
                return null;
        }
    }
}

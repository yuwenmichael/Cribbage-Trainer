package cribbage;

// Cribbage.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.Color;
import java.awt.Font;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

public class Cribbage extends CardGame {
    static Cribbage cribbage;  // Provide access to singleton

    static LogPrinter logPrinter = LogPrinter.getInstance();

    public enum Suit {
        CLUBS, DIAMONDS, HEARTS, SPADES
    }

    public enum Rank {
        // Order of cards is tied to card images
        ACE(1, 1), KING(13, 10), QUEEN(12, 10), JACK(11, 10), TEN(10, 10), NINE(9, 9), EIGHT(8, 8), SEVEN(7, 7), SIX(6, 6), FIVE(5, 5), FOUR(4, 4), THREE(3, 3), TWO(2, 2);
        public final int order;
        public final int value;

        Rank(int order, int value) {
            this.order = order;
            this.value = value;
        }
    }

    static int cardValue(Card c) {
        return ((Cribbage.Rank) c.getRank()).value;
    }

    class MyCardValues implements Deck.CardValues { // Need to generate a unique value for every card
        public int[] values(Enum suit) {  // Returns the value for each card in the suit
            return Stream.of(Rank.values()).mapToInt(r -> (((Rank) r).order - 1) * (Suit.values().length) + suit.ordinal()).toArray();
        }
    }

    static Random random;

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    static boolean ANIMATE;

    void transfer(Card c, Hand h) {
        if (ANIMATE) {
            c.transfer(h, true);
        } else {
            c.removeFromHand(true);
            h.insert(c, true);
        }
    }

    private void dealingOut(Hand pack, Hand[] hands) {
        for (int i = 0; i < nStartCards; i++) {
            for (int j = 0; j < nPlayers; j++) {
                Card dealt = randomCard(pack);
                dealt.setVerso(false);  // Show the face
                transfer(dealt, hands[j]);
            }
        }
    }

    static int SEED;

    public static Card randomCard(Hand hand) {
        int x = random.nextInt(hand.getNumberOfCards());
        return hand.get(x);
    }

    private final String version = "0.1";
    static public final int nPlayers = 2;
    public final int nStartCards = 6;
    public final int nDiscards = 2;
    private final int handWidth = 400;
    private final int cribWidth = 150;
    private final int segmentWidth = 180;

    public Deck getDeck() {
        return deck;
    }

    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover", new MyCardValues());
    private final Location[] handLocations = {
            new Location(360, 75),
            new Location(360, 625)
    };
    private final Location[] scoreLocations = {
            new Location(590, 25),
            new Location(590, 675)
    };
    private final Location[] segmentLocations = {  // need at most three as 3x31=93 > 2x4x10=80
            new Location(150, 350),
            new Location(400, 350),
            new Location(650, 350)
    };
    private final Location starterLocation = new Location(50, 625);
    private final Location cribLocation = new Location(700, 625);
    private final Location seedLocation = new Location(5, 25);
    // private final TargetArea cribTarget = new TargetArea(cribLocation, CardOrientation.NORTH, 1, true);
    private final Actor[] scoreActors = {null, null}; //, null, null };
    private final Location textLocation = new Location(350, 450);
    private final Hand[] hands = new Hand[nPlayers];
    private final Hand[] showHands = new Hand[nPlayers + 1];
    private Hand starter;
    private Hand crib;

    public static void setStatus(String string) {
        cribbage.setStatusText(string);
    }

    static private final IPlayer[] players = new IPlayer[nPlayers];
    private final int[] scores = new int[nPlayers];

    final Font normalFont = new Font("Serif", Font.BOLD, 24);
    final Font bigFont = new Font("Serif", Font.BOLD, 36);

    private int player = 0;
	private int lastPlayer;

    public int getLastPlayer() {
        return lastPlayer;
    }

    public void setLastPlayer(int lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public int[] getScores() {
        return scores;
    }

    private void initScore() {
        for (int i = 0; i < nPlayers; i++) {
            scores[i] = 0;
            scoreActors[i] = new TextActor("0", Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], scoreLocations[i]);
        }
    }

    private void updateScore(int player) {
        removeActor(scoreActors[player]);
        scoreActors[player] = new TextActor(String.valueOf(scores[player]), Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], scoreLocations[player]);
    }

    private void deal(Hand pack, Hand[] hands) {
        for (int i = 0; i < nPlayers; i++) {
            hands[i] = new Hand(deck);
            // players[i] = (1 == i ? new HumanPlayer() : new RandomPlayer());
            players[i].setId(i);
            players[i].startSegment(deck, hands[i]);
        }
        RowLayout[] layouts = new RowLayout[nPlayers];
        for (int i = 0; i < nPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(0);
            // layouts[i].setStepDelay(10);
            hands[i].setView(this, layouts[i]);
            hands[i].draw();
        }
        layouts[0].setStepDelay(0);
        // assign the cards to each player
        dealingOut(pack, hands);
        for (int i = 0; i < nPlayers; i++) {
            hands[i].sort(Hand.SortType.POINTPRIORITY, true);
        }
        layouts[0].setStepDelay(0);
        //write to the log file
        for (int i = 0; i < nPlayers; i++) {
            logPrinter.writeLog("deal,P" + i + "," + Utility.canonical(hands[i]));
        }

    }

    private void discardToCrib() {
        crib = new Hand(deck);
        RowLayout layout = new RowLayout(cribLocation, cribWidth);
        layout.setRotationAngle(0);
        crib.setView(this, layout);
        crib.draw();
        int numPlayer = 0;
        for (IPlayer player : players) {
            Hand discard = new Hand(deck);
            for (int i = 0; i < nDiscards; i++) {
                Card card = player.discard();
                transfer(card, crib);
                Card card_copy = new Card(deck,card.getCardNumber());
                discard.insert(card_copy, false);
            }
            crib.sort(Hand.SortType.POINTPRIORITY, true);

            logPrinter.writeLog("discard,P" + numPlayer + ',' + Utility.canonical(discard));
            numPlayer++;

            // save a copy of the dealt cards
            showHands[0] = Utility.handCopy(hands[0]);
            showHands[1] = Utility.handCopy(hands[1]);
        }
        showHands[2] = Utility.handCopy(crib);
    }

    public Card getStarterCard() {
        return starter.get(0);
    }

    private void starter(Hand pack) {
        starter = new Hand(deck);  // if starter is a Jack, the dealer gets 2 points
        RowLayout layout = new RowLayout(starterLocation, 0);
        layout.setRotationAngle(0);
        starter.setView(this, layout);
        starter.draw();
        Card dealt = randomCard(pack);
        logPrinter.writeLog("starter" + ',' + Utility.canonical(dealt));
        dealt.setVerso(false);
        transfer(dealt, starter);

        IScoringStrategy starterScoringStrategy = ScoringStrategyFactory.getInstance().getStrategy("starter");
        starterScoringStrategy.getScore(starter);
    }

    class Segment {
        Hand segment;
        boolean go;
        int lastPlayer;
        boolean newSegment;

        void reset(final List<Hand> segments) {
            segment = new Hand(deck);
            segment.setView(Cribbage.this, new RowLayout(segmentLocations[segments.size()], segmentWidth));
            segment.draw();
            go = false;        // No-one has said "go" yet
            lastPlayer = -1;   // No-one has played a card yet in this segment
            newSegment = false;  // Not ready for new segment yet
        }
    }

    public void setScore(int scoreToAdd, int i) {
        scores[i] += scoreToAdd;
        updateScore(i);
    }

    private void play() {
        final int thirtyone = 31;
        List<Hand> segments = new ArrayList<>();
        Segment s = new Segment();
        s.reset(segments);

        // if both of the players have card in their hand, keep looping
        while (!(players[0].emptyHand() && players[1].emptyHand())) {
            Card nextCard = players[getPlayer()].lay(thirtyone - Utility.total(s.segment));

            // if cannot place a card, then go
            if (nextCard == null) {
                if (s.go) {
                    // Another "go" after previous one with no intervening cards
                    // lastPlayer gets 1 point for a "go"
                    s.newSegment = true;
                    getScore("lastPlayer", s.segment);
                    setPlayer((getPlayer() + 1) % 2);
                } else {
                    // currentPlayer says "go"
                    s.go = true;
                    setPlayer((getPlayer() + 1) % 2);
                    continue;
                }

            } else {
                s.lastPlayer = getPlayer(); // last Player to play a card in this segment
                setLastPlayer(getPlayer());
                transfer(nextCard, s.segment); // put the card to the segment
                logPrinter.writeLog("play," + "P" + getPlayer() + "," + Utility.total(s.segment) + "," + Utility.canonical(nextCard));

                if (Utility.total(s.segment) == thirtyone) {
                    // lastPlayer gets 2 points for a 31
                    s.newSegment = true;
                    setPlayer((getPlayer() + 1) % 2);
                } else {
                    // if total(segment) == 15, lastPlayer gets 2 points for a 15
                    if (!s.go) { // if it is "go" then same player gets another turn
                        setPlayer((getPlayer() + 1) % 2);
                    }
                }
                getScore("play", s.segment);
            }

            if (players[0].emptyHand() && players[1].emptyHand() && Utility.total(s.segment) != thirtyone) {
                getScore("lastPlayer", s.segment);
            }


            // have a new segment if thirtyone
            if (s.newSegment) {
                segments.add(s.segment);
                s.reset(segments);
            }
        }
    }

    private void getScore(String type, Hand h) {
        switch (type) {
            case "play":
                IScoringStrategy playScoringStrategy = ScoringStrategyFactory.getInstance().getStrategy("play");
                new Score(playScoringStrategy).getScore(h);
                break;
            case "lastPlayer":
                IScoringStrategy lastPlayScoringStrategy = ScoringStrategyFactory.getInstance().getStrategy("lastPlayer");
                new Score(lastPlayScoringStrategy).getScore(h);
                break;
            case "show":
                IScoringStrategy showScoringStrategy = ScoringStrategyFactory.getInstance().getStrategy("show");
                new Score(showScoringStrategy).getScore(h);
                break;
        }
    }

    Hand insertStarterToHand(int i) {
        Hand h = new Hand(deck);
        for (Card c : showHands[i].getCardList()) {
            h.insert(c.clone(), false);
        }
        h.insert(starter.get(0), false);
        return h;
    }

    void showHandsCrib() {
        // score player 0 (non dealer)
        setPlayer(0);
        logPrinter.writeLog("show,P0," + Utility.canonical(starter.get(0)) + "+" + Utility.canonical(showHands[0]));
        Hand player0 = insertStarterToHand(0);
        getScore("show", player0);

        // score player 1 (dealer)
        setPlayer(1);
        logPrinter.writeLog("show,P1," + Utility.canonical(starter.get(0)) + "+" + Utility.canonical(showHands[1]));
        Hand player1 = insertStarterToHand(1);
        getScore("show", player1);

        // score crib (for dealer)
        logPrinter.writeLog("show,P1," + Utility.canonical(starter.get(0)) + "+" + Utility.canonical(showHands[2]));
        Hand crib = insertStarterToHand(2);
        getScore("show", crib);
    }

    public Cribbage() {
        super(850, 700, 30);
        cribbage = this;
        setTitle("Cribbage (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScore();

        Hand pack = deck.toHand(false);
        RowLayout layout = new RowLayout(starterLocation, 0);
        layout.setRotationAngle(0);
        pack.setView(this, layout);
        pack.setVerso(true);
        pack.draw();
        addActor(new TextActor("Seed: " + SEED, Color.BLACK, bgColor, normalFont), seedLocation);

        /* Play the round */
        deal(pack, hands);
        discardToCrib();
        starter(pack);
        play();
        showHandsCrib();

        addActor(new Actor("sprites/gameover.gif"), textLocation);
        setStatusText("Game over.");
        refresh();
    }

    public static void main(String[] args)
            throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {

        /* Handle Properties */
        // System.out.println("Working Directory = " + System.getProperty("user.dir"));
        Properties cribbageProperties = new Properties();
        // Default properties
        cribbageProperties.setProperty("Animate", "true");
        cribbageProperties.setProperty("Player0", "cribbage.RandomPlayer");
        cribbageProperties.setProperty("Player1", "cribbage.HumanPlayer");

        // Read properties
        try (FileReader inStream = new FileReader("cribbage.properties")) {
            cribbageProperties.load(inStream);
        }

        // Control Graphics
        ANIMATE = Boolean.parseBoolean(cribbageProperties.getProperty("Animate"));

        // Control Randomisation
        /* Read the first argument and save it as a seed if it exists */
        if (args.length > 0) { // Use arg seed - overrides property
            SEED = Integer.parseInt(args[0]);
        } else { // No arg
            String seedProp = cribbageProperties.getProperty("Seed");  //Seed property
            if (seedProp != null) { // Use property seed
                SEED = Integer.parseInt(seedProp);
            } else { // and no property
                SEED = new Random().nextInt(); // so randomise
            }
        }
        random = new Random(SEED);

        // Control Player Types
        Class<?> clazz;
        clazz = Class.forName(cribbageProperties.getProperty("Player0"));
        players[0] = (IPlayer) clazz.getConstructor().newInstance();
        clazz = Class.forName(cribbageProperties.getProperty("Player1"));
        players[1] = (IPlayer) clazz.getConstructor().newInstance();
        // End properties


        logPrinter.writeLog("seed," + SEED);
        logPrinter.writeLog(cribbageProperties.getProperty("Player0") + ",P0");
        logPrinter.writeLog(cribbageProperties.getProperty("Player1") + ",P1");
        new Cribbage();
    }

}

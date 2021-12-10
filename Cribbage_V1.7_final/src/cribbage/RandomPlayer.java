package cribbage;

import ch.aplu.jcardgame.Card;

public class RandomPlayer extends IPlayer {

	@Override
	public Card discard() {
		return Cribbage.randomCard(hand);
	}

	@Override
	Card selectToLay() {
		return hand.isEmpty() ? null : Cribbage.randomCard(hand);
	}
}

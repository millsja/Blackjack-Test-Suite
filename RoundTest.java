package edu.osu.blackjack;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;
import java.lang.Math;


// for the purposes of this test, I've modified...
// 1. Dealer.compareHandAndSettle: removed cleanup,
// 								   made handScore public
// 								   added getDeck func
// 2. SimpleBlackjack.playRound: removed shuffle
// 3. Player.getAction: added method to cue up 
// 	  multiple actions
// 4. Player.makeBet: added moveMoneyToBet
public class RoundTest {
	public static final Random r = new Random();
	public static final int TEST_COUNT = 100;
	public static final int MAX_PLAYERS = 4;


	// generates new player with random wallet,
	// insurance bet initialized to zero, and a
	// set of actions to perform during the game
	private static Player randPlayer(){
		// init new player with empty hand
		Player p = new Player();

		// currentWallet must be random	
		p.currentWallet = Math.abs(r.nextInt());
		p.currentInsurance = 0;

		// generate random assortment of actions
		int actionCount = Math.abs(r.nextInt());
		int action = -1;
		List<PlayerAction.ActionType> actionList = 
			new ArrayList<PlayerAction.ActionType>();
		for(int q = 0; q < actionCount; q++){
			action = r.nextInt(3);
			switch(action){
				case 0:
						actionList.add(PlayerAction.ActionType.HIT);
						break;
				case 1:
						actionList.add(PlayerAction.ActionType.DOUBLE);
						q = actionCount;
						break;
				case 2:
						actionList.add(PlayerAction.ActionType.STAND);
						q = actionCount;
						break;
			}	
			
		}

		// add actionList to		
		p.setActionList( actionList );

		return p;

	}

	// compares scores and checks for bust to
	// determine winner between dealer and player
	public boolean checkPlayerWin( Dealer d, Player p){
		if( Dealer.handScore(p.getHand()) > 
			Dealer.handScore(d.getHand()) &&
			Dealer.handScore(p.getHand()) <= 21 ){
			 return true;
		}
		else return false;
	}

	// simple random test that hcecks for correct number
	// of cards dealt, correct win calculation and bet settling, 
	// as well as deck size changes
	@Test
    public void testRound(){
		for(int i = 0; i < TEST_COUNT; i++){
			// generate dealer
			Dealer d = new Dealer();
			d.shuffleDeck();
			
			// generate players
			int playerCount = r.nextInt(MAX_PLAYERS);
			playerCount += 1; // we want 1-4 not 0-3
			List wallets = new ArrayList();
			PlayerAction[] playerList = new PlayerAction[playerCount];
			for(int q = 0; q < playerCount; q++){
				playerList[q] = randPlayer();
				wallets.add( ((Player)playerList[q]).currentWallet );
				
			}

			// build and run the game round
			SimpleBlackjack round = new SimpleBlackjack(d, playerList);
			round.playRound();

			// assert #1: ensure that the player has a hand with
			// two cards plus an additional card for each hit
			int totalCardsDealt = 0;
			int cardCount;
			Player p;
			for(int q = 0; q < playerCount; q++){
				cardCount = 0;
				p = (Player)playerList[q];
				// there are two cases: 
				// 1. the player hits and then stands
				// in this case, you have cards =
				// actions - 1 + 2 cards for initial
				// that are dealt
				if( p.actionList.get(p.actionList.size() - 1) ==
					PlayerAction.ActionType.STAND ){
					cardCount = p.actionList.size() + 1;
					totalCardsDealt += cardCount;
				}

				// 2. the player hits and then doubles
				// in this case, you have cards =
				// actions + 2 cards for initial
				// that are dealt
				else{
					cardCount = p.actionList.size() + 2;
					totalCardsDealt += cardCount;
				}

				// check to see whether we have the right
				// number of cards for the actions taken
				assertEquals( p.currentHand.size(), cardCount );	
			}


			// assert #2: check that any player with wallet
			// greater than or equal to ten has a current bet
			// and... 
			//
			// assert #3: ensure that the players with scores
			// > dealer's and <= 21 have wallets larger than
			// what they started with
			int startWallet;
			for(int q = 0; q < playerCount; q++){
				startWallet = (int) wallets.get(q);
				p = (Player) playerList[q];

				if( startWallet >= 10 ) assertTrue( p.currentBet > 0 );

				if( checkPlayerWin(d, p) && p.currentBet > 0 ){
					assertTrue( p.currentWallet > startWallet );
				}
			}
				
			// assert #4: ensure that the deck is smaller by a
			// number equal to 2 * the number of players plus
			// one card for each hit or double
			totalCardsDealt += d.getHand().size();
			assertTrue( (d.getDeck().size() + totalCardsDealt) == 52 );

		}

    }

}

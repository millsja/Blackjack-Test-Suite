package edu.osu.blackjack;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Random;
import static org.mockito.Mockito.*;

public class SimpleBlackjackTest {


	// Check whether the correct number of cards are
	// dealt before play begins (as well as a few other
	// controls for other tests)
	// Input: initialize player that will stand when
	// 		  getAction is called. Run the round and
	// 		  check the number of times dealCard is
	// 		  called from the dealer object.
	// Expected: in Blackjack, the dealer and each player
	// 			 start with two cards each. Therefore,
	// 			 dealCard should be called four times if
	// 			 the dealer and player are to have two
	// 			 cards each.
	// Note: also includes controls for some later tests
	// 		 e.g. check that doubleDownBet isn't called
	// 		 when double isn't one of the player's actions
    @Test
    public void testBasicDeal(){
		DealerAction dealer = mock(DealerAction.class);
		PlayerAction player = mock(PlayerAction.class);

		// Don't give the player any cards 
		when(player.getAction()).thenReturn(PlayerAction.ActionType.STAND);

		// Insurance is unavailable
		when(dealer.isInsuranceAvailable()).thenReturn(false);

		// Run through the round's actions
		SimpleBlackjack round = new SimpleBlackjack(dealer, 
								new PlayerAction[]{player});
		round.playRound();

		// Expected: before anyone makes a play, the dealer 
		// gives two cards to himself and two to the player
		verify(dealer, times(2)).dealCard(player);
		verify(dealer, times(2)).dealCard(dealer);

		// Expected: doubleDownBet and makeInsurance bet
		// 			 should not occur
		verify(player, times(0)).doubleDownBet();
		verify(player, times(0)).makeInsuranceBet();

		// Expected: makeBet and settling both occur 
		verify(player).makeBet();
		verify(dealer).compareHandAndSettle(player);

    }

	// Check whether player's HIT action causes the
	// appropriate number of dealCard calls to be made
	// Input: initialize player that will HIT when
	// 		  getAction is called. Run the round and
	// 		  check that dealCard is called the 
	// 		  appropriate number of times
	// Expected: dealCard should be called three times
	// 			 on the player: two for initial cards 
	// 			 and the once more for the hit
    @Test
    public void testPlayerHit(){
		DealerAction dealer = mock(DealerAction.class);
		PlayerAction player = mock(PlayerAction.class);

		// Player doubles
		when(player.getAction()).thenReturn(PlayerAction.ActionType.HIT)
								.thenReturn(PlayerAction.ActionType.STAND);

		// Run through the round's actions
		SimpleBlackjack round = new SimpleBlackjack(dealer, 
								new PlayerAction[]{player});
		round.playRound();

		// Dealer should deal twice for initial cards and
		// then once more in response to the player's
		// HIT action
		verify(dealer, times(3)).dealCard(player);

    }
	
	// Check whether doubling occurs correctly
	// Input: initialize player that will double when
	// 		  getAction is called. Run the round and
	// 		  check that doubleDownBet is called
	// Expected: doubleDownBet should be called and 
	// 			 dealCard method should be called
	// 			 an appropriate number of times
    @Test
    public void testPlayerDouble(){
		DealerAction dealer = mock(DealerAction.class);
		PlayerAction player = mock(PlayerAction.class);

		// Player doubles
		when(player.getAction()).thenReturn(PlayerAction.ActionType.DOUBLE);

		// Run through the round's actions
		SimpleBlackjack round = new SimpleBlackjack(dealer, 
								new PlayerAction[]{player});
		round.playRound();

		// Player's doubleDownBet ought to be called 
		// since double down is one if his actions
		verify(player).doubleDownBet();

		// Dealer should deal twice for initial cards and
		// then once more in response to the player's
		// DOUBLE action
		verify(dealer, times(3)).dealCard(player);

		// Doubling ends play for this round, so settling
		// should occur
		verify(dealer).compareHandAndSettle(player);

    }

	// Check that the player's makeInsuranceBet method
	// is called when dealer's isInsuranceAvailable method
	// returns true. Note: inverse is tested in testBasicDeal 
	// Input: initialize player that will stand when
	// 		  getAction is called, and the dealer such that
	// 		  when isInsuranceAvailable is called, it returns
	// 		  true. Run the round and check that makeInsuranceBet
	// 		  is called
	// Expected: makeInsuranceBet should be called
    @Test
    public void testPlayerInsuranceBet(){
		DealerAction dealer = mock(DealerAction.class);
		PlayerAction player = mock(PlayerAction.class);

		// Don't give the player any cards 
		when(dealer.isInsuranceAvailable()).thenReturn(true);
		when(player.getAction()).thenReturn(PlayerAction.ActionType.STAND);

		// Run through the round's actions
		SimpleBlackjack round = new SimpleBlackjack(dealer, 
								new PlayerAction[]{player});
		round.playRound();

		// Expected: before anyone makes a play, the dealer 
		// gives two cards to himself and two to the player
		verify(player).makeInsuranceBet();

    }

	// Test to see whether multiple players can be accomodated
	// successfully e.g., in the simplest case, that the correct 
	// number of cards is dealt
	// Input: initialize three or more players with STAND action
	// 		  Run the round and check to see how many cards
	// 		  are dealt to the dealer 
	// Expected: two cards only should be dealt to the dealer
    @Test
    public void testMultiPlayer(){
		DealerAction dealer = mock(DealerAction.class);
		PlayerAction playerA = mock(PlayerAction.class);
		PlayerAction playerB = mock(PlayerAction.class);
		PlayerAction playerC = mock(PlayerAction.class);

		when(playerA.getAction()).thenReturn(PlayerAction.ActionType.STAND);
		when(playerB.getAction()).thenReturn(PlayerAction.ActionType.STAND);
		when(playerC.getAction()).thenReturn(PlayerAction.ActionType.STAND);

		// Run through the round's actions
		SimpleBlackjack round = new SimpleBlackjack(dealer, 
								new PlayerAction[]{playerA, playerB, playerC});
		round.playRound();

		// Dealer should deal himself only twice
		verify(dealer, times(2)).dealCard(dealer);

    }

}

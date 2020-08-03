package com.InternSentinelSolutions.springboot.Blackjack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

public class StartGame {
	private static int currcard = 0; //top most card in deck
	private Shoe shoe = null;
	ArrayList <Card> dealer = new ArrayList<Card>();
	ArrayList<Card> player = new ArrayList<Card>();
	Hand phand = null;
	Hand dhand = null;
	AllHands all = null;

	public StartGame(){
		
	}
	
	
	public static void main(String[] args) {
		StartGame game = new StartGame();
		//game.run();
	}
	
	public void setShoe(int deck, int playable, int... cards) {
		shoe = new Shoe(deck, playable, cards);
		shoe.createDeck();
	}
	
	public void startGame(PlayerInfo info) {
		int statusP = 0;
		int statusD = 0;
		boolean shuffled = false;
		
		BigDecimal original = info.getOriginalBet();
		BigDecimal remaining = info.getRemaining(); 
		BigDecimal bet = info.getBet();
		
		phand = new Hand(player);
		dhand = new Hand(dealer);
		all = new AllHands();
		all.addData(phand);
		deal();
		
		PlayerTurn pmove = new PlayerTurn();
		//get force from button pushed
		//if chooses insure - create input # box to enter bet - get bet (set to playerInfo)
			
		for(int i = 0; i < all.getPlayerHands().size(); i++) {
			statusP = pmove.move(all.getPlayerHands().get(i), dhand, shoe, all, currcard);
		}
		if(statusP == 2 || statusP == 0) { //only call dealer if a split hand surrendered but not if single hand surrendered
			statusD = dealerTurn(info);
		}
		if((statusP == 1 || statusD == 1 || statusP == 2) && currcard <= shoe.getCurrDeck().size() - 4) { //still has 4+ cards to continue playing game
				//starting another game
				int winP = phand.getSum();
				int winD = dhand.getSum();
				
				if(winP == winD){ //"push" no one wins but player loses if both bust
					if(winP <= 21) {
						remaining = remaining.add(bet);
					}
				}
				if(currcard >= shoe.getPlayable() && shuffled == false) {
					shuffle();
					currcard = 0;
					shuffled = true; 
				}
				redeal(original, bet);
		}else if(currcard > shoe.getCurrDeck().size() - 1 || statusP == 2) { //end of deck so tally and terminate game
				int winP = 0;
				int winD = dhand.getSum();
				boolean accounted = false;
				boolean count = false;
				
				for(int i = 0; i < all.getPlayerHands().size(); i++) {
					winP = all.getPlayerHands().get(i).getSum();
					Hand current = all.getPlayerHands().get(i);
					ArrayList<Card> p = current.getHand();

					if(winP == 21 && winD != 21 && count == false) {
						if(p.size() == 2) {
							current.setBlackjack();
							BigDecimal back = bet.multiply(BigDecimal.valueOf(1.5));
							remaining = remaining.add(back); //payout for blackjack is 1.5*bet
						}else {
							remaining = remaining.add(bet);
						}
						count = true;
					}else if(winP > 21) {
						current.setBust();
						if(accounted == false) {
							if(all.getPlayerHands().size() > 1) {
								remaining = remaining.subtract(original);
							}
							else {
								remaining = remaining.subtract(bet);
							}
							accounted = true;
						}
					}else if(winD > winP && winD <= 21){
						if(all.getPlayerHands().size() > 1) {
							remaining = remaining.subtract(original);
						}
						else {
							if(accounted == false){
								remaining = remaining.subtract(bet);
							}
							accounted = true;
						}
					}else if(winP > winD && winP <= 21 && count == false) { //no one busted but player gets back original bet
						remaining = remaining.add(bet); //player gets back original betx2 (wins)
						count = true;
					}else if(current.isSurrender()) {
						remaining = remaining.subtract(bet);
					}
				}
				if(winD > 21) {
					dhand.setBust();
				}else if (winD == 21) {
					if(dealer.size() == 2) {
						dhand.setBlackjack();
					}
				}
		}
		//}
		clearHands();
	}
	
	
	
	public int dealerTurn(PlayerInfo info) {
		int status = 0;
		int tally = playerTally(phand);
		BigDecimal insure_bet = info.getInsure();
		BigDecimal remaining = info.getRemaining();
		
		if(dealer.get(1).getRank().equalsIgnoreCase("A")){ //dealer is currently showing an ace
			if(dealer.get(0).getValue() == 10) {
				if (tally != 4) {
					dhand.setBlackjack();
				}
				if(phand.isInsurance()) {
					BigDecimal won = insure_bet.multiply(BigDecimal.valueOf(2.0));
					remaining = remaining.add(won).add(insure_bet);
					//remaining += (insure_bet*2 + insure_bet); //add to remaining -> won money back but original bet is still taken away (as already done)
				}
				status = 1;
			}else {
				//current hand continues as normal for p w/o insurance
				//p w/ insurance loses insurance bet & current hand continues
				if(phand.isInsurance()) {
					remaining = remaining.subtract(insure_bet);
				}
				if(tally != 0) {
					status = 1;
				}
			}
		}
		else if(dealer.get(1).getValue() == 10) {  //no insurance asked for here
			if(dealer.get(0).getRank().equalsIgnoreCase("A")) {
				if (tally != 4) {
					dhand.setBlackjack();
				}
				status = 1;
				return status;
			}
		}
		else {
			while(dhand.getSum() < 17 && (currcard < shoe.getCurrDeck().size())) {
				dealer.add(shoe.getCurrDeck().get(currcard));
				currcard++;
		
				int sum = dhand.getSum();
				if(sum > 21) {
					dhand.setBust();
					if(tally == 0 || tally == 2) {
						//didnt bust or get blackjack
						//BigDecimal back = bet.multiply(BigDecimal.valueOf(2.0));
						remaining = remaining.add(info.getBet()); //player gets back betx2 (wins)
					}
					status = 1;
					return status;
				}else if(sum == 21) {
					if(tally != 4) {
						dhand.setBlackjack();
					}	
					status = 1;
					return status;
				}else if(sum >= 17 && sum < 21){
					status = 0; //has soft 17 hand
					return status;
				}
			}
		}
		return status;
	}
	
	public void shuffle(){
		Random rand = new Random(); 
	    for (int i = shoe.getCurrDeck().size() - 1; i > 0; i--) { 
	        // Random for remaining positions. 
	        int r = rand.nextInt(shoe.getCurrDeck().size()-i); 
	          
	         //swapping the elements 
	         Card temp = shoe.getCurrDeck().get(r); 
	         shoe.getCurrDeck().set(r, shoe.getCurrDeck().get(i)); 
	         shoe.getCurrDeck().set(i, temp);
	    } 
	}
	
	public void deal() {
		player.add(shoe.getCurrDeck().get(currcard));
		currcard++;
		
		dealer.add(shoe.getCurrDeck().get(currcard));
		currcard++;
		
		player.add(shoe.getCurrDeck().get(currcard));
		currcard++;
		
		dealer.add(shoe.getCurrDeck().get(currcard));
		currcard++;
	}
	
	public void redeal(BigDecimal original, BigDecimal bet) {
		//clear
		clearHands();
		if(bet.compareTo(BigDecimal.valueOf(-1.0)) == 0) { 
			//terminate
			return;
		}
		original = bet;
		//remaining = remaining.subtract(bet);
		all.addData(phand);
		deal(); //dealing first 2 cards to dealing & 2 to player from shuffled deck
	}
	
	public void clearHands() {
		//actionp = 0;
		dhand.clearData();
		dealer.clear();
		phand.clearData();
		all.clearData();
	}
	
	public Hand getDealerHand(){  //pass in dealer hand
		return dhand;
	}
	
	public ArrayList<Hand> getPlayerHands(){  //pass in player's hand(s)
		return all.getPlayerHands();
	}

	public int playerTally(Hand phand) {
		int status = 0;
		int sum = phand.getSum();
		int other = dhand.getSum();

		if(sum > 21) {
			if(all.getPlayerHands().size() == 1) {
				phand.setBust();
			}
			status = 1;
		}else if(sum == 21) {
			if(phand.getHand().size() > 2) {	
				status = 2; //dealer must go
				
			}else if(all.getPlayerHands().size() == 1 && phand.getHand().size() == 2) {
				/*if(other != 21) {
					phand.setBlackjack();
					BigDecimal back = original.multiply(BigDecimal.valueOf(1.5));
	
					remaining = remaining.add(back).add(bet); //payout for blackjack is 1.5*bet
				}else if(other == 21 && dhand.getHand().size() == 2) {
					remaining = remaining.add(bet); //tied game -> no blackjack
				}*/
				status = 4;
			}
		}else if(sum < 21) {
			status = 2;
		}
		return status;
	}
}
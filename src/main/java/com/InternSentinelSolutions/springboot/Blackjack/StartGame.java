package com.InternSentinelSolutions.springboot.Blackjack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

public class StartGame {
	private static int currcard = 0; //top most card in deck
	private Shoe shoe = null;
	private PlayerInfo infop = null;
	ArrayList <Card> dealer = new ArrayList<Card>();
	ArrayList<Card> player = new ArrayList<Card>();
	Hand phand = null;
	Hand dhand = null;
	AllHands all = null;

	public StartGame(PlayerInfo info){
		this.infop = info;
	}
	
	public void setShoe(int deck, int playable, int... cards) {
		shoe = new Shoe(deck, playable, cards);
		shoe.createDeck();
	}
	
	public Shoe getShoe() {
		return shoe;
	}
	
	public int getCurrCard() {
		return currcard;
	}
	
	public void setCurrCard() {
		currcard = 0;
	}
	
	public Hand getDealerHand(){  //pass in dealer hand
		return dhand;
	}
	
	public ArrayList<Hand> getPlayerHands(){  //pass in player's hand(s)
		return all.getPlayerHands();
	}
	
	public AllHands getAll() {
		return all;
	}
	
	public void start() {
		phand = new Hand(player);
		dhand = new Hand(dealer);
		all = new AllHands();
		all.addData(phand);
		deal();
	}

	public int dealerTurn() {
		int status = 0;
		int tally = playerTally(phand);
		BigDecimal insure_bet = infop.getInsure();
		BigDecimal remaining = infop.getBankroll();
		
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
						remaining = remaining.add(infop.getBet()); //player gets back betx2 (wins)
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
		dhand.clearData();
		dealer.clear();
		phand.clearData();
		all.clearData();
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
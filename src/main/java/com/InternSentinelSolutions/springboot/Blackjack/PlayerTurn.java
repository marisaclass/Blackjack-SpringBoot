package com.InternSentinelSolutions.springboot.Blackjack;

import java.math.BigDecimal;
import java.util.ArrayList;

public class PlayerTurn {
	private static PlayerInfo infop;
	private static StartGame start;
	private BigDecimal bet = infop.getBet();
	private BigDecimal original = infop.getOriginalBet();
	private boolean asked = false;
	private Action force = null;
	
	public PlayerTurn(PlayerInfo info, StartGame os) {
		infop = info;
		start = os;
	}
	
	public void setAction(String action) {
		this.force = Action.valueOf(action);
	}
	
	public Action getAction() {
		return force;
	}
	
	public int move(Hand phand, Hand dhand, Shoe shoe, AllHands all, int currcard) {
		int status = 0;
		
		if(phand.getHand().size() == 1) { //the next splitted hand
			phand.setSplit();
			phand.getHand().add(shoe.getCurrDeck().get(currcard));
			currcard++;
		}

		if(force == Action.SUGGESTION) {
			this.force = Suggestion.getAdvice(dhand.getHand().get(1).getValue(), phand, all);
		}
		
		if(asked == false && force == Action.INSURANCE && dhand.getHand().get(1).getRank().equalsIgnoreCase("A")){
			//dealer is currently showing an ace
			if(infop.getInsure().compareTo(BigDecimal.ZERO) == 1) {
				phand.setInsurance();
			}
			asked = true;
		}

		else if(force == Action.SPLIT && all.maxSplit() < 5 && phand.getHand().size() == 2) { 
			if(phand.getHand().get(0) == phand.getHand().get(1) && phand.hasAce() != 1) {	
				split(phand.getHand(), shoe, all, currcard);
				phand.setSplit();
				all.addSplit();
					
				return move(phand, dhand, shoe, all, currcard);
			}
		}
		else if(force == Action.DOUBLE && phand.getHand().size() == 2) {
				doubleDown(dhand);
				phand.getHand().add(shoe.getCurrDeck().get(currcard));
				currcard++;
				phand.setDoubleDown();
					
				int up2 = start.playerTally(phand);
				if(up2 == 1) { //busted
					status = 1;
				}else if(up2 == 2 || up2 == 4) { //dealer must have turn
					status = 2;
				}
				return status;
		}	
		else if(force == Action.SURRENDER) {
			if(dhand.getSum() != 21) {
				surrender();
				phand.setSurrender();
			}
			status = 0;
		}		
		else if(force == Action.HIT) {
			phand.getHand().add(shoe.getCurrDeck().get(currcard));
			currcard++;
			boolean hitP = true;
				
			while(hitP && (currcard < shoe.getCurrDeck().size() - 1)) {	
				force = Suggestion.getAdvice(dhand.getHand().get(1).getValue(), phand, all); //next action
				
				int up3 = start.playerTally(phand);	
				if(up3 == 1) { //busted
					hitP = false;
					status = 1;				
				}else if(up3 == 2 || up3 == 4) { //dealer must have turn
					status = 2;
					hitP = false;
				}else if(force == Action.HIT) {
					phand.getHand().add(shoe.getCurrDeck().get(currcard));
					currcard++;
					hitP = true;
				}else {
					if(force == Action.STAND || force != Action.HIT) {
						if(up3 == 1) { //busted
							status = 1;
						}else if(up3 == 2 || up3 == 4) { //dealer must have turn
							status = 2;
						}
						hitP = false;
					}			
				}
			} 
		}		
		else if(force == Action.STAND) {
			status = 2;
		}
		return status;
	}
	
	public void doubleDown(Hand dhand) {
		if(dhand.getSum() != 21) {
			//remaining = remaining.subtract(original); //taking away bet*2 but original bet is already accounted for
			bet = bet.add(original);
		}
	}
	
	public void surrender() {
		BigDecimal half = bet.divide(BigDecimal.valueOf(2.0));
		bet = bet.subtract(half); //loses half of split bet & doesnt get it back in remaining
	}
	
	public void split(ArrayList<Card> player, Shoe shoe, AllHands all, int currcard) {
		ArrayList <Card> split = new ArrayList<Card>();
		Hand new_hand = new Hand(split);

		split.add(player.get(1));
		player.remove(1);
		player.add(shoe.getCurrDeck().get(currcard));
		currcard++;
		
		if(player.get(0).getRank().equals("A")) {
			split.add(shoe.getCurrDeck().get(currcard));
			currcard++;
		}
		bet = bet.add(original); //doubling original bet -> represents bet for new split hand
		//remaining = remaining.subtract(original);
		all.addData(new_hand);
	}
}

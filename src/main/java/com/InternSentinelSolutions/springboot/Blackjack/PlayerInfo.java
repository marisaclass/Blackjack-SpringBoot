package com.InternSentinelSolutions.springboot.Blackjack;

import java.math.BigDecimal;

public class PlayerInfo {
	String name;
	private BigDecimal bankroll = BigDecimal.ZERO;
	private BigDecimal remaining = BigDecimal.ZERO;
	private BigDecimal bet = BigDecimal.ZERO;
	private static BigDecimal original = BigDecimal.ZERO;
	private BigDecimal insure_bet = BigDecimal.ZERO;
	private int decks;
	
	public void setDecks(int decks) {
		this.decks = decks;
	}
	
	public int getDecks() {
		return decks;
	}
	
	public void setBet(BigDecimal bet){
		this.bet = bet;
		original = bet;
	}
	
	public BigDecimal getBet(){
	    return bet;
    }
	    
    public BigDecimal getOriginalBet(){
    	return original;
	}
     
	public void setBankroll(BigDecimal bankroll){
		this.bankroll = bankroll;
		this.remaining = bankroll;
	}
		
	public BigDecimal getBankroll(){
		return bankroll;
	}

	public BigDecimal getRemaining(){
		return remaining;
	}
	
	public BigDecimal getPayout(){
		return remaining.setScale(0,BigDecimal.ROUND_HALF_UP);
	}
	
	public void setName(String fname){
		this.name = fname;
	}
	
	public String getName() {
		return name;
	}
	
	public void setInsure(BigDecimal insure) {
		this.insure_bet = insure;
	}
	
	public BigDecimal getInsure() {
		return insure_bet;
	}
}

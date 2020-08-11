package com.InternSentinelSolutions.Blackjack;

import java.math.BigDecimal;
import java.util.Random;

public class PlayerInfo {
	String name;
	private BigDecimal bankroll = BigDecimal.ZERO;
	private BigDecimal bet = BigDecimal.ZERO;
	private BigDecimal original = BigDecimal.ZERO;
	private BigDecimal insure_bet = BigDecimal.ZERO;
	private BigDecimal original_bank = BigDecimal.ZERO;
	private int decks;
	private int playable;
	
	public void setDecks(int decks) {
		this.decks = decks;
	}
	
	public int getDecks() {
		return decks;
	}
	
	public void setPlayable() {
    	Random rand = new Random();
    	int size = (getDecks()*52);
    	this.playable = rand.nextInt(size);
	}
	
	public int getPlayable() {
		return playable;
	}
	
	public void setBet(BigDecimal bet){
		this.bet = bet;
		this.original = bet;
	}
	
	public BigDecimal getBet(){
	    return bet;
    }
	    
    public BigDecimal getOriginalBet(){
    	return original;
	}
     
	public void setBankroll(BigDecimal bankroll){
		this.bankroll = bankroll;
	}
	
	public void setOriginalBankroll(BigDecimal bankroll){
		this.original_bank = bankroll;
	}
		
	public BigDecimal getBankroll(){
		return bankroll;
	}
	
	public BigDecimal getOriginalBankroll(){
	    return original_bank;
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

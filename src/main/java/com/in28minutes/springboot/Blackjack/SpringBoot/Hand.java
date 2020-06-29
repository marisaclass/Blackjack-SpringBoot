package com.in28minutes.springboot.Blackjack.SpringBoot;

import java.util.ArrayList;

import com.in28minutes.springboot.Blackjack.SpringBoot.Card;

public class Hand {
	private ArrayList <Card> player = new ArrayList<Card>();
	//private int value = 0;
	private boolean blackjack = false;
	private boolean split = false;
	private boolean bust = false;
	private boolean double_down = false;
	private boolean surrender = false;
	private boolean insurance = false;
	
	public Hand(ArrayList<Card> player) {
		this.player = player;
		//this.value = value;
	}
	
	public ArrayList<Card> getHand() {
		return player;
	}
	
	public void clearData() {
		player.clear();
		blackjack = false;
		split = false;
		bust = false;
		double_down = false;
		surrender = false;
		insurance = false;
	}
	
	public void printCurrentHand(ArrayList <Card> player) {
		System.out.print("You're hand: ");
		for(int i = 0; i < player.size(); i++) {
			System.out.print(player.get(i).toString() + ", ");
		}
	}
	
	public boolean isBust() {
		if(bust) {
			return true;
		}
		return false;
	}
	
	public boolean isBlackjack() {
		if(blackjack) {
			return true;
		}
		return false;
	}
	
	public boolean isDoubleDown() {
		if(double_down) {
			return true;
		}
		return false;
	}
	
	public boolean isSurrender() {
		if(surrender) {
			return true;
		}
		return false;
	}
	
	public boolean isInsurance() {
		if(insurance) {
			return true;
		}
		return false;
	}
	
	public boolean isSplit() {
		if(split) {
			return true;
		}
		return false;
	}
	
	
	public void setBust() {
		bust = true;
	}
	
	public void setBlackjack() {
		blackjack = true;
	}
	
	public void setDoubleDown() {
		double_down = true;
	}
	
	public void setSurrender() {
		surrender = true;
	}
	
	public void setInsurance() {
		insurance = true;
	}
	
	public void setSplit() {
		split = true;
	}
	
	public int getSum() {
		int count = 0;
		int ace = 0;
		int high = 0; //keeping track of values of 11 being used
		
		for(int i = 0; i < player.size(); i++) {
			if(player.get(i).getRank().equals("A")) {
				ace++;
			}
			else {
				count += player.get(i).getValue(); 
			}
			//need to do Ace (1 or 11) edge case -> by default, ace is 11
		}
		
		for(int i = 0; i < ace; i++) { 
			if(count >= 21) {
				count++;
			}
			
			else {
				count += 11;
				high++;
			
				if(count > 21) {
					count -= 11; //dont want to add 11 if originally count is already at 21 or above so just add 1
					count++;
					high--;
				}
			}
		}
		
		while(count > 21 && high > 0) { //checking at end 	
			count -= 11;
			count++; 
			high--;
		}

		return count; //returns sum but doesnt notify if a soft hand or not
	}
	
	public int getSoft() {
		int sum = 0;
		if(hasAce() > 0 && (getSum() - 11) <= 10) {
			for(int i = 0; i < player.size(); i++) {
				if(!player.get(i).getRank().equals("A")) {
					sum += player.get(i).getValue();
				}
			}
		}
		return sum;
	}

	public int hasAce() {
		int ace = 0;
		for(int i = 0; i < player.size(); i++) {
			if(player.get(i).getRank().equals("A")) {
					ace++;
			}
		}
		return ace;
	}
	
}
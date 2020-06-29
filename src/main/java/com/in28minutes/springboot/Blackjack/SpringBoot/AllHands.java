package com.in28minutes.springboot.Blackjack.SpringBoot;

import java.util.ArrayList;

import com.in28minutes.springboot.Blackjack.SpringBoot.Hand;

public class AllHands {
	
	ArrayList<Hand> playerHands = new ArrayList<Hand>(); //array of Json objects
	int split = 0;
	
	public void addData(Hand data) {
		playerHands.add(data);
	}
	
	public void removeData(Hand data) {
		playerHands.remove(data);
	}
	
	public void clearData() {
		playerHands.clear();
		split = 0;
	}
	
	public ArrayList<Hand> getPlayerHands(){
		return playerHands;
	}
	
	public int maxSplit() {
		return split;
	}
	
	public void addSplit() {
		split++;
	}
}

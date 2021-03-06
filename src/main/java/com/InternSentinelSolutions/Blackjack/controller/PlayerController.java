package com.InternSentinelSolutions.Blackjack.controller;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.InternSentinelSolutions.Blackjack.PlayerInfo;
import com.InternSentinelSolutions.Blackjack.PlayerTurn;
import com.InternSentinelSolutions.Blackjack.StartGame;
import com.InternSentinelSolutions.Blackjack.Action;
import com.InternSentinelSolutions.Blackjack.Card;
import com.InternSentinelSolutions.Blackjack.Hand;

@RestController
public class PlayerController {
	private PlayerInfo player;
	private StartGame os;
	private PlayerTurn pmove;
    private BigDecimal bankroll = BigDecimal.ZERO;
    private static BigDecimal betp = BigDecimal.ZERO;
    
    public PlayerController() {
    	this.player = new PlayerInfo();
    	this.os = new StartGame();
    }
	
    @GetMapping("/player/getBankroll")
    public String getBankroll() {
    	return player.getBankroll().toString();
    }
    
	@GetMapping("/player/setup")
    public void playerForm(@RequestParam("fname") String name, @RequestParam("decks") int decks, @RequestParam("bank") String bankroll,
    		@RequestParam("bet") String bet){
		
		betp = new BigDecimal(bet);
		this.bankroll = new BigDecimal(bankroll);
		player.setName(name);
		player.setDecks(decks);
		player.setBet(betp);
		player.setOriginalBankroll(this.bankroll);
		player.setBankroll(player.getOriginalBankroll().subtract(betp));
		os.setShoe(decks, player.getPlayable());
		this.pmove = new PlayerTurn(player, os);
    }

	@GetMapping("/startGame/playermove")
	public void playerAction(@RequestParam("action") String action, @RequestParam("insure") int insurance) {
		pmove.setAction(action);
		
		if(pmove.getAction() == Action.INSURANCE) {
			BigDecimal half = betp.divide(BigDecimal.valueOf(2.0));
			if(BigDecimal.valueOf(insurance).compareTo(half) <= 0) { //has to be atmost half of the current bet
				player.setInsure(BigDecimal.valueOf(insurance));
			}
			else {
				//return not valid bet
			}
		}
	}
	
	@GetMapping("/startGame/deal")
	public void deal() {
		os.start(); //print out dealt hands
	}
	
	@GetMapping("/startGame/showhands")
	public String getHands() {
		return "Dealer's hand:" + os.getDealerHand().getHand().get(1).toString() + "\n" + "Your hand:" + os.getPlayerHands().toString();
	}
	
	@GetMapping("/startGame/redeal")
	public void redeal(@RequestParam("bet") String bet) {
		betp = new BigDecimal(bet);
		player.setBet(betp);
		player.setBankroll(player.getOriginalBankroll().subtract(betp));
		
		os.redeal(player.getBet());
	}
	
	@GetMapping("/startGame")
    public String playGame() {
		int statusP = 0;
		int statusD = 0;
		boolean shuffled = false;
		String end = null;
			
		for(int i = 0; i < os.getAll().getPlayerHands().size(); i++) {
			statusP = pmove.move(os.getAll().getPlayerHands().get(i), os.getDealerHand(), os.getShoe(), os.getAll(), os.getCurrCard());
			//need to print dealer and players original/updating hands
		}
		if(statusP == 2 || statusP == 0) { //only call dealer if a split hand surrendered but not if single hand surrendered
			statusD = os.dealerTurn(player);
		}
		
		if(statusP == 1 || statusD == 1 || statusP == 2){ //end of deck so tally and terminate game
			int winP = 0;
			int winD = os.getDealerHand().getSum();
			boolean accounted = false;
			boolean count = false;
			
			for(int i = 0; i < os.getAll().getPlayerHands().size(); i++) {
				winP = os.getAll().getPlayerHands().get(i).getSum();
				Hand current = os.getAll().getPlayerHands().get(i);
				ArrayList<Card> p = current.getHand();

				if(winP == 21 && winD != 21 && count == false) {
					if(p.size() == 2) {
						current.setBlackjack();
						BigDecimal back = betp.multiply(BigDecimal.valueOf(1.5));
						bankroll = bankroll.add(back); //payout for blackjack is 1.5*bet
					}else {
						bankroll = bankroll.add(betp);
					}
					count = true;
				}else if(winP > 21) {
					current.setBust();
					if(accounted == false) {
						if(os.getAll().getPlayerHands().size() > 1) {
							bankroll = bankroll.subtract(player.getOriginalBet());
						}
						else {
							bankroll = bankroll.subtract(betp);
						}
						accounted = true;
					}
				}else if(winD > winP && winD <= 21){
					if(os.getAll().getPlayerHands().size() > 1) {
						bankroll = bankroll.subtract(player.getOriginalBet());
					}
					else {
						if(accounted == false){
							bankroll = bankroll.subtract(betp);
						}
						accounted = true;
					}
				}else if(winP > winD && winP <= 21 && count == false) { //no one busted but player gets back original bet
					bankroll = bankroll.add(betp); //player gets back original betx2 (wins)
					count = true;
				}else if(current.isSurrender()) {
					bankroll = bankroll.subtract(betp);
				}
			}
			if(winD > 21) {
				os.getDealerHand().setBust();
			}else if (winD == 21) {
				if(os.getDealerHand().getHand().size() == 2) {
					os.getDealerHand().setBlackjack();
				}
			}
			
			if(os.getCurrCard() <= os.getShoe().getCurrDeck().size() - 4) {
				if(os.getCurrCard() >= os.getShoe().getPlayable() && shuffled == false) {
					os.shuffle();
					os.setCurrCard();
					shuffled = true; 
				}
				end = "redeal";
			}
			else if(os.getCurrCard() > os.getShoe().getCurrDeck().size() - 1) {
				os.clearHands();
				end = "over";
			}
		}
		
		return end;
	}
}

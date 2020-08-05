package com.InternSentinelSolutions.Blackjack;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {
	private PlayerInfo player;
	private StartGame os;
	private PlayerTurn pmove;
    private BigDecimal bankroll;
    private BigDecimal bet;
    
    public PlayerController() {
    	this.player = new PlayerInfo();
    	this.os = new StartGame(player);
    	this.pmove = new PlayerTurn(player, os);
    	this.bankroll = BigDecimal.ZERO;
    	this.bet = BigDecimal.ZERO;
    }
	
    @GetMapping("/playercontroller/getBankroll")
    public String getBankroll() {
    	return player.getBankroll().toString();
    }
    
	@GetMapping("/playercontroller")
    public void playerForm(@RequestParam("fname") String name, @RequestParam("decks") int decks, @RequestParam("bankroll") String bankroll,
    		@RequestParam("bet") String bet){
		
		this.bet = new BigDecimal(bet);
		this.bankroll = this.bankroll.subtract(this.bet);
		player.setName(name);
		player.setDecks(decks);
		player.setBet(this.bet);
		player.setBankroll(this.bankroll);
		os.setShoe(decks, player.getPlayable());
    }

	@GetMapping("/startGame/playermove")
	public void playerAction(@RequestParam("action") String action, @RequestParam("insure") int insurance) {
		pmove.setAction(action);
		
		if(pmove.getAction() == Action.INSURANCE) {
			BigDecimal half = bet.divide(BigDecimal.valueOf(2.0));
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
	
	@GetMapping("startGame")
    public void playGame() {
		int statusP = 0;
		int statusD = 0;
		boolean shuffled = false;
			
		for(int i = 0; i < os.getAll().getPlayerHands().size(); i++) {
			statusP = pmove.move(os.getAll().getPlayerHands().get(i), os.getDealerHand(), os.getShoe(), os.getAll(), os.getCurrCard());
			//need to print dealer and players original/updating hands
		}
		if(statusP == 2 || statusP == 0) { //only call dealer if a split hand surrendered but not if single hand surrendered
			statusD = os.dealerTurn();
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
						BigDecimal back = bet.multiply(BigDecimal.valueOf(1.5));
						bankroll = bankroll.add(back); //payout for blackjack is 1.5*bet
					}else {
						bankroll = bankroll.add(bet);
					}
					count = true;
				}else if(winP > 21) {
					current.setBust();
					if(accounted == false) {
						if(os.getAll().getPlayerHands().size() > 1) {
							bankroll = bankroll.subtract(player.getOriginalBet());
						}
						else {
							bankroll = bankroll.subtract(bet);
						}
						accounted = true;
					}
				}else if(winD > winP && winD <= 21){
					if(os.getAll().getPlayerHands().size() > 1) {
						bankroll = bankroll.subtract(player.getOriginalBet());
					}
					else {
						if(accounted == false){
							bankroll = bankroll.subtract(bet);
						}
						accounted = true;
					}
				}else if(winP > winD && winP <= 21 && count == false) { //no one busted but player gets back original bet
					bankroll = bankroll.add(bet); //player gets back original betx2 (wins)
					count = true;
				}else if(current.isSurrender()) {
					bankroll = bankroll.subtract(bet);
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
				os.redeal(player.getOriginalBet(), bet);
			}
			else if(os.getCurrCard() > os.getShoe().getCurrDeck().size() - 1) {
				os.clearHands();
			}
		}
	}
}

package com.InternSentinelSolutions.springboot.Blackjack;

import java.math.BigDecimal;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.SessionAttributes;

@Service
@SessionAttributes("fname")
public class TodoService {
    public static void retrieveTodos(int DECKS, BigDecimal bankroll, BigDecimal bet) {
    	int playable = setPlayable(DECKS);
    	
    	StartGame os = new StartGame();
    	PlayerInfo info = new PlayerInfo();
    	info.setBankroll(bankroll);
    	info.setBet(bet);
    	os.setShoe(DECKS, playable);    	
    	os.startGame(info);
    }
    
    //creating random playable # to reshuffle
    public static int setPlayable(int DECKS) {
    	Random rand = new Random();
    	int size = (DECKS*52);
    	int playable = rand.nextInt(size);
    	
    	return playable;
    }
}
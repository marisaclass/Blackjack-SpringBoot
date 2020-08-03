package com.InternSentinelSolutions.springboot.Blackjack;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@ControllerAdvice
@SessionAttributes("fname")
@RequestMapping("/SetUp")
public class PlayerController {
	
	
	@GetMapping("/playerinfo")
    public void playerForm(Model model) {
        model.addAttribute("playerinfo", new PlayerInfo());
       //call retrieveTodos
    }
	
    @PostMapping("/playerinfo")
	public void playerStart(@ModelAttribute PlayerInfo player, Model model){
    	model.addAttribute("playerinfo", player);
    	
    	//start game -> 
    	TodoService.retrieveTodos(player.getDecks(), player.getBankroll(), player.getBet());
	}
	
}

package com.InternSentinelSolutions.springboot.Blackjack;

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
@RequestMapping("/Actions")
public class ActionController {
	@GetMapping("/playerturn")
    public void playerForm(Model model) {
        model.addAttribute("playerturn", new PlayerTurn());
    }
	
    @PostMapping("/playerturn")
	public void playerStart(@ModelAttribute PlayerTurn move, Model model){
    	model.addAttribute("playerturn", move);
	}
	
}


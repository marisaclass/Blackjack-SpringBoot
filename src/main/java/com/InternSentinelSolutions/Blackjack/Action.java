package com.InternSentinelSolutions.Blackjack;

public enum Action {

	HIT {
		@Override public String toString(){ 
			return "Hit"; 
		} 
	}, 
	STAND { 
		@Override public String toString(){ 
			return "Stand"; 
		} 
	}, 
	
	SPLIT { 
		@Override public String toString(){ 
			return "Split"; 
		} 
	},
	
	DOUBLE {
		@Override public String toString(){ 
			return "Double"; 
		} 
	}, 
	
	INSURANCE {
		@Override public String toString(){ 
			return "Insurance"; 
		} 
	}, 
	
	SUGGESTION {
		@Override public String toString(){ 
			return "Suggestion Utility"; 
		} 
	}, 
	
	SURRENDER { 
		@Override public String toString(){ 
			return "Surrender"; 
		} 
	};

}

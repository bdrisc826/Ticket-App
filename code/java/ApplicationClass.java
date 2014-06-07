package com.example.batmanbegins;

import org.json.JSONArray;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

//This class gets created first thing when the app opens, is called only once
public class ApplicationClass extends Application{
	String name = null, game = "Appalacian State";
	boolean umich = false, calledOnce = false;
	
	@Override
	public void onCreate() {
		//initialize database
		Parse.initialize(this, "dOUYAMVohWFopi8ZnakpvGeOt7nxv1T2iiLkF3O0", "XhPyzOal7cMCzIlCfrJG2faZDi0pzPXAS6TnV9d3");
		
		//determine if the user has a umich account and if so get their uniqname
		//this might not work, so far it has only been tested on my phone
		Context context = this.getApplicationContext();
		Account[] accounts = AccountManager.get(context).getAccounts();
		for(int i =0; i < accounts.length; i++) {
		    if(accounts[i].name.contains("@umich.edu")) {
		        umich = true;
		        name = accounts[i].name.substring(0, accounts[i].name.length()-10);
		        break;
		    }
		}
		//you can set whatever name you want here for testing purposes
		//also you can set umich = true if the permissions thing doesn't work on your phone
		
		if(umich){
			//set up 1 row in database for each game (7 rows per user, 6 cols per row)
			//hopefully it's pretty clear what each column does
			ParseQuery<ParseObject> query = ParseQuery.getQuery("TicketsForSale");
			ParseObject test = null;
			try {
				test = query.whereEqualTo("name", name).getFirst();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(test == null){
				for(int i=0; i<7; i++){
					ParseObject tickets = new ParseObject("TicketsForSale");
					JSONArray myArray = new JSONArray();
					tickets.put("price", -1);
					tickets.put("location", "");
					tickets.put("name", name);
					if(i==0){
						tickets.put("game", "Appalacian State");
					} else if(i==1){
						tickets.put("game", "Miami (Ohio)");
					} else if(i==2){
						tickets.put("game", "Utah");
					} else if(i==3){
						tickets.put("game", "Minnesota");
					} else if(i==4){
						tickets.put("game", "Penn State");
					} else if(i==5){
						tickets.put("game", "Indiana");
					} else if(i==6){
						tickets.put("game", "Maryland");
					}
					tickets.put("theiroffers", myArray);
					tickets.put("myoffers", myArray);
					tickets.saveInBackground();
				}
			}
		}	
	}
	
	//additional methods for accessing global variables
	public String getName() {
        return name;
    }
	
	public boolean getUmich() {
		return umich;
	}
	
	public void calledOnce(boolean called) {
		calledOnce = called;
	}
	
	public boolean getCalledOnce() {
		return calledOnce;
	}
	
	public String getGame() {
        return game;
    }
	
	public void setGame(String newGame) {
        game = newGame;
    }
	
}

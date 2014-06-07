package com.example.batmanbegins;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

//this activity displays a sorted list of all tickets for sale for the current game
public class MainActivity extends ListActivity {
	static String purchase="purchase", passName="passName", myLocation="myLocation", thisGame="thisGame";
	String messageGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String name = ((ApplicationClass) this.getApplication()).getName();
		
		boolean umich = ((ApplicationClass) this.getApplication()).getUmich();
		boolean calledOnce = ((ApplicationClass) this.getApplication()).getCalledOnce();
		//close the app if user is not a umich student
		if(!umich){
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setMessage("You must have a umich.edu account to use this app.")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				  }).setCancelable(false);
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
		//popup shows info about the app, only comes up upon first entering the app
		//might get rid of this or make a "do not show this again" option
		if(!calledOnce && umich){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("About This App")
	               .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       dialog.cancel();
	                   }
	               });
	        builder.setMessage("Here is how to use this app...");
	        AlertDialog alertDialog = builder.create();
	        alertDialog.show();
			((ApplicationClass) this.getApplication()).calledOnce(true);
		}
		
		//get the game and the location if coming from the sell activity
		Intent intent = getIntent();
		String messageLocation = intent.getStringExtra(SellActivity.myLocation);
		messageGame = ((ApplicationClass) this.getApplication()).getGame();
		//get the corresponding row in the database
		ParseQuery<ParseObject> query = ParseQuery.getQuery("TicketsForSale");
		ParseObject tickets = null;
		try {
			tickets = query.whereEqualTo("game", messageGame).whereEqualTo("name", name).getFirst();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//if you have just sold a ticket for the first time, update the database as such
		if(messageLocation != null && tickets.getInt("price") == -1){	
			tickets.put("price", -2);
			tickets.put("location", messageLocation);
			tickets.saveInBackground();	
		}
		
		//get the sorted list of tickets for sale for this game from the database
		query = ParseQuery.getQuery("TicketsForSale").whereEqualTo("game", messageGame);
		query = query.whereNotEqualTo("price", -1).addAscendingOrder("price");
		List<ParseObject> allTickets = null;
		try {
			allTickets = query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//display the ticket list in the activity
		String[] myStringArray1 = new String[allTickets.size()];
		for(int i=0; i<allTickets.size(); i++){
			int Price = allTickets.get(i).getInt("price");
			if(Price == -2){
				myStringArray1[i] = "No Offers Yet, ";
			} else {
				myStringArray1[i] = "$" + Price + ", ";
			}
			myStringArray1[i] = myStringArray1[i] + allTickets.get(i).getString("location")
					+ ", " + allTickets.get(i).getString("name");
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
		        R.layout.list_text, R.id.listText, myStringArray1);
		setListAdapter(adapter);
						
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		//go to buyActivity to place an offer upon selecting a row in the list
         super.onListItemClick(l, v, position, id);
         String name = ((ApplicationClass) this.getApplication()).getName();
         String  itemValue = (String) l.getItemAtPosition(position);
         String[] vals = itemValue.split(", ");
         
         //cannot buy your own ticket
         if(vals[2].equals(name)){        	
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("You cannot buy your own ticket")
			   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               dialog.cancel();
			           }
			       });
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
         } else {
	         Intent intent = new Intent(this, BuyActivity.class);
	         if(vals[0].equals("No Offers Yet")){
	        	 intent.putExtra(purchase, vals[0]);
	         } else {
	        	 intent.putExtra(purchase, vals[0].substring(1, vals[0].length()));
	         }
	         intent.putExtra(myLocation, vals[1]);
	         intent.putExtra(passName, vals[2]);
	         intent.putExtra(thisGame, messageGame);
	         startActivity(intent);
         }
         
    }
	
	//go to corresponding activity for each of these buttons
	public void sellTicket(View view) {	    
		
		Intent intent = new Intent(this, SellActivity.class);
		startActivity(intent);
	}
	
	public void viewTheirTickets(View view) {
	    
		Intent intent = new Intent(this, TicketsActivity.class);
		startActivity(intent);
	}
	
	public void viewYourTickets(View view) {
	    
		Intent intent = new Intent(this, YourOffersActivity.class);
		startActivity(intent);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//this code is in every activity and corresponds to the action bar options list
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.aboutthisapp){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("About This App")
	               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       dialog.cancel();
	                   }
	               }).setCancelable(false);
	        builder.setMessage("Here is how to use this app...");
	        AlertDialog alertDialog = builder.create();
	        alertDialog.show();
			return true;
		}
		if(id == R.id.games){
			Intent intent = new Intent(this, GamesActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

}

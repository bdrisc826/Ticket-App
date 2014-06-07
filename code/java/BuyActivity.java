package com.example.batmanbegins;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

//the user uses this activity to place an offer to buy a ticket
public class BuyActivity extends ActionBarActivity {
	static String myPrice="myPrice", myGame="myGame";
	String sellerName, thisGame, Location, highestOffer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//display text from mainActivity, info on the ticket
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy);
		Intent intent = getIntent();
		sellerName = intent.getStringExtra(MainActivity.passName);
		thisGame = intent.getStringExtra(MainActivity.thisGame);
		Location = intent.getStringExtra(MainActivity.myLocation);
		highestOffer = intent.getStringExtra(MainActivity.purchase);
		TextView text = (TextView) findViewById(R.id.sellername);
		text.setText("Seller Name: " + sellerName);
		TextView text1 = (TextView) findViewById(R.id.buygame);
		text1.setText("Game: " + thisGame);
		TextView text2 = (TextView) findViewById(R.id.sellerlocation);
		text2.setText("Location: " + Location);
		TextView text3 = (TextView) findViewById(R.id.highestoffer);
		text3.setText("Highest Offer So Far: " + highestOffer);
		
	}

	public void buy(View view) {
	    //this might be kinda confusing
		//execute the buy and start yourOffersActivity
		Intent intent = new Intent(this, YourOffersActivity.class);
		EditText Price = (EditText) findViewById(R.id.bidprice);
		String messagePrice = Price.getText().toString();
		if(isPosInteger(messagePrice)){
			
			int offerPrice = Integer.parseInt(messagePrice);
			String name = ((ApplicationClass) this.getApplication()).getName();
			ParseQuery<ParseObject> query = ParseQuery.getQuery("TicketsForSale");
			buyProcess(query, sellerName, name, "theiroffers", offerPrice);
			buyProcess(query, name, sellerName, "myoffers", offerPrice);
					
			startActivity(intent);
			
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("You must enter a positive integer for price")
			   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               dialog.cancel();
			           }
			       });
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
		}		
		
	}
	
	public void buyProcess(ParseQuery<ParseObject> query, String name1, String name2, String buyOrSell, int offerPrice) {
		ParseObject tickets = null;		
		try {
			tickets = query.whereEqualTo("game", thisGame).whereEqualTo("name", name1).getFirst();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//this is an array of all ticket offers for/by this person. Each element is a 3 element array itself.
		JSONArray myArray = tickets.getJSONArray(buyOrSell);
		//remove previous offer if this buyer already made an offer on this ticket
		if(!highestOffer.equals("No Offers Yet")){
			for(int i=0; i<myArray.length(); i++){			
				try {
					if(myArray.getJSONArray(i).get(0).equals(name2)){
						if(buyOrSell.equals("theiroffers") && myArray.getJSONArray(i).getInt(1) == Integer.parseInt(highestOffer)){
							highestOffer = "-2";
						}
						myArray.remove(i);
						break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		//put new buy offer in the database
		JSONArray temp = new JSONArray();
		temp.put(name2);
		temp.put(offerPrice);
		temp.put(Location);
		myArray.put(temp);
		tickets.put(buyOrSell, myArray);
		
		//update highest offer if new offer is the new highest offer
		if(buyOrSell.equals("theiroffers")){
			int curOffer;
			for(int i=0; i<myArray.length(); i++){
				try {
					curOffer = myArray.getJSONArray(i).getInt(1);
					if(highestOffer.equals("No Offers Yet")){
						highestOffer = Integer.toString(curOffer);
						break;
					}
					if(curOffer > Integer.parseInt(highestOffer)){
						highestOffer = Integer.toString(curOffer);
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			tickets.put("price", Integer.parseInt(highestOffer));
		}
		
		tickets.saveInBackground();
		
	}
	
	public static boolean isPosInteger(String s) {
		int x = -1;
	    try { 
	        x = Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    if(x > 0){
	    	return true;
	    }
	    return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.buy, menu);
		return true;
	}

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

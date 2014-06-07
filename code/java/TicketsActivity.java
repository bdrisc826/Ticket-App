package com.example.batmanbegins;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

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

//exactly the same as YourOffersActivity but 
//instead is a list of other user's offers to buy YOUR ticket(s)
public class TicketsActivity extends ListActivity {
	List<String> myStringList = new ArrayList<>();
	boolean execute = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tickets);
		String name = ((ApplicationClass) this.getApplication()).getName();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("TicketsForSale");
		JSONArray theiroffers = new JSONArray();
		query = query.whereEqualTo("name", name);
		List<ParseObject> allTickets = null;
		try {
			allTickets = query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=0; i<allTickets.size(); i++){
			ParseObject ticket = allTickets.get(i);			
			theiroffers = ticket.getJSONArray("theiroffers");
			for(int j=0; j<theiroffers.length(); j++){
				JSONArray temp = new JSONArray();
				try {
					temp = theiroffers.getJSONArray(j);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String tempStr = null;
				try {
					tempStr = temp.getString(0) + ", $" + temp.getInt(1) + 
							", " + temp.getString(2) + ", " + ticket.getString("game");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				myStringList.add(tempStr);
			}
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
		        R.layout.list_text, R.id.listText, myStringList);
		setListAdapter(adapter);
		
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		
		//remove this item from the list
		super.onListItemClick(l, v, position, id);
		String name = ((ApplicationClass) this.getApplication()).getName();

        String  itemValue = (String) l.getItemAtPosition(position);
        String[] vals = itemValue.split(", ");
        
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to accept this offer?")
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                   }
               })
               .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                       execute = false;
                   }
               }).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        
        if(execute){
	        ParseQuery<ParseObject> query = ParseQuery.getQuery("TicketsForSale");
	        Process(query, name, vals[0], "theiroffers", vals[3]);
	        Process(query, vals[0], name, "myoffers", vals[3]);
	        myStringList.remove(position);
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
			        R.layout.list_text, R.id.listText, myStringList);
			setListAdapter(adapter);
        }
		
	}
	
	public void Process(ParseQuery<ParseObject> query, String name1, String name2, String buyOrSell, String game) {
		ParseObject tickets = null;		
		try {
			tickets = query.whereEqualTo("game", game).whereEqualTo("name", name1).getFirst();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String highestOffer = Integer.toString(tickets.getInt("price"));
		JSONArray myArray = tickets.getJSONArray(buyOrSell);
		//remove their offer from offers lists, reset highest offer if necessary
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
		tickets.put(buyOrSell, myArray);
		
		//reset highest offer if necessary, only for the selling user
		if(buyOrSell.equals("theiroffers")){
			int curOffer;
			for(int i=0; i<myArray.length(); i++){
				try {
					curOffer = myArray.getJSONArray(i).getInt(1);
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

	public void mainPage(View view) {
	    
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	public void viewYourTickets(View view) {
	    
		Intent intent = new Intent(this, YourOffersActivity.class);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tickets, menu);
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

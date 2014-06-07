package com.example.batmanbegins;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

//the user uses this activity to sell a ticket
public class SellActivity extends ActionBarActivity {
	static String myLocation = "myLocation";
	static String myGame = "myGame";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//set up the drop down list
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sell);
		Spinner spinner = (Spinner) findViewById(R.id.game);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.games_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

	}
	
	//pretty straightforward, send the selling data to mainActivity
	public void sell(View view) {
		String name = ((ApplicationClass) this.getApplication()).getName();
		EditText Location = (EditText) findViewById(R.id.location);
		Spinner Game = (Spinner) findViewById(R.id.game);
		String messageLocation = Location.getText().toString();
		String messageGame = Game.getSelectedItem().toString();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("TicketsForSale");
		ParseObject tickets = null;
		try {
			tickets = query.whereEqualTo("game", messageGame).whereEqualTo("name", name).getFirst();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//can't sell 2 tickets for the same game at once
		if(tickets.getInt("price") == -1){	
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra(myLocation, messageLocation);
			((ApplicationClass) this.getApplication()).setGame(messageGame);
			startActivity(intent);
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setMessage("You are already selling a ticket for this game")
	               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       dialog.cancel();
	                   }
	               });
	        AlertDialog alertDialog = builder.create();
	        alertDialog.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sell, menu);
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

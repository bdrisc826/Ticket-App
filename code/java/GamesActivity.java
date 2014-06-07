package com.example.batmanbegins;

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

//this just changes what game you wanna buy tickets for
public class GamesActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games);
		
		String[] myStringArray1 = {"Appalacian State", "Miami (Ohio)", "Utah", "Minnesota",
				"Penn State", "Indiana", "Maryland"};		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
		        R.layout.list_text, R.id.listText, myStringArray1);
		setListAdapter(adapter);
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {         
         super.onListItemClick(l, v, position, id);
         String  itemValue = (String) l.getItemAtPosition(position);
         Intent intent = new Intent(this, MainActivity.class);
         ((ApplicationClass) this.getApplication()).setGame(itemValue);
         startActivity(intent);        
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.games, menu);
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

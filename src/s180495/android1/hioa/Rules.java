/*
 * Charlotte Sjøthun, s180495
 * Klassen er controller for rules
 */

package s180495.android1.hioa;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;

public class Rules extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rules);
	} // End of method onCreate(...)

	
	// Metode som avslutter denne aktiviteten og går til meny aktiviteten
	public void returnToMenu(View v)
	{
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	} // End of method returnToMenu(...)
	
	
	// Metode som avslutter denne aktiviteten og går til hangman aktiviteten
	public void startGame(View v)
	{
		Intent intent = new Intent(this, Hangman.class);
		int whichGame = Hangman.NEW_GAME;
		intent.putExtra("whichGame", whichGame);
		startActivity(intent);
		finish();
	} // End of method startGame(...)
}
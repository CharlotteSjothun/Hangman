/*
 * Charlotte Sjøthun, s180495
 * Klassen er controller for main
 */

package s180495.android1.hioa;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity
{
	Button continueGame;
	int showResumeButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent i = this.getIntent();
		showResumeButton = i.getIntExtra("savedGame", 0);
		
		if (showResumeButton == 0)
			readFile();
		
		if (showResumeButton == Hangman.RESUME_GAME)
			showResumeButton();
	} // End of method onCreate(...)

	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		writeFile();
	} // End of method onDestroy()


	// Metoden avslutter applikasjonen.
	public void exit(View v)
	{
		finish();
	} // End of method exit(...)
	
	
	// Metode som avslutter denne aktiviteten og går til rules aktiviteten
	public void goToRules(View v)
	{
		Intent intent = new Intent(this, Rules.class);
		startActivity(intent);
		finish();
	} // End of method goToRules(...)
	
	
	/* Metode som avslutter denne aktiviteten og går til hangman aktiviteten 
	 * (samme som resumeGame() ) bare det blir sendt med en annen konstant.
	 */
	public void goToGame(View v)
	{
		Intent intent = new Intent(this, Hangman.class);
		intent.putExtra("whichGame", Hangman.NEW_GAME);
		startActivity(intent);
		finish();
	} // End of method goToGame(...)
	
	
	// Hjelpemetode som oppretter en knapp og legger denne til i ett view.
	private void showResumeButton()
	{
		continueGame = new Button(this);
		continueGame.setMinimumWidth(100);
		continueGame.setText(R.string.continue_game);
		continueGame.setTextColor(getResources().getColor(R.color.dark_blue));
		continueGame.setBackgroundResource(R.drawable.button_green_state);
		continueGame.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				resumeGame();
			}
		});
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
																			   LinearLayout.LayoutParams.WRAP_CONTENT);

		layoutParams.setMargins(10, 0, 0, 0);
		
		LinearLayout addToLayout = (LinearLayout)findViewById(R.id.addButton);
		addToLayout.addView(continueGame, layoutParams);
	} // End of method showResumeButton()
	
	
	/* Hjelpemetode som avslutter denne aktiviteten og går til hangman aktiviteten 
	 * (samme som goToGame() ) bare det blir sendt med en annen konstant.
	 */
	private void resumeGame()
	{
		Intent intent = new Intent(this, Hangman.class);
		intent.putExtra("whichGame", Hangman.RESUME_GAME);
		startActivity(intent);
		finish();
	} // End of method resumeGame()
	
	// Metoden skriver til fil.
    private void writeFile()
    {
		try
		{
			FileOutputStream file = openFileOutput("saveMain", Context.MODE_PRIVATE);
			
			file.write(showResumeButton);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
    } // End of method writeFile()
    
    
    // Metoden leser fra fil.
    private void readFile()
    {
		try
		{
			FileInputStream file = openFileInput("saveMain");
			
			showResumeButton = file.read();	
		} 
		catch (FileNotFoundException e)
		{
			showResumeButton = Hangman.NEW_GAME;
		}
		catch (IOException e)
		{
			showResumeButton = Hangman.NEW_GAME;
		} 
    } // End of method writeFile()
}
/*
 * Charlotte Sjøthun, s180495
 * Klassen er controller for hangman
 */

package s180495.android1.hioa;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Hangman extends Activity
{
	public static final int NEW_GAME = 1;
	public static final int RESUME_GAME = 2;
	
	private static final int DIALOG_WON = 0;
	private static final int DIALOG_LOST =  1;
	private static final int DIALOG_NO_MORE_WORDS = 2;
	private static final int DONT_SHOW_DIALOG = -1;
	private int showDialogNum; // Konstantene over er lovlige verdier til denne variablen.
	
	private static final int DEFAULT_ENGLISH = 0; 
	private static final int NORWEGIAN = 1;
	private int language; // Konstantene over er lovlige verdier til denne variablen.
	
	private TextView showWord, showWon, showLost;
	private Button a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z; 
	private Button norwegianOne, norwegianTwo, norwegianThree;
	private ImageView image;
	private Game game;
	private Dialog myDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hangman);
		
		declareViews();
		showDialogNum = DONT_SHOW_DIALOG;
		myDialog = null;
		
		setLanguage();
		
		Intent i=getIntent();
		int value = i.getIntExtra("whichGame",0);
		
		if (value == NEW_GAME)
		{
			game = new Game(getResources().getStringArray(R.array.words));
			showWord.setText(game.getShowWord().toString());
		}
		else if (value == RESUME_GAME)
		{
			readGameFile();
			changeImageHangman();
			
			StringBuilder sWord = game.getShowWord();
			
			if (sWord != null) 
				showWord.setText(sWord.toString());
			else
				showWord.setText(" ");
			
			if (showDialogNum != DONT_SHOW_DIALOG)
				createDialog(showDialogNum);
		}
		
		showWon.setText(getString(R.string.view_won) + " " + game.getWon());
		showLost.setText(getString(R.string.view_lost) + " " + game.getLost());
	} // End of method onCreate(...)
	
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		writeGameFile();
		
		Intent i = this.getIntent();
		int whichGame = Hangman.RESUME_GAME;
		i.putExtra("whichGame", whichGame);
		
		if (myDialog != null) // myDialog er ikke lik null hvis den vises når man tilter mobilen.
			myDialog.dismiss();
	} // End of method onDestroy()

	
	// Metode som blir kalt når man trykker på new word knappen.
	public void newWord(View v)
	{
		newWord();
	} // End of method newWord(...)
	
	
	// Metode som blir kalt når man trykker på menu.
	public void returnToMenu(View v)
	{
		returnToMenu();
	} // End of method returnToMenu(...)
	
	
	// Metode som blir kalt når man trykker på en av bokstavknappene
	public void clickLetterButton(View v)
	{
		Button button = (Button)findViewById(v.getId());
		String letter = button.getText().toString();
		
		if (!(letter.equals(" "))) //Hvis knappen allerede er trykket så er teksten på den " " og da skal det ikke gjøres noe.
		{
			boolean foundLetter = game.isLetterThere(letter);
			button.setText(" "); //Setter teksten på knappen til " ", for å angi at den er trykket på.
			
			doOperations(foundLetter);
		}
	} // End of method clickLetterButton(...)
	
	
	// Hjelpemetode som finner hvilket språk som er valgt på mobilen og lagrer konstanten for det språket i en variabel
	private void setLanguage()
	{
		String s = Locale.getDefault().getDisplayLanguage();
		
		/* Skulle vært switch-case, men må bruke java 1.6 på grunn av laring til fil.
		 * og i 1.6 kan man ikke bruke switch-case på String.
		 */
		if (s.equals("norsk bokmål")) 
		{
			language = NORWEGIAN;
			declareExtraNorwegianViews();
		}
		else
			language = DEFAULT_ENGLISH;
	}
	
	
	/* Hjelpemetode som henter et nytt ord, resetter hangmanbilde og bokstavknappene.
	 * Dersom det ikke er flere ord i arrayen viser den en dialogboks med informasjon om det.
	 */
	private void newWord()
	{
		image.setImageResource(R.drawable.hangmanbilde0);
		resetButtons();
		if (language == NORWEGIAN) resetNorwegianButtons();
		
		String newWord = game.newWord();
		
		if (newWord != null) 
			showWord.setText(newWord);
		else
		{
			createDialog(DIALOG_NO_MORE_WORDS);
			showDialogNum = DIALOG_NO_MORE_WORDS;
		}
	} // End of method newWord()
	
	
	// Hjelpemetode som utfører riktige operasjoner ut ifra om bokstaven ble funnet i ordet eller ikke.
	private void doOperations(boolean foundLetter)
	{
		if (!foundLetter)
		{
			wrongLetter();
			return;
		}
		else if (game.getNumOfGuessedLetters() == game.getWord().length)
		{
			game.setWon(game.getWon()+1);
			createDialog(DIALOG_WON);
			showDialogNum = DIALOG_WON;
			showWon.setText(getString(R.string.view_won) + " " + game.getWon());
		}
		
		showWord.setText(game.getShowWord().toString());
	} // End of method doOperations(...)
	
	
	/* Hjelpemetode som oppdaterer numOfMistakes, kaller på metoden som 
	 * endrer bilde og sjekker om man har flere sjanser igjen til å gjette.
	 */
	private void wrongLetter()
	{
		int mistakes = game.getNumOfMistakes();
		game.setNumOfMistakes(mistakes+1);
		changeImageHangman();
		
		if (mistakes+1 == Game.MAX_MISTAKES)
		{	
			game.setLost(game.getLost()+1);
			createDialog(DIALOG_LOST);
			showDialogNum = DIALOG_LOST;
			showLost.setText(getString(R.string.view_lost) + " " + game.getLost());
		}
	} // End of method wrongLetter()
	
	
	// Hjelpemetode som endrer hangmanbilde når det blir gjettet på feil bokstav.
	private void changeImageHangman()
	{
		switch (game.getNumOfMistakes())
		{
			case 1 : image.setImageResource(R.drawable.hangmanbilde1);
					 break;
			case 2 : image.setImageResource(R.drawable.hangmanbilde2);
					 break;
			case 3 : image.setImageResource(R.drawable.hangmanbilde3);
			 		 break;
			case 4 : image.setImageResource(R.drawable.hangmanbilde4);
			 		 break;
			case 5 : image.setImageResource(R.drawable.hangmanbilde5);
			 		 break;
			case 6 : image.setImageResource(R.drawable.hangmanbilde6);
					 break;
			case 7 : image.setImageResource(R.drawable.hangmanbilde7);
			 		 break;
			case 8 : image.setImageResource(R.drawable.hangmanbilde8);
			 		 break;
			case 9 : image.setImageResource(R.drawable.hangmanbilde9);
			 		 break;
			case 10 : image.setImageResource(R.drawable.hangmanbilde10);
			 		  break;
		}
	} // End of method changeImageHangman()

	
	// Oppretter dialogvindu som vises på skjermen når man enten har vunnet eller tapt.
	private void createDialog(int id)
	{
		Builder builder = new AlertDialog.Builder(this);
		
		switch (id)
		{
			case 0 : builder = createWonOrLostDialog(builder, R.drawable.happyface, R.string.won);
					 break;
					 
			case 1 : builder = createWonOrLostDialog(builder, R.drawable.sadface, R.string.lost);
					 break;
					 
			case 2 : builder.setIcon(R.drawable.happyface); 
					 builder.setTitle(getString(R.string.no_more_words));
					 
					 builder.setMessage(getString(R.string.no_more_words_message)
							 		  + "\n\n" + getString(R.string.total_won) + " " + game.getWon() 
						 			  + "\n" + getString(R.string.total_lost)  + " " + game.getLost());
					 
					 builder.setPositiveButton(getString(R.string.new_game), new DialogInterface.OnClickListener() 
					 {
						 public void onClick(DialogInterface dialog, int whichButton) 
						 {
							 showDialogNum = DONT_SHOW_DIALOG;
							 game = new Game(getResources().getStringArray(R.array.words));
							 showWord.setText(game.getShowWord().toString());
							 showWon.setText(getString(R.string.view_won) + " " + game.getWon());
							 showLost.setText(getString(R.string.view_lost) + " " + game.getLost());
						 }
					 });
				
					 builder.setNegativeButton(getString(R.string.menu), new DialogInterface.OnClickListener() 
					 {
						 public void onClick(DialogInterface dialog, int whichButton) 
						 { 
							 showDialogNum = DONT_SHOW_DIALOG;
							 returnToMenu();
						 }
					 });
					 break;
		}
		
		myDialog = builder.create();
		myDialog.setCancelable(false); // Gjør at man ikke kan trykke på noe annet enn de valgene man får i dialogboksen.
		myDialog.show();
	} // End of method createDialog(...)
	
	
	// Hjelpemetode som bygger opp dialogboksen for enten vunnet eller tapt omgang.
	private Builder createWonOrLostDialog(Builder builder, int icon, int title)
	{
		String[] array = game.getWordArray();
		String theWord = array[game.getWordNr()-1]; // Må ta -1 fordi wordNr ligger 1 før. (den blir oppdatert med wordNr++)
		
		builder.setIcon(icon); 
		builder.setTitle(title);
		
		builder.setMessage(getString(R.string.word_was) + " " + theWord
				 	+ "\n\n" + getString(R.string.total_won) + " " + game.getWon() 
			 		+ "\n" + getString(R.string.total_lost)  + " " + game.getLost());
		 
		builder.setPositiveButton(getString(R.string.new_word), new DialogInterface.OnClickListener() 
		{
			 public void onClick(DialogInterface dialog, int whichButton) 
			 {
				 showDialogNum = DONT_SHOW_DIALOG;
				 newWord();
			 }
		 });
	
		builder.setNegativeButton(getString(R.string.menu), new DialogInterface.OnClickListener() 
		 {
			 public void onClick(DialogInterface dialog, int whichButton) 
			 { 
				 showDialogNum = DONT_SHOW_DIALOG;
				 newWord();
				 returnToMenu();
			 }
		 });
		 
		 return builder;
	} // End of method createWonOrLostDialog(...)
	
	
	// Hjelpemetode som avslutter denne aktiviteten og går til meny aktiviteten
	private void returnToMenu()
	{
		int savedGame = RESUME_GAME;
		
		if (game.getWordNr() == game.getWordArray().length)
		{
			game = null;
			savedGame = NEW_GAME;
		}
		
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("savedGame", savedGame);
		startActivity(intent);
		finish();
	} // End of method returnToMenu()
	
	
	// Hjelpemetode som resetter teksten på bokstavknappene.
	private void resetButtons()
	{
		a.setText("A");
		b.setText("B");
		c.setText("C");
		d.setText("D");
		e.setText("E");
		f.setText("F");
		g.setText("G");
		h.setText("H");
		i.setText("I");
		j.setText("J");
		k.setText("K");
		l.setText("L");
		m.setText("M");
		n.setText("N");
		o.setText("O");
		p.setText("P");
		q.setText("Q");
		r.setText("R");
		s.setText("S");
		t.setText("T");
		u.setText("U");
		v.setText("V");
		w.setText("W");
		x.setText("X");
		y.setText("Y");
		z.setText("Z");
	} // End of method resetButtons()
	
	
	// Hjelpemetode som resetter teksten på bokstavknappene.
	private void resetNorwegianButtons()
	{
		norwegianOne.setText("Æ");
		norwegianTwo.setText("Ø");
		norwegianThree.setText("Å");
	} // End of method resetNorwegianButtons()
	
	
	// Hjelpemetode som deklarerer komponentene.
	private void declareViews()
	{
		showWord = (TextView)findViewById(R.id.word);
		showWon = (TextView)findViewById(R.id.won);
		showLost = (TextView)findViewById(R.id.lost);
		image = (ImageView)findViewById(R.id.image);
		a = (Button)findViewById(R.id.A);
		b = (Button)findViewById(R.id.B);
		c = (Button)findViewById(R.id.C);
		d = (Button)findViewById(R.id.D);
		e = (Button)findViewById(R.id.E);
		f = (Button)findViewById(R.id.F);
		g = (Button)findViewById(R.id.G);
		h = (Button)findViewById(R.id.H);
		i = (Button)findViewById(R.id.I);
		j = (Button)findViewById(R.id.J);
		k = (Button)findViewById(R.id.K);
		l = (Button)findViewById(R.id.L);
		m = (Button)findViewById(R.id.M);
		n = (Button)findViewById(R.id.N);
		o = (Button)findViewById(R.id.O);
		p = (Button)findViewById(R.id.P);
		q = (Button)findViewById(R.id.Q);
		r = (Button)findViewById(R.id.R);
		s = (Button)findViewById(R.id.S);
		t = (Button)findViewById(R.id.T);
		u = (Button)findViewById(R.id.U);
		v = (Button)findViewById(R.id.V);
		w = (Button)findViewById(R.id.W);
		x = (Button)findViewById(R.id.X);
		y = (Button)findViewById(R.id.Y);
		z = (Button)findViewById(R.id.Z);
	} // End of method declareViews()
	
	
	// Hjelpemetode som deklarerer komponentene.
	private void declareExtraNorwegianViews()
	{
		norwegianOne = (Button)findViewById(R.id.Norsk1);
		norwegianTwo = (Button)findViewById(R.id.Norsk2);
		norwegianThree = (Button)findViewById(R.id.Norsk3);
	} // End of method declareExtraNorwegianViews()
	
	
	// Metoden skriver game til fil.
    private void writeGameFile()
    {
		try
		{
			FileOutputStream file = openFileOutput("savedgames", Context.MODE_PRIVATE);
			ObjectOutputStream toGameFile = new ObjectOutputStream(file);
			
			toGameFile.writeInt(language);
			toGameFile.writeObject(game);
			toGameFile.writeInt(showDialogNum);
			toGameFile.writeObject(a.getText());
			toGameFile.writeObject(b.getText());
			toGameFile.writeObject(c.getText());
			toGameFile.writeObject(d.getText());
			toGameFile.writeObject(e.getText());
			toGameFile.writeObject(f.getText());
			toGameFile.writeObject(g.getText());
			toGameFile.writeObject(h.getText());
			toGameFile.writeObject(i.getText());
			toGameFile.writeObject(j.getText());
			toGameFile.writeObject(k.getText());
			toGameFile.writeObject(l.getText());
			toGameFile.writeObject(m.getText());
			toGameFile.writeObject(n.getText());
			toGameFile.writeObject(o.getText());
			toGameFile.writeObject(p.getText());
			toGameFile.writeObject(q.getText());
			toGameFile.writeObject(r.getText());
			toGameFile.writeObject(s.getText());
			toGameFile.writeObject(t.getText());
			toGameFile.writeObject(u.getText());
			toGameFile.writeObject(v.getText());
			toGameFile.writeObject(w.getText());
			toGameFile.writeObject(x.getText());
			toGameFile.writeObject(y.getText());
			toGameFile.writeObject(z.getText());
			
			if (language == NORWEGIAN)
			{
				toGameFile.writeObject(norwegianOne.getText());
				toGameFile.writeObject(norwegianTwo.getText());
				toGameFile.writeObject(norwegianThree.getText());
			}
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
    } // End of method writeGameFile()
    
    
    // Metoden leser game fra fil.
    private void readGameFile()
    {
		try
		{
			FileInputStream file = openFileInput("savedgames");
			ObjectInputStream fromGameFile = new ObjectInputStream(file);
			
			int languageUsed = fromGameFile.readInt();
			Game gameFromFile = (Game)fromGameFile.readObject();
			
			if (gameFromFile != null && languageUsed == language)
			{
				game = gameFromFile;
				showDialogNum = fromGameFile.readInt();
				a.setText((String)fromGameFile.readObject());
				b.setText((String)fromGameFile.readObject());
				c.setText((String)fromGameFile.readObject());
				d.setText((String)fromGameFile.readObject());
				e.setText((String)fromGameFile.readObject());
				f.setText((String)fromGameFile.readObject());
				g.setText((String)fromGameFile.readObject());
				h.setText((String)fromGameFile.readObject());
				i.setText((String)fromGameFile.readObject());
				j.setText((String)fromGameFile.readObject());
				k.setText((String)fromGameFile.readObject());
				l.setText((String)fromGameFile.readObject());
				m.setText((String)fromGameFile.readObject());
				n.setText((String)fromGameFile.readObject());
				o.setText((String)fromGameFile.readObject());
				p.setText((String)fromGameFile.readObject());
				q.setText((String)fromGameFile.readObject());
				r.setText((String)fromGameFile.readObject());
				s.setText((String)fromGameFile.readObject());
				t.setText((String)fromGameFile.readObject());
				u.setText((String)fromGameFile.readObject());
				v.setText((String)fromGameFile.readObject());
				w.setText((String)fromGameFile.readObject());
				x.setText((String)fromGameFile.readObject());
				y.setText((String)fromGameFile.readObject());
				z.setText((String)fromGameFile.readObject());
				
				if (language == NORWEGIAN)
				{
					norwegianOne.setText((String)fromGameFile.readObject());
					norwegianTwo.setText((String)fromGameFile.readObject());
					norwegianThree.setText((String)fromGameFile.readObject());
				}
			}
			else
			{
				game = new Game(getResources().getStringArray(R.array.words));
				showWord.setText(game.getShowWord().toString());
				Toast t = Toast.makeText(getApplicationContext(), R.string.error_file, 
					       Toast.LENGTH_LONG);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();
			}
		} 
		catch (FileNotFoundException e)
		{
			game = new Game(getResources().getStringArray(R.array.words));
			showWord.setText(game.getShowWord().toString());
			Toast t = Toast.makeText(getApplicationContext(), R.string.error_file, 
				       Toast.LENGTH_LONG);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
		} 
		catch (IOException e)
		{
			game = new Game(getResources().getStringArray(R.array.words));
			showWord.setText(game.getShowWord().toString());
			Toast t = Toast.makeText(getApplicationContext(), R.string.error_file, 
				       Toast.LENGTH_LONG);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
		} 
		catch (ClassNotFoundException e)
		{
			game = new Game(getResources().getStringArray(R.array.words));
			showWord.setText(game.getShowWord().toString());
			Toast t = Toast.makeText(getApplicationContext(), R.string.error_file, 
				       Toast.LENGTH_LONG);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
		}
    } // End of method writeGameFile()
}
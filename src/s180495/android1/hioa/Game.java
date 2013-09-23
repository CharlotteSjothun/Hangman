/*
 * Charlotte Sjøthun, s180495
 * Klassen inneholder informasjon om et hangmanspill.
 * Klassen er model for hangman
 */

package s180495.android1.hioa;

import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.Random;

public class Game implements Serializable
{
    private static final long serialVersionUID = ObjectStreamClass.lookup(Game.class).getSerialVersionUID();
	public static final int MAX_MISTAKES = 10;
	
	private char[] word;
	private StringBuilder showWord;
	private String[] wordArray;
	private int wordNr, numOfMistakes, numOfGuessedLetters, won, lost;
	
	public Game(String[] array)
	{
		wordNr = 0;
		numOfMistakes = 0;
		numOfGuessedLetters = 0;
		won = 0;
		lost = 0;
		wordArray = array;
		shuffleArray();
		getNewWord();
	} // End of constructor
	
	public void setNumOfMistakes(int num)
	{
		numOfMistakes = num;
	} // End of method setNumOfMistakes(...)
	
	
	public void setNumOfGuessedLetters(int num)
	{
		numOfGuessedLetters = num;
	} // End of method setNumOfGuessedLetters(...)
	
	
	public void setWon(int num)
	{
		won = num;
	} // End of method setWon(...)
	
	
	public void setLost(int num)
	{
		lost = num;
	} // End of method setLost(...)
	
	
	public char[] getWord()
	{
		return word;
	} // End of method getWord()
	
	
	public StringBuilder getShowWord()
	{
		return showWord;
	} // End of method getShowWord()
	
	
	public int getNumOfMistakes()
	{
		return numOfMistakes;
	} // End of method getNumOfMistakes()
	
	
	public int getNumOfGuessedLetters()
	{
		return numOfGuessedLetters;
	} // End of method getNumOfGuessedLetters()
	
	
	public int getWon()
	{
		return won;
	} // End of method getWon()
	
	
	public int getLost()
	{
		return lost;
	} // End of method getLost()
	
	
	public String[] getWordArray()
	{
		return wordArray;
	} // End of method getWordArray()
	
	
	public int getWordNr()
	{
		return wordNr;
	} // End of method getWordNr()
	
	
	/* Metoden resetter tellerene numOfMistakes og numOfGuessedLetters, 
	 * henter ett nytt ord fra arrayen og returnerer variablen showWord.
	 */
	public String newWord()
	{
		numOfMistakes = 0;
		numOfGuessedLetters = 0;
		getNewWord();
		return (showWord != null) ? showWord.toString() : null;
	} // End of method newWord()
	
	
	// Hjelpemetode som henter et nytt ord fra arrayen.
	private void getNewWord()
	{
		if (wordNr < wordArray.length)
		{
			word = wordArray[wordNr++].toCharArray();
			
			showWord = new StringBuilder();
			showWord.append('_');
		
			for (int i = 0; i < word.length-1; i++)
			{
				showWord.append(' ').append('_');
			}
		}
		else
			showWord = null;
	} // End of method getNewWord()
	
	
	/* Metoden sjekker om bokstaven den får inn via parameter finnes 
	 * i ordet og hvis bokstaven finnes returnerer den true, ellers false.
	 */
	public boolean isLetterThere(String letter)
	{
		boolean foundLetter = false;
		char[] charArray = letter.toCharArray();
		
		for (int i = 0; i < word.length; i++)
		{
			if (word[i] == charArray[0])
			{
				word[i] = ' ';
				
				if (i == 0)  
					showWord.setCharAt(i, charArray[0]); 
				else showWord.setCharAt(i*2, charArray[0]);
				
				numOfGuessedLetters++;
				foundLetter = true;
			}
		}
		return foundLetter;
	} // End of method isLetterThere(...)
	
	
	/* Hjelpemetode som stokker om på ordene i arrayen 
	 * slik at de ikke kommer i samme rekkefølge hver gang.
	 */
	private void shuffleArray()
	{
		Random r = new Random();
		
		for (int i = wordArray.length - 1; i >= 0; i--)
		{
			int index = r.nextInt(i + 1);
			
			String a = wordArray[index];
			wordArray[index] = wordArray[i];
			wordArray[i] = a;
		}
	} // End of method shuffleArray()
}
package com.google.engedu.ghost;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Random;
import android.view.KeyEvent;

import static com.google.engedu.ghost.R.id.btnChallenge;


public class GhostActivity extends Activity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    SimpleDictionary simpleDictionary;
    FastDictionary fastDictionary;
    String wordFragment="";
    private TextView txtWord, txtLabel, txtUserScore, txtComputerScore;

    private int scoreUser = 0, scoreComputer = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        txtWord = (TextView) findViewById(R.id.ghostText);
        txtLabel = (TextView) findViewById(R.id.gameStatus);
        txtComputerScore = (TextView) findViewById(R.id.scoreComputer);
        txtUserScore = (TextView) findViewById(R.id.scoreUser);


        try {
            InputStream is=getAssets().open("words.txt");
            fastDictionary =new FastDictionary(is);
            //simpleDictionary=new SimpleDictionary(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        onStart(null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORD", txtWord.getText().toString());
        outState.putString("LABEL", txtLabel.getText().toString());
        outState.putInt("SCORE_USER", scoreUser);
        outState.putInt("SCORE_COMPUTER", scoreComputer);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null){
            txtWord.setText(savedInstanceState.getString("WORD"));
            txtLabel.setText(savedInstanceState.getString("LABEL"));
            userTurn = true;  // a saved state is always user turn
            scoreUser = savedInstanceState.getInt("SCORE_USER");
            scoreComputer = savedInstanceState.getInt("SCORE_COMPUTER");
            updateScoresOnBoard();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }


    private void end(boolean win){
        Button bChallenge = (Button) findViewById(R.id.btnChallenge);
        if(win){
            scoreUser+= 1;
        }
        else{
            scoreComputer +=1;
        }
        bChallenge.setEnabled(false);
        updateScoresOnBoard();

    }

    private void computerTurn() {
        txtComputerScore = (TextView) findViewById(R.id.scoreComputer);
        txtUserScore = (TextView) findViewById(R.id.scoreUser);

        TextView label = (TextView) findViewById(R.id.gameStatus);
        TextView txtInput=(TextView)findViewById(R.id.ghostText);
        Button bChallenge=(Button)findViewById(btnChallenge);
        // Do computer turn stuff then make it the user's turn again
        if (wordFragment.length() >= 4 && fastDictionary.isWord(wordFragment)) {
            txtInput.setText(wordFragment);
            Log.d("test", "Computer Wins");
            label.setText(wordFragment + " is a valid word. You Lose");
            end(false);

            bChallenge.setEnabled(false);
           // return;
        } else{
            Log.d("Test", "getting a word starting with "+wordFragment);
            String longerWord = fastDictionary.getGoodWordStartingWith(wordFragment);
            if (longerWord!=null) {
                Log.d("Test","longerWord word beginning with "+wordFragment+" is: "+longerWord);

                String letter = longerWord.substring(wordFragment.length(), wordFragment.length() + 1);
                wordFragment = wordFragment + letter;
                txtInput.setText(wordFragment);
                Log.d("Test", "New word fragment: " + wordFragment);
                userTurn = true;
                label.setText(USER_TURN);

            } else {
                Log.d("Test", "No word can be formed with "+wordFragment);

                txtInput.setText(wordFragment);
                label.setText("Computer Wins!!!");
                end(false);
                Log.d("key", "Computer Wins!");

                label.setText("No word can be formed with "+wordFragment+". You Lose!");
                txtInput.setEnabled(false);
                bChallenge.setEnabled(false);
            }

        }



    }

    /**
     * Do stuff when user presses a key
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        TextView txtResult=(TextView)findViewById(R.id.gameStatus);

        char c = (char) event.getUnicodeChar();
        if(Character.isLetter(c)) {
            wordFragment=wordFragment+Character.toString(c);
            Log.d("Test","word fragment: "+wordFragment);
            //if(fastDictionary.isWord(wordFragment)){

            //Log.d("Test","valid word");
            //}
            computerTurn();
            return true;
        }else{
            return super.onKeyUp(keyCode, event);
        }
    }
    public void challengeHandler(View view){
        txtComputerScore = (TextView) findViewById(R.id.scoreComputer);
        txtUserScore = (TextView) findViewById(R.id.scoreUser);

        TextView txtStatus=(TextView)findViewById(R.id.gameStatus);
        TextView txtInput=(TextView)findViewById(R.id.ghostText);
        Button bChallenge=(Button)findViewById(btnChallenge);
        Log.d("tag", wordFragment);
        if(!wordFragment.equals("")) {
            if (wordFragment.length() >= 4 && fastDictionary.isWord(wordFragment)) {
                txtStatus.setText("Congratulations, You Win! " + wordFragment + " is a real word");
                end(true);
                return;
            }
            String longWord = fastDictionary.getGoodWordStartingWith(wordFragment);
            if (longWord != null) {
                txtStatus.setText("You Lose! A possible word is " + longWord);
                end(false);
            } else {
                txtStatus.setText("Congratulations! You Win");
                end(true);
            }
            txtInput.setEnabled(false);
            bChallenge.setEnabled(false);

        }
    }
    private void updateScoresOnBoard(){
        txtUserScore.setText(scoreUser + "");
        txtComputerScore.setText(scoreComputer + "");}

    public boolean onStart(View view) {
        txtComputerScore = (TextView) findViewById(R.id.scoreComputer);
        txtUserScore = (TextView) findViewById(R.id.scoreUser);

        Button bChallenge=(Button)findViewById(btnChallenge);
        wordFragment="";
        bChallenge.setEnabled(true);
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        text.setEnabled(true);
        TextView label = (TextView) findViewById(R.id.gameStatus);
        txtComputerScore.setText("0");
        txtUserScore.setText("0");
        if (userTurn) {
        label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }
}

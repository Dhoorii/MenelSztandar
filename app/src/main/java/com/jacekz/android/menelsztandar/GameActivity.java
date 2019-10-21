package com.jacekz.android.menelsztandar;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    //static values
    private static final int TABLE_WIDTH  = 6;
    private static final int TABLE_HEIGHT = 7;
    //view values
    ImageView char1ImageView,char2ImageView,char3ImageView;
    Button buttonFight;
    ImageView enemy1ImageView,enemy2ImageView,enemy3ImageView;
    AnimationDrawable progressAnimationAtacker,progressAnimationDefender;
    TextView textViewHP,textViewMove;
    //values
    String[] tag = new String[]{"00","01","02"};
    String[] tagEnemy = new String[]{"36","46","56"};
    int[][] terrain = new int[TABLE_WIDTH][TABLE_HEIGHT];
    int[][] bonusTerrain = new int[TABLE_WIDTH][TABLE_HEIGHT];
    int charSelected;
    boolean selected = false;
    Menel char1,char2,char3;
    int[] originalHp = new int[3];
    Menel[] enemy = new Menel[3];
    int numberOfAlly = 3,numberOfEnemy = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        buttonFight = findViewById(R.id.buttonFight);
        char1 = (Menel) getIntent().getSerializableExtra("char1");
        char2 = (Menel) getIntent().getSerializableExtra("char2");
        char3 = (Menel) getIntent().getSerializableExtra("char3");
        textViewHP = findViewById(R.id.textViewHP);
        textViewMove = findViewById(R.id.textViewMove);
        originalHp[0] = char1.getHp();
        originalHp[1] = char2.getHp();
        originalHp[2] = char3.getHp();
        int lvls = (int) Math.floor((char1.getLvl()+char2.getLvl()+char3.getLvl())/3);
        for (int i = 0 ; i < 3; i ++)
        {
            enemy[i] = new Menel(lvls);
            enemy[i].setExp(enemy[i].getLvl()*50+50);
        }

        randomMap();
        ImageView imageView = findViewById(R.id.imageView00);
        imageView.setImageResource(R.drawable.chargood1);
        ImageView imageView1 = findViewById(R.id.imageView01);
        imageView1.setImageResource(R.drawable.chargood2);
        ImageView imageView2 = findViewById(R.id.imageView02);
        imageView2.setImageResource(R.drawable.chargood3);

        ImageView imageView4 = findViewById(R.id.imageView36);
        imageView4.setImageResource(R.drawable.charbad1);
        ImageView imageView5 = findViewById(R.id.imageView46);
        imageView5.setImageResource(R.drawable.charbad2);
        ImageView imageView6 = findViewById(R.id.imageView56);
        imageView6.setImageResource(R.drawable.charbad3);

        for (int i = 0 ; i < TABLE_WIDTH; i++)
        {
            for (int j = 0 ; j < TABLE_HEIGHT; j++)
            {
                terrain[i][j] = 0;
            }
        }
        terrain[0][0] = 1;
        terrain[0][1] = 1;
        terrain[0][2] = 1;
        terrain[3][6] = 2;
        terrain[4][6] = 2;
        terrain[5][6] = 2;
    }
    //button functions
    public void move(View view)
    {
        if (selected) {
            int allyx = Character.getNumericValue(tag[charSelected].charAt(0));
            int ally  = Character.getNumericValue(tag[charSelected].charAt(1));
            if (charSelected == 0) {
                int a = Character.getNumericValue(view.getTag().toString().charAt(0));
                int b = Character.getNumericValue(view.getTag().toString().charAt(1));
                char1ImageView = (ImageView) view;
                if (checkMove(a,b) && char1.getSpeed() > 0)
                {
                    hideAlly(charSelected);
                    a = Character.getNumericValue(tag[charSelected].charAt(0));
                    b = Character.getNumericValue(tag[charSelected].charAt(1));
                    terrain[a][b] = 0;
                    char1ImageView.setImageResource(R.drawable.chargood1);
                    tag[charSelected] = char1ImageView.getTag().toString();
                    selected = false;
                    textViewMove.setText("");
                    textViewHP.setText("");
                    char1.decrementSpeed();
                    a = Character.getNumericValue(tag[charSelected].charAt(0));
                    b = Character.getNumericValue(tag[charSelected].charAt(1));
                    terrain[a][b] = 1;
                }
                else if (terrain[a][b] == 2 && char1.getSpeed() > 0)
                {
                    for (int i=0; i < 3; i ++)
                    {
                        if (tagEnemy[i].equals(Integer.toString(a) + Integer.toString(b)))
                        {
                            Menel[] test;
                            test = battle(char1,enemy[i],bonusTerrain[allyx][ally],bonusTerrain[a][b],1);
                            if (test[0] == null) {
                                --numberOfAlly;
                                char1.setHp(0);
                                hideAlly(0);
                                enemy[i].setExp(test[1].getExp());
                                enemy[i].setHp(test[1].getHp());
                                terrain[allyx][ally] = 0;
                            }
                            else if (test[1] == null) {
                                --numberOfEnemy;
                                char1.setHp(test[0].getHp());
                                char1.setExp(test[0].getExp());
                                enemy[i].setHp(0);
                                hideEnemy(i);
                                terrain[a][b] = 0;
                            }
                            else {
                                char1.setHp(test[0].getHp());
                                enemy[i].setHp(test[1].getHp());
                            }
                            selected = false;
                            textViewMove.setText("");
                            textViewHP.setText("");
                            char1.decrementSpeed();
                        }
                    }
                }
                else if (terrain[a][b] == 1)
                {
                    for (int i=0; i < 3; i ++)
                    {
                        if (tag[i].equals(Integer.toString(a) + Integer.toString(b))) {
                            charSelected = i;
                        }
                    }
                }
            }
            if (charSelected == 1) {
                char2ImageView = (ImageView) view;
                int a = Character.getNumericValue(char2ImageView.getTag().toString().charAt(0));
                int b = Character.getNumericValue(char2ImageView.getTag().toString().charAt(1));
                if (checkMove(a,b)  && char2.getSpeed() > 0) {
                    hideAlly(charSelected);
                    a = Character.getNumericValue(tag[charSelected].charAt(0));
                    b = Character.getNumericValue(tag[charSelected].charAt(1));
                    terrain[a][b] = 0;
                    char2ImageView.setImageResource(R.drawable.chargood2);
                    tag[charSelected] = char2ImageView.getTag().toString();
                    selected = false;
                    textViewMove.setText("");
                    textViewHP.setText("");
                    char2.decrementSpeed();
                    a = Character.getNumericValue(tag[charSelected].charAt(0));
                    b = Character.getNumericValue(tag[charSelected].charAt(1));
                    terrain[a][b] = 1;
                }
                else if (terrain[a][b] == 2  && char2.getSpeed() > 0)
                {
                    for (int i=0; i < 3; i ++)
                    {
                        if (tagEnemy[i].equals(Integer.toString(a) + Integer.toString(b)))
                        {
                            Menel[] test;
                            test = battle(char2,enemy[i],bonusTerrain[allyx][ally],bonusTerrain[a][b],1);
                            if (test[0] == null)
                            {
                                --numberOfAlly;
                                char2.setHp(0);
                                hideAlly(1);
                                enemy[i].setExp(test[1].getExp());
                                enemy[i].setHp(test[1].getHp());
                            }
                            else if (test[1] == null) {
                                --numberOfEnemy;
                                char2.setHp(test[0].getHp());
                                char2.setExp(test[0].getExp());
                                enemy[i].setHp(0);
                                hideEnemy(i);
                                terrain[a][b] = 0;
                            }
                            else {
                                char2.setHp(test[0].getHp());
                                enemy[i].setHp(test[1].getHp());
                            }
                            selected = false;
                            textViewMove.setText("");
                            textViewHP.setText("");
                            char2.decrementSpeed();
                        }
                    }
                }
                else if (terrain[a][b] == 1)
                {
                    for (int i=0; i < 3; i ++)
                    {
                        if (tag[i].equals(Integer.toString(a) + Integer.toString(b))) {
                            charSelected = i;
                        }
                    }
                }
            }
            if (charSelected == 2) {
                char3ImageView = (ImageView) view;
                int a = Character.getNumericValue(char3ImageView.getTag().toString().charAt(0));
                int b = Character.getNumericValue(char3ImageView.getTag().toString().charAt(1));
                if (checkMove(a,b)  && char3.getSpeed() > 0) {
                    hideAlly(charSelected);
                    a = Character.getNumericValue(tag[charSelected].charAt(0));
                    b = Character.getNumericValue(tag[charSelected].charAt(1));
                    terrain[a][b] = 0;
                    char3ImageView.setImageResource(R.drawable.chargood3);
                    tag[charSelected] = char3ImageView.getTag().toString();
                    selected = false;
                    textViewMove.setText("");
                    textViewHP.setText("");
                    char3.decrementSpeed();
                    a = Character.getNumericValue(tag[charSelected].charAt(0));
                    b = Character.getNumericValue(tag[charSelected].charAt(1));
                    terrain[a][b] = 1;
                }
                else if (terrain[a][b] == 2  && char3.getSpeed() > 0)
                {
                    for (int i=0; i < 3; i ++)
                    {
                        if (tagEnemy[i].equals(Integer.toString(a) + Integer.toString(b)))
                        {
                            Menel[] test;
                            test=  battle(char3,enemy[i],bonusTerrain[allyx][ally],bonusTerrain[a][b],1);
                            if (test[0] == null) {
                                --numberOfAlly;
                                char3.setHp(0);
                                hideAlly(2);
                                enemy[i].setExp(test[1].getExp());
                                enemy[i].setHp(test[1].getHp());
                            }
                            else if (test[1] == null) {
                                --numberOfEnemy;
                                char3.setExp(test[0].getExp());
                                char3.setHp(test[0].getHp());
                                enemy[i].setHp(0);
                                hideEnemy(i);
                                terrain[a][b] = 0;
                            }
                            else {
                                char3.setHp(test[0].getHp());
                                enemy[i].setHp(test[1].getHp());
                            }
                            selected = false;
                            textViewMove.setText("");
                            textViewHP.setText("");
                            char3.decrementSpeed();
                        }
                    }

                }
                else if (terrain[a][b] == 1)
                {
                    for (int i=0; i < 3; i ++)
                    {
                        if (tag[i].equals(Integer.toString(a) + Integer.toString(b))) {
                            charSelected = i;
                        }
                    }
                }
            }
            if (numberOfEnemy == 0)
                buttonFight.setText("WIN");
            else if (numberOfAlly == 0)
                buttonFight.setText("LOSE");
        }
        else {
            for (int i = 0 ; i < 3 ; i ++) {
                char1ImageView = (ImageView) view;
                int ay = tag[i].charAt(1), ax = tag[i].charAt(0);
                int by = char1ImageView.getTag().toString().charAt(1), bx = char1ImageView.getTag().toString().charAt(0);
                if ((ay == by) && (ax == bx)) {
                    if (char1.getHp() > 0 && i ==0) {
                        selected = true;
                        charSelected = i;
                        textViewHP.setText("HP:"+char1.getHp());
                        textViewMove.setText("Move:"+char1.getSpeed());
                    }
                    if (char2.getHp() > 0 && i ==1) {
                        selected = true;
                        charSelected = i;
                        textViewHP.setText("HP:"+char2.getHp());
                        textViewMove.setText("Move:"+char2.getSpeed());
                    }
                    if (char3.getHp() > 0 && i ==2) {
                        selected = true;
                        charSelected = i;
                        textViewHP.setText("HP:"+char3.getHp());
                        textViewMove.setText("Move:"+char3.getSpeed());
                    }
                }
            }
            }
    }

    public void endTurn(View view)
    {
            int[] a = new int[3];
            for (int i = 0; i < 3; i++) {
                a[i] = Character.getNumericValue(tag[i].charAt(0)) + Character.getNumericValue(tag[i].charAt(1));
            }
            if (numberOfEnemy == 0)
                gameCheck();
            else if (numberOfAlly == 0)
                gameCheck();
            else if (numberOfAlly > 0 || numberOfEnemy > 0) {
                for (int i = 0; i < 3; i++)
                {
                    if (enemy[i].getHp() > 0) {
                        while (enemy[i].getSpeed() > 0)
                        {
                        int enemyNb = (i + numberOfAlly)%3;
                        int oldTagx = Character.getNumericValue(tagEnemy[i].charAt(0));
                        int oldTagy = Character.getNumericValue(tagEnemy[i].charAt(1));
                        int enemyX = Character.getNumericValue(tagEnemy[i].charAt(0)), enemyY = Character.getNumericValue(tagEnemy[i].charAt(1));
                        int allyX = Character.getNumericValue(tag[enemyNb].charAt(0)), allyY = Character.getNumericValue(tag[enemyNb].charAt(1));
                        if (enemyX > allyX && terrain[enemyX - 1][enemyY] != 2) {
                            enemyX--;
                        } else if (enemyX < allyX && terrain[enemyX + 1][enemyY] != 2) {
                            enemyX++;
                        } else if (enemyY > allyY && terrain[enemyX][enemyY - 1] != 2) {
                            enemyY--;
                        } else if (enemyY < allyY && terrain[enemyX][enemyY + 1] != 2) {
                            enemyY++;
                        }
                        if (enemyX == allyX && enemyY == allyY) {
                            for (int k = 0; k < 3; k++) {
                                if (tag[k].equals(Integer.toString(allyX) + Integer.toString(allyY))) {
                                    Menel[] test = new Menel[2];
                                    int multiply = 1;
                                    if (enemy[i].getSpeed() == 2)
                                        multiply=2;
                                    switch (i) {
                                        case 0:
                                            test = battle(enemy[i], char1, bonusTerrain[oldTagx][oldTagy], bonusTerrain[enemyX][enemyY],multiply);
                                            if (test[0] == null) {
                                                --numberOfEnemy;
                                                char1.setHp(test[1].getHp());
                                                char1.setExp(test[1].getExp());
                                                enemy[i].setHp(0);
                                                hideEnemy(i);
                                                terrain[oldTagx][oldTagy] = 0;
                                            } else if (test[1] == null) {
                                                --numberOfAlly;
                                                char1.setHp(0);
                                                enemy[i].setHp(test[0].getHp());
                                                enemy[i].setExp(test[0].getExp());
                                                hideAlly(enemyNb);
                                            } else {
                                                enemy[i].setHp(test[0].getHp());
                                                char1.setHp(test[1].getHp());
                                            }
                                            break;
                                        case 1:
                                            test = battle(enemy[i], char2, bonusTerrain[oldTagx][oldTagy], bonusTerrain[enemyX][enemyY],multiply);
                                            if (test[0] == null) {
                                                --numberOfEnemy;
                                                char2.setHp(test[1].getHp());
                                                char2.setExp(test[1].getExp());
                                                enemy[i].setHp(0);
                                                terrain[oldTagx][oldTagy] = 0;
                                                hideEnemy(i);
                                            } else if (test[1] == null) {
                                                --numberOfAlly;
                                                char2.setHp(0);
                                                hideAlly(enemyNb);
                                                enemy[i].setExp(test[0].getExp());
                                                enemy[i].setHp(test[0].getHp());
                                            } else {
                                                enemy[i].setHp(test[0].getHp());
                                                char2.setHp(test[1].getHp());
                                            }
                                            break;
                                        case 2:
                                            test = battle(enemy[i], char3, bonusTerrain[oldTagx][oldTagy], bonusTerrain[enemyX][enemyY],multiply);
                                            if (test[0] == null) {
                                                --numberOfEnemy;
                                                char3.setExp(test[1].getExp());
                                                char3.setHp(test[1].getHp());
                                                enemy[i].setHp(0);
                                                terrain[oldTagx][oldTagy] = 0;
                                                hideEnemy(i);
                                            } else if (test[1] == null) {
                                                --numberOfAlly;
                                                char3.setHp(0);
                                                hideAlly(enemyNb);
                                                enemy[i].setHp(test[0].getHp());
                                                enemy[i].setExp(test[0].getExp());
                                            } else {
                                                enemy[i].setHp(test[0].getHp());
                                                char3.setHp(test[1].getHp());
                                            }
                                            break;
                                    }
                                }
                            }
                            if (enemy[i].getSpeed() == 2)
                            {
                                enemy[i].decrementSpeed();
                                enemy[i].decrementSpeed();
                            }
                            else
                                enemy[i].decrementSpeed();
                        } else if (terrain[enemyX][enemyY] != 1 && terrain[enemyX][enemyY] != 2) {
                            terrain[oldTagx][oldTagy] = 0;
                            terrain[enemyX][enemyY] = 2;
                            String newTag = String.valueOf(enemyX) + String.valueOf(enemyY);
                            hideEnemy(i);
                            int id = getResources().getIdentifier("imageView" + newTag, "id", getPackageName());
                            switch (i) {
                                case 0:
                                    enemy1ImageView = findViewById(id);
                                    enemy1ImageView.setImageResource(R.drawable.charbad1);
                                    break;
                                case 1:
                                    enemy2ImageView = findViewById(id);
                                    enemy2ImageView.setImageResource(R.drawable.charbad2);
                                    break;
                                case 2:
                                    enemy3ImageView = findViewById(id);
                                    enemy3ImageView.setImageResource(R.drawable.charbad3);
                                    break;
                            }
                            enemy[i].decrementSpeed();
                            tagEnemy[i] = newTag;
                            }
                        }
                    }
                }
            }
            if (numberOfEnemy == 0)
                buttonFight.setText("WIN");
            else if (numberOfAlly == 0)
                buttonFight.setText("LOSE");
        char1.setSpeed();
        char2.setSpeed();
        char3.setSpeed();
        for (int i = 0 ; i < 3; i ++)
            enemy[i].setSpeed();
    }

    //functions used
    /**
     * Function to check game status
     * */
    private void gameCheck()
    {
        if (numberOfAlly == 0)
        {
            Intent data = new Intent();
            setResult(RESULT_CANCELED,data);
            finish();
        }
        else if (numberOfEnemy==0)
        {
            Intent data = new Intent();
            char1.setHp(char1.getMaxHP());
            char2.setHp(char2.getMaxHP());
            char3.setHp(char3.getMaxHP());
            data.putExtra("char1",char1);
            data.putExtra("char2",char2);
            data.putExtra("char3",char3);
            setResult(1,data);
            finish();
        }
    }
    /**
     * Function for battle between 2 Menel classes
     * atacker - Menel class that will atack 1st
     * defender - Menel class that is beeinh atacked
     * atackerBuff - value of place where atacker is standing
     * defenderBuff - value of place where defender is standing
     * */
    public Menel[] battle(Menel atacker, Menel defender,int atackerBuff,int defenderBuff,int multiply)
    {
        int[] bonusDef = new int[]{0,0};
        int[] bonusAtk = new int[]{0,0};
        if (atackerBuff == 0)
            bonusAtk[0] = 2 * multiply;
        if (defenderBuff == 0)
            bonusAtk[1] = 2  * multiply;
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        View dView = getLayoutInflater().inflate(R.layout.battle,null);
        final TextView textViewAttacker = dView.findViewById(R.id.textViewAttacker);
        final TextView textViewDefender = dView.findViewById(R.id.textViewDefender);
        Button buttonclose = dView.findViewById(R.id.buttonClose);
        textViewAttacker.setText(Integer.toString(atacker.getHp()));
        textViewDefender.setText(Integer.toString(defender.getHp()));
        ImageView atackImageView = dView.findViewById(R.id.imageViewAttacker),defenderImageView = dView.findViewById(R.id.imageViewDefender);
        atackImageView.setBackgroundResource(R.drawable.animationfront);
        defenderImageView.setBackgroundResource(R.drawable.animationback);
        progressAnimationAtacker =    (AnimationDrawable)atackImageView.getBackground();
        progressAnimationAtacker.start();
        progressAnimationDefender =    (AnimationDrawable)defenderImageView.getBackground();
        progressAnimationDefender.start();
        int numberOfAttacks = 1;
        int restDefenderHP = defender.getHp() - (((atacker.getStr() + bonusAtk[0])*2) - (defender.getDef()+ bonusDef[1]));
        int restAtackerHP = atacker.getHp();
        if (restDefenderHP > 0 )
            restAtackerHP = atacker.getHp() - (((defender.getStr()+ bonusAtk[1])*2) - (atacker.getDef() + bonusDef[0]));
        if (atacker.getAgi() > defender.getAgi()*2)
        {
            numberOfAttacks = 2;
            restDefenderHP = restDefenderHP - (((atacker.getStr() + bonusAtk[0])*2) - (defender.getDef()+ bonusDef[1]));
        }
        final int a = restDefenderHP;
        final int b = restAtackerHP;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                textViewAttacker.setText(Integer.toString(b));
                textViewDefender.setText(Integer.toString(a));
                progressAnimationDefender.stop();
                progressAnimationAtacker.stop();
            }
        }, 5000 * numberOfAttacks);
        builder.setView(dView);
        atacker.setHp(restAtackerHP);
        defender.setHp(restDefenderHP);
        final AlertDialog dialog = builder.create();
        buttonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        if (restDefenderHP <= 0) {
            atacker.addExp(defender.getExp());
            return new Menel[]{atacker, null};
        }
        if (restAtackerHP <= 0) {
            defender.addExp(atacker.getExp());
            return new Menel[]{null, defender};
        }
        if (atackerBuff == 4 && atacker.getHp()< atacker.getMaxHP()-2)
            atacker.setHp(atacker.getHp() + (2  * multiply));
        if (defenderBuff == 4 && atacker.getHp() < atacker.getMaxHP()-2)
            defender.setHp(atacker.getHp()+(2  * multiply));
        if (atackerBuff == 3)
            atacker.setHp(atacker.getHp() -(2  * multiply));
        if (defenderBuff == 3)
            defender.setHp(atacker.getHp()-(2  * multiply));
        return new Menel[] {atacker,defender};
    }
    /**
     * Function to check if a box that you want to move to is ocupied
     * a - x value for the table
     * b - y value of tha table
     * */
    private boolean checkMove(int a,int b)
    {
        return terrain[a][b] != 1 && terrain[a][b] != 2 &&
                ((Character.getNumericValue(tag[charSelected].charAt(0)) + 1 == a ||
                        Character.getNumericValue(tag[charSelected].charAt(0)) == a ||
                        Character.getNumericValue(tag[charSelected].charAt(0)) - 1 == a) &&
                        (Character.getNumericValue(tag[charSelected].charAt(1)) + 1 == b ||
                                Character.getNumericValue(tag[charSelected].charAt(1)) == b ||
                                Character.getNumericValue(tag[charSelected].charAt(1)) - 1 == b)
                        && !(Character.getNumericValue(tag[charSelected].charAt(1)) + 1 == b && Character.getNumericValue(tag[charSelected].charAt(0)) + 1 == a)
                        && !(Character.getNumericValue(tag[charSelected].charAt(1)) - 1 == b && Character.getNumericValue(tag[charSelected].charAt(0)) - 1 == a)
                        && !(Character.getNumericValue(tag[charSelected].charAt(1)) - 1 == b && Character.getNumericValue(tag[charSelected].charAt(0)) + 1 == a)
                        && !(Character.getNumericValue(tag[charSelected].charAt(1)) + 1 == b && Character.getNumericValue(tag[charSelected].charAt(0)) - 1 == a)
                );

    }
    /**
     * Function to hide enemy after defet or move
     * i - iteration of the enemy
     * */
    private void hideEnemy(int i)
    {
        int id = getResources().getIdentifier("imageView" + tagEnemy[i], "id", getPackageName());
        ImageView imageView = findViewById(id);
        imageView.setImageResource(0);
    }
    /**
     * Function to hide ally after defet or move
     * i - iteration of the ally
     * */
    private void hideAlly(int i)
    {
        int id = getResources().getIdentifier("imageView" + tag[i], "id", getPackageName());
        ImageView imageView = findViewById(id);
        imageView.setImageResource(0);
    }
    /**
     * Function to fill map with random bonuses
     * */
    private void randomMap()
    {
        for (int i = 0 ; i < TABLE_HEIGHT ; i ++)
        {
            for (int j = 0 ; j < TABLE_WIDTH ; j++)
            {
                int id = getResources().getIdentifier("imageView"+j+i, "id", getPackageName());
                ImageView currcell = findViewById(id);

                int min = 0;
                int max = 4;

                Random r = new Random();
                int i1 = r.nextInt(max - min + 1) + min;
                switch (i1)
                {
                    case 0:
                        currcell.setBackgroundColor(getResources().getColor(R.color.tree));
                        bonusTerrain[j][i]=0;
                        break;
                    case 1:
                        currcell.setBackgroundColor(getResources().getColor(R.color.ground));
                        bonusTerrain[j][i]=1;
                        break;
                    case 2:
                        currcell.setBackgroundColor(getResources().getColor(R.color.mountain));
                        bonusTerrain[j][i]=2;
                        break;
                    case 3:
                        currcell.setBackgroundColor(getResources().getColor(R.color.snow));
                        bonusTerrain[j][i]=3;
                        break;
                    case 4:
                        currcell.setBackgroundColor(getResources().getColor(R.color.wall));
                        bonusTerrain[j][i]=4;
                        break;

                }
            }
        }

    }

}

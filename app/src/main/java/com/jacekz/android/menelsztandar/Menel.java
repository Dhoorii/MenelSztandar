package com.jacekz.android.menelsztandar;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Jacek on 2019-04-02.
 */
@IgnoreExtraProperties
public class Menel implements Serializable{
    private int str,agi,def,hp,speed,exp,lvl,maxHP;
    String name;

    public Menel() {
        str = 1;
        agi = 1;
        def = 1;
        hp = 20;
        maxHP = hp;
        speed = 2;
        exp=0;
        lvl=1;

    }
    public Menel(String name) {
        str = 1;
        agi = 1;
        def = 1;
        hp = 20;
        maxHP = hp;
        speed = 2;
        exp=0;
        lvl=1;
        this.name = name;

    }

    public Menel(int str, int agi, int def, int hp, String name) {
        this.str = str;
        this.agi = agi;
        this.def = def;
        this.hp = hp;
        this.speed = 2;
        maxHP = hp;
        this.name = name;
        exp = 0;
        lvl=1;

    }
    public Menel(int lvl)
    {
        str = 1;
        agi = 1;
        def = 1;
        hp = 20;
        speed = 2;
        exp=0;
        maxHP = hp;
        for (int i = 1 ; i <=lvl ; i++)
        {
            lvlup();
        }
        this.lvl=lvl;
        this.name = "lol";
    }

    public int getStr() {
        return str;
    }

    public int getAgi() {
        return agi;
    }

    public int getDef() {
        return def;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed() {
        this.speed = 2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
    public void addExp(int exp)
    {
        this.exp += exp;
    }

    public int getLvl() {
        return lvl;
    }

    public void lvlup()
    {
        if (exp >= (Math.pow(lvl,2)*100)) {
            exp =exp - (int) (Math.pow(lvl,2)*100);
            lvl++;
            int min = 0;
            int max = 100;
            for (int i = 0; i < 4; i++) {
                Random r = new Random();
                int i1 = r.nextInt(max - min + 1) + min;
                if (i1 >= 50) {
                    addStats(i);
                }
            }
        }

    }
    private void addStats(int i)
    {
        switch (i) {
            case 0:
                str++;
                break;
            case 1:
                agi++;
                break;
            case 2:
                def++;
                break;
            case 3:
                Random r = new Random();
                int i1 = r.nextInt(10 - 1 + 1) + 1;
                hp = hp+i1;
                maxHP = hp;
                break;
        }
    }
    public void decrementSpeed()
    {
        speed--;
    }

    public int getMaxHP() {
        return maxHP;
    }

}

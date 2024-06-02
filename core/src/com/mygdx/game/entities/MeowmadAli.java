package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.Texture;

public class MeowmadAli extends Creature{
    public MeowmadAli() {
        super(100, 100, "MeowmadAli", "meowmad_ali.png");
        skill1 = new Skill();
        skill1.name = "cat scratch";
        skill1.damage = 1;
        skill2 = null;
        skill3 = null;
    }


    public final String name = "MeowmadAli";

}

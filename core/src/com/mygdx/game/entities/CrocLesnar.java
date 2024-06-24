package com.mygdx.game.entities;

import java.util.HashMap;
import java.util.Map;

public class CrocLesnar extends Creature {

    public CrocLesnar() {
        super(100, 100, "Croc Lesnar");
        skill1 = new Skill("Claw Scratch", 20);
        skill2 = new Skill("Croc bite", 50);
        skill3 = new Skill("UFC punch", 30);
        setType("CrocLesnar");
    }
}

package model.objects.macroobjects;

import model.objects.microobjects.Creature;
import model.objects.microobjects.Recruit;
import model.objects.microobjects.Soldier;

import java.util.ArrayList;

public class Cave {
    private final ArrayList<Creature> creatures=new ArrayList<>();

    public ArrayList<Creature> getCreatures() {
        return creatures;
    }

    public void loadCreatures() {
        creatures.add(new Recruit());
        creatures.add(new Soldier());
        creatures.add(new Soldier());
    }
}

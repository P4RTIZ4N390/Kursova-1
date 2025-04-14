package model.objects.macroobjects;

import model.objects.microobjects.Creature;

import java.util.ArrayList;

public class Crypt {
    private final ArrayList<Creature> creatures=new ArrayList<>();

    public ArrayList<Creature> getCreatures() {
        return creatures;
    }
}

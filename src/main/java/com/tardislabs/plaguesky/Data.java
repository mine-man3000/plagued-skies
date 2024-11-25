package com.tardislabs.plaguesky;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Objects;
import java.util.Scanner;

/*
* Yes... this is the cursed shit I had to do to get this to work properly... - Mineman
* */

public class Data {
    private boolean heal = false;

    private final String levelPath;

    public Data(String levelPath) {
        this.levelPath = levelPath;
        create();
    }

    public void save() {
        try {
            FileWriter myWriter = new FileWriter(levelPath + "plaguesky");
            myWriter.write(String.valueOf(heal));
            myWriter.close();
            PlagueSky.mutter("SAVED");
        } catch (IOException exception) {
            PlagueSky.mutter(exception.getMessage());
        }
    }

    public void create() {
        try {
            File myObj = new File(levelPath + "plaguesky");
            if (myObj.createNewFile()) {
                PlagueSky.mutter("File created: " + levelPath + "plaguesky");
                FileWriter myWriter = new FileWriter(levelPath + "plaguesky");
                myWriter.write("false");
                myWriter.close();
            } else {
                PlagueSky.mutter("File already exists.");
            }
        } catch (IOException exception) {
            PlagueSky.mutter(exception.getMessage());

        }
    }

    public boolean read() {
       try {
           File myObj = new File(levelPath + "plaguesky");
           Scanner myReader = new Scanner(myObj);
           String data = myReader.nextLine();

           heal = Objects.equals(data, "true");
           myReader.close();
           PlagueSky.mutter("READ");
       }
       catch (IOException exception) {
           PlagueSky.mutter(exception.getMessage());
       }
        return heal;
    }

    public boolean isHealing() {
        return read();
    }

    public void setHealing(boolean value) {
        heal = value;
        save();
    }

    public void setHealing() {
        setHealing(true);
    }
}

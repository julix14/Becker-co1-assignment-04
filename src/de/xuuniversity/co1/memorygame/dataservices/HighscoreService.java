package de.xuuniversity.co1.memorygame.dataservices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class HighscoreService {
    //Reads the Highscore from the file and returns it
    public static int getCurrentHighscore() {
        BufferedReader bufferedReader = null;
        int highscore = 0;
        try {
            bufferedReader = new BufferedReader(new FileReader("data/highscore.txt"));
            String line = bufferedReader.readLine();
            if(line != null) {
                highscore = Integer.parseInt(line.trim());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }
        return highscore;
    }

    //Saves the Highscore to the file
    public static void saveHighscore(int highscore) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter("data/highscore.txt"));
            bufferedWriter.write(String.valueOf(highscore));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }
    }
}

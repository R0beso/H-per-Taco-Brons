package com.htb.game.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HighScoreManager {

    private static final String FILE_NAME = ".htb_highscore";

    public static int load() {
        File file = new File(System.getProperty("user.home"), FILE_NAME);
        if (!file.exists()) return 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) return Integer.parseInt(line.trim());
        } catch (IOException | NumberFormatException e) {
            // ignore
        }
        return 0;
    }

    public static void save(int score) {
        int current = load();
        if (score <= current) return;
        File file = new File(System.getProperty("user.home"), FILE_NAME);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            // ignore
        }
    }
}

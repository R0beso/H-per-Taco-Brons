package com.htb.game.level;

import com.htb.game.entity.Platform;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {

    public static class LevelData {
        public final int background;
        public final int[][] tiles;
        public final List<Platform> platforms;
        public final int widthTiles;
        public final int heightTiles;
        public final List<double[]> enemySpawns;

        public LevelData(int background, int[][] tiles, List<Platform> platforms, int widthTiles, int heightTiles, List<double[]> enemySpawns) {
            this.background = background;
            this.tiles = tiles;
            this.platforms = platforms;
            this.widthTiles = widthTiles;
            this.heightTiles = heightTiles;
            this.enemySpawns = enemySpawns;
        }
    }

    public static List<LevelData> loadAllLevels(String resourcePath) {
        List<LevelData> levels = new ArrayList<>();

        try {
            InputStream is = LevelLoader.class.getResourceAsStream(resourcePath);
            if (is == null) {
                System.err.println("No se encontró el archivo de niveles: " + resourcePath);
                levels.add(createFallbackLevel());
                return levels;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            List<String> allLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                allLines.add(line);
            }
            reader.close();

            List<List<String>> sections = new ArrayList<>();
            List<String> currentSection = new ArrayList<>();

            for (String l : allLines) {
                if (l.trim().isEmpty()) {
                    if (!currentSection.isEmpty()) {
                        sections.add(currentSection);
                        currentSection = new ArrayList<>();
                    }
                } else {
                    currentSection.add(l);
                }
            }
            if (!currentSection.isEmpty()) {
                sections.add(currentSection);
            }

            for (List<String> section : sections) {
                if (section.isEmpty()) continue;
                if (section.size() < 2) continue;

                int background;
                try {
                    background = Integer.parseInt(section.get(0).trim());
                } catch (NumberFormatException e) {
                    background = 1;
                }

                List<String> tileLines = section.subList(1, section.size());
                int height = tileLines.size();
                int width = detectWidth(tileLines);

                if (width <= 0 || height <= 0) continue;

                int[][] tiles = new int[height][width];
                List<Platform> platforms = new ArrayList<>();
                List<double[]> enemySpawns = new ArrayList<>();

                for (int row = 0; row < height; row++) {
                    String[] values = tileLines.get(row).trim().split("\\s+");
                    int col = 0;
                    for (String val : values) {
                        if (val.isEmpty()) continue;
                        if (col >= width) break;
                        try {
                            int blockType = Integer.parseInt(val);
                            tiles[row][col] = blockType;
                            if (blockType == 8) {
                                double x = col * Platform.TILE_SIZE;
                                double y = row * Platform.TILE_SIZE;
                                enemySpawns.add(new double[]{x, y});
                            } else if (blockType != 0) {
                                double x = col * Platform.TILE_SIZE;
                                double y = row * Platform.TILE_SIZE;
                                platforms.add(new Platform(x, y, blockType));
                            }
                        } catch (NumberFormatException ignored) {
                        }
                        col++;
                    }
                }

                levels.add(new LevelData(background, tiles, platforms, width, height, enemySpawns));
            }

        } catch (Exception e) {
            System.err.println("Error cargando niveles: " + e.getMessage());
        }

        if (levels.isEmpty()) {
            levels.add(createFallbackLevel());
        }

        return levels;
    }

    private static int detectWidth(List<String> section) {
        int maxWidth = 0;
        for (String row : section) {
            String[] values = row.trim().split("\\s+");
            int count = 0;
            for (String v : values) {
                if (!v.isEmpty()) count++;
            }
            if (count > maxWidth) maxWidth = count;
        }
        return maxWidth;
    }

    private static LevelData createFallbackLevel() {
        int width = 16;
        int height = 15;
        int[][] tiles = new int[height][width];
        List<Platform> platforms = new ArrayList<>();

        for (int col = 0; col < width; col++) {
            tiles[14][col] = 1;
            platforms.add(new Platform(col * Platform.TILE_SIZE, 14 * Platform.TILE_SIZE, 1));
        }

        return new LevelData(1, tiles, platforms, width, height, new ArrayList<>());
    }
}

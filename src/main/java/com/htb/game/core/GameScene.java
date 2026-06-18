package com.htb.game.core;

import com.htb.game.entity.*;
import com.htb.game.level.LevelLoader;
import com.htb.game.level.LevelLoader.LevelData;
import com.htb.game.physics.PhysicsEngine;
import com.htb.game.render.Renderer;
import com.htb.tiktok.TikTokEvent;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameScene {

    private enum GameState { PLAYING, LIFE_LOST, GAME_OVER, LEVEL_COMPLETE }

    private Canvas canvas;
    private GraphicsContext gc;
    private Scene scene;
    private InputManager input;
    private Camera camera;
    private PhysicsEngine physics;
    private ConcurrentLinkedQueue<TikTokEvent> tiktokQueue;
    private MusicManager musicManager;
    private Runnable onExit;
    private AnimationTimer gameLoop;

    private Player player;
    private List<Platform> platforms;
    private List<Enemy> enemies;
    private List<Coin> coins;
    private List<TacoPowerUp> tacoPowerUps;
    private List<Entity> allEntities;

    private int score;
    private int coinCount;
    private int lives;
    private int backgroundType;
    private double levelWidth;
    private double levelHeight;

    private GameState gameState;
    private double overlayTimer;
    private boolean playerDiedThisFrame;
    private double levelTimer;

    private List<LevelData> allLevels;
    private int currentLevelIndex;

    public GameScene(ConcurrentLinkedQueue<TikTokEvent> tiktokQueue, MusicManager musicManager, Runnable onExit) {
        this.tiktokQueue = tiktokQueue;
        this.musicManager = musicManager;
        this.onExit = onExit;

        canvas = new Canvas(800, 750);
        gc = canvas.getGraphicsContext2D();
        input = new InputManager();
        camera = new Camera(800, 750);
        physics = new PhysicsEngine();

        StackPane root = new StackPane(canvas);
        scene = new Scene(root, 800, 750);
        input.attachToScene(scene);

        platforms = new ArrayList<>();
        enemies = new ArrayList<>();
        coins = new ArrayList<>();
        tacoPowerUps = new ArrayList<>();
        allEntities = new ArrayList<>();

        allLevels = LevelLoader.loadAllLevels("/world.txt");
        if (allLevels.isEmpty()) {
            System.err.println("No se pudo cargar ningún nivel");
        }

        score = 0;
        coinCount = 0;
        lives = 3;
        currentLevelIndex = 0;
        startLevel(currentLevelIndex);
    }

    private void startLevel(int index) {
        if (index < 0 || index >= allLevels.size()) {
            index = 0;
        }
        currentLevelIndex = index;
        LevelData data = allLevels.get(index);

        platforms.clear();
        for (int row = 0; row < data.tiles.length; row++) {
            for (int col = 0; col < data.tiles[row].length; col++) {
                int type = data.tiles[row][col];
                if (type != 0 && type != 8) {
                    platforms.add(new Platform(col * Platform.TILE_SIZE, row * Platform.TILE_SIZE, type));
                }
            }
        }

        backgroundType = data.background;
        levelWidth = data.widthTiles * Platform.TILE_SIZE;
        levelHeight = data.heightTiles * Platform.TILE_SIZE;

        Platform leftWall = new Platform(-50, 0, 1);
        leftWall.width = 50;
        leftWall.height = levelHeight + 100;
        leftWall.setInvisible(true);
        platforms.add(leftWall);

        Platform rightWall = new Platform(levelWidth, 0, 1);
        rightWall.width = 50;
        rightWall.height = levelHeight + 100;
        rightWall.setInvisible(true);
        platforms.add(rightWall);

        enemies.clear();
        for (int row = 0; row < data.tiles.length; row++) {
            for (int col = 0; col < data.tiles[row].length; col++) {
                if (data.tiles[row][col] == 8) {
                    enemies.add(new Enemy(col * Platform.TILE_SIZE, row * Platform.TILE_SIZE));
                }
            }
        }

        coins.clear();
        tacoPowerUps.clear();

        player = new Player(100, 550, input);
        playerDiedThisFrame = false;
        score += 10000;
        levelTimer = 0;
        rebuildEntityList();
        camera = new Camera(800, 750);
        gameState = GameState.PLAYING;
    }

    private void rebuildEntityList() {
        allEntities.clear();
        if (player != null && player.isAlive()) {
            allEntities.add(player);
        }
        allEntities.addAll(enemies);
        allEntities.addAll(coins);
        allEntities.addAll(tacoPowerUps);
    }

    public void start() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                double delta = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;
                if (delta > 0.05) delta = 0.05;
                update(delta);
                render();
            }
        };
        gameLoop.start();
    }

    private void update(double delta) {
        switch (gameState) {
            case PLAYING -> updatePlaying(delta);
            case LIFE_LOST -> updateLifeLost(delta);
            case GAME_OVER -> updateGameOver(delta);
            case LEVEL_COMPLETE -> updateLevelComplete(delta);
        }
    }

    private void updatePlaying(double delta) {
        playerDiedThisFrame = false;

        player.update(delta);
        for (Enemy enemy : enemies) enemy.update(delta);
        for (Coin coin : coins) coin.update(delta);
        for (TacoPowerUp taco : tacoPowerUps) taco.update(delta);

        physics.update(delta, allEntities, platforms);

        processTikTokEvents();
        checkBumpedBlocks();
        checkEnemyCollisions();
        checkEnemyEnemyCollisions();
        checkCoinCollisions();
        checkTacoCollisions();
        checkVoidFall();
        checkLevelComplete();
        checkScoreDrain(delta);
        removeDeadEntities();

        camera.follow(player.position, levelWidth, levelHeight);
    }

    private void processTikTokEvents() {
        TikTokEvent event;
        while ((event = tiktokQueue.poll()) != null) {
            String type = event.getType();
            String user = event.getUser();
            double spawnX = camera.getX() + camera.getViewportWidth() / 2;
            double spawnY = camera.getY() + 100;
            if (type.equalsIgnoreCase("rosa")) {
                TacoPowerUp taco = new TacoPowerUp(spawnX, spawnY);
                taco.donorName = user;
                tacoPowerUps.add(taco);
                rebuildEntityList();
            } else if (type.equalsIgnoreCase("gg")) {
                Enemy enemy = new Enemy(spawnX, spawnY);
                enemy.donorName = user;
                enemies.add(enemy);
                rebuildEntityList();
            }
        }
    }

    private void updateLifeLost(double delta) {
        overlayTimer -= delta;
        if (overlayTimer <= 0) {
            startLevel(currentLevelIndex);
        }
    }

    private void updateGameOver(double delta) {
        overlayTimer -= delta;
        if (overlayTimer <= 0) {
            HighScoreManager.save(score);
            score = 0;
            coinCount = 0;
            lives = 3;
            startLevel(0);
        }
    }

    private void updateLevelComplete(double delta) {
        overlayTimer -= delta;
        if (overlayTimer <= 0) {
            int next = currentLevelIndex + 1;
            if (next < allLevels.size()) {
                boolean wasBig = player.isBig();
                startLevel(next);
                if (wasBig) player.setBig(true);
            } else {
                score += lives * 10000;
                gameState = GameState.GAME_OVER;
                overlayTimer = 3.0;
            }
        }
    }

    private void checkBumpedBlocks() {
        for (Platform p : platforms) {
            if (p.isBumpable() && p.isBumped() && !p.hasSpawnedAfterBump()) {
                musicManager.playBumpSound();
                p.setSpawnedAfterBump(true);
                if (p.getBlockType() == 3) {
                    Coin coin = new Coin(p.position.x, p.position.y - 50);
                    coins.add(coin);
                    score += 300;
                } else if (p.getBlockType() == 9) {
                    TacoPowerUp taco = new TacoPowerUp(p.position.x, p.position.y - 50);
                    tacoPowerUps.add(taco);
                }
                rebuildEntityList();
            }
        }
    }

    private void checkEnemyCollisions() {
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive() || enemy.isDying()) continue;
            if (player.getCollisionBox().intersects(enemy.getCollisionBox())) {
                double playerFeet = player.position.y + player.height;
                double enemyTop = enemy.position.y;
                double overlap = playerFeet - enemyTop;
                if (player.velocity.y >= 0 && overlap < enemy.height * 0.7) {
                    enemy.stomp();
                    player.velocity.y = -300;
                    musicManager.playStompSound();
                    score += 200;
                } else if (!playerDiedThisFrame) {
                    player.hit();
                    playerDiedThisFrame = true;
                    if (!player.isAlive()) {
                        lives--;
                        score = Math.max(0, score - 20000);
                        if (lives > 0) {
                            gameState = GameState.LIFE_LOST;
                            overlayTimer = 2.0;
            } else {
                score += lives * 10000;
                gameState = GameState.GAME_OVER;
                overlayTimer = 3.0;
                        }
                    }
                }
            }
        }
    }

    private void checkEnemyEnemyCollisions() {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy a = enemies.get(i);
            if (!a.isAlive() || a.isDying()) continue;
            for (int j = i + 1; j < enemies.size(); j++) {
                Enemy b = enemies.get(j);
                if (!b.isAlive() || b.isDying()) continue;
                if (a.getCollisionBox().intersects(b.getCollisionBox())) {
                    double temp = a.velocity.x;
                    a.velocity.x = b.velocity.x;
                    b.velocity.x = temp;
                    if (a.position.x < b.position.x) {
                        a.position.x -= 4;
                        b.position.x += 4;
                    } else {
                        a.position.x += 4;
                        b.position.x -= 4;
                    }
                }
            }
        }
    }

    private void checkCoinCollisions() {
        Iterator<Coin> it = coins.iterator();
        while (it.hasNext()) {
            Coin coin = it.next();
            if (!coin.isAlive()) continue;
            if (player.getCollisionBox().intersects(coin.getCollisionBox())) {
                coin.setAlive(false);
                musicManager.playCoinSound();
                score += 300;
                coinCount++;
            }
        }
    }

    private void checkTacoCollisions() {
        for (TacoPowerUp taco : tacoPowerUps) {
            if (!taco.isAlive()) continue;
            if (player.getCollisionBox().intersects(taco.getCollisionBox())) {
                taco.setAlive(false);
                player.setBig(true);
                musicManager.playPowerUpSound();
                score += 500;
            }
        }
    }

    private void checkLevelComplete() {
        if (player.isAlive() && player.position.x >= levelWidth - 100) {
            gameState = GameState.LEVEL_COMPLETE;
            overlayTimer = 2.0;
        }
    }

    private void checkVoidFall() {
        if (player.isAlive() && player.position.y > levelHeight + 200) {
            player.alive = false;
            lives--;
            score = Math.max(0, score - 20000);
            if (lives > 0) {
                gameState = GameState.LIFE_LOST;
                overlayTimer = 2.0;
            } else {
                gameState = GameState.GAME_OVER;
                overlayTimer = 3.0;
            }
        }
    }

    private void checkScoreDrain(double delta) {
        if (gameState != GameState.PLAYING) return;
        levelTimer += delta;
        score -= (int)((currentLevelIndex + 1) * 100 * delta);
        if (score <= 0) {
            score = Math.max(0, score - 20000);
            player.alive = false;
            lives--;
            if (lives > 0) {
                gameState = GameState.LIFE_LOST;
                overlayTimer = 2.0;
            } else {
                HighScoreManager.save(0);
                gameState = GameState.GAME_OVER;
                overlayTimer = 3.0;
            }
        }
    }

    private void removeDeadEntities() {
        coins.removeIf(c -> !c.isAlive());
        tacoPowerUps.removeIf(t -> !t.isAlive());
        rebuildEntityList();
    }

    private void render() {
        Renderer.drawBackground(gc, camera.getViewportWidth(), camera.getViewportHeight(), backgroundType);

        for (Platform platform : platforms) platform.render(gc, camera);
        for (Coin coin : coins) coin.render(gc, camera);
        for (TacoPowerUp taco : tacoPowerUps) taco.render(gc, camera);
        for (Enemy enemy : enemies) enemy.render(gc, camera);
        player.render(gc, camera);

        Renderer.drawHUD(gc, score, coinCount, lives);

        switch (gameState) {
            case LIFE_LOST -> renderLivesOverlay();
            case GAME_OVER -> renderGameOverOverlay();
            case LEVEL_COMPLETE -> renderLevelCompleteOverlay();
        }
    }

    private void renderLivesOverlay() {
        gc.setFill(Color.rgb(0, 0, 0, 0.55));
        gc.fillRect(0, 0, 800, 750);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 72));
        gc.fillText("x " + lives, 280, 320);

        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 28));
        gc.fillText("VIDAS", 360, 380);
    }

    private void renderGameOverOverlay() {
        gc.setFill(Color.rgb(0, 0, 0, 0.75));
        gc.fillRect(0, 0, 800, 750);

        gc.setFill(Color.rgb(200, 30, 30));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 64));
        gc.fillText("GAME OVER", 120, 350);
    }

    private void renderLevelCompleteOverlay() {
        gc.setFill(Color.rgb(0, 0, 0, 0.55));
        gc.fillRect(0, 0, 800, 750);

        gc.setFill(Color.rgb(50, 200, 50));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 48));
        gc.fillText("NIVEL " + (currentLevelIndex + 1) + " COMPLETADO", 100, 350);
    }

    public Scene getScene() {
        return scene;
    }

    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
}

package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Scene1 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private List<WallShelter> wallShelters;
    private Player player;

    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;

    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;

    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);

    private Timer timer;
    private final int[][] MAP = {
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
    };

    private final HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;
    private static final String SHOT_SOUND = "src/audio/lasershot.wav";
    private static final String WALL_HIT_SOUND = "src/audio/explosiontrack.wav";
    private static final int WALL_BLOCK_SIZE = 18;
    private static final int WALL_ROWS = 3;
    private static final int WALL_COLUMNS = 4;
    private static final int WALL_FALL_SPEED = 1;
    private static final int WALL_SPAWN_START_FRAME = 260;
    private static final int WALL_SPAWN_INTERVAL = 120;
    private static final int WALL_SPAWN_X_START = 70;
    private static final int WALL_SPAWN_X_GAP = 145;
    private static final int WALL_SHELTER_COUNT = 4;
    private static final int WALL_SPAWN_Y = -70;

    private int nextWallShelterIndex = 0;

    public Scene1(Game game) {
        loadSpawnDetails();
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/scene1.wav";
            audioPlayer = new AudioPlayer(filePath);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void loadSpawnDetails() {
        spawnMap.put(50, new SpawnDetails("PowerUp-SpeedUp", 100, 0));
        spawnMap.put(200, new SpawnDetails("Alien1", 200, 0));
        spawnMap.put(300, new SpawnDetails("Alien1", 300, 0));

        spawnMap.put(400, new SpawnDetails("Alien1", 400, 0));
        spawnMap.put(401, new SpawnDetails("Alien1", 450, 0));
        spawnMap.put(402, new SpawnDetails("Alien1", 500, 0));
        spawnMap.put(403, new SpawnDetails("Alien1", 550, 0));

        spawnMap.put(500, new SpawnDetails("Alien1", 100, 0));
        spawnMap.put(501, new SpawnDetails("Alien1", 150, 0));
        spawnMap.put(502, new SpawnDetails("Alien1", 200, 0));
        spawnMap.put(503, new SpawnDetails("Alien1", 350, 0));
    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        gameInit();
        initAudio();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void gameInit() {
        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        wallShelters = new ArrayList<>();
        nextWallShelterIndex = 0;

        player = new Player();
    }

    private void spawnWallShelter() {
        if (nextWallShelterIndex >= WALL_SHELTER_COUNT) {
            return;
        }

        int x = WALL_SPAWN_X_START + (nextWallShelterIndex * WALL_SPAWN_X_GAP);
        wallShelters.add(new WallShelter(x, WALL_SPAWN_Y));
        nextWallShelterIndex++;
    }

    private void drawMap(Graphics g) {
        int scrollOffset = frame % BLOCKHEIGHT;
        int baseRow = frame / BLOCKHEIGHT;
        int rowsNeeded = (BOARD_HEIGHT / BLOCKHEIGHT) + 2;

        for (int screenRow = 0; screenRow < rowsNeeded; screenRow++) {
            int mapRow = (baseRow + screenRow) % MAP.length;
            int y = BOARD_HEIGHT - ((screenRow * BLOCKHEIGHT) - scrollOffset);

            if (y > BOARD_HEIGHT || y < -BLOCKHEIGHT) {
                continue;
            }

            for (int col = 0; col < MAP[mapRow].length; col++) {
                if (MAP[mapRow][col] == 1) {
                    int x = col * BLOCKWIDTH;
                    drawStarCluster(g, x, y, BLOCKWIDTH, BLOCKHEIGHT);
                }
            }
        }
    }

    private void drawStarCluster(Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.WHITE);

        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g.fillOval(centerX - 2, centerY - 2, 4, 4);

        g.fillOval(centerX - 15, centerY - 10, 2, 2);
        g.fillOval(centerX + 12, centerY - 8, 2, 2);
        g.fillOval(centerX - 8, centerY + 12, 2, 2);
        g.fillOval(centerX + 10, centerY + 15, 2, 2);

        g.fillOval(centerX - 20, centerY + 5, 1, 1);
        g.fillOval(centerX + 18, centerY - 15, 1, 1);
        g.fillOval(centerX - 5, centerY - 18, 1, 1);
        g.fillOval(centerX + 8, centerY + 20, 1, 1);
    }

    private void drawAliens(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }

            if (enemy.isDying()) {
                enemy.die();
            }
        }
    }

    private void drawPowreUps(Graphics g) {
        for (PowerUp p : powerups) {
            if (p.isVisible()) {
                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }

            if (p.isDying()) {
                p.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {
        if (player.isVisible()) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {
            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawWalls(Graphics g) {
        for (WallShelter shelter : wallShelters) {
            shelter.draw(g);
        }
    }

    private void drawExplosions(Graphics g) {
        List<Explosion> toRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }

        explosions.removeAll(toRemove);
    }

    private boolean wallCollidesWithPlayer() {
        for (WallShelter shelter : wallShelters) {
            if (shelter.collidesWith(player)) {
                return true;
            }
        }

        return false;
    }

    private boolean damageWallAt(int x, int y) {
        for (WallShelter shelter : wallShelters) {
            if (shelter.damageAt(x, y)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, 10, 10);

        if (inGame) {
            drawMap(g);
            drawExplosions(g);
            drawPowreUps(g);
            drawAliens(g);
            drawWalls(g);
            drawPlayer(g);
            drawShot(g);
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }

            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_WIDTH / 2);
    }

    private void update() {
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            switch (sd.type) {
                case "Alien1":
                    enemies.add(new Alien1(sd.x, sd.y));
                    break;
                case "PowerUp-SpeedUp":
                    powerups.add(new SpeedUp(sd.x, sd.y));
                    break;
                default:
                    System.out.println("Unknown enemy type: " + sd.type);
                    break;
            }
        }

        if (frame >= WALL_SPAWN_START_FRAME
                && (frame - WALL_SPAWN_START_FRAME) % WALL_SPAWN_INTERVAL == 0) {
            spawnWallShelter();
        }

        if (deaths == NUMBER_OF_ALIENS_TO_DESTROY) {
            inGame = false;
            timer.stop();
            message = "Game won!";
        }

        player.act();

        for (WallShelter shelter : wallShelters) {
            shelter.act();
        }

        if (player.isVisible() && !player.isDying() && wallCollidesWithPlayer()) {
            AudioPlayer.playEffect(WALL_HIT_SOUND);
            explosions.add(new Explosion(player.getX(), player.getY()));
            player.setDying(true);
            message = "You crashed into a wall!";
        }

        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                }
            }
        }

        for (Enemy enemy : enemies) {
            if (!enemy.isVisible()) {
                continue;
            }

            enemy.act(direction);

            if (player.isVisible() && !player.isDying() && player.collidesWith(enemy)) {
                AudioPlayer.playEffect(WALL_HIT_SOUND);
                explosions.add(new Explosion(player.getX(), player.getY()));
                player.setDying(true);
                message = "You crashed into an enemy!";
                break;
            }
        }

        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {
            if (!shot.isVisible()) {
                continue;
            }

            int shotX = shot.getX();
            int shotY = shot.getY();

            for (Enemy enemy : enemies) {
                int enemyX = enemy.getX();
                int enemyY = enemy.getY();

                if (enemy.isVisible() && shot.isVisible()
                        && shotX >= enemyX
                        && shotX <= (enemyX + ALIEN_WIDTH)
                        && shotY >= enemyY
                        && shotY <= (enemyY + ALIEN_HEIGHT)) {
                    var ii = new ImageIcon(IMG_EXPLOSION);
                    enemy.setImage(ii.getImage());
                    enemy.setDying(true);
                    explosions.add(new Explosion(enemyX, enemyY));
                    deaths++;
                    shot.die();
                    shotsToRemove.add(shot);
                    break;
                }
            }

            if (shot.isVisible() && damageWallAt(shotX, shotY)) {
                AudioPlayer.playEffect(WALL_HIT_SOUND);
                explosions.add(new Explosion(shotX, shotY));
                shot.die();
                shotsToRemove.add(shot);
            }

            int y = shot.getY() - 20;
            if (y < 0) {
                shot.die();
                shotsToRemove.add(shot);
            } else {
                shot.setY(y);
            }
        }

        shots.removeAll(shotsToRemove);

        wallShelters.removeIf(WallShelter::isEmptyAndOffScreen);
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                if (shots.size() < 4) {
                    AudioPlayer.playEffect(SHOT_SOUND);
                    shots.add(new Shot(x + (player.getWidth() / 2), y));
                }
            }
        }
    }

    private class WallShelter {

        private final int x;
        private int y;
        private final boolean[][] tiles = new boolean[WALL_ROWS][WALL_COLUMNS];

        WallShelter(int x, int y) {
            this.x = x;
            this.y = y;

            for (int row = 0; row < WALL_ROWS; row++) {
                for (int col = 0; col < WALL_COLUMNS; col++) {
                    tiles[row][col] = true;
                }
            }
        }

        void act() {
            y += WALL_FALL_SPEED;
        }

        void draw(Graphics g) {
            for (int row = 0; row < WALL_ROWS; row++) {
                for (int col = 0; col < WALL_COLUMNS; col++) {
                    if (!tiles[row][col]) {
                        continue;
                    }

                    int tileX = x + (col * WALL_BLOCK_SIZE);
                    int tileY = y + (row * WALL_BLOCK_SIZE);

                    g.setColor(new Color(102, 153, 102));
                    g.fillRect(tileX, tileY, WALL_BLOCK_SIZE, WALL_BLOCK_SIZE);
                    g.setColor(new Color(70, 115, 70));
                    g.drawRect(tileX, tileY, WALL_BLOCK_SIZE - 1, WALL_BLOCK_SIZE - 1);
                }
            }
        }

        boolean damageAt(int px, int py) {
            for (int row = 0; row < WALL_ROWS; row++) {
                for (int col = 0; col < WALL_COLUMNS; col++) {
                    if (!tiles[row][col]) {
                        continue;
                    }

                    int tileX = x + (col * WALL_BLOCK_SIZE);
                    int tileY = y + (row * WALL_BLOCK_SIZE);
                    if (px >= tileX && px < tileX + WALL_BLOCK_SIZE
                            && py >= tileY && py < tileY + WALL_BLOCK_SIZE) {
                        tiles[row][col] = false;
                        return true;
                    }
                }
            }

            return false;
        }

        boolean collidesWith(Player player) {
            int playerX = player.getX();
            int playerY = player.getY();
            int playerRight = playerX + player.getImage().getWidth(null);
            int playerBottom = playerY + player.getImage().getHeight(null);

            for (int row = 0; row < WALL_ROWS; row++) {
                for (int col = 0; col < WALL_COLUMNS; col++) {
                    if (!tiles[row][col]) {
                        continue;
                    }

                    int tileX = x + (col * WALL_BLOCK_SIZE);
                    int tileY = y + (row * WALL_BLOCK_SIZE);
                    int tileRight = tileX + WALL_BLOCK_SIZE;
                    int tileBottom = tileY + WALL_BLOCK_SIZE;

                    if (playerX < tileRight
                            && playerRight > tileX
                            && playerY < tileBottom
                            && playerBottom > tileY) {
                        return true;
                    }
                }
            }

            return false;
        }

        boolean isEmptyAndOffScreen() {
            for (int row = 0; row < WALL_ROWS; row++) {
                for (int col = 0; col < WALL_COLUMNS; col++) {
                    if (tiles[row][col]) {
                        return false;
                    }
                }
            }

            return y > BOARD_HEIGHT;
        }
    }
}
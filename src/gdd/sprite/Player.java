package gdd.sprite;

import static gdd.Global.*;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int width;
    private int height;
    private int currentSpeed = 2;

    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        var ii = new ImageIcon(IMG_PLAYER);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
        width = scaledImage.getWidth(null);
        height = scaledImage.getHeight(null);

        setX(START_X);
        setY(START_Y);
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {
        if (speed < 1) {
            speed = 1; // Ensure speed is at least 1
        }
        this.currentSpeed = speed;
        updateVelocity();
        return currentSpeed;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void act() {
        x += dx;
        y += dy;

        if (x <= 0) {
            x = 0;
        }

        if (x >= BOARD_WIDTH - width) {
            x = BOARD_WIDTH - width;
        }

        if (y <= 0) {
            y = 0;
        }

        if (y >= BOARD_HEIGHT - height) {
            y = BOARD_HEIGHT - height;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            left = true;
        }

        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            right = true;
        }

        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            up = true;
        }

        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            down = true;
        }

        updateVelocity();
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            left = false;
        }

        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            right = false;
        }

        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
            up = false;
        }

        if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
            down = false;
        }

        updateVelocity();
    }

    private void updateVelocity() {
        if (left && !right) {
            dx = -currentSpeed;
        } else if (right && !left) {
            dx = currentSpeed;
        } else {
            dx = 0;
        }

        if (up && !down) {
            dy = -currentSpeed;
        } else if (down && !up) {
            dy = currentSpeed;
        } else {
            dy = 0;
        }
    }
}

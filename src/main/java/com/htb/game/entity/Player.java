package com.htb.game.entity;

import com.htb.game.core.Camera;
import com.htb.game.core.InputManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player extends Entity {

    private static final double WALK_SPEED = 200;
    private static final double RUN_SPEED = 375;
    private static final double ACCELERATION = 1200;
    private static final double FRICTION = 800;
    private static final double JUMP_VELOCITY = -1280;
    private static final double JUMP_BOOST_GRAVITY = 3271;
    private static final double NORMAL_WIDTH = 28;
    private static final double NORMAL_HEIGHT = 44;
    private static final double BIG_WIDTH = 28;
    private static final double BIG_HEIGHT = 88;
    private static final double BIG_DURATION = -1;

    private final InputManager input;
    private int facingDirection = 1;
    private double animTimer = 0;
    private double walkTimer = 0;
    private boolean invincible = false;
    private double invincibleTimer = 0;
    private boolean big = false;
    private double bigTimer = 0;
    private boolean prevJump = false;

    public Player(double x, double y, InputManager input) {
        super(x, y, NORMAL_WIDTH, NORMAL_HEIGHT);
        this.input = input;
    }

    @Override
    public void update(double delta) {
        if (!alive) return;

        boolean moving = false;
        double targetSpeed = input.isRun() ? RUN_SPEED : WALK_SPEED;

        if (input.isLeft()) {
            if (velocity.x > -targetSpeed) {
                velocity.x -= ACCELERATION * delta;
                if (velocity.x < -targetSpeed) velocity.x = -targetSpeed;
            }
            moving = true;
        } else if (input.isRight()) {
            if (velocity.x < targetSpeed) {
                velocity.x += ACCELERATION * delta;
                if (velocity.x > targetSpeed) velocity.x = targetSpeed;
            }
            moving = true;
        } else {
            if (velocity.x > 0) {
                velocity.x -= FRICTION * delta;
                if (velocity.x < 0) velocity.x = 0;
            } else if (velocity.x < 0) {
                velocity.x += FRICTION * delta;
                if (velocity.x > 0) velocity.x = 0;
            }
        }

        if (velocity.x < 0) {
            facingDirection = -1;
        } else if (velocity.x > 0) {
            facingDirection = 1;
        }

        moving = Math.abs(velocity.x) > 1;

        boolean jumpDown = input.isJump();
        if (jumpDown && !prevJump && onGround) {
            velocity.y = JUMP_VELOCITY;
            onGround = false;
        }
        if (jumpDown && velocity.y < 0) {
            velocity.y += JUMP_BOOST_GRAVITY * delta;
        }
        if (!jumpDown && prevJump && velocity.y < 0) {
            velocity.y *= 0.175;
        }
        prevJump = jumpDown;

        animTimer += delta;
        if (onGround && moving) {
            walkTimer += delta * Math.abs(velocity.x) * 0.04;
        } else {
            walkTimer = 0;
        }

        if (invincible) {
            invincibleTimer -= delta;
            if (invincibleTimer <= 0) {
                invincible = false;
            }
        }
        if (big && bigTimer > 0) {
            bigTimer -= delta;
            if (bigTimer <= 0) {
                setBig(false);
            }
        }
    }

    public void hit() {
        if (invincible) return;
        if (big) {
            setBig(false);
            setInvincible(1.5);
            return;
        }
        alive = false;
    }

    public void setInvincible(double duration) {
        invincible = true;
        invincibleTimer = duration;
    }

    public void setBig(boolean value) {
        big = value;
        if (big) {
            width = BIG_WIDTH;
            height = BIG_HEIGHT;
            bigTimer = BIG_DURATION;
            position.y -= (BIG_HEIGHT - NORMAL_HEIGHT);
        } else {
            position.y += (BIG_HEIGHT - NORMAL_HEIGHT);
            width = NORMAL_WIDTH;
            height = NORMAL_HEIGHT;
            bigTimer = 0;
        }
    }

    public boolean isBig() {
        return big;
    }

    @Override
    public void render(GraphicsContext gc, Camera camera) {
        double screenX = position.x - camera.getX();
        double screenY = position.y - camera.getY();

        if (invincible && ((int)(animTimer * 10) % 2 == 0)) {
            return;
        }

        double centerX = screenX + width / 2;

        gc.save();

        if (facingDirection < 0) {
            gc.translate(centerX, 0);
            gc.scale(-1, 1);
            gc.translate(-centerX, 0);
        }

        if (big) {
            renderBig(gc, screenX, screenY);
        } else {
            renderNormal(gc, screenX, screenY);
        }

        gc.restore();
    }

    private void renderNormal(GraphicsContext gc, double x, double y) {

        //Camiseta arriba
        gc.setFill(Color.rgb(34, 177, 76));
        gc.fillRoundRect(x + 5, y + 10, width - 10, height - 14, 4, 4);

        //Camiseta abajo
        gc.setFill(Color.rgb(34, 177, 76));
        gc.fillRoundRect(x + 3 , y + 25, width - 6, height - 35, 0, 0);

        //Pantalon
        gc.setFill(Color.rgb(0, 162, 232));
        gc.fillRoundRect(x + 2 , y + 31, width - 4, height - 35, 0, 0);

        //Brazo
        gc.setFill(Color.rgb(34, 177, 76));
        gc.fillRoundRect(x + 10 , y + 17, width - 10, height - 35, 0, 0);
        //Mano
        gc.setFill(Color.rgb(185, 122, 87));
        gc.fillRoundRect(x + 26 , y + 21, width - 20, height - 35, 0, 0);

        //Cabeza
        gc.setFill(Color.rgb(185, 122, 87));
        gc.fillOval(x , y, width + 4, 18);

        //Cabello
        gc.setFill(Color.BLACK);
        gc.fillRect(x, y, width + 2, 6);
        gc.fillRect(x, y + 5, 7, 6);

        //Ojo
        gc.fillOval(x + 20, y + 5, 6, 6);

        //Boca
        gc.fillOval(x + 22, y + 13, 10, 3);

        //Pies
        double legSwing = Math.sin(walkTimer) * 2;
        gc.setFill(Color.rgb(0, 162, 232));
        gc.fillRect(x , y + height - 6 + legSwing, 8, 6);
        gc.fillRect(x + 20, y + height - 6 - legSwing, 8, 6);
    }

    private void renderBig(GraphicsContext gc, double x, double y) {
        final double sy = 2.0; // escala vertical

        // Camiseta arriba
        gc.setFill(Color.rgb(34, 177, 76));
        gc.fillRoundRect(x + 5, y + 18, width - 10, 30, 4, 4);

        // Camiseta abajo
        gc.fillRoundRect(x + 3, y + 42, width - 6, 18, 0, 0);

        // Brazo
        gc.fillRoundRect(x + 10, y + 30, width - 10, 18, 0, 0);

        // Mano
        gc.setFill(Color.rgb(185, 122, 87));
        gc.fillRoundRect(x + 26, y + 38, width - 20, 18, 0, 0);

        // Pantalón
        gc.setFill(Color.rgb(0, 162, 232));
        gc.fillRoundRect(x + 2, y + 56, width - 4, 18, 0, 0);

        // Cabeza
        gc.setFill(Color.rgb(185, 122, 87));
        gc.fillOval(x, y, width + 4, 18 * sy);

        // Cabello
        gc.setFill(Color.BLACK);
        gc.fillRect(x, y, width + 2, 6 * sy);
        gc.fillRect(x, y + 5 * sy, 7, 6 * sy);

        // Ojo
        gc.fillOval(x + 20, y + 5 * sy, 6, 6 * sy);

        // Boca
        gc.fillOval(x + 22, y + 13 * sy, 10, 3 * sy);

        // Pies
        gc.setFill(Color.rgb(0, 162, 232));
        double legSwing = Math.sin(walkTimer) * 4; // un poco más de movimiento
        gc.fillRect(x, y + 34 * sy + legSwing, 8, (6 * sy) + 7);
        gc.fillRect(x + 20, y + 34 * sy - legSwing, 8, (6 * sy) + 7);
    }
}

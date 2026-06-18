package com.htb.game.physics;

import com.htb.game.entity.Entity;
import com.htb.game.entity.Platform;

import java.util.List;

public class PhysicsEngine {

    private static final double GRAVITY = 1568;
    private static final double MAX_FALL_SPEED = 600;

    public void update(double delta, List<Entity> entities, List<Platform> platforms) {
        for (Entity entity : entities) {
            if (!entity.isAlive()) continue;

            entity.velocity.y += GRAVITY * delta;
            if (entity.velocity.y > MAX_FALL_SPEED) {
                entity.velocity.y = MAX_FALL_SPEED;
            }

            entity.position.x += entity.velocity.x * delta;
            resolveHorizontalCollisions(entity, platforms);

            entity.position.y += entity.velocity.y * delta;
            entity.onGround = false;
            resolveVerticalCollisions(entity, platforms);
        }
    }

    private void resolveHorizontalCollisions(Entity entity, List<Platform> platforms) {
        CollisionBox entityBox = entity.getCollisionBox();
        for (Platform platform : platforms) {
            if (!platform.isSolid()) continue;
            CollisionBox platformBox = platform.getCollisionBox();
            if (entityBox.intersects(platformBox)) {
                if (entity.velocity.x > 0) {
                    entity.position.x = platformBox.x - entity.width;
                } else if (entity.velocity.x < 0) {
                    entity.position.x = platformBox.x + platformBox.width;
                }
                if (entity.bouncesOffWalls) {
                    entity.velocity.x = -entity.velocity.x;
                } else {
                    entity.velocity.x = 0;
                }
                entityBox = entity.getCollisionBox();
            }
        }
    }

    private void resolveVerticalCollisions(Entity entity, List<Platform> platforms) {
        CollisionBox entityBox = entity.getCollisionBox();
        for (Platform platform : platforms) {
            if (!platform.isSolid()) continue;
            CollisionBox platformBox = platform.getCollisionBox();
            if (entityBox.intersects(platformBox)) {
                if (entity.velocity.y > 0) {
                    entity.position.y = platformBox.y - entity.height;
                    entity.onGround = true;
                } else if (entity.velocity.y < 0) {
                    entity.position.y = platformBox.y + platformBox.height;
                    if (platform.isBumpable() && !platform.isBumped()) {
                        platform.bump();
                    }
                }
                entity.velocity.y = 0;
                entityBox = entity.getCollisionBox();
            }
        }
    }
}

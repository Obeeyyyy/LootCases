package de.obey.lootcases;
/*

    Author - Obey -> LootCases
       21.02.2023 / 20:11

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import lombok.Getter;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Timer;
import java.util.TimerTask;

public final class EffectRunnable implements Runnable{

    @Getter
    private Timer timer = new Timer();

    private double yOffset = 0, currentAngle = 0, radius = 0.2;

    private int yDirection = 0;

    public void stop() {
        timer.cancel();
    }

    @Override
    public void run() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(Init.getInstance().getCaseHandler().getCaseBlocks().isEmpty())
                    return;

                for (final Block caseBlock : Init.getInstance().getCaseHandler().getCaseBlocks()) {

                    final Location location = caseBlock.getLocation().clone().add(-0.5, 0, 0.5);

                    double angle = 0;

                    for(double i = 0; i < 6.283; i+=0.2) {
                        location.add(radius * Math.sin(angle), 0, radius * Math.cos(angle));
                        angle += 0.2;

                        sendPacket(new PacketPlayOutWorldParticles(EnumParticle.FLAME, false,
                                (float )location.getX(),
                                (float) location.getY(),
                                (float) location.getZ(),
                                0.0f,0.0f, 0.0f, 0, 1, 0), caseBlock.getWorld());
                    }
/*
                    for(double i = 0; i < 6.283; i+=0.2) {
                        final Location animLocation = caseBlock.getLocation().clone().add(0.5, 0, 0.5);

                        animLocation.add(1 * Math.sin(currentAngle), yOffset, 1 * Math.cos(currentAngle));
                        currentAngle += 0.2;

                        if(yOffset >= 1.2) {
                            yDirection = 1;
                        }

                        if(yOffset <= 0) {
                            yDirection = 0;
                        }

                        if(yDirection == 0) {
                            yOffset += 0.1;
                        } else {
                            yOffset -= 0.1;
                        }

                        sendPacket(new PacketPlayOutWorldParticles(EnumParticle.FLAME, false,
                                (float) animLocation.getX(),
                                (float) animLocation.getY(),
                                (float) animLocation.getZ(),
                                0.0f, 0.0f, 0.0f, 0, 1, 0), caseBlock.getWorld());
                    }

 */
                }
            }
        }, 500, 250);
    }

    private void sendPacket(final Packet packet, final World world) {
        for (Entity entity : world.getEntities()) {
            if(entity instanceof Player)
                ((CraftPlayer) entity).getHandle().playerConnection.sendPacket(packet);
        }
    }
}

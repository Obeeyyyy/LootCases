package de.obey.lootcases.handler;
/*

    Author - Obey -> LootCases
       21.02.2023 / 20:11

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import de.obey.lootcases.Init;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public final class EffectRunnable implements Runnable{

    private final double radius = 1;
    private double angle = 0, angleOffset = 0.2, yOffset = 0.0, yState = 1;

    @Getter
    private final Timer timer = new Timer();

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
                    final Location location = caseBlock.getLocation().clone().add(0.5, 0.5, 0.5);

                    location.add(radius * Math.sin(angle), yOffset, radius * Math.cos(angle));

                    sendPacket(new PacketPlayOutWorldParticles(EnumParticle.CRIT_MAGIC, false,
                            (float) location.getX(),
                            (float) location.getY(),
                            (float) location.getZ(),
                            0.0f, 0.0f, 0f, 0, 5, 0), caseBlock);
                }

                angle += angleOffset;

                if(yState == 1) {
                    yOffset += 0.01;
                } else {
                    yOffset -= 0.01;
                }

                if(yOffset >= 0.35)
                    yState = -1;

                if(yOffset <= -0.25)
                    yState = 1;
            }
        }, 500, 40);
    }

    private void sendPacket(final Packet<?> packet, final Block block) {
        try {
            if (block == null || block.getWorld() == null || block.getWorld().getEntities() == null)
                return;

            final ArrayList<Entity> entities = new ArrayList<>(Arrays.asList(block.getChunk().getEntities()));


        for (int i = 1; i <= 2; i++) {
            Collections.addAll(entities, block.getWorld().getChunkAt(block.getChunk().getX() - i, block.getChunk().getZ()).getEntities());
            Collections.addAll(entities, block.getWorld().getChunkAt(block.getChunk().getX() + i, block.getChunk().getZ()).getEntities());

            Collections.addAll(entities, block.getWorld().getChunkAt(block.getChunk().getX(), block.getChunk().getZ() - i).getEntities());
            Collections.addAll(entities, block.getWorld().getChunkAt(block.getChunk().getX(), block.getChunk().getZ() + i).getEntities());
        }

            for (Entity entity : entities) {
                if (entity instanceof Player) {
                    ((CraftPlayer) entity).getHandle().playerConnection.sendPacket(packet);
                }
            }
        }catch (ConcurrentModificationException ignored) {}
    }
}

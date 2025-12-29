
package net.example.turtlecounter;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

public class TurtleHudOverlay {

    private static int turtleCount = 0;
    private static int lastTurtleCount = 0;

    private static int diffAmount = 0;
    private static long diffStartTime = 0;

    private static final long UPDATE_INTERVAL = 1000;
    private static final long DIFF_DISPLAY_TIME = 3000;

    private static long lastUpdate = 0;

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.world == null) return;

            long now = System.currentTimeMillis();

            if (now - lastUpdate >= UPDATE_INTERVAL) {
                lastUpdate = now;
                lastTurtleCount = turtleCount;
                turtleCount = countTurtles(client);

                int diff = turtleCount - lastTurtleCount;
                if (diff != 0) {
                    diffAmount = diff;
                    diffStartTime = now;
                }
            }

            drawContext.drawText(
                    client.textRenderer,
                    Text.literal("shellwise nearby (150m): " + turtleCount),
                    10, 10, 0x00FF00, true
            );

            long elapsed = now - diffStartTime;
            if (elapsed < DIFF_DISPLAY_TIME && diffAmount != 0) {
                float alphaRatio = 1.0f - (float) elapsed / DIFF_DISPLAY_TIME;
                int alpha = (int) (alphaRatio * 255);

                int color = diffAmount > 0
                        ? (alpha << 24) | 0x00FF00
                        : (alpha << 24) | 0xFF0000;

                String diffText = diffAmount > 0
                        ? "(+" + diffAmount + ")"
                        : "(" + diffAmount + ")";

                drawContext.drawText(
                        client.textRenderer,
                        Text.literal(diffText),
                        10, 22, color, true
                );
            }
        });
    }

    private static int countTurtles(MinecraftClient client) {
        Box box = client.player.getBoundingBox().expand(150);
        return client.world.getEntitiesByClass(TurtleEntity.class, box, t -> true).size();
    }
}

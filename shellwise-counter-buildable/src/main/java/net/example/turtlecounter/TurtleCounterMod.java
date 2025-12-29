
package net.example.turtlecounter;

import net.fabricmc.api.ClientModInitializer;

public class TurtleCounterMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TurtleHudOverlay.register();
    }
}

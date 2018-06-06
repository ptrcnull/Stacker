package ml.bjorn.stacker;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class Keybinds {
    public static KeyBinding stack;

    public static void register() {
        stack = new KeyBinding("key.stack", Keyboard.KEY_X, "key.categories.stacker");
        ClientRegistry.registerKeyBinding(stack);
    }
}

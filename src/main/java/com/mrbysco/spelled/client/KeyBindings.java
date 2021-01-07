package com.mrbysco.spelled.client;

import com.mrbysco.spelled.Reference;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

public class KeyBindings {
    public static KeyBinding KEY_USE = new KeyBinding(getKey("use"), InputMappings.INPUT_INVALID.getKeyCode(), getKey("category"));

    private static String getKey(String name) {
        return String.join(".", "key", Reference.MOD_ID, name);
    }
}

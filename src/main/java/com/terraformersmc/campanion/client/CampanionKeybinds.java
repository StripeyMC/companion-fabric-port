package com.terraformersmc.campanion.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.terraformersmc.campanion.Campanion;
import com.terraformersmc.campanion.network.C2SOpenBackpack;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;

public class CampanionKeybinds {
	public static final KeyMapping OPEN_BACKPACK_KEY = new KeyMapping(
        String.format("%s.%s", Campanion.MOD_ID, "open_backpack"),
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_B,
        "key.categories.gameplay"
    );

	public static void onClientTick() {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			while (CampanionKeybinds.OPEN_BACKPACK_KEY.consumeClick()) {
				ClientPlayNetworking.send(new C2SOpenBackpack());
			}
		}
	}
}

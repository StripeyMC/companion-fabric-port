package com.terraformersmc.campanion.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/** Registers packet types (both sides) and the server-side receivers. */
public class CampanionNetwork {

	public static void register() {
		PayloadTypeRegistry.playC2S().register(C2SOpenBackpack.TYPE, C2SOpenBackpack.CODEC);
		PayloadTypeRegistry.playC2S().register(C2SRotateHeldItem.TYPE, C2SRotateHeldItem.CODEC);
		PayloadTypeRegistry.playS2C().register(S2CSyncBackpackContents.TYPE, S2CSyncBackpackContents.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(C2SOpenBackpack.TYPE, (payload, context) -> C2SOpenBackpack.handle(context.player()));
		ServerPlayNetworking.registerGlobalReceiver(C2SRotateHeldItem.TYPE, (payload, context) -> C2SRotateHeldItem.handle(context.player()));
	}
}

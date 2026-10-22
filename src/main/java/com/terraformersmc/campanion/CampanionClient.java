package com.terraformersmc.campanion;

import com.terraformersmc.campanion.client.CampanionKeybinds;
import com.terraformersmc.campanion.entity.GrapplingHookUser;
import com.terraformersmc.campanion.item.CampanionItems;
import com.terraformersmc.campanion.item.SleepingBagItem;
import com.terraformersmc.campanion.item.TentBagItem;
import com.terraformersmc.campanion.network.S2CSyncBackpackContents;
import com.terraformersmc.campanion.backpack.BackpackStorePlayer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;

public class CampanionClient {

	/** Leather brown, the colour an undyed sleeping bag has. */
	public static final int DEFAULT_BAG_COLOUR = 0xA06540;

	public static void registerClientPacketHandlers() {
		ClientPlayNetworking.registerGlobalReceiver(S2CSyncBackpackContents.TYPE, (payload, context) -> {
			LocalPlayer player = context.player();
			if (player != null) {
				NonNullList<ItemStack> list = ((BackpackStorePlayer) player).getBackpackStacks();
				list.clear();
				list.addAll(payload.stacks());
			}
		});
	}

	public static void registerModelPredicateProviders() {
		register(ItemProperties::register);
	}

	private static void register(TriConsumer<Item, ResourceLocation, ClampedItemPropertyFunction> registry) {
		registry.accept(CampanionItems.GRAPPLING_HOOK, Campanion.id("deployed"), (stack, world, entity, seed) -> {
			if (entity instanceof Player) {
				for (InteractionHand value : InteractionHand.values()) {
					ItemStack heldStack = entity.getItemInHand(value);
					if (heldStack == stack && (((GrapplingHookUser) entity).getGrapplingHook() != null && !((GrapplingHookUser) entity).getGrapplingHook().isRemoved())) {
						return 1;
					}
				}
			}
			return 0;
		});
		registry.accept(CampanionItems.TENT_BAG, Campanion.id("open"), (stack, world, entity, seed) -> TentBagItem.isEmpty(stack) ? 1 : 0);
		registry.accept(CampanionItems.SLEEPING_BAG, Campanion.id("open"), (stack, world, entity, seed) -> SleepingBagItem.inUse(stack) ? 1 : 0);
	}

	public static int sleepingBagColour(ItemStack stack) {
		return DyedItemColor.getOrDefault(stack, DEFAULT_BAG_COLOUR);
	}
}

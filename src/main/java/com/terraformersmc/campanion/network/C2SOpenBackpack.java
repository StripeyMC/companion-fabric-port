package com.terraformersmc.campanion.network;

import com.terraformersmc.campanion.Campanion;
import com.terraformersmc.campanion.backpack.BackpackContainerFactory;
import com.terraformersmc.campanion.item.BackpackItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public record C2SOpenBackpack() implements CustomPacketPayload {
	public static final Type<C2SOpenBackpack> TYPE = new Type<>(Campanion.id("open_backpack"));
	public static final StreamCodec<RegistryFriendlyByteBuf, C2SOpenBackpack> CODEC = StreamCodec.unit(new C2SOpenBackpack());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerPlayer player) {
		ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);

		if (stack.getItem() instanceof BackpackItem backpack) {
			player.openMenu(new BackpackContainerFactory(backpack.type));
		}
	}
}

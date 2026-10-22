package com.terraformersmc.campanion.network;

import com.terraformersmc.campanion.Campanion;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record S2CSyncBackpackContents(List<ItemStack> stacks) implements CustomPacketPayload {
	public static final Type<S2CSyncBackpackContents> TYPE = new Type<>(Campanion.id("sync_backpack_contents"));
	public static final StreamCodec<RegistryFriendlyByteBuf, S2CSyncBackpackContents> CODEC =
			ItemStack.OPTIONAL_LIST_STREAM_CODEC.map(S2CSyncBackpackContents::new, S2CSyncBackpackContents::stacks);

	public S2CSyncBackpackContents(NonNullList<ItemStack> stacks) {
		this((List<ItemStack>) stacks);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}

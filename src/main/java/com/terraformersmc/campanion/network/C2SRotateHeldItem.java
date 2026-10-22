package com.terraformersmc.campanion.network;

import com.terraformersmc.campanion.Campanion;
import com.terraformersmc.campanion.item.PlaceableTentItem;
import com.terraformersmc.campanion.util.StackData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public record C2SRotateHeldItem() implements CustomPacketPayload {
	public static final Type<C2SRotateHeldItem> TYPE = new Type<>(Campanion.id("rotate_held_item"));
	public static final StreamCodec<RegistryFriendlyByteBuf, C2SRotateHeldItem> CODEC = StreamCodec.unit(new C2SRotateHeldItem());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ServerPlayer player) {
		ItemStack stack = player.getMainHandItem();
		if (!(stack.getItem() instanceof PlaceableTentItem tent) || !tent.hasBlocks(stack)) {
			return;
		}

		StackData.update(stack, root -> {
			ListTag blocks = root.getList("Blocks", Tag.TAG_COMPOUND);
			for (Tag block : blocks) {
				CompoundTag tag = (CompoundTag) block;
				BlockPos off = StackData.readPos(tag, "Pos");
				BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("BlockState"));

				BlockPos rotatedPos = StructureTemplate.transform(off, Mirror.NONE, Rotation.CLOCKWISE_90, BlockPos.ZERO);
				tag.put("Pos", StackData.writePos(rotatedPos));
				tag.put("BlockState", NbtUtils.writeBlockState(state.rotate(Rotation.CLOCKWISE_90)));
				// Block entity data is left untouched: the tent part block entities only store link data
				// that is rewritten when the tent is placed.
			}
		});
	}
}

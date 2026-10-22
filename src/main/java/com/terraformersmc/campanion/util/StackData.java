package com.terraformersmc.campanion.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.function.Consumer;

/**
 * Helpers for the "custom data" item component, which replaced raw item stack NBT in 1.20.5.
 */
public final class StackData {
	private StackData() {
	}

	/** Returns a copy of the custom data of the stack. Modifying it does not change the stack. */
	public static CompoundTag read(ItemStack stack) {
		return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
	}

	public static boolean has(ItemStack stack) {
		return stack.has(DataComponents.CUSTOM_DATA);
	}

	/** Modifies the custom data of the stack in place. */
	public static void update(ItemStack stack, Consumer<CompoundTag> updater) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
	}

	/** Writes a block position as a {X, Y, Z} compound (the same layout the 1.19 version used). */
	public static CompoundTag writePos(BlockPos pos) {
		CompoundTag tag = new CompoundTag();
		tag.putInt("X", pos.getX());
		tag.putInt("Y", pos.getY());
		tag.putInt("Z", pos.getZ());
		return tag;
	}

	/** Reads a block position written by {@link #writePos(BlockPos)}, or a vanilla int array. */
	public static BlockPos readPos(CompoundTag parent, String key) {
		Tag tag = parent.get(key);
		if (tag instanceof CompoundTag compound) {
			return new BlockPos(compound.getInt("X"), compound.getInt("Y"), compound.getInt("Z"));
		}
		if (tag instanceof IntArrayTag array && array.size() == 3) {
			return new BlockPos(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt());
		}
		return BlockPos.ZERO;
	}
}

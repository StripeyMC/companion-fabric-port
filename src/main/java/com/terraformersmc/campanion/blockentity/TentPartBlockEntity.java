package com.terraformersmc.campanion.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import com.terraformersmc.campanion.util.StackData;
import net.minecraft.world.level.block.state.BlockState;

public class TentPartBlockEntity extends SerializableBlockEntity {

	private BlockPos linkedPos = BlockPos.ZERO;
	private BlockPos size = BlockPos.ZERO;

	public TentPartBlockEntity(BlockPos pos, BlockState state) {
		super(CampanionBlockEntities.TENT_PART, pos, state);
	}

	public BlockPos getLinkedPos() {
		return linkedPos;
	}

	public void setLinkedPos(BlockPos linkedPos) {
		this.linkedPos = linkedPos;
	}

	public void setTentSize(BlockPos size) {
		this.size = size;
	}

	public BlockPos getSize() {
		return size;
	}

	@Override
	public void toTag(CompoundTag tag) {
		tag.put("LinkedPos", StackData.writePos(this.linkedPos));
		tag.put("Size", StackData.writePos(this.size));
	}

	@Override
	public void fromTag(CompoundTag tag) {
		this.linkedPos = StackData.readPos(tag, "LinkedPos");
		this.size = StackData.readPos(tag, "Size");
	}

	@Override
	public void toClientTag(CompoundTag tag) {
		toTag(tag);
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		fromTag(tag);
	}
}

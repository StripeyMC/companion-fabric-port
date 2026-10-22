package com.terraformersmc.campanion.blockentity;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

// based on https://github.com/Technici4n/Modern-Dynamics/blob/master/src/main/java/dev/technici4n/moderndynamics/MdBlockEntity.java
/// by shartte & Technici4n
public abstract class SerializableBlockEntity extends BlockEntity {
	private boolean shouldClientRemesh = true;

	public SerializableBlockEntity(BlockEntityType<?> bet, BlockPos pos, BlockState state) {
		super(bet, pos, state);
	}

	public void sync(boolean shouldRemesh) {
		Preconditions.checkNotNull(level); // Maintain distinct failure case from below
		if (!(level instanceof ServerLevel))
			throw new IllegalStateException("Cannot call sync() on the logical client! Did you check world.isClient first?");

		shouldClientRemesh = shouldRemesh | shouldClientRemesh;
		((ServerLevel) level).getChunkSource().blockChanged(getBlockPos());
	}

	public void sync() {
		sync(true);
	}

	public abstract void toTag(CompoundTag tag);

	public abstract void fromTag(CompoundTag tag);

	public abstract void toClientTag(CompoundTag tag);

	public abstract void fromClientTag(CompoundTag tag);

	@Override
	public final ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public final CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag nbt = super.getUpdateTag(registries);
		toClientTag(nbt);
		nbt.putBoolean("#c", shouldClientRemesh); // mark client tag
		shouldClientRemesh = false;
		return nbt;
	}

	@Override
	protected final void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.saveAdditional(nbt, registries);
		toTag(nbt);
	}

	@Override
	protected final void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.loadAdditional(nbt, registries);
		if (nbt.contains("#c")) {
			fromClientTag(nbt);
			if (nbt.getBoolean("#c")) {
				remesh();
			}
		} else {
			fromTag(nbt);
		}
	}

	public final void remesh() {
		Preconditions.checkNotNull(level);
		if (!level.isClientSide)
			throw new IllegalStateException("Cannot call remesh() on the server!");

		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 0);
	}
}

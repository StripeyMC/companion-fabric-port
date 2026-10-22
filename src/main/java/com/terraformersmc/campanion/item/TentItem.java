package com.terraformersmc.campanion.item;

import com.terraformersmc.campanion.Campanion;
import com.terraformersmc.campanion.mixin.AccessorStructureTemplate;
import com.terraformersmc.campanion.util.StackData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public class TentItem extends PlaceableTentItem {

	private final ResourceLocation structure;

	public TentItem(Properties settings, String structureName) {
		super(settings);
		this.structure = Campanion.id("tents/" + structureName + "_tent");
	}

	public ResourceLocation getStructure() {
		return structure;
	}

	@Override
	public void onPlaceTent(ItemStack stack) {
		stack.shrink(1);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if (!hasBlocks(stack) && world instanceof ServerLevel serverLevel) {
			Optional<StructureTemplate> template = serverLevel.getStructureManager().get(((TentItem) stack.getItem()).getStructure());
			template.ifPresentOrElse(
					structure -> initNbt(stack, structure),
					() -> Campanion.LOG.error("Missing tent structure {}", ((TentItem) stack.getItem()).getStructure())
			);
		}
		super.inventoryTick(stack, world, entity, slot, selected);
	}

	public static void initNbt(ItemStack stack, StructureTemplate structure) {
		Vec3i size = structure.getSize();
		ListTag list = new ListTag();
		for (StructureTemplate.StructureBlockInfo info : ((AccessorStructureTemplate) structure).getPalettes().get(0).blocks()) {
			CompoundTag tag = new CompoundTag();
			tag.put("Pos", StackData.writePos(info.pos().offset(-size.getX() / 2, 0, -size.getZ() / 2)));
			tag.put("BlockState", NbtUtils.writeBlockState(info.state()));
			if (info.nbt() != null && !info.nbt().isEmpty()) {
				tag.put("BlockEntityData", info.nbt());
			}
			list.add(tag);
		}

		StackData.update(stack, root -> {
			root.put("Blocks", list);
			root.put("TentSize", StackData.writePos(new BlockPos(size)));
		});
	}
}

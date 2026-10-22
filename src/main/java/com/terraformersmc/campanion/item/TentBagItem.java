package com.terraformersmc.campanion.item;

import com.terraformersmc.campanion.util.StackData;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class TentBagItem extends PlaceableTentItem {
	public TentBagItem(Properties settings) {
		super(settings.stacksTo(1));
	}

	@Override
	public void onPlaceTent(ItemStack stack) {
		StackData.update(stack, tag -> tag.remove("Blocks"));
	}

	public static boolean isEmpty(ItemStack stack) {
		return !(stack.getItem() == CampanionItems.TENT_BAG && StackData.read(stack).contains("Blocks", Tag.TAG_LIST));
	}
}

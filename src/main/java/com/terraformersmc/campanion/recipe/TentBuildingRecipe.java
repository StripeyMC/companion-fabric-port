package com.terraformersmc.campanion.recipe;

import com.terraformersmc.campanion.item.CampanionItems;
import com.terraformersmc.campanion.item.TentBagItem;
import com.terraformersmc.campanion.item.TentItem;
import com.terraformersmc.campanion.util.StackData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class TentBuildingRecipe extends CustomRecipe {

	private static final ThreadLocal<StructureTemplate> STRUCTURE_CACHE = ThreadLocal.withInitial(() -> null);

	public TentBuildingRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput inv, Level world) {
		boolean foundTent = false;
		boolean foundBag = false;

		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);

			if (isBag(stack) && !foundBag) {
				foundBag = true;
			} else if (isTent(stack) && !foundTent) {
				if (world instanceof ServerLevel) {
					STRUCTURE_CACHE.set(((ServerLevel) world).getStructureManager().get(((TentItem) stack.getItem()).getStructure()).orElseThrow());
				}
				foundTent = true;
			} else if (!stack.isEmpty()) {
				STRUCTURE_CACHE.set(null);
				return false;
			}

			if (foundBag && foundTent) {
				return true;
			}
		}

		STRUCTURE_CACHE.set(null);
		return false;
	}

	@Override
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
		StructureTemplate structure = STRUCTURE_CACHE.get();
		STRUCTURE_CACHE.set(null);

		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);

			if (isTent(stack)) {
				if (structure != null) {
					ItemStack out = new ItemStack(CampanionItems.TENT_BAG);
					TentItem.initNbt(out, structure);
					return out;
				}
			}

		}
		return ItemStack.EMPTY;
	}

	private static boolean isBag(ItemStack stack) {
		return stack.getItem() instanceof TentBagItem && !StackData.read(stack).contains("Blocks", Tag.TAG_LIST);
	}

	private static boolean isTent(ItemStack stack) {
		return stack.getItem() instanceof TentItem;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return CampanionRecipeSerializers.TENT_BUILDING_RECIPE;
	}
}

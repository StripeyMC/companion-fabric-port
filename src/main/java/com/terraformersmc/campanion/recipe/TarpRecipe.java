package com.terraformersmc.campanion.recipe;

import com.terraformersmc.campanion.item.CampanionItems;
import com.terraformersmc.campanion.tag.CampanionItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TarpRecipe extends CustomRecipe {
	public TarpRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput inv, Level world) {
		int woolAmount = 0;
		int shearAmount = 0;
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.is(CampanionItemTags.SHEARS)) {
				shearAmount++;
			} else if (stack.is(ItemTags.WOOL)) {
				woolAmount++;
			}
		}
		return woolAmount == 3 && shearAmount == 1;
	}

	@Override
	public boolean isSpecial() {
		return false;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();

		list.add(Ingredient.of(ItemTags.WOOL));
		list.add(Ingredient.of(ItemTags.WOOL));
		list.add(Ingredient.of(ItemTags.WOOL));

		list.add(Ingredient.of(CampanionItemTags.SHEARS));
		return list;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return new ItemStack(CampanionItems.WOOL_TARP);
	}

	@Override
	public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
		return new ItemStack(CampanionItems.WOOL_TARP);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 4;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return CampanionRecipeSerializers.TARP_RECIPE;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		NonNullList<ItemStack> remaining = super.getRemainingItems(input);

		for (int i = 0; i < remaining.size(); ++i) {
			ItemStack stack = input.getItem(i);
			if (stack.is(CampanionItemTags.SHEARS)) {
				ItemStack copy = stack.copy();
				copy.setDamageValue(copy.getDamageValue() + 1);
				if (copy.getDamageValue() < copy.getMaxDamage()) {
					remaining.set(i, copy);
				}
			}
		}

		return remaining;
	}
}

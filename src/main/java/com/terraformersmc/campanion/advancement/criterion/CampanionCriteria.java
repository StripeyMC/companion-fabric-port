package com.terraformersmc.campanion.advancement.criterion;

import com.terraformersmc.campanion.Campanion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class CampanionCriteria {
	public static final PlayerTrigger SLEPT_IN_SLEEPING_BAG = register("slept_in_sleeping_bag", new PlayerTrigger());
	public static final CountCriterion STONE_SKIPS = register("stone_skips", new CountCriterion());
	public static final KilledWithStoneCriterion KILLED_WITH_STONE = register("killed_with_stone", new KilledWithStoneCriterion());

	private static <T extends CriterionTrigger<?>> T register(String id, T criterion) {
		return Registry.register(BuiltInRegistries.TRIGGER_TYPES, Campanion.id(id), criterion);
	}

	public static void loadClass() {

	}
}

package com.terraformersmc.campanion.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public class KilledWithStoneCriterion extends SimpleCriterionTrigger<KilledWithStoneCriterion.TriggerInstance> {

	@Override
	public Codec<TriggerInstance> codec() {
		return TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, Entity entity, int count) {
		this.trigger(player, (conditions) -> conditions.matches(player, entity, count));
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<EntityPredicate> entity, MinMaxBounds.Ints skips) implements SimpleCriterionTrigger.SimpleInstance {
		public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
				EntityPredicate.CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity),
				MinMaxBounds.Ints.CODEC.optionalFieldOf("skips", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::skips)
		).apply(instance, TriggerInstance::new));

		public boolean matches(ServerPlayer player, Entity entity, int count) {
			return this.skips.matches(count) && (this.entity.isEmpty() || this.entity.get().matches(player, entity));
		}
	}
}

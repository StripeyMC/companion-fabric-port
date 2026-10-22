package com.terraformersmc.campanion.item;

import com.terraformersmc.campanion.entity.SpearEntity;
import com.terraformersmc.campanion.sound.CampanionSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class SpearItem extends TridentItem {

	private final Tier material;
	private final float attackDamage;
	private final Supplier<EntityType<SpearEntity>> typeSupplier;
	private EntityType<SpearEntity> cachedType = null;

	public SpearItem(Tier material, float attackDamage, float attackSpeed, Supplier<EntityType<SpearEntity>> typeSupplier, Item.Properties settings) {
		super(settings
				.durability(material.getUses())
				.attributes(spearAttributes(attackDamage + material.getAttackDamageBonus(), attackSpeed)));
		this.material = material;
		this.attackDamage = attackDamage + material.getAttackDamageBonus();
		this.typeSupplier = typeSupplier;
	}

	private static ItemAttributeModifiers spearAttributes(float attackDamage, float attackSpeed) {
		return ItemAttributeModifiers.builder()
				.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
				.build();
	}

	public EntityType<SpearEntity> getType() {
		if (cachedType == null) {
			cachedType = typeSupplier.get();
		}
		return cachedType;
	}

	public Tier getMaterial() {
		return this.material;
	}

	@Override
	public int getEnchantmentValue() {
		return this.material.getEnchantmentValue();
	}

	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
		return this.material.getRepairIngredient().test(ingredient) || super.isValidRepairItem(stack, ingredient);
	}

	public float getAttackDamage() {
		return this.attackDamage;
	}

	@Override
	public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
		return !miner.isCreative();
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		if (state.is(Blocks.COBWEB)) {
			return 15.0F;
		}
		return state.is(BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
		return true;
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner) {
		if (state.getDestroySpeed(world, pos) != 0.0F) {
			stack.hurtAndBreak(2, miner, EquipmentSlot.MAINHAND);
		}

		return true;
	}

	@Override
	public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
		if (user instanceof Player playerEntity) {
			int i = this.getUseDuration(stack, user) - remainingUseTicks;
			if (i >= 10) {
				if (!world.isClientSide) {
					stack.hurtAndBreak(1, playerEntity, LivingEntity.getSlotForHand(user.getUsedItemHand()));
					SpearEntity spearEntity = new SpearEntity(world, playerEntity, this, stack);
					spearEntity.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0.0F, 2.5F, 1.0F);
					if (playerEntity.getAbilities().instabuild) {
						spearEntity.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
					}

					world.addFreshEntity(spearEntity);
					world.playSound(null, spearEntity, CampanionSoundEvents.SPEAR_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
					if (!playerEntity.getAbilities().instabuild) {
						playerEntity.getInventory().removeItem(stack);
					}
				}

				playerEntity.awardStat(Stats.ITEM_USED.get(this));
			}
		}
	}
}

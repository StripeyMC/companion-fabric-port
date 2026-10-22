package com.terraformersmc.campanion.entity;

import com.terraformersmc.campanion.Campanion;
import com.terraformersmc.campanion.item.CampanionItems;
import com.terraformersmc.campanion.item.SpearItem;
import com.terraformersmc.campanion.sound.CampanionSoundEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpearEntity extends AbstractArrow {
	/** The damage type of spears. Defined in data/campanion/damage_type/spear.json. */
	public static final ResourceKey<DamageType> SPEAR_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Campanion.id("spear"));

	private static final EntityDataAccessor<Boolean> ENCHANTMENT_GLINT = SynchedEntityData.defineId(SpearEntity.class, EntityDataSerializers.BOOLEAN);

	private ItemStack spearStack;
	private final Set<UUID> piercedEntities = new HashSet<>();

	public SpearEntity(EntityType<? extends SpearEntity> entityType, Level world, SpearItem item) {
		super(entityType, world);
		this.spearStack = new ItemStack(item);
	}

	public SpearEntity(Level world, LivingEntity owner, SpearItem item, ItemStack stack) {
		super(item.getType(), owner, world, stack.copy(), null);
		this.spearStack = stack.copy();
		this.entityData.set(ENCHANTMENT_GLINT, stack.hasFoil());
	}

	public SpearEntity(Level world, double x, double y, double z, SpearItem item) {
		super(item.getType(), x, y, z, world, new ItemStack(item), null);
		this.spearStack = new ItemStack(item);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(ENCHANTMENT_GLINT, false);
	}

	/**
	 * The entity ids of the spears are the same as the ids of their items.
	 * This is called while the super constructor runs, so it can only use the entity type.
	 */
	@Override
	protected ItemStack getDefaultPickupItem() {
		Item item = BuiltInRegistries.ITEM.get(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()));
		return new ItemStack(item == net.minecraft.world.item.Items.AIR ? CampanionItems.WOODEN_SPEAR : item);
	}


	public boolean isEnchanted() {
		return this.entityData.get(ENCHANTMENT_GLINT);
	}

	private int getLevel(ResourceKey<Enchantment> key) {
		Holder<Enchantment> holder = this.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key);
		return EnchantmentHelper.getItemEnchantmentLevel(holder, this.spearStack);
	}

	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		int level = this.getLevel(Enchantments.PIERCING);
		Entity hitEntity = entityHitResult.getEntity();
		if (this.piercedEntities.contains(hitEntity.getUUID()) || this.piercedEntities.size() > level) {
			return;
		}
		this.piercedEntities.add(hitEntity.getUUID());
		float damage = ((SpearItem) this.spearStack.getItem()).getAttackDamage() * 2;
		if (hitEntity instanceof Animal) {
			int impalingLevel = this.getLevel(Enchantments.IMPALING);
			if (impalingLevel > 0) {
				damage += impalingLevel * 1.5F;
			}
		}

		Entity owner = this.getOwner();
		DamageSource damageSource = createSpearDamageSource(this, owner == null ? this : owner);
		SoundEvent soundEvent = CampanionSoundEvents.SPEAR_HIT_FLESH;
		if (hitEntity.hurt(damageSource, damage)) {
			if (hitEntity.getType() == EntityType.ENDERMAN) {
				return;
			}

			if (hitEntity instanceof LivingEntity hitLivingEntity) {
				if (this.level() instanceof ServerLevel serverLevel) {
					EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, hitLivingEntity, damageSource, this.spearStack);
				}

				this.doPostHurtEffects(hitLivingEntity);
			}
		}

		if (this.piercedEntities.size() > level) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
		} else {
			this.setDeltaMovement(this.getDeltaMovement().scale(0.75));
		}
		this.playSound(soundEvent, 1.0F, 1.0F);
	}

	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent() {
		return CampanionSoundEvents.SPEAR_HIT_GROUND;
	}

	@Override
	public void playerTouch(Player player) {
		Entity entity = this.getOwner();
		if (entity == null || entity.getUUID() == player.getUUID()) {
			super.playerTouch(player);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("Item", Tag.TAG_COMPOUND)) {
			ItemStack.parse(this.registryAccess(), tag.getCompound("Item")).ifPresent(stack -> {
				this.spearStack = stack;
				this.entityData.set(ENCHANTMENT_GLINT, this.spearStack.hasFoil());
			});
		}

		this.piercedEntities.clear();
		if (tag.contains("HitEntities", Tag.TAG_LIST)) {
			for (Tag hitEntity : tag.getList("HitEntities", Tag.TAG_COMPOUND)) {
				this.piercedEntities.add(((CompoundTag) hitEntity).getUUID("UUID"));
			}
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.put("Item", this.spearStack.save(this.registryAccess()));

		ListTag tags = new ListTag();
		for (UUID uuid : this.piercedEntities) {
			CompoundTag c = new CompoundTag();
			c.putUUID("UUID", uuid);
			tags.add(c);
		}
		tag.put("HitEntities", tags);
	}

	@Override
	protected void tickDespawn() {
		if (this.pickup != AbstractArrow.Pickup.ALLOWED) {
			super.tickDespawn();
		}
	}

	@Override
	public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
		return true;
	}

	public static DamageSource createSpearDamageSource(Entity spear, Entity owner) {
		return new DamageSource(
				spear.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SPEAR_DAMAGE),
				spear, owner
		);
	}
}

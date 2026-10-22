package com.terraformersmc.campanion.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Tints campfire smoke with the average colour of any dyes cooking on the campfire (used for coloured signals).
 */
@Mixin(CampfireSmokeParticle.class)
public abstract class MixinCampfireSmokeParticle extends TextureSheetParticle {
	protected MixinCampfireSmokeParticle(ClientLevel level, double x, double y, double z) {
		super(level, x, y, z);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void campanion$tintSmoke(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, boolean signal, CallbackInfo ci) {
		CampfireBlockEntity campfire = campanion$findCampfire(level, BlockPos.containing(x, y, z));
		if (campfire == null) {
			return;
		}

		int red = 0;
		int green = 0;
		int blue = 0;
		int dyes = 0;
		for (ItemStack stack : campfire.getItems()) {
			if (stack.getItem() instanceof DyeItem dye) {
				int rgb = dye.getDyeColor().getFireworkColor();
				red += (rgb >> 16) & 0xFF;
				green += (rgb >> 8) & 0xFF;
				blue += rgb & 0xFF;
				dyes++;
			}
		}

		if (dyes > 0) {
			this.setColor(red / (255F * dyes), green / (255F * dyes), blue / (255F * dyes));
		}
	}

	/** Smoke particles spawn above the fire, so look at the particle's block and the ones below it. */
	@Unique
	private static CampfireBlockEntity campanion$findCampfire(ClientLevel level, BlockPos start) {
		for (int i = 0; i < 3; i++) {
			if (level.getBlockEntity(start.below(i)) instanceof CampfireBlockEntity campfire) {
				return campfire;
			}
		}
		return null;
	}
}

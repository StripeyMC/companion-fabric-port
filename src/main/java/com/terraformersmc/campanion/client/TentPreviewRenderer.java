package com.terraformersmc.campanion.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.terraformersmc.campanion.client.renderer.item.BuiltTentItemRenderer;
import com.terraformersmc.campanion.client.util.TentPreviewImmediate;
import com.terraformersmc.campanion.item.PlaceableTentItem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Draws the translucent preview of a built tent while the player holds it. */
public class TentPreviewRenderer {

	public static void render(WorldRenderContext context) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		PoseStack matrices = context.matrixStack();
		if (level == null || matrices == null || !(context.consumers() instanceof MultiBufferSource.BufferSource bufferSource)) {
			return;
		}
		TentPreviewImmediate immediate = TentPreviewImmediate.STORAGE;
		immediate.begin(bufferSource);

		for (Player player : level.players()) {
			ItemStack stack = player.getMainHandItem();
			if (stack.getItem() instanceof PlaceableTentItem tent && tent.hasBlocks(stack)) {
				HitResult result = player.pick(10, 0, true);
				if (result instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK) {
					BlockPos placePos = hit.getBlockPos().relative(hit.getDirection());
					Vec3 d = context.camera().getPosition().subtract(Vec3.atLowerCornerOf(placePos));
					matrices.pushPose();
					matrices.translate(-d.x, -d.y, -d.z);
					List<BlockPos> list = tent.getErrorPosition(level, placePos, stack);
					immediate.setApplyModifiers(!list.isEmpty());
					BuiltTentItemRenderer.INSTANCE.render(stack, matrices, placePos, immediate, -1);
					for (BlockPos pos : list) {
						matrices.pushPose();
						matrices.translate(pos.getX() - placePos.getX(), pos.getY() - placePos.getY(), pos.getZ() - placePos.getZ());
						if (level.getBlockState(pos).isAir()) {
							BlockState stone = Blocks.STONE.defaultBlockState();
							minecraft.getBlockRenderer().renderBatched(stone, pos, level, matrices, immediate.getBuffer(null), false, RandomSource.create());
						} else {
							float scale = 1.03F;
							matrices.scale(scale, scale, scale);
							matrices.translate(-0.5 * scale + 0.5, -0.5 * scale + 0.5, -0.5 * scale + 0.5);
							BuiltTentItemRenderer.renderFakeBlock(level, pos, BlockPos.ZERO, matrices, immediate);
						}
						matrices.popPose();
					}
					matrices.popPose();
				}
			}
		}
		immediate.endBatch();
	}
}

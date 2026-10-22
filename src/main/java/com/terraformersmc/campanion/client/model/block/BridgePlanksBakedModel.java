package com.terraformersmc.campanion.client.model.block;

import com.terraformersmc.campanion.Campanion;
import com.terraformersmc.campanion.blockentity.RopeBridgePlanksBlockEntity;
import com.terraformersmc.campanion.client.renderer.RopeBridgePlankRenderer;
import com.terraformersmc.campanion.client.rendering.BlockModelPartCreator;
import com.terraformersmc.campanion.ropebridge.RopeBridge;
import com.terraformersmc.campanion.ropebridge.RopeBridgePlank;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class BridgePlanksBakedModel implements BakedModel, FabricBakedModel {

	public static final Material[] PLANKS = IntStream.range(0, RopeBridge.PLANK_VARIANT_TEXTURES)
			.mapToObj(i -> new Material(InventoryMenu.BLOCK_ATLAS, Campanion.id("ropebridge/plank" + i)))
			.toArray(Material[]::new);
	public static final Material ROPE = new Material(InventoryMenu.BLOCK_ATLAS, Campanion.id("ropebridge/rope"));
	public static final Material STOPPER = new Material(InventoryMenu.BLOCK_ATLAS, Campanion.id("ropebridge/stopper"));

	/** Set for the "ghost" models used by the post renderer; null for the in-world block model. */
	private Mesh staticMesh;

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
		if (this.staticMesh != null) {
			context.meshConsumer().accept(this.staticMesh);
			return;
		}
		if (blockView.getBlockEntity(pos) instanceof RopeBridgePlanksBlockEntity blockEntity) {
			context.meshConsumer().accept(blockEntity.getModelMesh());
		}
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction face, RandomSource random) {
		return Collections.emptyList();
	}

	@Override
	public boolean useAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean usesBlockLight() {
		return false;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return Minecraft.getInstance().getBlockRenderer().getBlockModel(Blocks.OAK_PLANKS.defaultBlockState()).getParticleIcon();
	}

	@Override
	public ItemTransforms getTransforms() {
		return ItemTransforms.NO_TRANSFORMS;
	}

	@Override
	public ItemOverrides getOverrides() {
		return ItemOverrides.EMPTY;
	}

	public static BridgePlanksBakedModel createStaticModel(List<RopeBridgePlank> planks) {
		BridgePlanksBakedModel model = new BridgePlanksBakedModel();
		BlockModelPartCreator emitter = new BlockModelPartCreator();
		for (RopeBridgePlank plank : planks) {
			RopeBridgePlankRenderer.emitAllQuads(plank, true, emitter);
		}
		model.staticMesh = emitter.created();
		return model;
	}
}

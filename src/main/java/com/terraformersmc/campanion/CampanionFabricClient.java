package com.terraformersmc.campanion;

import com.terraformersmc.campanion.block.CampanionBlocks;
import com.terraformersmc.campanion.blockentity.CampanionBlockEntities;
import com.terraformersmc.campanion.client.CampanionKeybinds;
import com.terraformersmc.campanion.client.TentPreviewRenderer;
import com.terraformersmc.campanion.client.model.block.BridgePlanksUnbakedModel;
import com.terraformersmc.campanion.client.renderer.blockentity.RopeBridgePostBlockEntityRenderer;
import com.terraformersmc.campanion.client.renderer.entity.EmptyRenderer;
import com.terraformersmc.campanion.client.renderer.entity.GrapplingHookEntityRenderer;
import com.terraformersmc.campanion.client.renderer.entity.SpearEntityRenderer;
import com.terraformersmc.campanion.entity.CampanionEntities;
import com.terraformersmc.campanion.item.CampanionItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class CampanionFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		registerRenderLayers();

		ModelLoadingPlugin.register(plugin -> plugin.modifyModelOnLoad().register((model, context) -> {
			if (context.topLevelId() != null) {
				String id = context.topLevelId().toString();
				if (id.startsWith("campanion:rope_bridge_planks#") || id.startsWith("campanion:rope_bridge_post#")) {
					return new BridgePlanksUnbakedModel();
				}
			}
			return model;
		}));

		KeyBindingHelper.registerKeyBinding(CampanionKeybinds.OPEN_BACKPACK_KEY);

		BlockEntityRenderers.register(CampanionBlockEntities.ROPE_BRIDGE_POST, RopeBridgePostBlockEntityRenderer::new);

		EntityRendererRegistry.register(CampanionEntities.WOODEN_SPEAR, SpearEntityRenderer::new);
		EntityRendererRegistry.register(CampanionEntities.STONE_SPEAR, SpearEntityRenderer::new);
		EntityRendererRegistry.register(CampanionEntities.IRON_SPEAR, SpearEntityRenderer::new);
		EntityRendererRegistry.register(CampanionEntities.GOLDEN_SPEAR, SpearEntityRenderer::new);
		EntityRendererRegistry.register(CampanionEntities.DIAMOND_SPEAR, SpearEntityRenderer::new);
		EntityRendererRegistry.register(CampanionEntities.NETHERITE_SPEAR, SpearEntityRenderer::new);
		EntityRendererRegistry.register(CampanionEntities.GRAPPLING_HOOK, GrapplingHookEntityRenderer::new);
		EntityRendererRegistry.register(CampanionEntities.LAWN_CHAIR, EmptyRenderer::new);
		EntityRendererRegistry.register(CampanionEntities.THROWING_STONE, ThrownItemRenderer::new);
		EntityRendererRegistry.register(CampanionEntities.FLARE, ThrownItemRenderer::new);

		CampanionClient.registerModelPredicateProviders();
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 0 ? (0xFF000000 | CampanionClient.sleepingBagColour(stack)) : -1, CampanionItems.SLEEPING_BAG);
		CampanionClient.registerClientPacketHandlers();

		WorldRenderEvents.AFTER_TRANSLUCENT.register(TentPreviewRenderer::render);
		ClientTickEvents.END_CLIENT_TICK.register(e -> CampanionKeybinds.onClientTick());
	}

	private static void registerRenderLayers() {
		BlockRenderLayerMap.INSTANCE.putBlock(CampanionBlocks.ROPE_LADDER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(CampanionBlocks.LEATHER_TANNER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(CampanionBlocks.FLARE_BLOCK, RenderType.cutout());
	}
}

package com.terraformersmc.campanion;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.terraformersmc.campanion.advancement.criterion.CampanionCriteria;
import com.terraformersmc.campanion.block.CampanionBlocks;
import com.terraformersmc.campanion.config.CampanionConfigManager;
import com.terraformersmc.campanion.item.CampanionItems;
import com.terraformersmc.campanion.network.CampanionNetwork;
import com.terraformersmc.campanion.tag.CampanionBlockTags;
import com.terraformersmc.campanion.tag.CampanionItemTags;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Campanion {

	public static final String MOD_ID = "campanion";
	public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
	public static final Logger LOG = LoggerFactory.getLogger("Campanion");

	public static final ResourceKey<CreativeModeTab> TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, id("items"));

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	public static void init() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB, FabricItemGroup.builder()
				.title(Component.translatable("itemGroup." + MOD_ID + ".items"))
				.icon(() -> new ItemStack(CampanionItems.SMORE))
				.displayItems((parameters, output) -> CampanionBlocks.getItemBlocks().values().forEach(output::accept))
				.build());

		CampanionConfigManager.initializeConfig();

		CampanionCriteria.loadClass();

		CampanionNetwork.register();

		CampanionBlockTags.load();
		CampanionItemTags.load();
	}

	public static void registerDispenserBehavior() {
		// The items implement ProjectileItem to describe the projectile they dispense.
		DispenserBlock.registerProjectileBehavior(CampanionItems.SKIPPING_STONE);
		DispenserBlock.registerProjectileBehavior(CampanionItems.FLARE);
	}
}

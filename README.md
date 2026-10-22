# Campanion — Fabric 1.21.1 port (UNTESTED, never compiled)

Port of TerraformersMC's Campanion (LGPL-3.0, https://github.com/TerraformersMC/Campanion, branch `1.19`)
from Forge 1.19.2 to **Fabric 1.21.1**. Original authors: Wyn Price, NeusFear, Prospector, TerraformersMC and contributors.
If you share this, keep LICENSE.md and make this source available (LGPL-3.0).

## Build
Requirements: Java 21. Then:

    ./gradlew build

The jar is in `build/libs/`. Put it in a Fabric 1.21.1 `mods` folder together with Fabric API.
If Gradle can't resolve Fabric API `0.115.0+1.21.1`, change `fabric_version` in `gradle.properties` to a current 1.21.1 build.

## IMPORTANT
This port was written without access to a compiler or the game. Expect compile errors on the first build:
send them back and they get fixed in rounds. APIs I was least sure of:
- `Model.renderToBuffer(..., int color)` and `ModelPart.render(..., color)` signatures
- `AbstractArrow` constructors / `getDefaultPickupItem`, `EnchantmentHelper.doPostAttackEffectsWithItemSource` (SpearEntity)
- `Entity.canChangeDimensions(Level, Level)`, `Item.getUseDuration(ItemStack)`
- `BlockEntityType.Builder.of`, `Minecraft.getTimer().getGameTimeDeltaPartialTick`
- Fabric `ModelLoadingPlugin` id matching for rope bridge models, `MaterialFinder.blendMode`
- `ClientLevel` constructor used by `FakeWorld` (tent item preview)
- `UnbakedModel.bake/resolveParents` signatures

## Known behaviour differences
- Enchantments are data-driven in 1.21: spears are added to trident/crossbow/durability/vanishing enchantable tags
  (so loyalty/channeling/etc. may also appear on spears).
- Sleeping bag dye uses the vanilla dyed-colour component (`minecraft:dyeable` tag).
- Campfire "dye" recipe now yields charcoal (1.21 recipes cannot output air).
- Coloured campfire smoke uses the dye's firework colour.
- Item NBT is stored in the custom-data component; old 1.19.2 worlds/items are not migrated except backpack data from players.
- Lawn chair seat offset (`passengerAttachments(0.5F)`) is a guess.
- Removed: Forge/NeoForge code, Trinkets/Nourish data is kept but unused.

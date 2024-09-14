package archives.tater.necromancer

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.ItemModelGenerator
import net.minecraft.data.client.Model
import net.minecraft.util.Identifier
import java.util.*

object NecromancerDataGenerator : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		fabricDataGenerator.createPack().apply {
			addProvider(::ModelGenerator)
		}
	}

	class ModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
		override fun generateBlockStateModels(blockStateModelGenerator: BlockStateModelGenerator?) {

		}

		override fun generateItemModels(itemModelGenerator: ItemModelGenerator) {
			itemModelGenerator.register(Necromancer.NECROMANCER_SPAWN_EGG, Model(Optional.of(Identifier("item/template_spawn_egg")), Optional.empty()))
		}
	}
}

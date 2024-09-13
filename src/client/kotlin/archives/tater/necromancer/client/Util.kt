package archives.tater.necromancer.client

import net.minecraft.client.model.ModelPartBuilder

fun ModelPartBuilder(init: ModelPartBuilder.() -> Unit) = ModelPartBuilder.create().apply(init)

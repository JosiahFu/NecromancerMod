package archives.tater.necromancer.lib.tag

import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

abstract class TagCollection<T>(private val namespace: String, private val registryKey: RegistryKey<Registry<T>>) {
    protected fun tag(id: Identifier): TagKey<T> = TagKey.of(registryKey, id)
    protected fun tag(path: String): TagKey<T> = tag(Identifier(namespace, path))
}

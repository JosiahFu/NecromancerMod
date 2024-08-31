package archives.tater.necromancer.tag

import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

abstract class TagCollection<T>(private val registryKey: RegistryKey<Registry<T>>) {
    protected fun tag(id: Identifier): TagKey<T> = TagKey.of(registryKey, id)
}

abstract class NSTagCollection<T>(private val namespace: String, registryKey: RegistryKey<Registry<T>>): TagCollection<T>(registryKey) {
    protected fun tag(path: String): TagKey<T> = tag(Identifier(namespace, path))
}

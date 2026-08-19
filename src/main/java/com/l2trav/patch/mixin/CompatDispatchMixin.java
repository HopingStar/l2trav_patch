package com.l2trav.patch.mixin;

import dev.xkmc.l2weaponry.compat.CompatDispatch;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 修复 l2weaponry 3.1.2 与 IceAndFireCE 的兼容问题：
 * CompatDispatch.register() 里 if (ModList.isLoaded("iceandfire")) new DragonCompat()
 * 会让 DragonCompat 静态初始化崩溃（缺少 IafToolMaterials 类）。
 * 这里让 isLoaded("iceandfire") 恒返回 false，跳过冰火联动。
 */
@Mixin(CompatDispatch.class)
public abstract class CompatDispatchMixin {

    @Redirect(method = "register",
              at = @At(value = "INVOKE", target = "Lnet/neoforged/fml/ModList;isLoaded(Ljava/lang/String;)Z"))
    private static boolean skipIceAndFire(ModList instance, String modId) {
        if ("iceandfire".equals(modId)) return false;
        return instance.isLoaded(modId);
    }
}

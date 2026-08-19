package com.l2trav.patch.mixin;

import com.gametechbc.traveloptics.loot.TOLootModifiers;
import com.gametechbc.traveloptics.loot.UniversalLootModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Supplier;

/**
 * 修复 traveloptics 4.4.0.1 重复注册 bug：
 * TOLootModifiers.<clinit> 里 "universal_loot" 误用了 KeyLootModifier.CODEC
 * （应为 UniversalLootModifier.CODEC），导致同一 CODEC 实例注册两次 -> duplicate value。
 * 这里把第 2 个 DeferredRegister.register 调用的 Supplier 参数改回 UniversalLootModifier.CODEC。
 */
@Mixin(TOLootModifiers.class)
public abstract class TOLootModifiersMixin {

    @ModifyArg(method = "<clinit>",
               at = @At(value = "INVOKE", ordinal = 1,
                        target = "Lnet/neoforged/neoforge/registries/DeferredRegister;register(Ljava/lang/String;Ljava/util/function/Supplier;)Lnet/neoforged/neoforge/registries/DeferredHolder;"),
               index = 1)
    private static Supplier<?> fixUniversalLoot(Supplier<?> original) {
        return UniversalLootModifier.CODEC;
    }
}

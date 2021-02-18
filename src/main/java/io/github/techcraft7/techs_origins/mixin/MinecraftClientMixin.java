package io.github.techcraft7.techs_origins.mixin;

import io.github.techcraft7.techs_origins.TechsOrigins;
import io.github.techcraft7.techs_origins.client.MutationDataClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@Inject(at = @At("HEAD"), method = "startIntegratedServer(Ljava/lang/String;)V")
	void startIntegratedServer(String worldName, CallbackInfo ci) {
		TechsOrigins.LOGGER.info("Resetting player mutation states!");
		MutationDataClient.resetPlayerData();
	}

	@Inject(at = @At("HEAD"), method = "method_29607")
	void startIntegratedServer2(String worldName, LevelInfo levelInfo, DynamicRegistryManager.Impl registryTracker, GeneratorOptions generatorOptions, CallbackInfo ci) {
		TechsOrigins.LOGGER.info("Resetting player mutation states!");
		MutationDataClient.resetPlayerData();
	}
}

package io.github.techcraft7.techs_origins.mixin;

import io.github.techcraft7.techs_origins.core.MutationDataServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.*;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Inject(at = @At("HEAD"), method = "startServer")
	private static void startServer(Function<Thread, MinecraftServer> serverFactory, CallbackInfoReturnable<MinecraftServer> cir) {
		MutationDataServer.resetData();
	}

}

package io.github.techcraft7.techs_origins.mixin;

import io.github.techcraft7.techs_origins.core.MutationDataServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.*;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

	@Shadow private PlayerManager playerManager;

	@Inject(at = @At("HEAD"), method = "startServer")
	private static void startServer(Function<Thread, MinecraftServer> serverFactory, CallbackInfoReturnable<MinecraftServer> cir) {
		MutationDataServer.resetData();
	}

	@Inject(at = @At("TAIL"), method = "method_16208")
	private void method_16208(CallbackInfo ci) {
		if (MutationDataServer.NEEDS_SYNC.get()) {
			this.playerManager.getPlayerList().forEach(MutationDataServer::sendData);
			MutationDataServer.NEEDS_SYNC.set(false);
		}
	}

}

package io.github.techcraft7.techs_origins.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class CommandRegistererMixin {

	@Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

	@Inject(at = @At("TAIL"), method = "<init>")
	public void init(CommandManager.RegistrationEnvironment environment, CallbackInfo ci) {
	}

}

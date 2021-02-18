package io.github.techcraft7.techs_origins.mixin;

import io.github.techcraft7.techs_origins.client.MutationDataClient;
import io.github.techcraft7.techs_origins.core.PlayerMutationData;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.*;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {

	@Shadow
	@Final
	private ResourceManager resourceContainer;

	@Shadow
	public abstract void registerTexture(Identifier identifier, AbstractTexture abstractTexture);

	@Inject(at = @At("HEAD"), method = "getTexture", cancellable = true)
	public void getTexture(Identifier id, CallbackInfoReturnable<AbstractTexture> cir) {
		UUID uuid = PlayerMutationData.getUUIDFromPartialMutation(id);
		if (uuid == null) {
			return;
		}

		AbstractTexture tex = getTexture(uuid);

		if (tex == null) {
			return;
		}

		cir.setReturnValue(tex);
		cir.cancel();
	}

	// Called by registerTexture, calls abstractTexture#load(ResourceManager)
	@Inject(at = @At("HEAD"), method = "method_24303", cancellable = true)
	public void method_24303(Identifier id, AbstractTexture abstractTexture, CallbackInfoReturnable<AbstractTexture> cir) {
		UUID uuid = PlayerMutationData.getUUIDFromPartialMutation(id);
		if (uuid == null) {
			return;
		}
		AbstractTexture tex = getTexture(uuid);

		if (tex == null) {
			return;
		}

		try {
			tex.load(this.resourceContainer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		cir.setReturnValue(tex);
		cir.cancel();
	}

	private AbstractTexture getTexture(UUID uuid) {
		AbstractTexture tex;

		if (MutationDataClient.isTextureCached(uuid)) {
			tex = MutationDataClient.getCachedTexture(uuid);
		} else {
			tex = MutationDataClient.createAndCacheTexture(uuid);
			this.registerTexture(PlayerMutationData.createPartialID(uuid), tex);
		}
		return tex;
	}
}

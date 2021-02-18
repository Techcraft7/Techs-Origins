package io.github.techcraft7.techs_origins.mixin;

import io.github.techcraft7.techs_origins.client.MutationDataClient;
import io.github.techcraft7.techs_origins.core.PlayerMutationData;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(TextureManager.class)
public class BindTextureInnerMixin {


	@Inject(at = @At("HEAD"), method = "bindTextureInner", cancellable = true)
	public void bindTextureInner(Identifier id, CallbackInfo ci) {
		TextureManager tm = (TextureManager)(Object)this;
		UUID uuid = PlayerMutationData.getUUIDFromPartialMutation(id);
		if (uuid == null) {
			return;
		}
		AbstractTexture tex;

		if (MutationDataClient.isTextureCached(uuid)) {
			tex = MutationDataClient.getCachedTexture(uuid);
		} else {
			tex = MutationDataClient.createAndCacheTexture(uuid);
		}

		if (tex != null) {
			tex.bindTexture();
			ci.cancel();
		}

	}

}

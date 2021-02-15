package io.github.techcraft7.techs_origins.mixin;

import io.github.techcraft7.techs_origins.TechsOrigins;
import io.github.techcraft7.techs_origins.core.MutationDataClient;
import io.github.techcraft7.techs_origins.core.PlayerMutationData;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.*;

@Mixin(TextureManager.class)
public class TextureManagerMixin {

	private static Field textures = null;

	static {
		TechsOrigins.LOGGER.info("Searching for TextureManager.textures!");
		try {
			textures = TextureManager.class.getDeclaredField("textures");
			TechsOrigins.LOGGER.info("SUCCESS!");
		} catch (Throwable t) {
			TechsOrigins.LOGGER.info("Error", t);

		}
	}

	@SuppressWarnings("unchecked")
	@Inject(at = @At("HEAD"), method = "getTexture", cancellable = true)
	public void getTexture(Identifier id, CallbackInfoReturnable<AbstractTexture> cir) {
		if (textures == null) {
			return;
		}
		TextureManager tm = (TextureManager)(Object)this;
		UUID uuid = PlayerMutationData.getUUIDFromPartialMutation(id);
		if (uuid == null) {
			return;
		}
		if (MutationDataClient.isTextureCached(uuid)) {
			cir.setReturnValue(MutationDataClient.getCachedTexture(uuid));
		} else {
			AbstractTexture tex = MutationDataClient.createAndCacheTexture(uuid);
			((Map<Identifier, AbstractTexture>)textures.get(tm)).put(tex);
			cir.setReturnValue(tex);
		}
		cir.cancel();
	}

}

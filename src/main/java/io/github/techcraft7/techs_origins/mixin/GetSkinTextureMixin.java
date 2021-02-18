package io.github.techcraft7.techs_origins.mixin;

import io.github.techcraft7.techs_origins.client.MutationDataClient;
import io.github.techcraft7.techs_origins.core.PlayerMutationData;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class GetSkinTextureMixin {

	@Inject(at = @At("HEAD"), method = "getTexture", cancellable = true)
	public void getTexture(AbstractClientPlayerEntity player, CallbackInfoReturnable<Identifier> cir) {
		PlayerEntityRenderer renderer = (PlayerEntityRenderer)(Object)this;
		PlayerMutationData mutationData = MutationDataClient.getPlayerData(player);
		boolean slim = player.getModel().equalsIgnoreCase("slim");
		if (mutationData != null) {
			Identifier texture = mutationData.getTexture(player);
			if (texture != null) {
				cir.setReturnValue(texture);
			}
		}
	}

}

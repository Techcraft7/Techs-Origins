package io.github.techcraft7.techs_origins.mixin;

import io.github.techcraft7.techs_origins.init.TOPowers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEndCrystalBeamRendererMixin {

	@Inject(at = @At("TAIL"), method = "render")
	public void render(AbstractClientPlayerEntity player, float f, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		if (!TOPowers.CRYSTAL_REGEN.isActive(player)) {
			return;
		}
		// In inventory, don't render
		if (MinecraftClient.getInstance().currentScreen != null) {
			return;
		}
		// Get nearby end crystals in 32 block radius
		List<EndCrystalEntity> entityList = player.getEntityWorld()
			.getEntitiesByType(EntityType.END_CRYSTAL, player.getBoundingBox().expand(32), e -> true);
		// Sort by distance
		entityList.sort(Comparator.comparingDouble(e -> e.getPos().distanceTo(player.getPos())));
		// Render beams for the 3 closest crystals
		entityList.stream().limit(3).forEach(crystal -> {
			if (crystal == null) {
				return;
			}
			matrixStack.push();
			matrixStack.translate(0, -1, 0);
			float dx = (float)(crystal.getPos().x - player.getPos().x);
			float dy = (float)(crystal.getPos().y - player.getPos().y);
			float dz = (float)(crystal.getPos().z - player.getPos().z);
			float offset = EndCrystalEntityRenderer.getYOffset(crystal, partialTicks);
			EnderDragonEntityRenderer.renderCrystalBeam(dx,
				dy + offset + 1,
				dz,
				f,
				player.age,
				matrixStack,
				vertexConsumerProvider,
				i
			);
			matrixStack.pop();
		});
	}
}


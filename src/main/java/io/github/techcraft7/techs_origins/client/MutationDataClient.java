package io.github.techcraft7.techs_origins.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.techcraft7.techs_origins.TechsOrigins;
import io.github.techcraft7.techs_origins.core.PlayerMutationData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.*;

@Environment(EnvType.CLIENT)
public class MutationDataClient {

	private static final Map<UUID, PlayerMutationData> PLAYER_DATA = new HashMap<>();
	private static final Map<UUID, MutationTexture> TEXTURE_CACHE = new HashMap<>();
	private static Field textures = null;

	static {
		try {
			textures = TextureManager.class.getDeclaredField("textures");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public static void resetPlayerData() {
		PLAYER_DATA.clear();
		try {
			TEXTURE_CACHE.forEach((key, value) -> destroyTexture(value));
		} catch (Throwable t) {
			TechsOrigins.LOGGER.debug("Error resetting TextureManager textures!", t);
		}
		TEXTURE_CACHE.clear();
	}

	private static void destroyTexture(MutationTexture value) {
		RenderSystem.recordRenderCall(() -> {
			TechsOrigins.LOGGER.debug("Destroying texture: " + value.getID());
			try {
				MinecraftClient.getInstance().getTextureManager().destroyTexture(value.getID());
			} catch (Throwable ignored) {
				return;
			}
			try {
				textures.setAccessible(true);
				//noinspection unchecked
				((Map<Identifier, AbstractTexture>)textures.get(MinecraftClient.getInstance().getTextureManager())).remove(value.getID());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}

	public static PlayerMutationData getPlayerData(UUID uuid) {
		return PLAYER_DATA.getOrDefault(uuid, null);
	}

	public static PlayerMutationData getPlayerData(PlayerEntity player) {
		if (player == null) {
			return null;
		}
		return getPlayerData(player.getUuid());
	}

	public static boolean isTextureCached(UUID uuid) {
		return TEXTURE_CACHE.containsKey(uuid);
	}

	public static MutationTexture getCachedTexture(UUID uuid) {
		return TEXTURE_CACHE.getOrDefault(uuid, null);
	}

	public static MutationTexture createAndCacheTexture(UUID uuid) {
		if (isTextureCached(uuid)) {
			if (!getCachedTexture(uuid).getPlayerData().equals(getPlayerData(uuid))) {
				destroyTexture(getCachedTexture(uuid));
				return TEXTURE_CACHE.put(uuid, new MutationTexture(uuid));
			}
			return getCachedTexture(uuid);
		}
		return TEXTURE_CACHE.put(uuid, new MutationTexture(uuid));
	}

	public static void updateMultipleStates(Map<UUID, PlayerMutationData> map) {
		Objects.requireNonNull(map);
		map.forEach((uuid, data) -> {
			if (getPlayerData(uuid) != null) {
				// If mutation state updated, then the texture is now invalid
				if (getPlayerData(uuid).getB() != data.getB()) {
					if (TEXTURE_CACHE.containsKey(uuid)) {
						try {
							destroyTexture(TEXTURE_CACHE.remove(uuid));
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				}
			}
			PLAYER_DATA.put(uuid, data);
		});
	}
}

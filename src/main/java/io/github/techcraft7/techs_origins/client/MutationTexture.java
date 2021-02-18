package io.github.techcraft7.techs_origins.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.techcraft7.techs_origins.TechsOrigins;
import io.github.techcraft7.techs_origins.client.util.SkinDownloader;
import io.github.techcraft7.techs_origins.core.*;
import io.github.techcraft7.techs_origins.util.Tuple;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.lang.reflect.Method;
import java.util.*;

@Environment(EnvType.CLIENT)
public class MutationTexture extends ResourceTexture {

	public static final Identifier PARTIAL_1 = TechsOrigins.identifier("textures/skins/partial_1.png");
	public static final Identifier PARTIAL_2 = TechsOrigins.identifier("textures/skins/partial_2.png");
	public static final Identifier PARTIAL_3 = TechsOrigins.identifier("textures/skins/partial_3.png");
	public static final Identifier PARTIAL_4 = TechsOrigins.identifier("textures/skins/partial_4.png");
	public static final Identifier PARTIAL_1_SLIM = TechsOrigins.identifier("textures/skins/partial_1_slim.png");
	public static final Identifier PARTIAL_2_SLIM = TechsOrigins.identifier("textures/skins/partial_2_slim.png");
	public static final Identifier PARTIAL_3_SLIM = TechsOrigins.identifier("textures/skins/partial_3_slim.png");
	public static final Identifier PARTIAL_4_SLIM = TechsOrigins.identifier("textures/skins/partial_4_slim.png");

	private static final Map<Tuple<MutationState, Boolean>, NativeImage> MASKS = new HashMap<>();
	private static final Map<Tuple<MutationType, Boolean>, NativeImage> SKINS = new HashMap<>();

	private static Method loadTextureData;

	static {

		TechsOrigins.LOGGER.info("Searching for ResourceTexture#loadTextureData...");
		try {
			loadTextureData = ResourceTexture.class.getDeclaredMethod("loadTextureData", ResourceManager.class);
			TechsOrigins.LOGGER.info("Found ResourceTexture#loadTextureData!");
		} catch (Throwable t) {
			TechsOrigins.LOGGER.error("Failed to find ResourceTexture#loadTextureData!", t);
		}

		TechsOrigins.LOGGER.info("Building mask map...");
		MASKS.put(new Tuple<>(MutationState.PARTIAL_1, false), loadResourceTexture(PARTIAL_1));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_2, false), loadResourceTexture(PARTIAL_2));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_3, false), loadResourceTexture(PARTIAL_3));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_4, false), loadResourceTexture(PARTIAL_4));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_1, true), loadResourceTexture(PARTIAL_1_SLIM));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_2, true), loadResourceTexture(PARTIAL_2_SLIM));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_3, true), loadResourceTexture(PARTIAL_3_SLIM));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_4, true), loadResourceTexture(PARTIAL_4_SLIM));
		TechsOrigins.LOGGER.info("Done!");

		TechsOrigins.LOGGER.info("Building skin map...");
		for (MutationType type : MutationType.values()) {
			TechsOrigins.LOGGER.debug(String.format("Loading texture: %s_slim.png", type.name().toLowerCase()));
			SKINS.put(new Tuple<>(type, false), loadResourceTexture(type.getSkin(false)));
			TechsOrigins.LOGGER.debug(String.format("Loading texture: %s.png", type.name().toLowerCase()));
			SKINS.put(new Tuple<>(type, true), loadResourceTexture(type.getSkin(true)));
		}
		TechsOrigins.LOGGER.info("Done!");
	}

	private final NativeImage image;
	private final PlayerMutationData playerData;
	private boolean uploaded = false;

	public MutationTexture(UUID uuid) {
		super(PlayerMutationData.createPartialID(uuid));
		playerData = MutationDataClient.getPlayerData(uuid);
		// Create a profile
		GameProfile profile = new GameProfile(uuid, "placeholder");
		// Fill textures
		MinecraftClient.getInstance().getSessionService().fillProfileProperties(profile, true);
		// Get textures
		MinecraftProfileTexture mpt = MinecraftClient.getInstance()
			.getSessionService()
			.getTextures(profile, true)
			.getOrDefault(MinecraftProfileTexture.Type.SKIN, null);
		// If the texture getting fails, use steve / alex texture
		if (mpt == null) {
			image = maskTexture(loadResourceTexture(DefaultSkinHelper.getTexture(uuid)), playerData);
			return;
		}
		// Otherwise load the skin
		Identifier id = MinecraftClient.getInstance().getSkinProvider().loadSkin(mpt,
			MinecraftProfileTexture.Type.SKIN
		);
		// Create mutated texture
		image = maskTexture(loadPlayerSkinTexture(id), playerData);
		// Throw error, this should not happen but just in case :)
		if (image == null) {
			throw new IllegalStateException("Failed to create MutationTexture!");
		}
	}

	public static NativeImage loadPlayerSkinTexture(Identifier id) {
		TechsOrigins.LOGGER.debug("Loading SKIN texture: " + id);
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(id);
		// Skin textures are stored as PlayerSkinTextures in the TextureManager
		if (tex instanceof PlayerSkinTexture) {
			try {
				// Download the skin
				// Though PlayerSkinTexture is a ResourceTexture, using
				// ResourceTexture#loadTextureData to extract a
				// NativeImage will give a Steve texture!
				return SkinDownloader.downloadSkin((PlayerSkinTexture)tex);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return null;
	}

	public static NativeImage loadResourceTexture(Identifier id) {
		TechsOrigins.LOGGER.debug("Loading texture: " + id);
		ResourceTexture tex = new ResourceTexture(id);
		if (loadTextureData == null) {
			throw new IllegalStateException("ResourceTexture#loadTextureData is missing!");
		}
		try {
			loadTextureData.setAccessible(true);
			return ((ResourceTexture.TextureData)loadTextureData.invoke(tex,
				MinecraftClient.getInstance().getResourceManager()
			)).getImage();
		} catch (Throwable t) {
			TechsOrigins.LOGGER.error("Error loading texture!", t);
		}
		return null;
	}

	public static NativeImage maskTexture(NativeImage playerSkin, PlayerMutationData data) {
		if (playerSkin == null) {
			return null;
		}

		MutationType type = data.getA();
		MutationState state = data.getB();
		boolean slim = data.isSlim();

		// Don't mask if the state is null, NONE, or FULL (This should not happen)
		if (state == null || state == MutationState.NONE || state == MutationState.FULL) {
			return playerSkin;
		}
		// Get mask
		NativeImage mask = MASKS.getOrDefault(new Tuple<>(state, slim), null);
		// Null check just in case
		if (mask == null) {
			return playerSkin;
		}

		// Some skins don't have a second layer, so they may be 64x32
		int width = Math.min(playerSkin.getWidth(), mask.getWidth());
		int height = Math.min(playerSkin.getHeight(), mask.getHeight());

		// New texture to write to
		NativeImage newTex = new NativeImage(width, height, false);

		// Mutated skin
		NativeImage mutatedSkin = SKINS.get(new Tuple<>(type, slim));

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				// Black = player skin
				// White = mutated skin
				boolean isWhite = Integer.compareUnsigned(mask.getPixelOpacity(x, y), 0) > 0 && Integer.compareUnsigned(mask.getPixelColor(x, y),
					0xFF000000
				) > 0;
				int pixel = isWhite ? mutatedSkin.getPixelColor(x, y) : playerSkin.getPixelColor(x, y);
				newTex.setPixelColor(x, y, pixel);
			}
		}

		return newTex;
	}

	public PlayerMutationData getPlayerData() {
		return playerData;
	}

	@Override
	public void load(ResourceManager manager) {
		if (uploaded) {
			return; // Prevent crash
		}
		// Stolen from ResourceTexture
		if (!RenderSystem.isOnRenderThreadOrInit()) {
			RenderSystem.recordRenderCall(() -> this.upload(image));
		} else {
			this.upload(image);
		}
	}

	// Also stolen from ResourceTexture
	private void upload(NativeImage nativeImage) {
		uploaded = true;
		TextureUtil.allocate(this.getGlId(), 0, nativeImage.getWidth(), nativeImage.getHeight());
		nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), false, false, false, true);
	}

	public Identifier getID() {
		return this.location;
	}
}

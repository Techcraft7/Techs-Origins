package io.github.techcraft7.techs_origins.client;

import io.github.techcraft7.techs_origins.TechsOrigins;
import io.github.techcraft7.techs_origins.core.MutationState;
import io.github.techcraft7.techs_origins.core.MutationType;
import io.github.techcraft7.techs_origins.core.PlayerMutationData;
import io.github.techcraft7.techs_origins.util.Tuple;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
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
		MASKS.put(new Tuple<>(MutationState.PARTIAL_1, false), loadTexture(PARTIAL_1));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_2, false), loadTexture(PARTIAL_2));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_3, false), loadTexture(PARTIAL_3));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_4, false), loadTexture(PARTIAL_4));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_1, true), loadTexture(PARTIAL_1_SLIM));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_2, true), loadTexture(PARTIAL_2_SLIM));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_3, true), loadTexture(PARTIAL_3_SLIM));
		MASKS.put(new Tuple<>(MutationState.PARTIAL_4, true), loadTexture(PARTIAL_4_SLIM));
		TechsOrigins.LOGGER.info("Done!");

		TechsOrigins.LOGGER.info("Building skin map...");
		for (MutationType type : MutationType.values()) {
			SKINS.put(new Tuple<>(type, true), loadTexture(type.getSkin(true)));
			SKINS.put(new Tuple<>(type, false), loadTexture(type.getSkin(false)));
		}
		TechsOrigins.LOGGER.info("Done!");
	}


	public MutationTexture(UUID uuid) {
		super(PlayerMutationData.createPartialID(uuid));
	}

	private static NativeImage loadTexture(Identifier id) {
		TechsOrigins.LOGGER.info("Loading texture: " + id);
		ResourceTexture tex = new ResourceTexture(id);
		if (loadTextureData == null) {
			throw new IllegalStateException("ResourceTexture#loadTextureData is missing!");
		}
		try {
			loadTextureData.setAccessible(true);
			return ((ResourceTexture.TextureData)loadTextureData.invoke(
				tex,
				MinecraftClient.getInstance()
					.getResourceManager()
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

		if (state == null || state == MutationState.NONE || state == MutationState.FULL) {
			return playerSkin;
		}
		NativeImage mask = MASKS.getOrDefault(new Tuple<>(state, slim), null);
		if (mask == null) {
			return playerSkin;
		}

		int width = Math.min(playerSkin.getWidth(), mask.getWidth());
		int height = Math.min(playerSkin.getHeight(), mask.getHeight());

		NativeImage newTex = new NativeImage(width, height, false);

		NativeImage mutatedSkin = SKINS.get(new Tuple<>(type, slim));

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				// BLACK PIXEL
				// WHITE PIXEL
				newTex.setPixelColor(
					x,
					y,
					mask.getPixelColor(x, y) > 0x7F000000 ?
						playerSkin.getPixelColor(x, y) :
						mutatedSkin.getPixelColor(x, y)
				);
			}
		}

		return newTex;
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		System.out.println("what do i do?");
	}

	private void upload(NativeImage nativeImage, boolean blur, boolean clamp) {
		TextureUtil.allocate(this.getGlId(), 0, nativeImage.getWidth(), nativeImage.getHeight());
		nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), blur, clamp, false, true);
	}
}

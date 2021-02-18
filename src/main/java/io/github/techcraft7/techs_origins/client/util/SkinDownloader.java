package io.github.techcraft7.techs_origins.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

public class SkinDownloader {

	private static Method remapTexture;
	private static Field url, cacheFile, convertLegacy;

	static {
		// Acquire reflection objects
		try {
			url = PlayerSkinTexture.class.getDeclaredField("url");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		try {
			cacheFile = PlayerSkinTexture.class.getDeclaredField("cacheFile");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		try {
			convertLegacy = PlayerSkinTexture.class.getDeclaredField("convertLegacy");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		try {
			remapTexture = PlayerSkinTexture.class.getDeclaredMethod("remapTexture", NativeImage.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		if (url == null) {
			throw new IllegalStateException("Failed to find PlayerSkinTexture.url!");
		}
		if (cacheFile == null) {
			throw new IllegalStateException("Failed to find PlayerSkinTexture.cacheFile!");
		}
		if (convertLegacy == null) {
			throw new IllegalStateException("Failed to find PlayerSkinTexture.convertLegacy!");
		}
		if (remapTexture == null) {
			throw new IllegalStateException("Failed to find PlayerSkinTexture#remapTexture!");
		}
	}


	public static NativeImage downloadSkin(PlayerSkinTexture pst) throws IllegalAccessException {
		// Null check
		if (pst == null) {
			return null;
		}
		// Check if the skin is cached
		cacheFile.setAccessible(true);
		File _cacheFile = (File)cacheFile.get(pst);
		if (_cacheFile == null) {
			// If the skin is not cached, then download it
			url.setAccessible(true);
			String _url = (String)url.get(pst);
			HttpURLConnection httpURLConnection = null;
			try {
				httpURLConnection = (HttpURLConnection)(new URL(_url)).openConnection(MinecraftClient.getInstance()
					.getNetworkProxy());
				httpURLConnection.setDoInput(true);
				httpURLConnection.setDoOutput(false);
				httpURLConnection.connect();
				// Checks for HTTP 2xx status code
				if (httpURLConnection.getResponseCode() / 100 != 2) {
					return null;
				}
				// Get image
				return remap(NativeImage.read(httpURLConnection.getInputStream()), pst);
			} catch (Throwable t) {
				t.printStackTrace();
				return null;
			} finally { // Cleanup
				if (httpURLConnection != null) {
					httpURLConnection.disconnect();
				}
			}
		} else {
			// Read cache file as image
			try {
				return remap(NativeImage.read(new FileInputStream(_cacheFile)), pst);
			} catch (Throwable t) {
				return null;
			}
		}
	}

	private static NativeImage remap(NativeImage img, PlayerSkinTexture pst) {
		// Use the remapTexture method to convert legacy skin to new format
		if (remapTexture == null) {
			throw new IllegalStateException("PlayerSkinTexture#remapTexture is missing!");
		}
		remapTexture.setAccessible(true);
		try {
			return (NativeImage)remapTexture.invoke(pst, img);
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

}

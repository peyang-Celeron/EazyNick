package net.dev.eazynick.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;

public class MineSkinAPI {

	private final String URL_FORMAT = "https://api.mineskin.org/get/id/%s";
	
	public Collection<Property> getTextureProperties(String id) {
		ArrayList<Property> props = new ArrayList<>();
		
		try {
			HttpURLConnection textureConnection = (HttpURLConnection) new URL(String.format(URL_FORMAT, id)).openConnection();
			textureConnection.setRequestProperty("User-Agent", "JustixDevelopment/APIClient");
			textureConnection.setRequestMethod("GET");
			textureConnection.setReadTimeout(5000);
			
			JsonObject jsonObject = new JsonParser().parse(new BufferedReader(new InputStreamReader(textureConnection.getInputStream()))).getAsJsonObject();
			JsonObject texture = jsonObject.get("data").getAsJsonObject().get("texture").getAsJsonObject();
			
			props.add(new Property("textures", texture.get("value").getAsString(), texture.get("signature").getAsString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return props;
	}
}

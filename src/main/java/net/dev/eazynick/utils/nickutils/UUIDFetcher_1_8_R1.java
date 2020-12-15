package net.dev.eazynick.utils.nickutils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.*;
import com.mojang.util.UUIDTypeAdapter;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.NickNameFileUtils;
import net.dev.eazynick.utils.Utils;

public class UUIDFetcher_1_8_R1 {

	private Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

	private final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
	private final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";
	private Map<String, UUID> uuidCache = new HashMap<String, UUID>();
	private Map<UUID, String> nameCache = new HashMap<UUID, String>();

	private String name;
	private UUID id;

	public UUID getUUID(String name) {
		return getUUIDAt(name, System.currentTimeMillis());
	}

	public UUID getUUIDAt(String name, long timestamp) {
		EazyNick eazyNick = EazyNick.getInstance();
		Utils utils = eazyNick.getUtils();
		
		name = name.toLowerCase();

		if (uuidCache.containsKey(name))
			return uuidCache.get(name);

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, timestamp / 1000)).openConnection();
			connection.setReadTimeout(5000);

			UUIDFetcher_1_8_R1 data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher_1_8_R1.class);

			uuidCache.put(name, data.id);
			nameCache.put(data.id, data.name);

			return data.id;
		} catch (Exception e) {
			NickNameFileUtils nickNameFileUtils = eazyNick.getNickNameFileUtils();

			List<String> list = nickNameFileUtils.getConfig().getStringList("NickNames");
			ArrayList<String> toRemove = new ArrayList<>();
			final String finalName = name;

			list.stream().filter(s -> s.equalsIgnoreCase(finalName)).forEach(s -> toRemove.add(s));

			if (toRemove.size() >= 1) {
				toRemove.forEach(s -> {
					list.remove(s);
					utils.getNickNames().remove(s);
				});

				nickNameFileUtils.getConfig().set("NickNames", list);
				nickNameFileUtils.saveFile();

				utils.sendConsole("§cThere is no account with username §6" + name + " §cin the mojang database");
			}
		}

		return null;
	}

	public String getName(String name, UUID uuid) {
		if (nameCache.containsKey(uuid))
			return nameCache.get(uuid);

		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
			connection.setReadTimeout(5000);

			UUIDFetcher_1_8_R1[] nameHistory = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher_1_8_R1[].class);
			UUIDFetcher_1_8_R1 currentNameData = nameHistory[nameHistory.length - 1];
			uuidCache.put(currentNameData.name.toLowerCase(), uuid);
			nameCache.put(uuid, currentNameData.name);

			return currentNameData.name;
		} catch (Exception e) {
		}

		return name;
	}

}
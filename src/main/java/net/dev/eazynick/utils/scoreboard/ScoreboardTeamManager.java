package net.dev.eazynick.utils.scoreboard;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.utils.FileUtils;
import net.dev.eazynick.utils.ReflectUtils;

import me.clip.placeholderapi.PlaceholderAPI;

public class ScoreboardTeamManager {

	private EazyNick eazyNick;

	private Player p;
	private String teamName, prefix, suffix;
	private Object packet;
	
	public ScoreboardTeamManager(Player p, String prefix, String suffix, int sortID, String rank) {
		this.eazyNick = EazyNick.getInstance();
		this.p = p;
		this.prefix = prefix;
		this.suffix = suffix;
		this.teamName = sortID + rank + p.getUniqueId().toString().substring(0, 14);
		
		if(this.teamName.length() > 16)
			this.teamName = this.teamName.substring(0, 16);
		
		if(this.prefix == null)
			this.prefix = "";
		
		if(this.suffix == null)
			this.suffix = "";
		
		if(this.prefix.length() > 16)
			this.prefix = this.prefix.substring(0, 16);
		
		if(this.suffix.length() > 16)
			this.suffix = this.suffix.substring(0, 16);
	}
	
	public void destroyTeam() {
		ReflectUtils reflectUtils = eazyNick.getReflectUtils();
		
		try {
			packet = reflectUtils.getNMSClass("PacketPlayOutScoreboardTeam").getConstructor(new Class[0]).newInstance(new Object[0]);
			
			if(!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4"))) {
				if(eazyNick.getUtils().isNewVersion()) {
					try {
						reflectUtils.setField(packet, "a", teamName);
						reflectUtils.setField(packet, "b", getAsIChatBaseComponent(teamName));
						reflectUtils.setField(packet, "e", "ALWAYS");
						reflectUtils.setField(packet, "i", 1);
					} catch (Exception ex) {
						reflectUtils.setField(packet, "a", teamName);
						reflectUtils.setField(packet, "b", getAsIChatBaseComponent(teamName));
						reflectUtils.setField(packet, "e", "ALWAYS");
						reflectUtils.setField(packet, "j", 1);
					}
				} else {
					try {
						reflectUtils.setField(packet, "a", teamName);
						reflectUtils.setField(packet, "b", teamName);
						reflectUtils.setField(packet, "e", "ALWAYS");
						reflectUtils.setField(packet, "h", 1);
					} catch (Exception ex) {
						reflectUtils.setField(packet, "a", teamName);
						reflectUtils.setField(packet, "b", teamName);
						reflectUtils.setField(packet, "e", "ALWAYS");
						reflectUtils.setField(packet, "i", 1);
					}
				}
			} else {
				try {
					reflectUtils.setField(packet, "a", teamName);
					reflectUtils.setField(packet, "f", 1);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
			for(Player t : Bukkit.getOnlinePlayers())
				sendPacket(t, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createTeam() {
		FileUtils fileUtils = eazyNick.getFileUtils();
		ReflectUtils reflectUtils = eazyNick.getReflectUtils();
		
		try {
			for(Player t : Bukkit.getOnlinePlayers()) {
				packet = reflectUtils.getNMSClass("PacketPlayOutScoreboardTeam").getConstructor(new Class[0]).newInstance(new Object[0]);
				
				String prefixForPlayer = prefix;
				String suffixForPlayer = suffix;
				List<String> contents = Arrays.asList(p.getName());
				
				if(t.hasPermission("nick.bypass") && fileUtils.getConfig().getBoolean("EnableBypassPermission") && fileUtils.getConfig().getBoolean("BypassFormat.Show")) {
					contents = Arrays.asList(new NickManager(p).getRealName());
					prefixForPlayer = fileUtils.getConfigString(p, "BypassFormat.NameTagPrefix");
					suffixForPlayer = fileUtils.getConfigString(p, "BypassFormat.NameTagSuffix");
				}
				
				if(eazyNick.getUtils().placeholderAPIStatus()) {
					prefixForPlayer = PlaceholderAPI.setPlaceholders(p, prefixForPlayer);
					suffixForPlayer = PlaceholderAPI.setPlaceholders(p, suffixForPlayer);
				}
				
				if(!(eazyNick.getVersion().equalsIgnoreCase("1_7_R4"))) {
					if(eazyNick.getUtils().isNewVersion()) {
						try {
							reflectUtils.setField(packet, "a", teamName);
							reflectUtils.setField(packet, "b", getAsIChatBaseComponent(teamName));
							reflectUtils.setField(packet, "c", getAsIChatBaseComponent(prefixForPlayer));
							reflectUtils.setField(packet, "d", getAsIChatBaseComponent(suffixForPlayer));
							reflectUtils.setField(packet, "e", "ALWAYS");
							reflectUtils.setField(packet, "g", contents);
							reflectUtils.setField(packet, "i", 0);
						} catch (Exception ex) {
							reflectUtils.setField(packet, "a", teamName);
							reflectUtils.setField(packet, "b", getAsIChatBaseComponent(teamName));
							reflectUtils.setField(packet, "c", getAsIChatBaseComponent(prefixForPlayer));
							reflectUtils.setField(packet, "d", getAsIChatBaseComponent(suffixForPlayer));
							reflectUtils.setField(packet, "e", "ALWAYS");
							reflectUtils.setField(packet, "h", contents);
							reflectUtils.setField(packet, "j", 0);
						}
					} else {
						try {
							reflectUtils.setField(packet, "a", teamName);
							reflectUtils.setField(packet, "b", teamName);
							reflectUtils.setField(packet, "c", prefixForPlayer);
							reflectUtils.setField(packet, "d", suffixForPlayer);
							reflectUtils.setField(packet, "e", "ALWAYS");
							reflectUtils.setField(packet, "g", contents);
							reflectUtils.setField(packet, "h", 0);
						} catch (Exception ex) {
							reflectUtils.setField(packet, "a", teamName);
							reflectUtils.setField(packet, "b", teamName);
							reflectUtils.setField(packet, "c", prefixForPlayer);
							reflectUtils.setField(packet, "d", suffixForPlayer);
							reflectUtils.setField(packet, "e", "ALWAYS");
							reflectUtils.setField(packet, "h", contents);
							reflectUtils.setField(packet, "i", 0);
						}
					}
				} else {
					reflectUtils.setField(packet, "a", teamName);
					reflectUtils.setField(packet, "b", teamName);
					reflectUtils.setField(packet, "c", prefixForPlayer);
					reflectUtils.setField(packet, "d", suffixForPlayer);
					reflectUtils.setField(packet, "e", contents);
					reflectUtils.setField(packet, "f", 0);
					reflectUtils.setField(packet, "g", 0);
				}
			
				sendPacket(t, packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Object getAsIChatBaseComponent(String txt) {
		ReflectUtils reflectUtils = eazyNick.getReflectUtils();
		
		try {
			return reflectUtils.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(reflectUtils.getNMSClass("IChatBaseComponent"), "{\"text\":\"" + txt + "\"}");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void sendPacket(Player p, Object packet) {
		try {
			Object playerHandle = p.getClass().getMethod("getHandle", new Class[0]).invoke(p, new Object[0]);
			Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
			playerConnection.getClass().getMethod("sendPacket", new Class[] { eazyNick.getReflectUtils().getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
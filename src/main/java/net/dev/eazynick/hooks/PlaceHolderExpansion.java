package net.dev.eazynick.hooks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.api.NickManager;
import net.dev.eazynick.sql.MySQLNickManager;
import net.dev.eazynick.utils.Utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceHolderExpansion extends PlaceholderExpansion {
	
	private Plugin plugin;
	
	public PlaceHolderExpansion(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		EazyNick eazyNick = EazyNick.getInstance();
		MySQLNickManager mysqlNickManager = eazyNick.getMySQLNickManager();
		Utils utils = eazyNick.getUtils();
		
		if(p != null) {
			NickManager api = new NickManager(p);
			String displayName = utils.getPlayerNicknames().containsKey(p.getUniqueId()) ? utils.getPlayerNicknames().get(p.getUniqueId()) : p.getName();
			
			if(identifier.equals("is_nicked") || identifier.equals("is_disguised"))
				return String.valueOf(api.isNicked());
			
			if(identifier.equals("display_name"))
				return displayName;
			
			if(identifier.equals("global_name"))
				return ((mysqlNickManager != null) ? ((mysqlNickManager.isPlayerNicked(p.getUniqueId()) && !(api.isNicked())) ? mysqlNickManager.getNickName(p.getUniqueId()) : displayName) : displayName);
			
			if(identifier.equals("chat_prefix"))
				return api.getChatPrefix();
			
			if(identifier.equals("chat_suffix"))
				return api.getChatSuffix();
			
			if(identifier.equals("tab_prefix"))
				return api.getTabPrefix();
			
			if(identifier.equals("tab_suffix"))
				return api.getTabSuffix();
			
			if(identifier.equals("tag_prefix"))
				return api.getTagPrefix();
			
			if(identifier.equals("tag_suffix"))
				return api.getTagSuffix();
			
			if(identifier.equals("real_name"))
				return api.getRealName();
			
			if(identifier.equals("rank"))
				return api.getGroupName();
		}
		
		return null;
	}
	
	@Override
	public String getIdentifier() {
		return plugin.getDescription().getName();
	}
	
	@Override
	public boolean canRegister() {
		return true;
	}
	
	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}
	
	@Override
	public String getAuthor() {
		return plugin.getDescription().getAuthors().toString().replace("[", "").replace("]", "");
	}
	
}

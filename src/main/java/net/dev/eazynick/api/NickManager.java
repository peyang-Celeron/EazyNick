package net.dev.eazynick.api;

import org.bukkit.entity.*;

import java.util.*;

public class NickManager {
	public NickManager(Player p) {
	}
	
	public void setPlayerListName(String name) {
	}
	
	public void changeSkin(String skinName) {

	}
	
	public void setName(String nickName) {

	}
	
	private void performAuthMeLogin() {

	}

	public void nickPlayer(String nickName) {
		nickPlayer(nickName, nickName);
	}
	
	public void nickPlayer(String nickName, String skinName) {
	}
	
	public void unnickPlayer() {
	}
	
	public void unnickPlayerWithoutRemovingMySQL(boolean isQuitUnnick) {

	}
	
	public String getRealName() {
		return "";
	}
	
	public String getChatPrefix() {
		return "";
	}

	public void setChatPrefix(String chatPrefix) {

	}

	public String getChatSuffix() {
		return "";
	}

	public void setChatSuffix(String chatSuffix) {

	}

	public String getTabPrefix() {
		return "";
	}

	public void setTabPrefix(String tabPrefix) {

	}

	public String getTabSuffix() {
		return "";
	}

	public void setTabSuffix(String tabSuffix) {

	}
	
	public String getTagPrefix() {
		return "";
	}

	public void setTagPrefix(String tagPrefix) {
	}

	public String getTagSuffix() {
		return "";
	}

	public void setTagSuffix(String tagSuffix) { }

	public boolean isNicked() {
		return false;
	}
	
	public String getRandomStringFromList(ArrayList<String> list) {
		return "";
	}
	
	public String getRandomName() {
		return "";
	}
	
	public String getNickName() {
		return "";
	}
	
	public String getNickFormat() {
		return "";
	}
	
	public String getOldDisplayName() {
		return "";
	}
	
	public String getOldPlayerListName() {
		return "";
	}
	
	public String getGroupName() {
		return "";
	}
	
	public void setGroupName(String rank) {
	}
	
	public void unsetGroupName() {
	}
	
	public void updatePrefixSuffix(String tagPrefix, String tagSuffix, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix) {

	}
	
	public void updatePrefixSuffix(String tagPrefix, String tagSuffix, String chatPrefix, String chatSuffix, String tabPrefix, String tabSuffix, int sortID, String groupName) {

	}
	
	public void changeCloudNET(String prefix, String suffix) {

	}
	
	public void resetCloudNET() {

	}

}

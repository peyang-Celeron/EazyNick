package net.dev.eazynick.utils.anvilutils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.dev.eazynick.EazyNick;
import net.dev.eazynick.utils.ReflectUtils;

public class AnvilGUI {
	
	private EazyNick eazyNick;
	private Player p;
	public AnvilClickEventHandler handler;
	private HashMap<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();
	private Class<?> blockPositionClass, containerAnvil, entityHuman;
	private Inventory inv;
	private Listener listener;

	public AnvilGUI(final Player p, final AnvilClickEventHandler handler) {
		eazyNick = EazyNick.getInstance();
		ReflectUtils reflectUtils = eazyNick.getReflectUtils();
		
		blockPositionClass = reflectUtils.getNMSClass("BlockPosition");
		containerAnvil = reflectUtils.getNMSClass("ContainerAnvil");
		entityHuman = reflectUtils.getNMSClass("EntityHuman");
		
		this.p = p;
		this.handler = handler;

		this.listener = new Listener() {
			
			@EventHandler
			public void onInventoryClick(InventoryClickEvent e) {
				if (e.getWhoClicked() instanceof Player) {
					if (e.getInventory().equals(inv)) {
						e.setCancelled(true);

						ItemStack item = e.getCurrentItem();
						int slot = e.getRawSlot();
						String name = "";

						if (item != null) {
							if (item.hasItemMeta()) {
								ItemMeta meta = item.getItemMeta();

								if (meta.hasDisplayName())
									name = meta.getDisplayName();
							}
						}

						AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name);

						handler.onAnvilClick(clickEvent);

						if (clickEvent.getWillClose())
							e.getWhoClicked().closeInventory();

						if (clickEvent.getWillDestroy())
							destroy();
					}
				}
			}

			@EventHandler
			public void onInventoryClose(InventoryCloseEvent e) {
				if (e.getPlayer() instanceof Player) {
					Inventory inv = e.getInventory();
					
					p.setLevel(p.getLevel() - 1);
					
					if (inv.equals(AnvilGUI.this.inv)) {
						inv.clear();
						
						destroy();
					}
				}
			}

			@EventHandler
			public void onPlayerQuit(PlayerQuitEvent e) {
				if (e.getPlayer().equals(getPlayer())) {
					p.setLevel(p.getLevel() - 1);
					
					destroy();
				}
			}
			
		};

		Bukkit.getPluginManager().registerEvents(listener, eazyNick);
	}

	public Player getPlayer() {
		return p;
	}

	public void setSlot(AnvilSlot slot, ItemStack item) {
		items.put(slot, item);
	}

	public void open() throws IllegalAccessException, InvocationTargetException, InstantiationException {
		ReflectUtils reflectUtils = eazyNick.getReflectUtils();
		
		p.setLevel(p.getLevel() + 1);

		try {
			String version = eazyNick.getVersion();
			Class<?> playerInventoryClass = reflectUtils.getNMSClass("PlayerInventory"), worldClass = reflectUtils.getNMSClass("World");
			Object player = p.getClass().getMethod("getHandle").invoke(p), playerInventory = getPlayerField(p, "inventory"), world = getPlayerField(p, "world"), blockPosition = blockPositionClass.getConstructor(int.class, int.class, int.class).newInstance(0, 0, 0);
			int c = (int) invokeMethod("nextContainerCounter", player);
			Object container = (version.startsWith("1_16") || version.startsWith("1_15") || version.startsWith("1_14")) ? containerAnvil.getConstructor(int.class, playerInventoryClass, reflectUtils.getNMSClass("ContainerAccess")).newInstance(c, playerInventory, reflectUtils.getNMSClass("ContainerAccess").getMethod("at", worldClass, blockPositionClass).invoke(null, world, blockPosition)) : containerAnvil.getConstructor(playerInventoryClass, worldClass, blockPositionClass, entityHuman).newInstance(playerInventory, world, blockPosition, player);
			
			reflectUtils.getField(reflectUtils.getNMSClass("Container"), "checkReachable").set(container, false);

			Object bukkitView = invokeMethod("getBukkitView", container);
			inv = (Inventory) invokeMethod("getTopInventory", bukkitView);
			
			for (AnvilSlot slot : items.keySet())
				inv.setItem(slot.getSlot(), items.get(slot));

			Constructor<?> chatMessageConstructor = reflectUtils.getNMSClass("ChatMessage").getConstructor(String.class, Object[].class);
			Object playerConnection = getPlayerField(p, "playerConnection"), chatMessage = chatMessageConstructor.newInstance("Repairing", new Object[] {});
			Object packet = version.startsWith("1_16") ? reflectUtils.getNMSClass("PacketPlayOutOpenWindow").getConstructor(int.class, reflectUtils.getNMSClass("Containers"), reflectUtils.getNMSClass("IChatBaseComponent")).newInstance(c, reflectUtils.getNMSClass("Containers").getDeclaredField("ANVIL").get(null), chatMessage) : reflectUtils.getNMSClass("PacketPlayOutOpenWindow").getConstructor(int.class, String.class, reflectUtils.getNMSClass("IChatBaseComponent"), int.class).newInstance(c, "minecraft:anvil", chatMessage, 0);

			Method sendPacket = getMethod("sendPacket", playerConnection.getClass(), reflectUtils.getNMSClass("Packet"));
			sendPacket.invoke(playerConnection, packet);

			Field activeContainerField = reflectUtils.getField(entityHuman, "activeContainer");
			
			if (activeContainerField != null) {
				activeContainerField.set(player, container);

				reflectUtils.getField(reflectUtils.getNMSClass("Container"), "title").set(activeContainerField.get(player), chatMessageConstructor.newInstance("Type in some text", new Object[] {}));
				reflectUtils.getField(reflectUtils.getNMSClass("Container"), "windowId").set(activeContainerField.get(player), c);

				activeContainerField.get(player).getClass().getMethod("addSlotListener", reflectUtils.getNMSClass("ICrafting")).invoke(activeContainerField.get(player), player);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Method getMethod(String name, Class<?> clazz, Class<?>... args) {
		try {
			Method method = clazz.getMethod(name, args);
			
			return method;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private Object invokeMethod(String name, Object clazz) {
		try {
			return clazz.getClass().getMethod(name).invoke(clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	private Object getPlayerField(Player p, String name) {
		try {
			Object getHandle = p.getClass().getMethod("getHandle").invoke(p);
			
			return getHandle.getClass().getField(name).get(getHandle);
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
	}

	public void destroy() {
		p = null;
		handler = null;
		items = null;

		HandlerList.unregisterAll(listener);

		listener = null;
	}

	public enum AnvilSlot {
		
		INPUT_LEFT(0), INPUT_RIGHT(1), OUTPUT(2);

		private int slot;

		private AnvilSlot(int slot) {
			this.slot = slot;
		}

		public static AnvilSlot bySlot(int slot) {
			for (AnvilSlot anvilSlot : values()) {
				if (anvilSlot.getSlot() == slot)
					return anvilSlot;
			}

			return null;
		}

		public int getSlot() {
			return slot;
		}
		
	}

	public interface AnvilClickEventHandler {
		
		void onAnvilClick(AnvilClickEvent event);
		
	}

	public class AnvilClickEvent {
		
		private AnvilSlot slot;
		private String name;
		private boolean close = true;
		private boolean destroy = true;

		public AnvilClickEvent(AnvilSlot slot, String name) {
			this.slot = slot;
			this.name = name;
		}

		public AnvilSlot getSlot() {
			return slot;
		}

		public String getName() {
			return name;
		}

		public boolean getWillClose() {
			return close;
		}

		public void setWillClose(boolean close) {
			this.close = close;
		}

		public boolean getWillDestroy() {
			return destroy;
		}

		public void setWillDestroy(boolean destroy) {
			this.destroy = destroy;
		}
		
	}
	
}
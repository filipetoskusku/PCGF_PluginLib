/*
 *   Copyright (C) 2020 GeorgH93
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package at.pcgamingfreaks.Bukkit.Util;

import at.pcgamingfreaks.Bukkit.MCVersion;
import at.pcgamingfreaks.Bukkit.NMSReflection;
import at.pcgamingfreaks.Bukkit.NmsReflector;
import at.pcgamingfreaks.Bukkit.OBCReflection;
import at.pcgamingfreaks.Reflection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import static at.pcgamingfreaks.Bukkit.Util.Utils_Reflection.*;

@SuppressWarnings("ConstantConditions")
public class InventoryUtils_Reflection implements IInventoryUtils
{
	private static final Class<?> CLASS_CONTAINER = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? null : NmsReflector.INSTANCE.getNmsClass("Container");
	private static final Class<?> CLASS_CONTAINERS = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? null : NmsReflector.INSTANCE.getNmsClass("Containers");
	private static final Constructor<?> CONSTRUCTOR_CHAT_MESSAGE = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? null : Reflection.getConstructor(NmsReflector.INSTANCE.getNmsClass("ChatMessage"), String.class, Object[].class);
	private static final Constructor<?> CONSTRUCTOR_PACKET_PLAY_OUT_OPEN_WINDOW = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? null : Reflection.getConstructor(NmsReflector.INSTANCE.getNmsClass("PacketPlayOutOpenWindow"), int.class, CLASS_CONTAINERS, NMSReflection.getNMSClass("IChatBaseComponent"));
	private static final Field FIELD_ACTIVE_CONTAINER = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? null : Reflection.getFieldIncludeParents(ENTITY_PLAYER, "activeContainer");
	private static final Field FIELD_CONTAINER_WINDOW_ID = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? null : Reflection.getField(CLASS_CONTAINER, "windowId");
	private static final Method METHOD_ENTITY_PLAYER_UPDATE_INVENTORY = (MCVersion.isOlderThan(MCVersion.MC_1_14)) ? null : Reflection.getMethod(ENTITY_PLAYER, "updateInventory", CLASS_CONTAINER);
	private static final Class<?> NBT_TAG_COMPOUND_CLASS = NmsReflector.INSTANCE.getNmsClass("NBTTagCompound");
	private static final Method AS_NMS_COPY_METHOD = OBCReflection.getOBCMethod("inventory.CraftItemStack", "asNMSCopy", ItemStack.class);
	private static final Method SAVE_NMS_ITEM_STACK_METHOD = NmsReflector.INSTANCE.getNmsMethod("ItemStack", "save", NBT_TAG_COMPOUND_CLASS);

	@Override
	public void updateInventoryTitle(final @NotNull Player player, final @NotNull String newTitle)
	{
		if(MCVersion.isOlderThan(MCVersion.MC_1_14)) return;
		InventoryView view = player.getOpenInventory();
		Inventory topInv = view.getTopInventory();
		if(topInv.getType() == InventoryType.CRAFTING) return;
		try
		{
			Object entityPlayer = NmsReflector.getHandle(player);
			if(entityPlayer == null || entityPlayer.getClass() != ENTITY_PLAYER) return; // Not a real player
			Object title = CONSTRUCTOR_CHAT_MESSAGE.newInstance(newTitle, new Object[0]);
			Object activeContainer = FIELD_ACTIVE_CONTAINER.get(entityPlayer);
			Object windowId = FIELD_CONTAINER_WINDOW_ID.get(activeContainer);
			String type = topInv.getType().name();
			if(topInv.getType() == InventoryType.CHEST) type = "GENERIC_9X" + (topInv.getSize() / 9);
			else if(topInv.getType() == InventoryType.DISPENSER || topInv.getType() == InventoryType.DROPPER) type = "GENERIC_3X3";
			else if(topInv.getType() == InventoryType.BREWING) type = "BREWING_STAND";
			Object packet = CONSTRUCTOR_PACKET_PLAY_OUT_OPEN_WINDOW.newInstance(windowId, Reflection.getField(CLASS_CONTAINERS, type).get(null), title);
			SEND_PACKET.invoke(PLAYER_CONNECTION.get(entityPlayer), packet);
			METHOD_ENTITY_PLAYER_UPDATE_INVENTORY.invoke(entityPlayer, activeContainer);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String convertItemStackToJson(@NotNull ItemStack itemStack, @NotNull Logger logger)
	{
		try
		{
			return SAVE_NMS_ITEM_STACK_METHOD.invoke(AS_NMS_COPY_METHOD.invoke(null, itemStack), NBT_TAG_COMPOUND_CLASS.newInstance()).toString();
		}
		catch (Throwable t)
		{
			logger.log(Level.SEVERE, "Failed to serialize item stack to NMS item! Bukkit Version: " + Bukkit.getServer().getVersion() + "\n", t);
		}
		return "";
	}
}
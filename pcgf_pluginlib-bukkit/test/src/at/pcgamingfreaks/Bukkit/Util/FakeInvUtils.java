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

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class FakeInvUtils implements IInventoryUtils
{
	@Override
	public void updateInventoryTitle(@NotNull Player player, @NotNull String newTitle) {}

	@Override
	public String convertItemStackToJson(@NotNull ItemStack itemStack, @NotNull Logger logger)
	{
		return "{\"id\":\"" + itemStack.getType().name() + "\",\"Count\":\"" + itemStack.getAmount() + "\"}";
	}
}
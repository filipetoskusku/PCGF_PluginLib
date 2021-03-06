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

package at.pcgamingfreaks.Command;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class CommandExecutorWithSubCommands<SUB_COMMAND extends SubCommand>
{
	protected Map<String, SUB_COMMAND> subCommandMap = new HashMap<>();
	protected Collection<SUB_COMMAND> commands = new ArrayList<>();
	protected SUB_COMMAND defaultSubCommand = null;

	public void close()
	{
		for(SUB_COMMAND command : commands)
		{
			command.close();
		}
		commands.clear();
		subCommandMap.clear();
	}

	public void setDefaultSubCommand(SUB_COMMAND command)
	{
		defaultSubCommand = command;
	}

	public void registerSubCommand(@NotNull SUB_COMMAND command)
	{
		command.beforeRegister();
		commands.add(command);
		@SuppressWarnings("unchecked") @NotNull List<String> aliases = command.getAliases();
		for(String alias : aliases)
		{
			subCommandMap.put(alias, command);
		}
		command.registerSubCommands();
		command.afterRegister();
	}

	public void unRegisterSubCommand(@NotNull SUB_COMMAND command)
	{
		command.beforeUnregister();
		command.unRegisterSubCommands();
		commands.remove(command);
		@SuppressWarnings("unchecked") @NotNull List<String> aliases = command.getAliases();
		for(String alias : aliases)
		{
			subCommandMap.remove(alias);
		}
		command.afterUnRegister();
	}
}
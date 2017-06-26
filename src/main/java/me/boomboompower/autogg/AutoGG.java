/*
 *     Copyright (C) 2016 boomboompower
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.boomboompower.autogg;

import me.boomboompower.autogg.commands.AutoGGCommand;
import me.boomboompower.autogg.config.FileUtils;
import me.boomboompower.autogg.events.AutoGGEvents;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AutoGG.MODID, version = AutoGG.VERSION, acceptedMinecraftVersions="*")
public class AutoGG {

    public static final String MODID = "autogg";
    public static final String VERSION = "2.2.7";

    public static FileUtils fileUtils;
    public static boolean isOn = true;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        fileUtils = new FileUtils(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        fileUtils.loadConfig();

        ClientCommandHandler.instance.registerCommand(new AutoGGCommand());
        MinecraftForge.EVENT_BUS.register(new AutoGGEvents());
    }
}

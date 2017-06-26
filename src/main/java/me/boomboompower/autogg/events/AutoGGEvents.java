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

package me.boomboompower.autogg.events;

import me.boomboompower.autogg.AutoGG;
import me.boomboompower.autogg.utils.GlobalUtils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoGGEvents {

    public static int delay = 20;
    private int tick = -1;

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onGameEndEvent(ClientChatReceivedEvent event) {
        try {
            String message = event.message.getUnformattedText();
            if (GlobalUtils.isEndOfGame(message) && GlobalUtils.isNotSpecial(message)) {
                tick = delay;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onGameTick(TickEvent.ClientTickEvent event) {
        if (tick == 0) {
            tick = -1;
            if (AutoGG.isOn)  {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/achat gg");
            }
        } else {
            if (tick > 0) {
                tick--;
            }
        }
    }
}

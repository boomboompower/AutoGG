/*
 *     Copyright (C) 2017 boomboompower
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
import me.boomboompower.autogg.utils.ChatColor;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class AutoGGEvents {

    private Pattern chatPattern = Pattern.compile(
            "(?<rank>\\[.+] )?(?<player>\\S{1,16}): (?<message>.*)");

    private final List<String> endingStrings = Arrays.asList(
            "Winner - ", "1st Place - ", "1st Killer - ", "Winner: ", "WINNER!",
            "Winning Team - ", "1st - ", "Winners: ", "Winning Team: ", " won the game!", "1st Place: ");

    private boolean running = false;
    private int tick = -1;

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onGameEndEvent(ClientChatReceivedEvent event) {
        try {
            String message = ChatColor.stripColor(event.message.getUnformattedText());

            if (!AutoGG.getInstance().isOn() || this.chatPattern.matcher(message).find()) {
                return;
            }

            if (isEndOfGame(message)) {
                this.running = true;
                this.tick = AutoGG.getInstance().getTickDelay();
            }
        } catch (Exception ex) {
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onGameTick(TickEvent.ClientTickEvent event) {
        if (this.tick == 0) {
            if (AutoGG.getInstance().isOn() && this.running)  {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/ac gg");
                this.running = false;
            }
            this.tick = -1;
        } else {
            if (this.tick > 0) {
                this.tick--;
            }
        }
    }

    private boolean isEndOfGame(String message) {
        return this.endingStrings.stream().anyMatch(a -> ChatColor.stripColor(message).contains(a) && !message.startsWith(" "));
    }
}

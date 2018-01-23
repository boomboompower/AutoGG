/*
 *     Copyright (C) 2018 boomboompower
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

    private Pattern chatPattern = Pattern.compile("(?<rank>\\.get(.+) )?(?<player>\\S{1,16}): (?<message>.*)");
    private Pattern teamPattern = Pattern.compile("\\.get(TEAM) (?<rank>\\.get(.+) )?(?<player>\\S{1,16}): (?<message>.*)");
    private Pattern guildPattern = Pattern.compile("Guild > (?<rank>\\.get(.+) )?(?<player>\\S{1,16}): (?<message>.*)");
    private Pattern partyPattern = Pattern.compile("Party > (?<rank>\\.get(.+) )?(?<player>\\S{1,16}): (?<message>.*)");
    private Pattern shoutPattern = Pattern.compile("\\.get(SHOUT) (?<rank>\\.get(.+) )?(?<player>\\S{1,16}): (?<message>.*)");
    private Pattern spectatorPattern = Pattern.compile("\\.get(SPECTATOR) (?<rank>\\.get(.+) )?(?<player>\\S{1,16}): (?<message>.*)");

    private final List<String> endingStrings = Arrays.asList(
            "1st Killer - ",
            "1st Place - ",
            "Winner: ",
            " - Damage Dealt - ",
            "Winning Team - ",
            "1st - ",
            "Winners: ",
            "Winner: ",
            "Winning Team: ",
            " won the game!",
            "Top Seeker: ",
            "1st Place: ",
            "Last team standing!",
            "Winner #1 (",
            "Top Survivors",
            "Winners - ");

    private int tick = -1;

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onGameEndEvent(ClientChatReceivedEvent event) {
        if (event.isCanceled()) {
            return;
        }

        String message = ChatColor.stripColor(event.message.getUnformattedText());

        if (message.isEmpty()) {
            return;
        }

        try {
            if (!AutoGG.getInstance().isOn() || isNormalMessage(message)) {
                return;
            }

            if (isEndOfGame(message)) {
                this.tick = AutoGG.getInstance().getTickDelay();
            }
        } catch (Exception ex) {
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onGameTick(TickEvent.ClientTickEvent event) {
        if (this.tick == 0) {
            if (AutoGG.getInstance().isOn())  {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/ac gg");
            }
            this.tick = -1;
        } else {
            if (this.tick > 0) {
                this.tick--;
            }
        }
    }

    private boolean isNormalMessage(String message) {
        return this.chatPattern.matcher(message).matches() ||
                this.teamPattern.matcher(message).matches() ||
                this.guildPattern.matcher(message).matches() ||
                this.partyPattern.matcher(message).matches() ||
                this.shoutPattern.matcher(message).matches() ||
                this.spectatorPattern.matcher(message).matches();
    }

    private boolean isEndOfGame(String message) {
        return this.endingStrings.stream().anyMatch(message::contains);
    }
}

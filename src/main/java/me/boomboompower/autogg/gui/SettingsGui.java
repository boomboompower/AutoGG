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

package me.boomboompower.autogg.gui;

import me.boomboompower.autogg.AutoGG;

import me.boomboompower.autogg.events.AutoGGEvents;
import me.boomboompower.autogg.utils.GlobalUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.lwjgl.input.Keyboard;

import java.awt.*;

//        - 94
//        - 70
//        - 46
//        - 22
//        + 2
//        + 26
//        + 50
//        + 74
public class SettingsGui extends GuiScreen {

    private GuiButton set;
    private GuiButton reset;

    private TextBox text;
    private String input = "";

    public SettingsGui() {
        this("");
    }

    public SettingsGui(String input) {
        this.input = input;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        text = new TextBox(0, this.fontRendererObj, this.width / 2 - 75, this.height / 2 - 27, 150, 20);

        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + 2, 200, 20, "AutoGG: " + getAutoGG()));
        this.buttonList.add(this.set = new GuiButton(2, this.width / 2 - 100, this.height / 2 + 26, 200, 20, "Set Delay"));
        this.buttonList.add(this.reset = new GuiButton(3, this.width / 2 - 100, this.height / 2 + 50, 200, 20, "Reset Delay"));

        text.setText(input);
    }

    @Override
    public void drawScreen(int x, int y, float ticks) {
        drawDefaultBackground();

        drawTitle("AutoGG v" + AutoGG.VERSION);
        drawInfo();

        this.set.enabled = AutoGG.isOn;
        this.reset.enabled = AutoGG.isOn;

        text.drawTextBox();
        super.drawScreen(x, y, ticks);
    }

    @Override
    protected void keyTyped(char c, int key)  {
        if (key == 1) {
            mc.displayGuiScreen(null);
        } else {
            text.textboxKeyTyped(c, key);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) {
        try {
            super.mouseClicked(x, y, btn);
            text.mouseClicked(x, y, btn);
        } catch (Exception ex) {}
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        text.updateCursorCounter();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                // Dont need to do anything
                break;
            case 1:
                AutoGG.isOn = !AutoGG.isOn;
                button.displayString = "AutoGG: " + getAutoGG();
                text.pressToggle();
                break;
            case 2:
                if (!text.getText().isEmpty()) {
                    try {
                        if (Integer.valueOf(text.getText()) > 0) {
                            AutoGGEvents.delay = Integer.valueOf(text.getText());
                            sendChatMessage(String.format("Delay has been set to %s ticks!", EnumChatFormatting.RED + text.getText() + EnumChatFormatting.GRAY));
                        } else {
                            sendChatMessage("Tick delay must be over 0.");
                        }
                    } catch (Exception ex) {
                        sendChatMessage("Please only use numbers!");
                    }
                } else {
                    sendChatMessage("No text provided!");
                }
                mc.displayGuiScreen(null);
                break;
            case 3:
                reset();
                mc.displayGuiScreen(null);
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        AutoGG.fileUtils.saveConfig();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void sendChatMessage(String message) {
        GlobalUtils.sendMessage(message);
    }

    public void display() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        MinecraftForge.EVENT_BUS.unregister(this);
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    private void reset() {
        AutoGGEvents.delay = 20;
        sendChatMessage("Delay has been reset!");
    }

    private String getAutoGG() {
        return AutoGG.isOn ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled";
    }

    private void drawInfo() {
        drawCenteredString(mc.fontRendererObj, String.format("Delay is currently %s ticks", EnumChatFormatting.GOLD.toString() + AutoGGEvents.delay + EnumChatFormatting.WHITE), this.width / 2, this.height / 2 - 58, Color.WHITE.getRGB());
        drawCenteredString(mc.fontRendererObj, String.format("Which is about %s second%s" , (EnumChatFormatting.GOLD.toString() + (double) AutoGGEvents.delay / 20 + EnumChatFormatting.WHITE), AutoGGEvents.delay == 20D ? "" : "s"), this.width / 2, this.height /2 - 46, Color.WHITE.getRGB());
    }

    private void drawTitle(String text) {
        drawCenteredString(mc.fontRendererObj, text, this.width / 2, this.height / 2 - 80, Color.WHITE.getRGB());
        drawHorizontalLine(this.width / 2 - mc.fontRendererObj.getStringWidth(text) / 2 - 5, this.width / 2 + mc.fontRendererObj.getStringWidth(text) / 2 + 5, this.height / 2 - 70, Color.WHITE.getRGB());
    }
}

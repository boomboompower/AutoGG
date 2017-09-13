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

package me.boomboompower.autogg.gui;

import me.boomboompower.autogg.AutoGG;

import me.boomboompower.autogg.gui.modern.ModernButton;
import me.boomboompower.autogg.gui.modern.ModernTextBox;
import me.boomboompower.autogg.utils.ChatColor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
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

    private ModernTextBox text;
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

        this.text = new ModernTextBox(0, this.width / 2 - 100, this.height / 2 - 27, 200, 20, true);

        this.buttonList.add(new ModernButton(1, this.width / 2 - 100, this.height / 2 + 2, 200, 20, "AutoGG: " + getAutoGG()));
        this.buttonList.add(this.set = new ModernButton(2, this.width / 2 - 100, this.height / 2 + 26, 200, 20, "Set Delay"));
        this.buttonList.add(this.reset = new ModernButton(3, this.width / 2 - 100, this.height / 2 + 50, 200, 20, "Reset Delay"));

        this.text.setText(this.input);
    }

    @Override
    public void drawScreen(int x, int y, float ticks) {
        drawDefaultBackground();

        drawTitle("AutoGG v" + AutoGG.VERSION);
        drawCenteredString(this.mc.fontRendererObj, String.format("Delay is currently %s ticks", ChatColor.GOLD.toString() + AutoGG.getInstance().getTickDelay() + ChatColor.WHITE), this.width / 2, this.height / 2 - 58, Color.WHITE.getRGB());
        drawCenteredString(this.mc.fontRendererObj, String.format("Which is about %s second%s" , (ChatColor.GOLD.toString() + (double) AutoGG.getInstance().getTickDelay() / 20 + ChatColor.WHITE), AutoGG.getInstance().getTickDelay() == 20D ? "" : "s"), this.width / 2, this.height /2 - 46, Color.WHITE.getRGB());

        ((ModernButton) this.set).enabled = AutoGG.getInstance().isOn();
        ((ModernButton) this.reset).enabled = AutoGG.getInstance().isOn();

        text.drawTextBox();
        super.drawScreen(x, y, ticks);
    }

    @Override
    protected void keyTyped(char c, int key)  {
        if (key == 1) {
            this.mc.displayGuiScreen(null);
        } else {
            this.text.textboxKeyTyped(c, key);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) {
        try {
            super.mouseClicked(x, y, btn);
            this.text.mouseClicked(x, y, btn);
        } catch (Exception ex) {}
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.text.updateCursorCounter();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (!(button instanceof ModernButton)) return;

        switch (((ModernButton) button).id) {
            case 1:
                AutoGG.getInstance().setOn(!AutoGG.getInstance().isOn());
                ((ModernButton) button).displayString = "AutoGG: " + getAutoGG();
                this.text.pressToggle();
                break;
            case 2:
                if (!this.text.getText().isEmpty()) {
                    try {
                        int ticks = Integer.valueOf(this.text.getText());
                        if (ticks >= 0 && ticks <= 100) {
                            AutoGG.getInstance().setTickDelay(ticks);
                            sendChatMessage(String.format("Delay has been set to %s ticks!", ChatColor.RED + this.text.getText() + ChatColor.GRAY));
                        } else {
                                sendChatMessage("Tick delay must be over 0 and under 100.");
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
        AutoGG.getInstance().getFileUtils().saveConfig();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void sendChatMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(ChatColor.GOLD + "AutoGG" + ChatColor.AQUA + " > " + ChatColor.GRAY + ChatColor.translateAlternateColorCodes(message)));
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
        AutoGG.getInstance().setTickDelay(20);
        sendChatMessage("Tick delay has been reset!");
    }

    private String getAutoGG() {
        return AutoGG.getInstance().isOn() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled";
    }

    private void drawTitle(String text) {
        drawCenteredString(this.mc.fontRendererObj, text, this.width / 2, this.height / 2 - 80, Color.WHITE.getRGB());
        drawHorizontalLine(this.width / 2 - this.mc.fontRendererObj.getStringWidth(text) / 2 - 5, this.width / 2 + this.mc.fontRendererObj.getStringWidth(text) / 2 + 5, this.height / 2 - 70, Color.WHITE.getRGB());
    }

    @Override
    public void drawCenteredString(FontRenderer fontRenderer, String text, int x, int y, int color) {
        fontRenderer.drawString(text, (float)(x - fontRenderer.getStringWidth(text) / 2), (float) y, color, false);
    }

    @Override
    public void drawDefaultBackground() {
        long lastPress = System.currentTimeMillis();
        int color = Math.min(255, (int) (2L * (System.currentTimeMillis() - lastPress)));
        Gui.drawRect(0, 0, width, height, 2013265920 + (color << 16) + (color << 8) + color);
    }
}

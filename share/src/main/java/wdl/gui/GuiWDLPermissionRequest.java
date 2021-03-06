/*
 * This file is part of World Downloader: A mod to make backups of your
 * multiplayer worlds.
 * http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2520465
 *
 * Copyright (c) 2014 nairol, cubic72
 * Copyright (c) 2017-2018 Pokechu22, julialy
 *
 * This project is licensed under the MMPLv2.  The full text of the MMPL can be
 * found in LICENSE.md, or online at https://github.com/iopleke/MMPLv2/blob/master/LICENSE.md
 * For information about this the MMPLv2, see http://stopmodreposts.org/
 *
 * Do not redistribute (in modified or unmodified form) without prior permission.
 */
package wdl.gui;

import java.util.Map;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import wdl.WDLPluginChannels;
import wdl.gui.widget.Button;
import wdl.gui.widget.ButtonDisplayGui;
import wdl.gui.widget.Screen;
import wdl.gui.widget.TextList;

/**
 * GUI for requesting permissions.  Again, this is a work in progress.
 */
public class GuiWDLPermissionRequest extends Screen {
	private static final int TOP_MARGIN = 61, BOTTOM_MARGIN = 32;

	private TextList list;
	/**
	 * Parent GUI screen; displayed when this GUI is closed.
	 */
	private final GuiScreen parent;
	/**
	 * Field in which the wanted request is entered.
	 */
	private GuiTextField requestField;
	/**
	 * GUIButton for submitting the request.
	 */
	private GuiButton submitButton;

	public GuiWDLPermissionRequest(GuiScreen parent) {
		this.parent = parent;
	}

	@Override
	public void initGui() {
		this.list = this.addList(new TextList(mc, width, height, TOP_MARGIN, BOTTOM_MARGIN));

		list.addLine("\u00A7c\u00A7lThis is a work in progress.");
		list.addLine("You can request permissions in this GUI, although " +
				"it currently requires manually specifying the names.");
		list.addBlankLine();
		list.addLine("Boolean fields: " + WDLPluginChannels.BOOLEAN_REQUEST_FIELDS);
		list.addLine("Integer fields: " + WDLPluginChannels.INTEGER_REQUEST_FIELDS);
		list.addBlankLine();


		//Get the existing requests.
		for (Map.Entry<String, String> request : WDLPluginChannels
				.getRequests().entrySet()) {
			list.addLine("Requesting '" + request.getKey() + "' to be '"
					+ request.getValue() + "'.");
		}

		this.requestField = this.addTextField(new GuiTextField(0, fontRenderer,
				width / 2 - 155, 18, 150, 20));

		this.submitButton = this.addButton(new Button(
				width / 2 + 5, 18, 150, 20,
				"Submit request") {
			public @Override void performAction() {
				WDLPluginChannels.sendRequests();
				displayString = "Submitted!";
			}
		});
		this.submitButton.enabled = !(WDLPluginChannels.getRequests().isEmpty());

		this.addButton(new ButtonDisplayGui(width / 2 - 100, height - 29,
				200, 20, this.parent));

		this.addButton(new ButtonDisplayGui(this.width / 2 - 155, 39, 100, 20,
				I18n.format("wdl.gui.permissions.current"), () -> new GuiWDLPermissions(this.parent)));
		this.addButton(new Button(this.width / 2 - 50, 39, 100, 20,
				I18n.format("wdl.gui.permissions.request")) {
			public @Override void performAction() {
				// Would open this GUI; do nothing.
			}
		});
		this.addButton(new ButtonDisplayGui(this.width / 2 + 55, 39, 100, 20,
				I18n.format("wdl.gui.permissions.overrides"), () -> new GuiWDLChunkOverrides(this.parent)));
	}

	@Override
	public void charTyped(char keyChar) {
		if (requestField.isFocused()) {
			String request = requestField.getText();
			if (isValidRequest(request) && keyChar == '\n') {
				String[] requestData = request.split("=", 2);
				String key = requestData[0];
				String value = requestData[1];

				WDLPluginChannels.addRequest(key, value);
				list.addLine("Requesting '" + key + "' to be '"
						+ value + "'.");
				submitButton.enabled = true;

				requestField.setText("");
			}
		}
	}

	@Override
	public void anyKeyPressed() {
		if (requestField.isFocused()) {
			String request = requestField.getText();
			requestField.setTextColor(isValidRequest(request) ? 0x40E040 : 0xE04040);
		}
	}

	private boolean isValidRequest(String request) {
		if (request.contains("=")) {
			String[] requestData = request.split("=", 2);
			if (requestData.length == 2) {
				String key = requestData[0];
				String value = requestData[1];

				return WDLPluginChannels.isValidRequest(key, value);
			}
		}
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);

		this.drawCenteredString(this.fontRenderer, "Permission request",
				this.width / 2, 8, 0xFFFFFF);
	}
}

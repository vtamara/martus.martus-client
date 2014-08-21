/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2014, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/
package org.martus.client.swingui.jfx.landing.general;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.jfx.generic.FxFlexibleShellController;

public class SettingsController extends FxFlexibleShellController
{
	public SettingsController(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		firstTabToDisplay = SERVER_TAB;
	}
	
	public void firstTabToDisplay(String tab)
	{
		firstTabToDisplay = tab;
	}
	
	@Override
	public Parent createContents() throws Exception
	{
		Parent shellContents = super.createContents();
		loadControllerAndEmbedInPane(new SettingsforServerController(getMainWindow()), serverContentPane);
		loadControllerAndEmbedInPane(new SettingsforSystemController(getMainWindow()), systemContentPane);
		loadControllerAndEmbedInPane(new SettingsForTorController(getMainWindow()), torContentPane);
		
		selectInitialTabView();
		return shellContents;
	}

	public void selectInitialTabView()
	{
		if(firstTabToDisplay.equals(serverTab.getId()))
			settingTabPane.getSelectionModel().select(serverTab);
		if(firstTabToDisplay.equals(systemTab.getId()))
			settingTabPane.getSelectionModel().select(systemTab);
		if(firstTabToDisplay.equals(torTab.getId()))
			settingTabPane.getSelectionModel().select(torTab);
	}

	@Override
	public String getFxmlLocation()
	{
		return "landing/general/Settings.fxml";
	}
	
	
	static public final String SERVER_TAB = "serverTab";
	static public final String SYSTEM_TAB = "systemTab";
	static public final String TOR_TAB = "torTab";
	
	@FXML
	private TabPane settingTabPane;

	@FXML
	private Tab serverTab;
	
	@FXML
	private Tab systemTab;

	@FXML
	private Tab torTab;

	@FXML
	private Pane serverContentPane;

	@FXML
	private Pane systemContentPane;

	@FXML
	private Pane torContentPane;
	
	private String firstTabToDisplay;
}

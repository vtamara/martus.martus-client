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
package org.martus.client.swingui.jfx.contacts;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.stage.FileChooser;

import org.martus.client.swingui.MartusLocalization;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.jfx.setupwizard.step4.FxWizardAddContactsController;
import org.martus.common.MartusLogger;


public class FxManageContactsController extends FxWizardAddContactsController
{

	public FxManageContactsController(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		super.initialize(location, resources);
		sendToByDefaultColumn.setVisible(true);
		MartusLocalization localization = getLocalization();
		fxAddManageContactLabel.setText(localization.getFieldLabel("ManageContacts"));
		fxAddManageContactsDescriptionLabel.setText(localization.getFieldLabel("ManageContactsDescription"));
		showOldPublicCodeDuringVerification();
		addContactButton.setText(localization.getButtonLabel("AddContactFromServer"));
	}

	public void importContactFromFile()
	{
		FileChooser fileChooser = new FileChooser();
		File martusRootDir = getApp().getMartusDataRootDirectory();
		fileChooser.setInitialDirectory(martusRootDir);
		MartusLocalization localization = getLocalization();
		fileChooser.setTitle(localization.getWindowTitle("ImportContactPublicKey"));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter(localization.getFieldLabel("KeyPairFileFilter"), "*.mpi"),
				new FileChooser.ExtensionFilter(localization.getFieldLabel("AllFiles"), "*.*"));
		File importFile = fileChooser.showOpenDialog(null);

		if(importFile == null)
			return;
		
			try
			{
				String publicKeyString = getMainWindow().getApp().extractPublicInfo(importFile);
				verifyContactAndAddToTable(publicKeyString);
			} 	
			catch (Exception e)
			{
				MartusLogger.logException(e);
				showNotifyDialog("PublicInfoFileError");
			} 
	}
}
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

import javafx.application.Platform;

import javax.swing.JFileChooser;

import org.martus.client.core.MartusApp;
import org.martus.client.swingui.MartusLocalization;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.actions.ActionDoer;
import org.martus.client.swingui.filefilters.AllFileFilter;
import org.martus.client.swingui.filefilters.PublicInfoFileFilter;
import org.martus.common.MartusLogger;

public class ImportContactAction implements ActionDoer
{
	public ImportContactAction(FxManageContactsController fxManageContactsControllerToUse)
	{
		fxManageContactsController = fxManageContactsControllerToUse;
	}
	
	@Override
	public void doAction()
	{
		importContactFromFile();
	}
	
	private void importContactFromFile()
	{
		File martusRootDir = getApp().getMartusDataRootDirectory();
		JFileChooser fileChooser = new JFileChooser(martusRootDir);
		MartusLocalization localization = getLocalization();
		fileChooser.setDialogTitle(localization.getWindowTitle("ImportContactPublicKey"));
		fileChooser.addChoosableFileFilter(new PublicInfoFileFilter(getLocalization()));
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new AllFileFilter(getLocalization()));

		int userResult = fileChooser.showOpenDialog(getMainWindow());
		if (userResult != JFileChooser.APPROVE_OPTION)
			return;

		File importFile = fileChooser.getSelectedFile();		
		if(importFile == null)
			return;

		verifyContactAndAddToTable(importFile); 
	}

	public void verifyContactAndAddToTable(File importFile)
	{
		Platform.runLater(new ContactVerifyAndAddToTableRunnable(importFile));
	}
	
	private MartusLocalization getLocalization()
	{
		return getFxManageContactsController().getLocalization();
	}

	protected MartusApp getApp()
	{
		return getFxManageContactsController().getApp();
	}
	
	protected FxManageContactsController getFxManageContactsController()
	{
		return fxManageContactsController;
	}
	
	protected UiMainWindow getMainWindow()
	{
		return getFxManageContactsController().getMainWindow();
	}
	
	protected class ContactVerifyAndAddToTableRunnable implements Runnable
	{
		public ContactVerifyAndAddToTableRunnable(File publicKeyFileToUse)
		{
			publicKeyFile = publicKeyFileToUse;
		}
		
		@Override
		public void run()
		{
			try
			{
				String publicKeyAsString = getApp().extractPublicInfo(publicKeyFile);
				getFxManageContactsController().verifyContactAndAddToTable(publicKeyAsString);
			} 	
			catch (Exception e)
			{
				MartusLogger.logException(e);
				UiMainWindow.showNotifyDlgOnSwingThread(getMainWindow(), "PublicInfoFileError");
			}
		}
		
		private File publicKeyFile;
	}
	
	private FxManageContactsController fxManageContactsController;
}

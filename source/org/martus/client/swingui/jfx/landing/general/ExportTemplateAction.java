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

import java.io.File;

import javax.swing.JFileChooser;

import org.martus.client.core.MartusApp;
import org.martus.client.swingui.MartusLocalization;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.actions.ActionDoer;
import org.martus.client.swingui.filefilters.BulletinXmlFileFilter;
import org.martus.client.swingui.filefilters.MCTFileFilter;
import org.martus.client.swingui.jfx.generic.FxInSwingContentController;
import org.martus.clientside.FormatFilter;
import org.martus.common.MartusLogger;
import org.martus.common.fieldspec.FormTemplate;

public class ExportTemplateAction implements ActionDoer
{
	public ExportTemplateAction(UiMainWindow mainWindowToUse, FormTemplate formTemplateToExportToUse)
	{
		mainWindow = mainWindowToUse;
		formTemplateToExport = formTemplateToExportToUse;
	}
	
	@Override
	public void doAction()
	{
		try
		{
			exportTemplate(formTemplateToExport);
		}
		catch (Exception e)
		{
			MartusLogger.logException(e);
		}
	}
	
	private void exportTemplate(FormTemplate template) throws Exception
	{
		JFileChooser fileChooser = new JFileChooser(getApp().getMartusDataRootDirectory());
		fileChooser.setDialogTitle(getLocalization().getWindowTitle("FileDialogExportCustomization"));
		fileChooser.addChoosableFileFilter(new MCTFileFilter(getLocalization()));
		fileChooser.addChoosableFileFilter(new BulletinXmlFileFilter(getLocalization()));
		fileChooser.setAcceptAllFileFilterUsed(false);
		int userChoice = fileChooser.showSaveDialog(getMainWindow());
		if (userChoice != JFileChooser.APPROVE_OPTION)
			return;
		
		File templateFile = fileChooser.getSelectedFile();
		if(templateFile == null)
			return;
		
		FormatFilter chosenExtensionFilter = (FormatFilter) fileChooser.getFileFilter();
		templateFile = getFileWithExtension(templateFile, chosenExtensionFilter);

		if (FxInSwingContentController.isMctFileFilterSelected(getLocalization(), chosenExtensionFilter, templateFile))
			template.exportTemplate(getApp().getSecurity(), templateFile);
		
		if (FxInSwingContentController.isXmlExtensionSelected(getLocalization(), chosenExtensionFilter, templateFile))
			template.exportTopSection(templateFile);
	}

	public File getFileWithExtension(File templateFile,
			FormatFilter chosenExtensionFilter)
	{
		String extension = chosenExtensionFilter.getExtension();
		String fileName = templateFile.getName();
		if(!fileName.endsWith(extension))
		{
			StringBuilder fileNameWithExtension = new StringBuilder(fileName);
			fileNameWithExtension.append(extension);
			templateFile = new File(templateFile.getParentFile(), fileNameWithExtension.toString());
		}
		return templateFile;
	}
	
	private MartusLocalization getLocalization()
	{
		return getMainWindow().getLocalization();
	}

	private MartusApp getApp()
	{
		return getMainWindow().getApp();
	}
	
	private UiMainWindow getMainWindow()
	{
		return mainWindow;
	}

	private UiMainWindow mainWindow;
	private FormTemplate formTemplateToExport;
}

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
package org.martus.client.swingui.jfx.generic;

import java.awt.Window;

import javafx.application.Platform;

import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.jfx.landing.bulletins.FxGenericStage;
import org.martus.common.MartusLogger;

public class FxRunner implements Runnable
{
	public FxRunner(FxInSwingStage stageToUse)
	{
		stage = stageToUse;
	}
	
	public void run()
	{
		try
		{
			stage.showCurrentPage();
		} 
		catch (Exception e)
		{
			MartusLogger.logException(e);
			if(!shouldAbortImmediatelyOnError)
			{
				stage.getMainWindow().unexpectedErrorDlg(e);
			}
			System.exit(1);
		}
	}
	
	public void setAbortImmediatelyOnError()
	{
		shouldAbortImmediatelyOnError = true;
	}

	public static FxGenericStage createAndActivateEmbeddedStage(UiMainWindow observerToUse, Window windowToUse, FxShellController shellController, String cssName)
	{
		FxGenericStage stage = new FxGenericStage(observerToUse, windowToUse, shellController, cssName);
		
		FxRunner fxRunner = new FxRunner(stage);
		fxRunner.setAbortImmediatelyOnError();
		Platform.runLater(fxRunner);
		
		return stage;
	}

	private FxInSwingStage stage;
	private boolean shouldAbortImmediatelyOnError;
}
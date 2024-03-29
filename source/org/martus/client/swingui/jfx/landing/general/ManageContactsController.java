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

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.actions.ActionMenuManageContactsWithoutResignIn;
import org.martus.client.swingui.jfx.generic.FxInSwingController;

public class ManageContactsController extends FxInSwingController
{
	public ManageContactsController(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle bundle)
	{
		super.initialize(location, bundle);
	}

	@Override
	public String getFxmlLocation()
	{
		return "landing/general/ManageContactsController.fxml";
	}
	
	@FXML
	public void onManageContacts(ActionEvent event)
	{
		
		doAction(new ActionMenuManageContactsWithoutResignIn(getMainWindow()));
	}
}

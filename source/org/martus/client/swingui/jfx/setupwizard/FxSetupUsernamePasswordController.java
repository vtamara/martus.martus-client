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
package org.martus.client.swingui.jfx.setupwizard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.jfx.FxController;
import org.martus.client.swingui.jfx.FxWizardStage;
import org.martus.common.MartusLogger;

public class FxSetupUsernamePasswordController extends FxController
{
	public FxSetupUsernamePasswordController(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
	}

	@FXML
	protected void handleNext(ActionEvent event) 
	{
		try
		{
			createAccount();
			getStage().handleNavigationEvent(FxWizardStage.NAVIGATION_NEXT);
		}
		catch (Exception e)
		{
			MartusLogger.logException(e);
		}
	}

	private void createAccount() throws Exception
	{
		String userNameValue = userName.getText();
		String passwordValue = passwordField.getText();
		
		getMainWindow().getApp().createAccount(userNameValue, passwordValue.toCharArray());
	}

	@FXML
	protected void handleBack(ActionEvent event) 
	{
		getStage().handleNavigationEvent(FxWizardStage.NAVIGATION_BACK);
	}
	
	@FXML
	protected void handleUsernameChanged(KeyEvent keyEvent)
	{
		String userNameValue = userName.getText();
		String passwordValue = passwordField.getText();
		try
		{ 
			enableNext();
			errorLabel.setText("");
			if (getMainWindow().getApp().doesAccountExist(userNameValue, passwordValue.toCharArray()))
			{
				disableNext();
				errorLabel.setText("Account already Exists!");
			}
		}
		catch (Exception e)
		{
			MartusLogger.logException(e);
		}
	}
	
	private void disableNext()
	{
		nextButton.setDisable(true);
	}
	
	private void enableNext()
	{
		nextButton.setDisable(false);
	}
	
	@FXML
	private Button nextButton; 

	@FXML
	private TextField userName;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private PasswordField confirmPasswordField;
	
	@FXML
	private Label errorLabel;
}
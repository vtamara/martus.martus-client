/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2005, Beneficent
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
package org.martus.client.swingui.dialogs;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.martus.common.clientside.CurrentUiState;
import org.martus.common.clientside.UiBasicLocalization;
import org.martus.swing.UiLabel;
import org.martus.swing.UiParagraphPanel;
import org.martus.swing.UiRadioButton;
import org.martus.swing.UiTabbedPane;
import org.martus.swing.UiWrappedTextArea;


public class UiInitialSigninDlg extends UiSigninDlg
{
	public UiInitialSigninDlg(UiBasicLocalization localizationToUse, CurrentUiState uiStateToUse, JFrame owner, int mode, String userName, char[] password)
	{
		super(localizationToUse, uiStateToUse, owner, mode, userName, password);
	}

	protected JPanel createMainPanel()
	{
		JPanel scrolledPanel = new JPanel(); 
		tabbedPane = new UiTabbedPane();
		tabLabelSignIn = localization.getButtonLabel("SignIn");
		tabLabelNewAccount = localization.getButtonLabel("NewAccountTab");
		tabLabelRecoverAccount = localization.getButtonLabel("RecoverAccountTab");
		if(currentMode == INITIAL)
			tabbedPane.add(signinPane,tabLabelSignIn);
		tabbedPane.add(createNewAccountPanel(), tabLabelNewAccount);
		tabbedPane.add(createRecoverAccountPanel(), tabLabelRecoverAccount);
		scrolledPanel.add(tabbedPane);
		return scrolledPanel;
	}

	JComponent createNewAccountPanel()
	{
		String message = "HowToCreateNewAccount";
		if(currentMode == INITIAL_NEW_RECOVER_ACCOUNT)
			message = "HowToCreateInitialAccount";
		String text = localization.getFieldLabel(message);
		return new UiWrappedTextArea("\n" + text);
	}
	
	JPanel createRecoverAccountPanel()
	{
		UiParagraphPanel radioButtonPanel = new UiParagraphPanel();
		radioBackupFile = new UiRadioButton(localization.getButtonLabel("RecoverAccountByBackup"), true);
		radioBackupFile.setActionCommand("backupFile");
		radioShare = new UiRadioButton(localization.getButtonLabel("RecoverAccountByShare"), false);
		radioShare.setActionCommand("share");

		recoveryTypeGroup = new ButtonGroup();
		recoveryTypeGroup.add(radioBackupFile);
		recoveryTypeGroup.add(radioShare);

		radioButtonPanel.addOnNewLine(radioBackupFile);
		radioButtonPanel.addOnNewLine(radioShare);
		
		UiParagraphPanel recoverAccountPanel = new UiParagraphPanel();
		recoverAccountPanel.addOnNewLine(new UiLabel(localization.getFieldLabel("RecoverAccount")));
		recoverAccountPanel.addOnNewLine(radioButtonPanel);
		return recoverAccountPanel;
	}
	public void handleOk()
	{
		
		int tabNumber = tabbedPane.getSelectedIndex();
		if(tabbedPane.getTitleAt(tabNumber).equals(tabLabelSignIn))
			usersChoice = SIGN_IN;
		else if(tabbedPane.getTitleAt(tabNumber).equals(tabLabelNewAccount))
			usersChoice = NEW_ACCOUNT;
		else if(tabbedPane.getTitleAt(tabNumber).equals(tabLabelRecoverAccount))
		{
			ButtonModel model = recoveryTypeGroup.getSelection();
			if(model.getActionCommand().equals("share"))
				usersChoice = RECOVER_ACCOUNT_BY_SHARE;
			else 
				usersChoice = RECOVER_ACCOUNT_BY_BACKUP_FILE;
		}
		dispose();
	}

	private ButtonGroup recoveryTypeGroup;
	private JTabbedPane tabbedPane;
	private UiRadioButton radioShare;
	private UiRadioButton radioBackupFile;
	
	private String tabLabelSignIn;
	private	String tabLabelNewAccount;
	private	String tabLabelRecoverAccount;

}

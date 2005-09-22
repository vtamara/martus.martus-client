/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2005, Beneficent
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.martus.client.search.FancySearchGridEditor;
import org.martus.client.search.SearchTreeNode;
import org.martus.client.swingui.UiMainWindow;
import org.martus.common.MiniLocalization;
import org.martus.swing.UiButton;
import org.martus.swing.UiWrappedTextPanel;
import org.martus.swing.Utilities;
import org.martus.util.TokenReplacement;
import org.martus.util.TokenReplacement.TokenInvalidException;

public class UiFancySearchDlg extends UiSearchDlg
{
	public UiFancySearchDlg(UiMainWindow owner)
	{
		super(owner);
	}
	
	UiButton createBody(UiMainWindow mainWindow)
	{
		setTitle(localization.getWindowTitle("search"));
		
		UiButton search = new UiButton(localization.getButtonLabel("search"));
		search.addActionListener(this);

		String helpButtonText = localization.getButtonLabel("help"); 
		UiButton help = new UiButton(helpButtonText);
		help.addActionListener(new HelpListener(mainWindow));
		
		UiButton cancel = new UiButton(localization.getButtonLabel("cancel"));
		cancel.addActionListener(this);
		UiDialogLauncher dlgLauncher = new UiDialogLauncher(mainWindow.getCurrentActiveFrame(), localization);
		grid = FancySearchGridEditor.create(mainWindow.getStore(), dlgLauncher);
		grid.setText(getPreviousSearch());

		JPanel instructionPanel = new JPanel();
		instructionPanel.setLayout(new BorderLayout());
		instructionPanel.add(new UiWrappedTextPanel(localization.getFieldLabel("SearchBulletinRules")), BorderLayout.NORTH);
		UiWrappedTextPanel uiWrappedTextPanel = new UiWrappedTextPanel(localization.getFieldLabel("SearchBulletinAddingRules"));
		uiWrappedTextPanel.setBorder(new EmptyBorder(10, 0, 10,0));
		instructionPanel.add(uiWrappedTextPanel, BorderLayout.CENTER);
		try
		{
			String helpInfo = TokenReplacement.replaceToken(localization.getFieldLabel("SearchBulletinHelp"), "#SearchHelpButton#", helpButtonText);
			UiWrappedTextPanel uiWrappedTextPanel2 = new UiWrappedTextPanel(helpInfo);
			uiWrappedTextPanel2.setBorder(new EmptyBorder(0,0,10,0));
			instructionPanel.add(uiWrappedTextPanel2, BorderLayout.SOUTH);
		}
		catch(TokenInvalidException e)
		{
			e.printStackTrace();
		}

		Box buttonBox = Box.createHorizontalBox();
		buttonBox.setBorder(new EmptyBorder(10,0,0,0));
		Utilities.addComponentsRespectingOrientation(buttonBox, new Component[] {search, Box.createHorizontalGlue(),help, cancel });

		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5,5,5,5));
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(instructionPanel,BorderLayout.NORTH);
		mainPanel.add(grid.getComponent(),BorderLayout.CENTER);
		mainPanel.add(buttonBox,BorderLayout.SOUTH);

		getContentPane().add(mainPanel);
		getRootPane().setDefaultButton(search);
		
		return search;
	}
	
	private class HelpListener implements ActionListener
	{
		HelpListener(UiMainWindow mainWindowToUse)
		{
			mainWindow = mainWindowToUse;
		}
		public void actionPerformed(ActionEvent e)
		{
			String closeHelpButton = localization.getButtonLabel("CloseHelp");
			String title = localization.getWindowTitle("FancySearchHelp");

			StringBuffer rawHelpMessage = new StringBuffer(localization.getFieldLabel("FancySearchHelpMsg1"));
			rawHelpMessage.append(localization.getFieldLabel("FancySearchHelpMsg2"));
			if(notInEnglishSoExplainUsingEnglishAndOr())
				rawHelpMessage.append(localization.getFieldLabel("FancySearchHelpMsg3"));
			
			try
			{
				HashMap tokenReplacement = new HashMap();
				tokenReplacement.put("#And#", localization.getKeyword("and"));
				tokenReplacement.put("#Or#", localization.getKeyword("or"));
				String helpMessage = TokenReplacement.replaceTokens(rawHelpMessage.toString(), tokenReplacement);
				showHelp(title, helpMessage, closeHelpButton);
			}
			catch(TokenInvalidException e1)
			{
				e1.printStackTrace();
			}
		}
		
		private void showHelp(String title, String message, String closeButton)
		{
			JDialog dlg = new JDialog(mainWindow, title, true);
			JPanel panel = new JPanel();
			panel.setBorder(new EmptyBorder(5,5,5,5));
			panel.setLayout(new BorderLayout());
			UiWrappedTextPanel messagePanel = new UiWrappedTextPanel(message);
			messagePanel.setBorder(new EmptyBorder(5,5,5,5));
			messagePanel.setPreferredSize(new Dimension(500,500));
			panel.add(messagePanel, BorderLayout.CENTER);

			UiButton button = new UiButton(closeButton);
			button.addActionListener(new CloseHelpDialog(dlg));
			Box hbox = Box.createHorizontalBox();
			hbox.add(Box.createHorizontalGlue());
			hbox.add(button);
			hbox.add(Box.createHorizontalGlue());
			JPanel buttonPanel = new JPanel();
			buttonPanel.setBorder(new EmptyBorder(5,5,0,5));
			buttonPanel.add(hbox);
			panel.add(buttonPanel, BorderLayout.SOUTH);
			
			dlg.getContentPane().add(panel);
			Utilities.centerDlg(dlg);
			dlg.setVisible(true);
		}
		
		class CloseHelpDialog implements ActionListener
		{
			public CloseHelpDialog(JDialog dlgToUse)
			{
				dlg = dlgToUse;
			}
			public void actionPerformed(ActionEvent e)
			{
				dlg.dispose();
			}
			private JDialog dlg;
		}
		
		private boolean notInEnglishSoExplainUsingEnglishAndOr()
		{
			return !localization.getCurrentLanguageCode().equals(MiniLocalization.ENGLISH);
		}
		
		private UiMainWindow mainWindow;
	}

	public SearchTreeNode getSearchTree()
	{
		return grid.getSearchTree();
	}
	
	void memorizeSearch()
	{
		previousSearch = grid.getText();
	}
	
	String getPreviousSearch()
	{
		return previousSearch;
	}

	FancySearchGridEditor grid;
	private static String previousSearch = "";
}

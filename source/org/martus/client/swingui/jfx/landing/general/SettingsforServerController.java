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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;

import org.martus.client.core.ConfigInfo;
import org.martus.client.core.MartusApp;
import org.martus.client.core.MartusApp.SaveConfigInfoException;
import org.martus.client.swingui.MartusLocalization;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.jfx.generic.FxController;
import org.martus.client.swingui.jfx.generic.data.ObservableChoiceItemList;
import org.martus.client.swingui.jfx.setupwizard.step3.FxAdvancedServerStorageSetupController;
import org.martus.client.swingui.jfx.setupwizard.step3.FxSetupStorageServerController;
import org.martus.client.swingui.jfx.setupwizard.tasks.ConnectToServerTask;
import org.martus.client.swingui.jfx.setupwizard.tasks.GetServerPublicKeyTask;
import org.martus.clientside.ClientSideNetworkGateway;
import org.martus.common.Exceptions.ServerNotAvailableException;
import org.martus.common.MartusLogger;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.fieldspec.ChoiceItem;

public class SettingsforServerController extends FxController
{
	public SettingsforServerController(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle bundle)
	{
		super.initialize(location, bundle);
		initializeSyncFrequency();
		initializeServerInfo();
	}

	private void initializeServerInfo()
	{
		serverPublicKey = getApp().getConfigInfo().getServerPublicKey();
		String ipAddress = getApp().getConfigInfo().getServerName();
		updateServerInfo(ipAddress, serverPublicKey);
	}

	private void updateServerInfo(String ipAddress, String publicKey)
	{
		try
		{
			String publicCode = MartusCrypto.computeFormattedPublicCode40(publicKey);
			serverIpAddress.setText(ipAddress);
			serverPublicCode.setText(publicCode);
		} 
		catch (Exception e)
		{
			getStage().logAndNotifyUnexpectedError(e);
		} 
	}

	private void initializeSyncFrequency()
	{
		ObservableList<ChoiceItem> autoMaticSyncChoices = createChoices();
		automaticSyncFrequency.setItems(autoMaticSyncChoices);
		String currentSyncFrequency = getApp().getConfigInfo().getSyncFrequencyMinutes();
		selectByCode(automaticSyncFrequency, currentSyncFrequency);
		automaticSyncFrequency.getSelectionModel().selectedItemProperty().addListener(new SyncFrequencyChangeHandler(getApp()));
	}
	
	class SyncFrequencyChangeHandler implements ChangeListener<ChoiceItem>
	{
		public SyncFrequencyChangeHandler(MartusApp appToUse)
		{
			app = appToUse;
		}
		
		@Override
		public void changed(ObservableValue<? extends ChoiceItem> observable, ChoiceItem oldValue, ChoiceItem newValue)
		{
			try
			{
				app.getConfigInfo().setSyncFrequencyMinutes(newValue.getCode());
				app.saveConfigInfo();
			} 
			catch (SaveConfigInfoException e)
			{
				getStage().logAndNotifyUnexpectedError(e);
			}
		}
		
		private MartusApp app;
	}

	private static void selectByCode(ChoiceBox choiceBox, String codeToFind)
	{
		ObservableChoiceItemList choices = new ObservableChoiceItemList(choiceBox.getItems());
		ChoiceItem current = choices.findByCode(codeToFind);
		SingleSelectionModel model = choiceBox.getSelectionModel();
		model.select(current);
	}

	private ObservableList<ChoiceItem> createChoices()
	{
		ObservableList<ChoiceItem> choices = new ObservableChoiceItemList();

		choices.add(new ChoiceItem(NEVER, getLocalization().getFieldLabel("SyncFrequencyNever")));
		choices.add(new ChoiceItem(ON_STARTUP, getLocalization().getFieldLabel("SyncFrequencyOnStartup")));
		choices.add(new ChoiceItem("60", getLocalization().getFieldLabel("SyncFrequencyOneHour")));
		choices.add(new ChoiceItem("15", getLocalization().getFieldLabel("SyncFrequencyFifteenMinutes")));
		choices.add(new ChoiceItem("2", getLocalization().getFieldLabel("SyncFrequencyTwoMinutes")));
		
		return choices;
	}
	
	@Override
	public String getFxmlLocation()
	{
		return "landing/general/SettingsForServer.fxml";
	}
	
	@FXML
	public void onConnectToDefaultServer()
	{
		updateServerInfo(FxSetupStorageServerController.getDefaultServerIp(),
						 FxSetupStorageServerController.getDefaultServerPublicKey());
	}
	
	@FXML
	public void onSaveServerSetupChanges()
	{
		String ipAddress = serverIpAddress.getText();
		String publicCode = serverPublicCode.getText();
		boolean askComplianceAcceptance = !ipAddress.equals(FxSetupStorageServerController.getDefaultServerIp());
		if(attemptToConnect(ipAddress, publicCode, askComplianceAcceptance))
		{
			MartusApp app = getApp();
			app.getConfigInfo().setServerPublicKey(serverPublicKey);
			app.getConfigInfo().setServerName(ipAddress);
			try
			{
				app.saveConfigInfo();
			} 
			catch (SaveConfigInfoException e)
			{
				getStage().logAndNotifyUnexpectedError(e);
			}
		}
	}
	
	private boolean attemptToConnect(String serverIPAddress, String publicCode, boolean askComplianceAcceptance)
	{
		MartusLogger.log("Attempting to connect to: " + serverIPAddress);
		MartusApp app = getApp();
		getMainWindow().clearStatusMessage();
		try
		{
			GetServerPublicKeyTask getPublicKeyTask = new GetServerPublicKeyTask(getApp(), serverIPAddress);
			showTimeoutDialog(getLocalization().getFieldLabel("GettingServerInformation"), getPublicKeyTask);
			
			String newServerPublicKey = getPublicKeyTask.getPublicKey();
			if(!FxAdvancedServerStorageSetupController.doesPublicCodeMatch(newServerPublicKey, publicCode))
			{
				showNotifyDialog("ServerCodeWrong");
				return false;
			}
			ClientSideNetworkGateway gateway = ClientSideNetworkGateway.buildGateway(serverIPAddress, newServerPublicKey, getApp().getTransport());
			ConnectToServerTask connectToServerTask = new ConnectToServerTask(getApp(), gateway, "");
			MartusLocalization localization = getLocalization();
			String connectingToServerMsg = localization.getFieldLabel("AttemptToConnectToServerAndGetCompliance");
			showTimeoutDialog(connectingToServerMsg, connectToServerTask);
			if(!connectToServerTask.isAvailable())
			{
				showNotifyDialog("AdvanceServerNotResponding");
				return false; 
			}
			if(!connectToServerTask.isAllowedToUpload())
			{
				showNotifyDialog("ErrorServerOffline");
				return false;
			}
			String complianceStatement = connectToServerTask.getComplianceStatement();
			if(askComplianceAcceptance)
			{
				if(complianceStatement.equals(""))
				{
					showNotifyDialog("ServerComplianceFailed");
					return false;
				}
				
				if(!acceptCompliance(complianceStatement))
				{
					ConfigInfo previousServerInfo = app.getConfigInfo();
					String previousServerName = previousServerInfo.getServerName();
					String previousServerKey = previousServerInfo.getServerPublicKey();
					String previousServerCompliance = previousServerInfo.getServerCompliance();
	
					//TODO:The following line shouldn't be necessary but without it, the trustmanager 
					//will reject the old server, we don't know why.
					ClientSideNetworkGateway.buildGateway(previousServerName, previousServerKey, getApp().getTransport());
					getApp().setServerInfo(previousServerName,previousServerKey,previousServerCompliance);
					return false;
				}
			}

			app.setServerInfo(serverIPAddress, newServerPublicKey, complianceStatement);
			
			app.getStore().clearOnServerLists();
			
			getMainWindow().forceRecheckOfUidsOnServer();
			app.getStore().clearOnServerLists();
			getMainWindow().repaint();
			getMainWindow().setStatusMessageReady();
			serverPublicKey = newServerPublicKey;
			return true;
		}
		catch(UserCancelledException e)
		{
		}
		catch (SaveConfigInfoException e)
		{
			MartusLogger.logException(e);
			showNotifyDialog("ErrorSavingFile");
		}
		catch (ServerNotAvailableException e)
		{
			MartusLogger.logException(e);
			showNotifyDialog("AdvanceServerNotResponding");
		}
		catch (Exception e)
		{
			MartusLogger.logException(e);
			showNotifyDialog("UnexpectedError");
		}
		
		return false;
	}
	
	private boolean acceptCompliance(String newServerCompliance)
	{
		MartusLocalization localization = getLocalization();
		String title = localization.getWindowTitle("ServerCompliance");
		String complianceStatementMsg = String.format("%s\n\n%s", localization.getFieldLabel("ServerComplianceDescription"), newServerCompliance);
		if(!showConfirmationDialog(title, complianceStatementMsg))
		{
			showNotifyDialog("UserRejectedServerCompliance");
			return false;
		}
		return true;
	}
	

	@FXML
	public void onSaveServerPreferenceChanges()
	{
	}

	public final static String NEVER = "";
	public final static String ON_STARTUP = "OnStartup";

	@FXML
	private ChoiceBox automaticSyncFrequency;
	@FXML
	private TextField serverIpAddress;
	@FXML
	private TextField serverPublicCode;
	@FXML
	private CheckBox serverDefaultToOn;
	@FXML
	private CheckBox automaticallyDownloadFromServer;
	
	private String serverPublicKey;
	
}
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

package org.martus.client.swingui;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.SwingUtilities;
import org.martus.client.core.BackgroundUploader;
import org.martus.client.core.BulletinFolder;
import org.martus.client.core.ClientBulletinStore;
import org.martus.client.core.MartusApp;
import org.martus.common.ProgressMeterInterface;
import org.martus.common.clientside.ClientSideNetworkGateway;
import org.martus.common.clientside.Exceptions.ServerCallFailedException;
import org.martus.common.clientside.Exceptions.ServerNotAvailableException;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MartusCrypto.MartusSignatureException;
import org.martus.common.network.NetworkInterfaceConstants;
import org.martus.common.network.NetworkResponse;
import org.martus.common.packet.UniversalId;

class BackgroundUploadTimerTask extends TimerTask
{
	public BackgroundUploadTimerTask(UiMainWindow mainWindowToUse)
	{
		mainWindow = mainWindowToUse;
		ProgressMeterInterface progressMeter = mainWindow.statusBar.getBackgroundProgressMeter();
		uploader = new BackgroundUploader(mainWindow.getApp(), progressMeter);
	}
	
	public void forceRecheckOfUidsOnServer()
	{
		gotUpdatedOnServerUids = false;
	}

	public void run()
	{
		if(mainWindow.inConfigServer)
			return;
		if(inComplianceDialog)
			return;
		if(mainWindow.preparingToExitMartus)
			return;
			
		if(!getApp().isServerConfigured())
		{
			mainWindow.setStatusMessageTag("ServerNotConfiguredProgressMessage");	
			return;
		}												
			
		try
		{
			checkComplianceStatement();
			checkForNewsFromServer();
			getUpdatedListOfBulletinsOnServer();
			doUploading();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void doUploading()
		throws InterruptedException, InvocationTargetException
	{		
		String tag = "StatusReady";
		if(mainWindow.isServerConfigured())
		{					
			try
			{								
				BackgroundUploader.UploadResult uploadResult = uploader.backgroundUpload(); 
				mainWindow.uploadResult = uploadResult.result;				
				if(uploadResult.result == null)
				{
					tag = "UploadFailedProgressMessage"; 
					if(uploadResult.exceptionThrown == null)
						tag = "NoServerAvailableProgressMessage";
				}
				else if(uploadResult.uid != null)
				{
					//System.out.println("UiMainWindow.Tick.run: " + uploadResult);
					updateDisplay();
				}
				else
					tag = "";							
			}
			catch (MartusApp.DamagedBulletinException e)
			{
				ThreadedNotify damagedBulletin = new ThreadedNotify("DamagedBulletinMovedToDiscarded");
				SwingUtilities.invokeAndWait(damagedBulletin);
				mainWindow.folderContentsHaveChanged(getStore().getFolderSealedOutbox());
				mainWindow.folderContentsHaveChanged(getStore().getFolderDraftOutbox());
				mainWindow.folderContentsHaveChanged(getApp().createOrFindFolder(getStore().getNameOfFolderDamaged()));
				mainWindow.folderTreeContentsHaveChanged();
			}
		}

		if (tag != "")			
		 mainWindow.setStatusMessageTag(tag);
	}
	
	private void getUpdatedListOfBulletinsOnServer()
	{
		if(gotUpdatedOnServerUids)
			return;
		
		if(!getApp().isSSLServerAvailable())
			return;
		
		System.out.println("Entering BackgroundUploadTimerTask.getUpdatedListOfBulletinsOnServer");
		String myAccountId = getApp().getAccountId();
		HashSet uidsOnServer = new HashSet(1000);
		try
		{
			uidsOnServer.addAll(getUidsFromServer(myAccountId));
			
			ClientSideNetworkGateway gateway = getApp().getCurrentNetworkInterfaceGateway();
			MartusCrypto security = getApp().getSecurity();
			NetworkResponse myFieldOfficesResponse = gateway.getFieldOfficeAccountIds(security, getApp().getAccountId());
			if(NetworkInterfaceConstants.OK.equals(myFieldOfficesResponse.getResultCode()))
			{
				Vector fieldOfficeAccounts = myFieldOfficesResponse.getResultVector();
				System.out.println("My FO accounts: " + fieldOfficeAccounts.size());
				for(int i = 0; i < fieldOfficeAccounts.size(); ++i)
				{
					String fieldOfficeAccountId = (String)fieldOfficeAccounts.get(i);
					uidsOnServer.addAll(getUidsFromServer(fieldOfficeAccountId));
				}
			}
			getStore().updateOnServerLists(uidsOnServer);

			class CurrentFolderRefresher implements Runnable
			{
				public void run()
				{
					mainWindow.allBulletinsInCurrentFolderHaveChanged();
				}
			}
			SwingUtilities.invokeLater(new CurrentFolderRefresher());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		gotUpdatedOnServerUids = true;
		
		System.out.println("Exiting BackgroundUploadTimerTask.getUpdatedListOfBulletinsOnServer");
	}
	
	private Vector getUidsFromServer(String accountId) throws MartusSignatureException
	{
		Vector uidsOnServer = new Vector();
		Vector mySealedUids = tryToGetSealedUidsFromServer(accountId);
		uidsOnServer.addAll(mySealedUids);

		Vector myDraftUids = tryToGetDraftUidsFromServer(accountId);
		uidsOnServer.addAll(myDraftUids);
		System.out.println("Adding uids from server: " + uidsOnServer.size());
		return uidsOnServer;
	}

	private Vector tryToGetDraftUidsFromServer(String accountId) throws MartusSignatureException
	{
		ClientSideNetworkGateway gateway = getApp().getCurrentNetworkInterfaceGateway();
		MartusCrypto security = getApp().getSecurity();
		NetworkResponse myDraftResponse = gateway.getDraftBulletinIds(security, accountId, new Vector());
		if(NetworkInterfaceConstants.OK.equals(myDraftResponse.getResultCode()))
			return buildUidVector(accountId, myDraftResponse.getResultVector());
		return new Vector();
	}

	private Vector tryToGetSealedUidsFromServer(String accountId) throws MartusSignatureException
	{
		ClientSideNetworkGateway gateway = getApp().getCurrentNetworkInterfaceGateway();
		MartusCrypto security = getApp().getSecurity();
		NetworkResponse mySealedResponse = gateway.getSealedBulletinIds(security, accountId, new Vector());
		if(NetworkInterfaceConstants.OK.equals(mySealedResponse.getResultCode()))
			return buildUidVector(accountId, mySealedResponse.getResultVector());
		return new Vector();
	}

	private Vector buildUidVector(String accountId, Vector localIds)
	{
		Vector result = new Vector();
		for(int i=0; i < localIds.size(); ++i)
		{
			String localId = (String)localIds.get(i);
			int delimiterAt = localId.indexOf('=');
			if(delimiterAt >= 0)
				localId = localId.substring(0, delimiterAt);
			result.add(UniversalId.createFromAccountAndLocalId(accountId, localId));
		}
		
		return result;
	}
		
	private void updateDisplay()
	{
		class Updater implements Runnable
		{
			public void run()
			{
				ClientBulletinStore store = getStore();
				mainWindow.folderContentsHaveChanged(store.getFolderSaved());
				mainWindow.folderContentsHaveChanged(store.getFolderSealedOutbox());
				mainWindow.folderContentsHaveChanged(store.getFolderDraftOutbox());
				BulletinFolder discardedFolder = store.findFolder(store.getNameOfFolderDamaged());
				if(discardedFolder != null)
					mainWindow.folderContentsHaveChanged(discardedFolder);
			}
		}
		Updater updater = new Updater();
		
		final boolean crashMode = false;
		if(crashMode)
		{
			updater.run();
		}
		else
		{
			try
			{
				SwingUtilities.invokeAndWait(updater);
			}
			catch (Exception notMuchWeCanDoAboutIt)
			{
				notMuchWeCanDoAboutIt.printStackTrace();
			}
		}
	}

	public void checkComplianceStatement()
	{
		if(alreadyCheckedCompliance)
			return;
		try
		{
			ClientSideNetworkGateway gateway = getApp().getCurrentNetworkInterfaceGateway();
			String compliance = getApp().getServerCompliance(gateway);
			alreadyCheckedCompliance = true;
			if (compliance != null)
				mainWindow.setStatusMessageTag("StatusReady");
			
			if(!compliance.equals(getApp().getConfigInfo().getServerCompliance()))
			{
				ThreadedServerComplianceDlg dlg = new ThreadedServerComplianceDlg(compliance);
				SwingUtilities.invokeAndWait(dlg);
			}
		}
		catch (ServerCallFailedException userAlreadyKnows)
		{
			alreadyCheckedCompliance = true;			
			return;
		}
		catch (ServerNotAvailableException weWillTryAgainLater)
		{
			mainWindow.setStatusMessageTag("NoServerAvailableProgressMessage");
			return;
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		} 
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	public void checkForNewsFromServer()
	{
		if(alreadyGotNews)
			return;
		Vector newsItems = getApp().getNewsFromServer();
		if (newsItems.size() > 0)
			mainWindow.setStatusMessageTag("StatusReady");
			
		for (Iterator iter = newsItems.iterator(); iter.hasNext();)
		{
			String newsItem = (String) iter.next();
			ThreadedMessageDlg newsDlg = new ThreadedMessageDlg("ServerNews", newsItem);
			try
			{
				SwingUtilities.invokeAndWait(newsDlg);
			}
			catch (Exception e)
			{
				mainWindow.setStatusMessageTag("NoServerAvailableProgressMessage");
				e.printStackTrace();
			}
		}
		alreadyGotNews = true;
	}

	class ThreadedNotify implements Runnable
	{
		public ThreadedNotify(String tag)
		{
			notifyTag = tag;
		}

		public void run()
		{
			mainWindow.notifyDlg(notifyTag);
		}
		String notifyTag;
	}
		
	class ThreadedServerComplianceDlg implements Runnable
	{
		public ThreadedServerComplianceDlg(String newComplianceToUse)
		{
			newCompliance = newComplianceToUse;
		}
			
		public void run()
		{
			inComplianceDialog = true;
			if(mainWindow.confirmServerCompliance("ServerComplianceChangedDescription", newCompliance))
			{
				String serverAddress = getApp().getConfigInfo().getServerName();
				String serverKey = getApp().getConfigInfo().getServerPublicKey();
				getApp().setServerInfo(serverAddress, serverKey, newCompliance);
			}
			else
			{
				getApp().setServerInfo("", "", "");
				mainWindow.notifyDlg("ExistingServerRemoved");
			}
			inComplianceDialog = false;
		}
			
		String newCompliance;
	}
		
	class ThreadedMessageDlg implements Runnable
	{
		public ThreadedMessageDlg(String tag, String message)
		{
			titleTag = tag;
			messageContents = message;
		}

		public void run()
		{
			mainWindow.messageDlg(mainWindow, titleTag, messageContents);
		}
		String titleTag;
		String messageContents;
	}
		
	MartusApp getApp()
	{
		return mainWindow.getApp();
	}
		
	ClientBulletinStore getStore()
	{
		return getApp().getStore();
	}

	UiMainWindow mainWindow;
	BackgroundUploader uploader;

	boolean alreadyCheckedCompliance;
	boolean inComplianceDialog;
	boolean alreadyGotNews;
	boolean gotUpdatedOnServerUids;
}


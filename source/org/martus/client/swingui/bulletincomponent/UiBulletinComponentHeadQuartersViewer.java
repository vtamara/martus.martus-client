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
package org.martus.client.swingui.bulletincomponent;

import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import org.martus.client.swingui.UiMainWindow;
import org.martus.common.HQKey;
import org.martus.common.bulletin.Bulletin;
import org.martus.swing.UiLabel;
import org.martus.swing.UiScrollPane;
import org.martus.swing.UiTable;

public class UiBulletinComponentHeadQuartersViewer extends UiBulletinComponentHeadQuarters
{
	public UiBulletinComponentHeadQuartersViewer(UiMainWindow mainWindowToUse, Bulletin bulletinToUse, String tagQualifierToUse)
	{
		super(mainWindowToUse, bulletinToUse, tagQualifierToUse);

		UiLabel hqLabel = new UiLabel(getLabel("Headquarters"));
		if(hqKeysAuthorizedToReadThisBulletin.size() > 0)
		{
			UiScrollPane hqScroller = createHeadquartersTable();
			addComponents(hqLabel, hqScroller);
		}
		else
			addComponents(hqLabel, new UiLabel(getLocalization().getFieldLabel("NoHQsConfigured")));
	}

	private UiScrollPane createHeadquartersTable()
	{
		DefaultTableModel hqModel = new DefaultTableModel();
		hqModel.addColumn(getLabel("HQLabel"));
		int numberOfHQsConfigured = hqKeysAuthorizedToReadThisBulletin.size();
		for(int i=0; i < numberOfHQsConfigured; ++i)
		{
			HQKey key = hqKeysAuthorizedToReadThisBulletin.get(i);
			hqModel.addRow(new Object[]{getHQLabelIfPresent(key)});
		}
		UiTable hqTable = new UiTable(hqModel);
		hqTable.setColumnSelectionAllowed(false);
		hqTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		hqTable.setShowGrid(true);
		hqTable.resizeTable();
		hqTable.setEnabled(false);
		UiScrollPane hqScroller = new UiScrollPane(hqTable);
		return hqScroller;
	}

}

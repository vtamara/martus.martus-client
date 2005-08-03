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
package org.martus.client.swingui.fields;

import javax.swing.JComponent;

import org.martus.clientside.UiLocalization;

public class UiGridDateRangeEditorViewer extends UiFlexiDateEditor
{
	public UiGridDateRangeEditorViewer(UiLocalization localizationToUse)
	{
		super(localizationToUse, null);
		removeExactDatePanel();
	}
	
	protected boolean isFlexiDate()
	{
		return true;
	}
	
	protected boolean isExactDate()
	{
		return false;
	}
	
	protected boolean isCustomDate()
	{
		return true;
	}
	
	public JComponent[] getFocusableComponents()
	{
		JComponent[] beginDate = UiDateEditor.getComponentsInOrder(bgYearCombo, bgMonthCombo, bgDayCombo, localization);
		JComponent[] endDate = UiDateEditor.getComponentsInOrder(endYearCombo, endMonthCombo, endDayCombo, localization);
		return new JComponent[] { 
				beginDate[0], beginDate[1], beginDate[2],
				endDate[0], endDate[1], endDate[2],};
	}
	
	public void validate() throws DataInvalidException 
	{
		super.validate();
	}

	public JComponent getComponent()
	{
		return flexiDateBox;
	}
}

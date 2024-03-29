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
package org.martus.client.swingui.jfx.generic.controls;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.util.Callback;

import org.martus.client.swingui.actions.ActionDoer;

public class FxButtonTableCellFactory implements Callback<TableColumn<Object, Boolean>, TableCell<Object, Boolean>>
{
	public static FxButtonTableCellFactory createNormalButtonTableCellFactory(Image buttonImageToUse, ActionDoer doerToUse)
	{
		return new FxButtonTableCellFactory(buttonImageToUse, doerToUse);
	}

	public static FxButtonTableCellFactory createNarrowButtonTableCell(Image buttonImageToUse, ActionDoer doerToUse)
	{
		FxButtonTableCellFactory fxButtonTableCellFactory = new FxButtonTableCellFactory(buttonImageToUse, doerToUse);
		fxButtonTableCellFactory.useNarrowButtons();
		return fxButtonTableCellFactory;
	}
	
	
	private FxButtonTableCellFactory(Image imageToUse, ActionDoer doerToUse)
	{
		buttonImage = imageToUse;
		doer = doerToUse;
	}
	
	private void useNarrowButtons()
	{
		narrowButtons = true;
	}

	@Override
	public TableCell call(final TableColumn param) 
	{
		FxButtonTableCell cell;
		if(narrowButtons)
			cell = FxButtonTableCell.createNarrowButtonTableCell(buttonImage, doer);
		else
			cell = FxButtonTableCell.createNormalButtonTableCell(buttonImage, doer);
		return cell;
	}
	
	private boolean narrowButtons;
	private Image buttonImage;
	private ActionDoer doer;
}

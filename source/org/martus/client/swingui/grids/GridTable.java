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

package org.martus.client.swingui.grids;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.martus.client.swingui.UiLocalization;
import org.martus.client.swingui.fields.UiDateEditor;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.swing.UiTableWithCellEditingProtection;

public class GridTable extends UiTableWithCellEditingProtection
{
	public GridTable(GridTableModel model, UiLocalization localizationToUse)
	{
		super(model);
		localization = localizationToUse;

		stringRenderer = new GridNormalCellEditor(localization);
		dateRenderer = new GridDateCellEditor(localization);
		booleanRenderer = new GridBooleanCellEditor();
		dropDownRenderer = new GridDropDownCellEditor();

		stringEditor = new GridNormalCellEditor(localization);
		dateEditor = new GridDateCellEditor(localization);
		booleanEditor = new GridBooleanCellEditor();
		dropDownEditor = new GridDropDownCellEditor();
		
		setMaxColumnWidthToHeaderWidth(0);
		for(int i = 1 ; i < model.getColumnCount(); ++i)
			setColumnWidthToHeaderWidth(i);
		setAutoResizeMode(AUTO_RESIZE_OFF);
	}
	
	FieldSpec getFieldSpecForColumn(int column)
	{
		return ((GridTableModel)getModel()).getFieldSpec(column);		
	}
	
	public void changeSelection(int rowIndex, int columnIndex,
			boolean toggle, boolean extend)
	{
		if(columnIndex == 0)
			columnIndex = 1;
		super.changeSelection(rowIndex, columnIndex, toggle, extend);
	}

	public TableCellEditor getCellEditor(int row, int column)
	{
		GridTableModel model = (GridTableModel)getModel();
		int type = model.getCellType(row, column);
		switch(type)
		{
			case FieldSpec.TYPE_NORMAL:
				return stringEditor;
			case FieldSpec.TYPE_DATE:
				return dateEditor;
			case FieldSpec.TYPE_DROPDOWN:
				return dropDownEditor;
			case FieldSpec.TYPE_BOOLEAN:
				return booleanEditor;
			default:
				System.out.println("GridTable.getCellEditor Unexpected type: " + type);
				return stringEditor;
		}
	}

	public TableCellRenderer getCellRenderer(int row, int column)
	{
		GridTableModel model = (GridTableModel)getModel();
		int type = model.getCellType(row, column);
		switch(type)
		{
			case FieldSpec.TYPE_NORMAL:
				return stringRenderer;
			case FieldSpec.TYPE_DATE:
				return dateRenderer;
			case FieldSpec.TYPE_DROPDOWN:
				return dropDownRenderer;
			case FieldSpec.TYPE_BOOLEAN:
				return booleanRenderer;
			default:
				System.out.println("GridTable.getCellEditor Unexpected type: " + type);
				return stringRenderer;
		}
	}

	UiLocalization localization;

	GridNormalCellEditor stringRenderer;
	GridDateCellEditor dateRenderer;
	GridBooleanCellEditor booleanRenderer;
	GridDropDownCellEditor dropDownRenderer;

	GridNormalCellEditor stringEditor;
	GridDateCellEditor dateEditor;
	GridBooleanCellEditor booleanEditor;
	GridDropDownCellEditor dropDownEditor;

}

class GridDateCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer
{
	GridDateCellEditor(UiLocalization localization)
	{
		widget = new UiDateEditor(localization, null);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
	{
		return widget.getComponent();
	}

	public Object getCellEditorValue()
	{
		return widget.getText();
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int row, int column)
	{
		widget.setText((String)value);
		return widget.getComponent();
	}

	UiDateEditor widget;
}

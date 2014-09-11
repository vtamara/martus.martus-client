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
package org.martus.client.swingui.jfx.landing.bulletins;

import java.util.Vector;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import org.martus.client.core.FxBulletin;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.jfx.generic.FxController;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.MessageFieldSpec;
import org.martus.common.fieldspec.StandardFieldSpecs;

public class BulletinEditorBodyController extends FxController
{
	public BulletinEditorBodyController(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
	}

	@Override
	public String getFxmlLocation()
	{
		return "landing/bulletins/BulletinEditorBody.fxml";
	}

	public void showBulletin(FxBulletin bulletinToShow) throws RuntimeException
	{
		fieldsGrid.getChildren().clear();
		
		Vector<FieldSpec> fieldSpecs = bulletinToShow.getFieldSpecs();
		for(int row = 0; row < fieldSpecs.size(); ++row)
		{
			FieldSpec spec = fieldSpecs.get(row);
			if(shouldOmitField(spec))
				continue;

			String tag = spec.getTag();
			SimpleStringProperty property = bulletinToShow.getFieldProperty(tag);
			createFieldForSpec(row, spec, property);
		}
	}

	public void scrollToTop()
	{
		scrollPane.vvalueProperty().set(0);
	}

	private boolean shouldOmitField(FieldSpec spec)
	{
		Vector<String> tagsToOmit = new Vector<String>();
		tagsToOmit.add(Bulletin.TAGTITLE);
		tagsToOmit.add(Bulletin.TAGWASSENT);
		
		return tagsToOmit.contains(spec.getTag());
	}

	private void createFieldForSpec(int row, FieldSpec spec, SimpleStringProperty property)
	{
		String tag = spec.getTag();
		String labelText = spec.getLabel();
		if(StandardFieldSpecs.isStandardFieldTag(tag))
			labelText = getLocalization().getFieldLabel(tag);
		Label label = new Label(labelText);
		fieldsGrid.add(label, LABEL_COLUMN, row);
		if(spec.getType().isString())
			createStringField(row, property);
		else if(spec.getType().isMultiline())
			createMultilineField(row, property);
		else if(spec.getType().isMessage())
			createMessageField(row, spec);
		else if(spec.getType().isBoolean())
			createBooleanField(row, property);
	}

	private void createBooleanField(int row, SimpleStringProperty property)
	{
		CheckBox checkBox = new CheckBox();
		checkBox.setOnAction(new CheckBoxHandler(checkBox, property));
		checkBox.setSelected(isTrue(property));
		fieldsGrid.add(checkBox, DATA_COLUMN, row);
	}
	
	private boolean isTrue(SimpleStringProperty property)
	{
		return property.getValue().equals(FieldSpec.TRUESTRING);
	}

	private static class CheckBoxHandler implements EventHandler<ActionEvent> 
	{
		public CheckBoxHandler(CheckBox checkBoxToUse, SimpleStringProperty property)
		{
			checkBox = checkBoxToUse;
			booleanStringProperty = property;
		}

		@Override
		public void handle(ActionEvent event)
		{
			String value = FieldSpec.FALSESTRING;
			if(checkBox.isSelected())
				value = FieldSpec.TRUESTRING;
			booleanStringProperty.setValue(value);
		}
		
		private CheckBox checkBox;
		private SimpleStringProperty booleanStringProperty;
	}

	private void createMessageField(int row, FieldSpec spec)
	{
		String message = ((MessageFieldSpec)(spec)).getMessage();
		TextArea textArea = new TextArea(message);
		textArea.setPrefColumnCount(NORMAL_TEXT_FIELD_WIDTH_IN_CHARACTERS);
		textArea.setPrefRowCount(1);
		textArea.setFocusTraversable(false);
		textArea.setWrapText(true);
		textArea.setEditable(false);
		fieldsGrid.add(textArea, DATA_COLUMN, row);
	}

	public void createStringField(int row, SimpleStringProperty property)
	{
		TextField textField = new TextField();
		textField.setPrefColumnCount(NORMAL_TEXT_FIELD_WIDTH_IN_CHARACTERS);
		textField.textProperty().bindBidirectional(property);
		fieldsGrid.add(textField, DATA_COLUMN, row);
	}
	
	private void createMultilineField(int row, SimpleStringProperty property)
	{
		TextArea textArea = new TextArea();
		textArea.setPrefColumnCount(NORMAL_TEXT_FIELD_WIDTH_IN_CHARACTERS);
		textArea.setPrefRowCount(MULTILINE_FIELD_HEIGHT_IN_ROWS);
		textArea.setWrapText(true);
		textArea.textProperty().bindBidirectional(property);
		fieldsGrid.add(textArea, DATA_COLUMN, row);
	}

	private static final int LABEL_COLUMN = 0;
	private static final int DATA_COLUMN = 1;
	private static final int NORMAL_TEXT_FIELD_WIDTH_IN_CHARACTERS = 60;
	private static final int MULTILINE_FIELD_HEIGHT_IN_ROWS = 5;

	@FXML
	private ScrollPane scrollPane;
	
	@FXML
	private GridPane fieldsGrid;

}

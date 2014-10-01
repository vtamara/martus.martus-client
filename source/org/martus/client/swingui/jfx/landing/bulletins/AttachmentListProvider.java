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

import javafx.collections.ObservableList;

import org.martus.client.swingui.jfx.generic.data.ArrayObservableList;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.database.ReadableDatabase;


public class AttachmentListProvider extends ArrayObservableList<AttachmentTableRowData>
{
	public AttachmentListProvider(ObservableList<AttachmentProxy> attachments, ReadableDatabase database)
	{
		super(attachments.size());
		for (AttachmentProxy attachmentProxy : attachments)
		{
			AttachmentTableRowData attachmentRow = new AttachmentTableRowData(attachmentProxy, database);
			add(attachmentRow);
		}
	}
}

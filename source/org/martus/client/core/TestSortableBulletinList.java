/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2006, Beneficent
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
package org.martus.client.core;

import org.martus.common.MiniLocalization;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.crypto.MockMartusSecurity;
import org.martus.common.field.MartusDateRangeField;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.FieldTypeNormal;
import org.martus.common.fieldspec.StandardFieldSpecs;
import org.martus.common.packet.UniversalId;
import org.martus.common.utilities.MartusFlexidate;
import org.martus.util.MultiCalendar;
import org.martus.util.TestCaseEnhanced;

public class TestSortableBulletinList extends TestCaseEnhanced
{
	public TestSortableBulletinList(String name)
	{
		super(name);
	}

	public void testBasics() throws Exception
	{
		MiniLocalization localization = new MiniLocalization();
		localization.setCurrentLanguageCode(MiniLocalization.ENGLISH);
		MockMartusSecurity security = MockMartusSecurity.createClient();
		
		String tags[] = {Bulletin.TAGAUTHOR, Bulletin.TAGTITLE};

		SortableBulletinList list = new SortableBulletinList(localization, tags);
		String[] authors = {"Sue", "Wendy", "Alan", "Wendy", };
		String[] titles = {"Wow", "Yowza", "Yippee", "Eureka!", };
		Bulletin[] bulletins = new Bulletin[authors.length];
		for(int i = 0; i < authors.length; ++i)
		{
			bulletins[i] = new Bulletin(security);
			bulletins[i].set(Bulletin.TAGAUTHOR, authors[i]);
			bulletins[i].set(Bulletin.TAGTITLE, titles[i]);
			list.add(bulletins[i]);
		}
		
		UniversalId[] uids = list.getSortedUniversalIds();
		assertEquals("Alan not first?", bulletins[2].getUniversalId(), uids[0]);
		assertEquals("Sue not second?", bulletins[0].getUniversalId(), uids[1]);
		assertEquals("Wendy/Eureka not third?", bulletins[3].getUniversalId(), uids[2]);
		assertEquals("Wendy Yowza not last?", bulletins[1].getUniversalId(), uids[3]);
	}
	
	public void testSubFields() throws Exception
	{
		MiniLocalization localization = new MiniLocalization();
		localization.setCurrentLanguageCode(MiniLocalization.ENGLISH);
		MockMartusSecurity security = MockMartusSecurity.createClient();
		
		MultiCalendar lowDate = MultiCalendar.createFromGregorianYearMonthDay(1995, 12, 27);
		MultiCalendar middleDate = MultiCalendar.createFromGregorianYearMonthDay(2003, 07, 25);
		MultiCalendar highDate = MultiCalendar.createFromGregorianYearMonthDay(2007, 01, 14);
		
		Bulletin lowHigh = new Bulletin(security);
		lowHigh.set(Bulletin.TAGEVENTDATE, MartusFlexidate.toBulletinFlexidateFormat(lowDate, highDate));
		
		Bulletin middle = new Bulletin(security);
		middle.set(Bulletin.TAGEVENTDATE, MartusFlexidate.toBulletinFlexidateFormat(middleDate, middleDate));
		
		Bulletin highLow = new Bulletin(security);
		highLow.set(Bulletin.TAGEVENTDATE, MartusFlexidate.toBulletinFlexidateFormat(highDate, highDate));
		
		String tags[] = {Bulletin.TAGEVENTDATE + "." + MartusDateRangeField.SUBFIELD_BEGIN};
		SortableBulletinList begin = new SortableBulletinList(localization, tags);
		begin.add(lowHigh);
		begin.add(middle);
		begin.add(highLow);
		
		PartialBulletin[] beginBulletins = begin.getSortedPartialBulletins();
		assertEquals("begin low not first?", lowDate.toIsoDateString(), beginBulletins[0].getData(tags[0]));
		assertEquals("begin middle not middle?", middleDate.toIsoDateString(), beginBulletins[1].getData(tags[0]));
		assertEquals("begin high not last?", highDate.toIsoDateString(), beginBulletins[2].getData(tags[0]));
	}
	
	public void testMissingFieldSorting() throws Exception
	{
		MiniLocalization localization = new MiniLocalization();
		localization.setCurrentLanguageCode(MiniLocalization.ENGLISH);
		MockMartusSecurity security = MockMartusSecurity.createClient();
		
		String tag = "tag";
		FieldSpec[] publicFields = new FieldSpec[] {
			FieldSpec.createCustomField(tag, "Label", new FieldTypeNormal()),
		};
		Bulletin missingCustom = new Bulletin(security);
		Bulletin hasFullCustom = new Bulletin(security, publicFields, StandardFieldSpecs.getDefaultBottomSectionFieldSpecs());
		hasFullCustom.set(tag, "blah blah blah");
		
		String[] tags = {tag};
		SortableBulletinList list = new SortableBulletinList(localization, tags);
		list.add(missingCustom);
		list.add(hasFullCustom);
		
		PartialBulletin[] result = list.getSortedPartialBulletins();
		assertEquals("Missing not first?", "", result[0].getData(tag));
		assertEquals("Full not last?", hasFullCustom.get(tag), result[1].getData(tag));
		
	}
	
}

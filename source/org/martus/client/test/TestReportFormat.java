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

package org.martus.client.test;

import org.martus.client.reports.ReportFormat;
import org.martus.util.TestCaseEnhanced;


public class TestReportFormat extends TestCaseEnhanced
{
	public TestReportFormat(String name)
	{
		super(name);
	}

	public void testDetailSection()
	{
		String sampleDetailSection = "blah blah blah";
		ReportFormat rf = new ReportFormat(sampleDetailSection);
		assertEquals(sampleDetailSection, rf.getDetailSection());
	}
}

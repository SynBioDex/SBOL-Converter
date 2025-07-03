package org.sbolstandard.converter.tests;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AllFilesTest {


	
	@Test
	public void TestSBOL2AllFiles() throws Exception {
		//TestUtil.runTestSuiteFile2_to_3(new File("../SBOLTestSuite/SBOL2/SequenceConstraintOutput.xml"));
		testRecursive(new File("../SBOLTestSuite/SBOL2/"));
		
	}
	
	private static void testRecursive(File folder) throws Exception
	{
		File[] files=folder.listFiles();
		for (int i=0;i<files.length;i++)
		{
			File file=files[i];
			if (file.isDirectory())
			{
				testRecursive(folder);
			}
			else
			{	
				System.out.println ("**********************************************");
				System.out.println ("Testing " + file.getName());
				
				List<String> errors=TestUtil.roundTripConvert(file);
				if (errors!=null && errors.size()>0)
				{
					TestUtil.DisplayErrors(errors);
					System.out.println ("Tested " + file.getName());
					System.out.println ("////////////////////////////////////////////");
				}
			}
		}
	}

}

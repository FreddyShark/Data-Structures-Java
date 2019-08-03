import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ExtraTestAssignment 
{
	// Set up JUnit to be able to check for expected exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// This will make it a bit easier for us to make Date objects
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	// This will make it a bit easier for us to make Date objects
	private static Date getDate(String s) 
	{
		try {
			return df.parse(s);
		} catch (ParseException e)
		{
			e.printStackTrace();
			fail("The test case is broken, invalid SimpleDateFormat parse");
		}
		// unreachable
		return null;
	}
	
	// helper method to compare two Submissions using assertions
	private static void testHelperEquals(Submission expected, Submission actual) {
		assertEquals(expected.getUnikey(), actual.getUnikey());
		assertEquals(expected.getTime(), actual.getTime());
		assertEquals(expected.getGrade(), actual.getGrade());
	}

	// helper method to compare two Submissions using assertions
	private static void testHelperEquals(String unikey, Date timestamp, Integer grade, Submission actual) {
		assertEquals(unikey, actual.getUnikey());
		assertEquals(timestamp, actual.getTime());
		assertEquals(grade, actual.getGrade());
	}
	
	// helper method that adds a new appointment AND checks the return value is correct
	private static Submission testHelperAdd(SubmissionHistory history, String unikey, Date timestamp, Integer grade) {
		Submission s = history.add(unikey, timestamp, grade);
		testHelperEquals(unikey, timestamp, grade, s);
		return s;
	}

//////////////////////////////////////////////////////////////////////////////////////////
//////////////////////			MY TEST CASES			/////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////
	@Test(timeout = 100)
	public void testAddingEntries()
	{
		Assignment ass1 = new Assignment();
	
		/*		unikey tests	*/
		//only digits
		Submission a = testHelperAdd(ass1, "000", new Date(12000) , new Integer(70));
		//contains symbols
		Submission b = testHelperAdd(ass1, "b*43-5", new Date(12000) , new Integer(70));	
		//single digit
		Submission c = testHelperAdd(ass1, "9", new Date(12000), new Integer(70));		
		//starts with symbol
		Submission d = testHelperAdd(ass1, "-*m;", new Date(12000) , new Integer(70));
			
		testHelperEquals("000", new Date(12000), new Integer(70), a);
		testHelperEquals("b*43-5", new Date(12000), new Integer(70), b);
		testHelperEquals("9", new Date(12000), new Integer(70), c);
		testHelperEquals("-*m;", new Date(12000), new Integer(70), d);
		
		/*		grade tests		*/
		//multiple zeros
		Submission f = testHelperAdd(ass1, "aaa34", new Date(12000) , new Integer(000));
		testHelperEquals("aaa34", new Date(12000) , new Integer(0), f);
		//negative value 
		//(adapted from provided test case)
		try {
			ass1.add("aaa34", getDate("2016/09/03 13:00:00"), -49);
			fail("did not throw IllegalArgumentException when adding with a negative grade");
		} catch (IllegalArgumentException e) {
			; //do nothing, this is the exception we wanted to see
		}
			
	}
	
	@Test(timeout = 100)
	public void testLargeDataSet()
	{
		// add some known testable cases with helper method
		Assignment ass1 = makeTestCases();
		
		String[] unikeys = {"a","b","c","f","g","h","i","j","k","l","m","n"}; 
		
		// generate 25 submissions for each unikey to make data set large
		for (String unikey: unikeys)
		{
			for (int i = 0; i < 25; i++)
			{
				//use of helper method to generate random entries (at bottom of this class)
				int day = randBetween(10, 27);
				int hour = randBetween(10, 23);
				int minute = randBetween(10, 59);
				int second = randBetween(10, 59);
				int score = randBetween(0, 60);
			
				testHelperAdd(ass1, unikey, getDate("2017/07/" + day + " " + hour + ":" + minute + ":" + second), score);
			}
		}
		
		//check number of students
		assertEquals(14, ass1.numOfStudents());
		//check number of entries for some students
		assertEquals(25, ass1.howManySubmissions("m"));		//only entries randomly generated
		assertEquals(25, ass1.howManySubmissions("l"));
		assertEquals(25, ass1.howManySubmissions("k"));
		assertEquals(1, ass1.howManySubmissions("d"));		//single entries
		assertEquals(1, ass1.howManySubmissions("e"));
		assertEquals(29, ass1.howManySubmissions("a"));		//randomly generated plus specifically created
		assertEquals(27, ass1.howManySubmissions("b"));
		assertEquals(28, ass1.howManySubmissions("f"));

		//check best grade
		assertEquals(new Integer(100), ass1.getBestGrade("a"));
		assertEquals(new Integer(100), ass1.getBestGrade("b"));
		assertEquals(new Integer(100), ass1.getBestGrade("c"));
		assertEquals(new Integer(97), ass1.getBestGrade("d"));
		assertEquals(new Integer(88), ass1.getBestGrade("e"));
		assertEquals(new Integer(75), ass1.getBestGrade("f"));
		
		Submission a0 = new SubmissionEntry("a", getDate("2017/07/29 12:00:00") , new Integer(87));
		Submission b0 = new SubmissionEntry("b", getDate("2017/07/29 08:59:32") , new Integer(90));
		Submission c0 = new SubmissionEntry("c", getDate("2017/07/29 06:24:32") , new Integer(100));
		Submission d0 = new SubmissionEntry("d", getDate("2017/07/29 00:25:00") , new Integer(97));
		Submission f0 = new SubmissionEntry("f", getDate("2017/07/29 06:00:00") , new Integer(65));
		
		//check submission before deadline
		testHelperEquals(a0, ass1.getSubmissionBefore("a", getDate("2017/07/29 12:00:00")));
		testHelperEquals(b0, ass1.getSubmissionBefore("b", getDate("2017/07/29 12:00:00")));
		testHelperEquals(c0, ass1.getSubmissionBefore("c", getDate("2017/07/29 12:00:00")));
		testHelperEquals(d0, ass1.getSubmissionBefore("d", getDate("2017/07/29 12:00:00")));
		assertNull(ass1.getSubmissionBefore("e", getDate("2017/07/29 12:00:00")));
		testHelperEquals(f0, ass1.getSubmissionBefore("f", getDate("2017/07/29 12:00:00")));
		
		Submission a1 = new SubmissionEntry("a", getDate("2017/07/29 12:27:35") , new Integer(100));
		Submission b1 = new SubmissionEntry("b", getDate("2017/07/30 12:24:32") , new Integer(100));
		Submission c1 = new SubmissionEntry("c", getDate("2017/07/30 12:24:25") , new Integer(93));
		Submission d1 = new SubmissionEntry("d", getDate("2017/07/29 00:25:00") , new Integer(97));
		Submission e1 = new SubmissionEntry("e", getDate("2017/07/29 14:00:00") , new Integer(88));
		Submission f1 = new SubmissionEntry("f", getDate("2017/07/29 06:00:00") , new Integer(65));
		
		//check final submission
		testHelperEquals(a1, ass1.getSubmissionFinal("a"));
		testHelperEquals(b1, ass1.getSubmissionFinal("b"));
		testHelperEquals(c1, ass1.getSubmissionFinal("c"));
		testHelperEquals(d1, ass1.getSubmissionFinal("d"));
		testHelperEquals(e1, ass1.getSubmissionFinal("e"));
		testHelperEquals(f1, ass1.getSubmissionFinal("f"));
		
		//check topStudentsList (from provided test cases)
		List<String> expected = Arrays.asList("a","b","c");
		List<String> actual = ass1.listTopStudents();
		
		Collections.sort(actual);
		Collections.sort(expected);

		assertEquals(expected, actual);
		
		//check known regressions
		List<String> expectedRegress = Arrays.asList("c","f");
		List<String> noRegression = Arrays.asList("a", "b", "d", "e");
		
		assertTrue(ass1.listRegressions().containsAll(expectedRegress));	
		assertTrue(!ass1.listRegressions().containsAll(noRegression));
		
	 }
	
	@Test(timeout = 100)
	public void testBestGradeWithAddRemove()
	{
		Assignment ass2 = makeTestCases();
		
		//add some extra cases and test highest score
		testHelperAdd(ass2, "a", getDate("2017/07/25 12:24:32"), 32);	//add lower grade before best
		assertEquals(new Integer(100), ass2.getBestGrade("a"));
		testHelperAdd(ass2, "b", getDate("2017/07/31 00:00:00"), 212);	//add higher grade after best
		assertEquals(new Integer(212), ass2.getBestGrade("b"));
		testHelperAdd(ass2, "c", getDate("2017/07/31 12:24:32"), 25);	//add lower grade after best
		assertEquals(new Integer(100), ass2.getBestGrade("c"));
		testHelperAdd(ass2, "f", getDate("2017/07/31 00:00:01"), 102);	//add higher grade before best
		assertEquals(new Integer(102), ass2.getBestGrade("f"));
		testHelperAdd(ass2, "a", getDate("2017/07/23 12:24:00"), 145);	//multiple additions on 1 student
		testHelperAdd(ass2, "a", getDate("2017/07/24 12:24:59"), 174);
		assertEquals(new Integer(174), ass2.getBestGrade("a"));
		testHelperAdd(ass2, "a", getDate("2017/07/22 12:00:32"), 30000);	//large grade (same student)
		assertEquals(new Integer(30000), ass2.getBestGrade("a"));
		testHelperAdd(ass2, "q", getDate("2017/07/31 00:02:03"), 0);	// edge case 0 (first entry)
		assertEquals(new Integer(0), ass2.getBestGrade("q"));
		testHelperAdd(ass2, "q", getDate("2017/07/30 00:02:03"), 55);
		assertEquals(new Integer(55), ass2.getBestGrade("q"));
		testHelperAdd(ass2, "g", getDate("2017/07/29 00:25:01"), 87);
		testHelperAdd(ass2, "g", getDate("2017/07/29 00:25:02"), 97);	// same score submissions
		testHelperAdd(ass2, "g", getDate("2017/07/29 00:25:03"), 97);
		testHelperAdd(ass2, "g", getDate("2017/07/29 00:24:04"), 97);
		assertEquals(new Integer(97), ass2.getBestGrade("g"));
		
		Submission q1 = new SubmissionEntry("q", getDate("2017/07/30 00:02:03") , new Integer(55));
		ass2.remove(q1);
		assertEquals(new Integer(0), ass2.getBestGrade("q"));	// 0 best grade
		Submission q2 = new SubmissionEntry("q", getDate("2017/07/31 00:02:03") , new Integer(0));
		ass2.remove(q2);
		assertNull(ass2.getBestGrade("q"));		// q has no submissions
		Submission a1 = new SubmissionEntry("a", getDate("2017/07/22 12:00:32") , new Integer(30000));
		Submission a2 = new SubmissionEntry("a", getDate("2017/07/23 12:24:00") , new Integer(145));
		Submission a3 = new SubmissionEntry("a", getDate("2017/07/24 12:24:59") , new Integer(174));
		ass2.remove(a1);
		ass2.remove(a2);
		ass2.remove(a3);
		assertEquals(new Integer(100), ass2.getBestGrade("a"));
		Submission g1 = new SubmissionEntry("g", getDate("2017/07/29 00:25:02") , new Integer(97));
		Submission g2 = new SubmissionEntry("g", getDate("2017/07/29 00:25:03") , new Integer(97));
		Submission g3 = new SubmissionEntry("g", getDate("2017/07/29 00:24:04") , new Integer(97));
		ass2.remove(g1);
		assertEquals(new Integer(97), ass2.getBestGrade("g"));
		ass2.remove(g2);
		assertEquals(new Integer(97), ass2.getBestGrade("g"));
		ass2.remove(g3);
		assertEquals(new Integer(87), ass2.getBestGrade("g"));
	}
	
	@Test(timeout = 100)
	public void testFinalSubmissionWithAddRemove()
	{
		Assignment ass2 = makeTestCases();
		
		//add extra cases and test add and remove
		testHelperAdd(ass2, "a", getDate("2017/07/30 12:24:32"), 32);	//add lower grade after best
		testHelperAdd(ass2, "b", getDate("2017/07/31 00:00:00"), 212);	//add higher grade after best
		testHelperAdd(ass2, "c", getDate("2017/07/31 12:24:32"), 25);	//add lower grade after best
		testHelperAdd(ass2, "a", getDate("2017/07/23 12:24:00"), 145);	//multiple additions on 1 student
		testHelperAdd(ass2, "a", getDate("2017/07/24 12:24:59"), 174);
		testHelperAdd(ass2, "a", getDate("2017/07/22 12:00:32"), 30000);	//large grade (same student)
		testHelperAdd(ass2, "q", getDate("2017/07/31 00:02:03"), 0);	// edge case 0 (first entry)
		testHelperAdd(ass2, "q", getDate("2017/07/30 00:02:03"), 55);
	
		testHelperEquals("a", getDate("2017/07/30 12:24:32"), new Integer(32), ass2.getSubmissionFinal("a"));
		testHelperEquals("b", getDate("2017/07/31 00:00:00"), new Integer(212), ass2.getSubmissionFinal("b"));
		testHelperEquals("c", getDate("2017/07/31 12:24:32"), new Integer(25), ass2.getSubmissionFinal("c"));
		testHelperEquals("q", getDate("2017/07/31 00:02:03"), new Integer(0), ass2.getSubmissionFinal("q"));
		
		
		Submission q1 = new SubmissionEntry("q", getDate("2017/07/30 00:02:03") , new Integer(55));
		ass2.remove(q1);
		Submission q2 = new SubmissionEntry("q", getDate("2017/07/31 00:02:03") , new Integer(0));
		ass2.remove(q2);
		assertNull(ass2.getSubmissionFinal("q"));		// q has no submissions
		Submission a1 = new SubmissionEntry("a", getDate("2017/07/30 12:24:32") , new Integer(32));
		ass2.remove(a1);
		testHelperEquals("a", getDate("2017/07/29 12:27:35"), new Integer(100), ass2.getSubmissionFinal("a"));
	}
	
	@Test(timeout = 100)
	public void testDeadlineSubmissionWithAddRemove()
	{
		Assignment ass2 = makeTestCases();
		
		//add some extra cases and test deadLine submissions after remove
		testHelperAdd(ass2, "g", getDate("2017/07/29 00:25:01"), 87);
		testHelperAdd(ass2, "g", getDate("2017/07/29 00:25:02"), 97);	// same score submissions
		testHelperAdd(ass2, "g", getDate("2017/07/29 00:25:03"), 97);
		
		
		Submission az = new SubmissionEntry("a", getDate("2017/07/29 12:00:00"), new Integer(87));
		ass2.remove(az);
		testHelperEquals("a", getDate("2017/07/29 07:24:32"), 100, ass2.getSubmissionBefore("a", getDate("2017/07/29 12:00:00")));
		Submission cz = new SubmissionEntry("c", getDate("2017/07/29 06:24:32"), new Integer(93));
		ass2.remove(cz);
		//test null with entries still existing after deadline for given unikey
		assertNull(ass2.getSubmissionBefore("c", getDate("2017/07/29 12:00:00")));
		//test null for non-existent entry
		assertNull(ass2.getSubmissionBefore("j",getDate("2017/07/29 12:00:00")));
		//on deadline
		testHelperEquals("g", getDate("2017/07/29 00:25:02"), 97, ass2.getSubmissionBefore("g", getDate("2017/07/29 00:25:02")));
		Submission gz = new SubmissionEntry("g", getDate("2017/07/29 00:25:02"), new Integer(97));
		ass2.remove(gz);
		testHelperEquals("g", getDate("2017/07/29 00:25:01"), 87, ass2.getSubmissionBefore("g", getDate("2017/07/29 00:25:02")));
		//change deadline
		testHelperEquals("g", getDate("2017/07/29 00:25:03"), 97, ass2.getSubmissionBefore("g", getDate("2017/07/29 00:25:03")));	
	}
	
	@Test(timeout = 100)
	public void testListedEdgeCases()
	{
		Assignment ass3 = new Assignment();
		
		testHelperAdd(ass3, "aa", getDate("2017/07/22 12:12:12"), new Integer(0));	//top score 0
		testHelperAdd(ass3, "aa", getDate("2017/07/23 12:12:12"), new Integer(0));
		testHelperAdd(ass3, "bb", getDate("2017/07/23 12:12:12"), new Integer(0));
		
		//check topStudentsList 
		List<String> expected = Arrays.asList("aa","bb");
		List<String> actual = ass3.listTopStudents();
				
		assertEquals(expected, actual);
		
		testHelperAdd(ass3, "bb", getDate("2017/07/22 12:12:12"), new Integer(3));
				
		//check known regressions
		List<String> expectedRegress = Arrays.asList("bb");
		List<String> noRegression = Arrays.asList("aa");
				
		assertTrue(ass3.listRegressions().containsAll(expectedRegress));	
		assertTrue(!ass3.listRegressions().containsAll(noRegression));	// also checks equal scores not included in regression
		
		//Test empty cases
		//no regressions
		Submission bb0 = new SubmissionEntry("bb", getDate("2017/07/22 12:12:12") , new Integer(3));
		ass3.remove(bb0);
		assertTrue(ass3.listRegressions().isEmpty());
		//no submissions (no top scores)
		Submission aa0 = new SubmissionEntry("aa", getDate("2017/07/22 12:12:12") , new Integer(0));
		Submission aa1 = new SubmissionEntry("aa", getDate("2017/07/23 12:12:12") , new Integer(0));
		Submission bb1 = new SubmissionEntry("bb", getDate("2017/07/23 12:12:12") , new Integer(0));
		ass3.remove(aa0);
		ass3.remove(aa1);
		ass3.remove(bb1);
		assertTrue(ass3.listTopStudents().isEmpty());	
	}
	
	//method creates 13 test cases after 2017/07/27 23:59:59 with score >= 60
	public Assignment makeTestCases()
	{
		Assignment ass1 = new Assignment();
		
		testHelperAdd(ass1, "a", getDate("2017/07/29 12:24:32"), 92);	
		testHelperAdd(ass1, "a", getDate("2017/07/29 12:27:35"), 100);	//final
		testHelperAdd(ass1, "a", getDate("2017/07/29 07:24:32"), 100);
		testHelperAdd(ass1, "a", getDate("2017/07/29 12:00:00"), 87);	//on deadline
		testHelperAdd(ass1, "b", getDate("2017/07/30 12:24:32"), 100);	//final
		testHelperAdd(ass1, "b", getDate("2017/07/29 08:59:32"), 90);	//before deadline
		testHelperAdd(ass1, "c", getDate("2017/07/29 06:24:32"), 100);	//before deadline
		testHelperAdd(ass1, "c", getDate("2017/07/30 12:24:25"), 93);	//regression and final
		testHelperAdd(ass1, "d", getDate("2017/07/29 00:25:00"), 97);	//single entry before deadline
		testHelperAdd(ass1, "e", getDate("2017/07/29 14:00:00"), 88);	//single entry after deadline
		testHelperAdd(ass1, "f", getDate("2017/07/29 02:00:00"), 75);
		testHelperAdd(ass1, "f", getDate("2017/07/29 04:00:00"), 60);
		testHelperAdd(ass1, "f", getDate("2017/07/29 06:00:00"), 65); //regression and final before deadline
		
		return ass1;
	}
	    
	/**this method was taken from
	 *https://stackoverflow.com/questions/3985392/generate-random-date-of-birth
	 *generates random numbers for defined range
	 * @param start int beginning of range
	 * @param end int end of range
	 * @return a random int
	 */
	public static int randBetween(int start, int end)
	{
	    return start + (int)Math.round(Math.random() * (end - start));
	}
}

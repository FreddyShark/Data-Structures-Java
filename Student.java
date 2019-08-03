import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Student class creates instances of students
 * stores all submissions for that student and provides access
 * @author fady
 */

public class Student
{
	//student identifier
	private String unikey;
	//Binary Search Tree to store submissions (key = date) (value = grade)
	private TreeMap<Date, Integer> submissionsByDate;
	//Binary Search Tree to store submissions (key = grade) (value = # of submissions with that grade)
	private TreeMap<Integer, Integer> submissionsByGrade;
	
	/**
	 * constructor
	 * @param unikey unique String identifier of student
	 */
	public Student(String unikey)
	{
		this.unikey = unikey;
		
		submissionsByDate = new TreeMap<Date, Integer>();
		submissionsByGrade = new TreeMap<Integer, Integer>();
	}

	/**
	 * adds a submission to the student's record
	 * @param anEntry a submission made (an instance of a SubmissionEntry) 
	 */
	public void addEntry(Submission anEntry)
	{
		//add to date keyed tree
		submissionsByDate.put(anEntry.getTime(), anEntry.getGrade());
		
		/* add to grade keyed tree */
		
		//if the grade is unique (has not occurred before)
		if (!submissionsByGrade.containsKey(anEntry.getGrade()))
			submissionsByGrade.put(anEntry.getGrade(), 1);
		
		//if grade has occurred before, replace value with incremented value
		else
		{
			int update = submissionsByGrade.get(anEntry.getGrade());
			update++;
			submissionsByGrade.replace(anEntry.getGrade(), update);
		}
	}
	
	/**
	 * removes a submission from the student's record
	 * @param anEntry a submission made (an instance of a SubmissionEntry)
	 */
	public void removeEntry(Submission anEntry)
	{
		//remove from Date keyed tree
		submissionsByDate.remove(anEntry.getTime());
		
		/* remove from grade keyed tree */
		
		//if the grade occurred with multiple submissions, minus 1 to the counter value in tree
		int gradeOccurances = submissionsByGrade.get(anEntry.getGrade());
		if (gradeOccurances > 1)
		{
			gradeOccurances--;
			submissionsByGrade.replace(anEntry.getGrade(), gradeOccurances);
		}
		
		//else remove the grade from grade keyed tree
		else
			submissionsByGrade.remove(anEntry.getGrade());
	}
	
	/**
	 * retrieves  last submission before or on deadline time made by student
	 * @param deadline a time of type Date
	 * @return a submission made (an instance of a SubmissionEntry)
	 */
	public Submission getDeadlineSubmission(Date deadline)
	{
		// retrieve map entry with key value <= deadline date
		Map.Entry<Date, Integer> deadlineSubmission = submissionsByDate.floorEntry(deadline);
		
		//check if a submission was made before the deadline
		if (deadlineSubmission == null)
			return null;
		
		//create a SubmissionEntry instance with retrieved values to return
		Submission entry = new SubmissionEntry(unikey, deadlineSubmission.getKey(), deadlineSubmission.getValue());
		return entry;
	}
	
	/**
	 * retrieves the last submission made by student
	 * @return a submission made (an instance of a SubmissionEntry)
	 */
	public Submission getLastSubmission()
	{
		//retrieve map entry with greatest key value (latest date)
		Map.Entry<Date, Integer> lastSubmission = submissionsByDate.lastEntry();
		//create a SubmissionEntry instance with retrieved values to return
		Submission entry = new SubmissionEntry(unikey, lastSubmission.getKey(), lastSubmission.getValue());
		return entry;
	}
	
	/**
	 * retrieves the highest grade achieved by the student from all submissions
	 * @return an Integer grade
	 */
	public Integer getTopGrade() 
	{
		return submissionsByGrade.lastKey();
	}

	/**
	 * method returns number of submissions made by the student
	 * @return number of submissions for student
	 */
	public int numOfSubmissions()
	{
		return submissionsByDate.size();
	}
}

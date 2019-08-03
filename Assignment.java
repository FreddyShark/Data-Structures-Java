
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class Assignment implements SubmissionHistory 
{
	// Binary Search Tree to hold all students (key = unikey) (value = Student instance)
	private TreeMap<String, Student> students;
	
	/**
	 * constructor
	 * creates TreeMap for student storage
	 */
	public Assignment() 
	{ 
		students  = new TreeMap<String, Student>();
	}
	
	@Override
	public Integer getBestGrade(String unikey) 
	{
		if (unikey == null)
			throw new IllegalArgumentException();
		
		try
		{
			//retrieve student object and call top grade method
			return students.get(unikey).getTopGrade();
		}
		catch (NullPointerException e)	//if entry does not exist
		{
			return null;
		}
	}

	@Override
	public Submission getSubmissionFinal(String unikey) 
	{
		if (unikey == null)
			throw new IllegalArgumentException();
		
		try
		{
			//retrieve student object and call last submission method
			return students.get(unikey).getLastSubmission();
		}
		catch (NullPointerException e)	//if entry does not exist
		{
			return null;
		}
	}

	@Override
	public Submission getSubmissionBefore(String unikey, Date deadline) 
	{
		if (unikey == null || deadline == null)
			throw new IllegalArgumentException();
		
		try
		{
			//retrieve student object and call deadline submission method.
			return students.get(unikey).getDeadlineSubmission(deadline);
		}
		catch (NullPointerException e)	//if entry does not exist
		{
			return null;
		}
		
	}

	@Override
	public Submission add(String unikey, Date timestamp, Integer grade)  
	{
		if (unikey == null || timestamp == null || grade == null || grade < 0)
			throw new IllegalArgumentException();
		
		//create new SubmisssionEntry instance
		Submission anEntry = new SubmissionEntry(unikey, timestamp, grade);
		
		if (!students.containsKey(unikey))
		{
			//create new student instance
			Student aStudent = new Student(unikey);
			//add student to this class' tree
			students.put(unikey, aStudent);
			//add the submission entry into the student object
			aStudent.addEntry(anEntry);
		}
		else
			//get corresponding student object and add the submission entry into it.
			(students.get(unikey)).addEntry(anEntry);
		
		return anEntry;
	}

	@Override
	public void remove(Submission submission) 
	{
		if (submission == null)
			throw new IllegalArgumentException();
		
		/*get unikey associated with submission,  
		  use it  to retrieve student object from tree
		  and remove the submission from it */
		students.get(submission.getUnikey()).removeEntry(submission);	
		
		//if student has no other submission, remove student from treeMap record (helper method at bottom of class)
		if (howManySubmissions(submission.getUnikey()) == 0)
			students.remove(submission.getUnikey());
	}

	@Override
	public List<String> listTopStudents() 
	{
		int topGrade = 0;
		LinkedList<String> topStudents = new LinkedList<String>();
		
		//iterate through keys
		for (String unikey : students.keySet())
		{
			int currentGrade = students.get(unikey).getTopGrade();
			
			// if student has top grade add them to list
			if (currentGrade == topGrade)
			{
				topStudents.offer(unikey);
			}
			
			// if student has better grade, clear list and insert student
			if (currentGrade > topGrade)
			{
				topGrade = currentGrade;
				topStudents.clear();
				topStudents.offer(unikey);
			}
		}
		return topStudents;
	}

	@Override
	public List<String> listRegressions() 
	{ 	
		LinkedList<String> regressedStudents = new LinkedList<String>();
		
		//iterate through keys
		for (String unikey : students.keySet())
		{
			Student aStudent = students.get(unikey);
			
			if (aStudent.getTopGrade() > aStudent.getLastSubmission().getGrade())
				regressedStudents.add(unikey);
		}
		return regressedStudents;
	}
	
	/**
	 * method for testing and code clarity
	 * @return number of submissions for student specified
	 */
	public int howManySubmissions(String unikey)
	{
		return students.get(unikey).numOfSubmissions();
	}
	/**
	 * method for testing
	 * @return number of students who made a submission
	 */
	public int numOfStudents()
	{
		return students.size();
	}
	
}

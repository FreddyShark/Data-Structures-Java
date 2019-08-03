import java.util.Date;

/**
 * SubmissionEntry class is an implementation of the Submission interface
 * instances are individual submissions by students
 * @author fady
 */
public class SubmissionEntry implements Submission 
{
	private String unikey = null;;
	private Date time = null;
	private Integer grade = null;	
	
	/**
	 * constructor
	 * initiates unikey, time and grade values for a SubmissionEntry object
	 * */
	public SubmissionEntry(String unikey, Date time, Integer grade)
	{
		if (unikey == null || time == null || grade == null  || grade < 0)
			throw new IllegalArgumentException();
		this.unikey = unikey;
		this.time = time;
		this.grade = grade;
	}
	
	@Override
	public String getUnikey() 
	{
		return unikey;
	}

	@Override
	public Date getTime() 
	{
		return time;
	}

	@Override
	public Integer getGrade() 
	{
		return grade;
	}
	
}

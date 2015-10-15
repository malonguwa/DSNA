package au.edu.uwa.csse;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CodeFile {

	public String fileName;
	private Map<Developer, Integer> developers = new HashMap<Developer, Integer>(); 
	private Map<Developer, Date> modifyTimes = new HashMap<Developer, Date>(); 
	
	
	public CodeFile(String fileName) {
		this.fileName = fileName;
	}
	
	public String toString()
	{
		return this.fileName;
	}
	
	void addDeveloper(Developer developer, Date modifyDate)
	{
		if (this.developers.containsKey(developer))
		{
			this.developers.put(developer, this.developers.get(developer)+1);
			Date d1 = this.modifyTimes.get(developer);
			if (modifyDate.before(d1))
				this.modifyTimes.put(developer, modifyDate);
		} else
		{
			this.developers.put(developer, 1);
			this.modifyTimes.put(developer, modifyDate);
		}
		developer.addCodeFile(this);
	}
	
	public Map<Developer, Integer> getDevelopers()
	{
		return this.developers;
	}
	
	public Map<Developer, Date> getModifyTimes()
	{
		return this.modifyTimes;
	}
	
	
	
}

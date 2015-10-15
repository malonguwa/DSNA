package au.edu.uwa.csse;

import java.util.Date;
import java.util.Map;

public class EditInfo {
	public Developer developer;
	public CodeFile codeFile;
	public Date time;
	final String DEVELOPER_PREFIX = "DEVELOPER=\"";
	final String CODEFILE_PREFIX = "CODEFILE=\"";
	final String TIME_PREFIX = "TIME=\"";
	
	public EditInfo()
	{
	}
	
	public EditInfo(String str, Map<String, Developer> developers, Map<String, CodeFile> codeFiles)
	{
		int startPos = str.indexOf(DEVELOPER_PREFIX);
		if (startPos == -1)
			return;
		startPos = startPos + DEVELOPER_PREFIX.length();
		int endPos = str.indexOf("\", ", startPos);
		if (endPos == -1)
			return;
		String developerName = str.substring(startPos, endPos);
		this.developer = null;
		if (developers.containsKey(developerName))
			developer = developers.get(developerName);
		else
		{
			developer = new Developer(developerName);
			developers.put(developerName, developer);
		}
		
		startPos = str.indexOf(CODEFILE_PREFIX);
		if (startPos == -1)
			return;
		startPos = startPos + CODEFILE_PREFIX.length();
		endPos = str.indexOf("\",", startPos);
		if (endPos == -1)
			return;
		String codeFileName = str.substring(startPos,endPos);
		this.codeFile = null;
		
		startPos = str.indexOf(TIME_PREFIX);
		if (startPos == -1)
			return;
		startPos = startPos + TIME_PREFIX.length();
		endPos = str.indexOf("\"]", startPos);
		if (endPos == -1)
			return;
		this.time = new Date(str.substring(startPos, endPos));
		
		if (codeFiles.containsKey(codeFileName))
		{
			this.codeFile = codeFiles.get(codeFileName);
			codeFile.addDeveloper(developer, time);
		}
		else
		{
			this.codeFile = new CodeFile(codeFileName);
			codeFile.addDeveloper(developer, time);
			codeFiles.put(codeFileName, this.codeFile);
		}
	}
	
	public EditInfo(Developer developer, CodeFile codeFile, Date time)
	{
		this.developer = developer;
		this.codeFile = codeFile;
		this.time = time;
	}

	@Override
	public String toString() {
		return "EditInfo ["+ DEVELOPER_PREFIX + developer + "\", " + CODEFILE_PREFIX + codeFile
				+ "\"," + TIME_PREFIX + time + "\"]";
	}

}

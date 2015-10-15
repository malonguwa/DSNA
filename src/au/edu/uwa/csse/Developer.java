package au.edu.uwa.csse;

import java.util.ArrayList;
import java.util.List;

public class Developer {
	public String developerName;
	private List<CodeFile> codeFiles = new ArrayList<CodeFile>();
	
	public Developer(String developerName) {
		this.developerName = developerName;
	}
	
	public String toString()
	{
		return this.developerName;
	}
	
	public void addCodeFile(CodeFile codeFile)
	{
		this.codeFiles.add(codeFile);
	}
	
	public List<CodeFile> getCodeFiles()
	{
		return this.codeFiles;
	}
	
	public boolean compareTo(Developer d2)
	{
		return this.developerName.equals(d2.developerName);
	}

}

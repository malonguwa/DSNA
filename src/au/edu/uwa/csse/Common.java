package au.edu.uwa.csse;

import java.util.HashSet;
import java.util.Set;

public class Common {
	static public Set<String> APPENDIX = new HashSet<String>() {
		{
			add("cpp");
			add("java");
			add("c");
			add("h");
			add("cxx");
			add("hpp");
			add("sh");
			add("py");
			add("pl");
		}
	};
	
	static public boolean isTarget(String fileName)
	{
		for (String appendix : APPENDIX)
		{
			if (fileName.endsWith(appendix))
			{
				return true;
			}
		}
		return false;
	}

}

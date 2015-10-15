package au.edu.uwa.csse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CVSLogExtractor {

	public String CVSRROOT = "";
	public static Map<String, Developer> developers = new HashMap<String, Developer>();
	public static Map<String, CodeFile> codeFiles = new HashMap<String, CodeFile>();

	public static void getCode(String path, String localPath, String projectName) {
		generateBATFile1(path, localPath, projectName);
		Runtime rt = Runtime.getRuntime();

		try {
			Process process = rt.exec("cmd /c start " + localPath + "/" + projectName + ".bat");

			InputStream ins = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(ins));
			String msg = "";
			String line = "";
			while ((line = br.readLine()) != null) {
				msg += line + "\n";
			}
			System.out.println(msg);

			ins = process.getErrorStream();
			br = new BufferedReader(new InputStreamReader(ins));
			msg = "";
			line = "";
			while ((line = br.readLine()) != null) {
				msg += line + "\n";
			}
			System.out.println(msg);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void getLog(String path, String localPath, String projectName) {
		generateBATFile2(projectName, localPath, projectName);
		Runtime rt = Runtime.getRuntime();

		try {
			Process process = rt.exec("cmd /c start " + localPath + "\\" + projectName + ".bat");

			InputStream ins = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(ins));
			String msg = "";
			String line = "";
			while ((line = br.readLine()) != null) {
				msg += line + "\n";
			}
			System.out.println(msg);

			ins = process.getErrorStream();
			br = new BufferedReader(new InputStreamReader(ins));
			msg = "";
			line = "";
			while ((line = br.readLine()) != null) {
				msg += line + "\n";
			}
			System.out.println(msg);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void analysisLog(String projectName, String localPath)
	{
		List<String> fileList = getLogFile(projectName + "_log", localPath);
		List<EditInfo> editInfo = null;
		PrintWriter pw;
		try {
			pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(localPath + "\\" + projectName + ".DSN", false)));
			for (String fileName : fileList)
			{
				editInfo = parseLog(fileName);
				for (EditInfo info : editInfo)
					pw.println(info);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void generateBATFile1(String path, String localPath,
			String projectName) {
		// TODO Auto-generated method stub
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(localPath + "\\" + projectName + ".bat", false)));
			String line = "set CVSROOT=" + path;
			pw.println(line);
			int pos = localPath.indexOf(":");
			line = localPath.substring(0, pos+1);
			pw.println(line);
			line = "cd " + localPath;
			pw.println(line);
			line = "\"c:\\program files\\CVSNT\\cvs\" co " + projectName;
			pw.println(line);
			line = "exit";
			pw.println(line);
			pw.close();
			System.out.println("Finish generating BAT file 1!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void generateBATFile2(String path, String localPath,
			String projectName) {
		// TODO Auto-generated method stub
		try {
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(localPath + "\\" + projectName + ".bat", false)));
			String line = "set CVSROOT=" + path;
			pw.println(line);
			int pos = localPath.indexOf(":");
			line = localPath.substring(0, pos+1);
			pw.println(line);
			line = "cd " + localPath;
			pw.println(line);
			line = "mkdir " + projectName + "_log";
			pw.println(line);
			line = "cd " + projectName;
			pw.println(line);
			if (localPath.endsWith("\\"))
				localPath = localPath.substring(0, localPath.length() - 1);
			List<String> fileList = getLogFile(projectName, localPath);
			for (String filePath : fileList) {
				int index = filePath.indexOf(projectName + "/");
				String fileName = filePath;
				if (index != -1)
					fileName = filePath.substring(index + projectName.length() + 1);
				line = "\"c:\\program files\\CVSNT\\cvs\" log \"" + fileName + "\" > \"" + localPath + "\\" + projectName + "_log\\" + fileName + ".log\"";
				pw.println(line);
			}
			line = "exit";
			pw.println(line);
			pw.close();
			System.out.println("Finish generating BAT file 2!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<String> getSubFile(String fileName, String path, String projectName) {
		List<String> fileList = new ArrayList<String>();
		int pos = path.indexOf("./" + projectName);
		String name = path;
		if (pos == 0)
		{
			name = "." + name.substring(projectName.length() + 2);
		}
		File parentF = new File(path + "/" + fileName);
		if (!parentF.exists()) {
			return fileList;
		}
		if (parentF.isFile()) {
			
			fileList.add(name + "/" + fileName);
			return fileList;
		}
		String[] subFiles = parentF.list();
		path += "/" + fileName;
		if (path.equals("./"))
			path = ".";

		for (int i = 0; i < subFiles.length; i++) {
			fileList.addAll(getSubFile(subFiles[i], path, projectName));
		}
		return fileList;
	}
	
	public static List<String> getLogFile(String fileName, String path) {
		List<String> fileList = new ArrayList<String>();
		if (path.endsWith("\\") || path.endsWith("/"))
			path = path.substring(0, path.length() - 1);
		File parentF = new File(path + "/" + fileName);
		if (!parentF.exists()) {
			return fileList;
		}
		if (parentF.isFile()) {
			fileList.add(path + "/" + fileName);
			return fileList;
		}
		String[] subFiles = parentF.list();
		path += "/" + fileName;
		if (path.equals("./"))
			path = ".";

		for (int i = 0; i < subFiles.length; i++) {
			fileList.addAll(getLogFile(subFiles[i], path));
		}
		return fileList;
	}

	public static List<EditInfo> parseLog(String logPath) {
		List<EditInfo> edits = new ArrayList<EditInfo>();
		FileReader fr;
		Pattern fileNamePattern = Pattern.compile("RCS file: (.+?),v");
		Pattern timePattern = Pattern.compile("date: (.+?);");
		Pattern authorPattern = Pattern.compile("author: (.+?);");
		CodeFile codeFile = null;
		try {
			fr = new FileReader(logPath);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				Date dateTime = null;
				Developer developer = null;
				Matcher matcher = fileNamePattern.matcher(line);
				if (matcher.find()) {
					String fileName = matcher.group(1);
					if (codeFiles.containsKey(fileName))
						codeFile = codeFiles.get(fileName);
					else {
						codeFile = new CodeFile(fileName);
						codeFiles.put(fileName, codeFile);
					}
				}
				matcher = timePattern.matcher(line);
				if (matcher.find()) {
					dateTime = new Date(matcher.group(1));
				}
				matcher = authorPattern.matcher(line);
				if (matcher.find()) {
					String developerName = matcher.group(1);
					if (developers.containsKey(developerName))
						developer = developers.get(developerName);
					else {
						developer = new Developer(developerName);
						developers.put(developerName, developer);
					}
				}
				if (null != developer && null != codeFile) {
					EditInfo editInfo = new EditInfo(developer, codeFile,
							dateTime);
					edits.add(editInfo);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return edits;
	}

	public static void main(String[] args) {
		final String svn_root = "http://svn.apache.org/repos/asf/maven/maven-3/trunk";
		final String localPath = "D:\\report\\job2012_2\\projects\\";
		final String projectName = "maven-3";
		
		try {
			FileReader fr = new FileReader("D:\\report\\job2012_2\\projects\\cvs_list.txt");
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				String segs[] = line.trim().split("\t");
				CVSLogExtractor.getCode(segs[1], localPath, segs[0]);
				//CVSLogExtractor.getLog(segs[1], localPath, segs[0]);
				//CVSLogExtractor.analysisLog(segs[0], localPath);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		/*
		CVSLogExtractor.getCode(cvs_root, localPath,
		   projectName);
		CVSLogExtractor.getLog(cvs_root, localPath,
		   projectName);
		CVSLogExtractor.analysisLog(projectName, localPath);
		*/
	}
}

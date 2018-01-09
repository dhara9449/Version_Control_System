package ase.vcs;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dhara patel (dharapatel270594@gmail.com)
 *  
 * @description FileLogs class creates the Activity folder inside the repository 
 * and adds manifest file every time a change is made in the source path.
 * 
 * @version 2.0
 **/

public class FileLogs {

	public static FileLogs getInstance() {
		return new FileLogs();
	}

	private FileLogs () {
	}

	/**
	 * @description This method logs all the files that were added/modified.
	 * 
	 * @param cmd - The command entered by the user which is to be added in the log file.
	 * @param sourcePath - The Source path of directory given by the user.
	 * @param targetPath - The target path of repository directory given by the user.
	 * @param logFactory - An ArrayList of all the artifacts that are copied or modified.
	 * 
	 * @throws IOException If the parent directory does not exist an any I/O error occurs.
	 **/

	public void log(String cmd, String sourcePath, String targetPath, List<LogFactory> logFactory, String label) throws IOException{
		ArrayList<String> filelines = new ArrayList<String>();
		LocalDateTime timeStamp = LocalDateTime.now();

		String Date = String.valueOf(timeStamp.getMonthValue()
				+ "-" +timeStamp.getDayOfMonth() + "-" 
				+timeStamp.getYear());

		String time = String.valueOf(timeStamp.getHour() + "."
				+ timeStamp.getMinute() + "." 
				+ timeStamp.getSecond()) + "_Hrs";

		String manifestfileName = "Manifest_" + Date + "_" +time+ ".txt";
		Path manifestPath = FileSystems.getDefault().getPath(targetPath 
				+ FileSystems.getDefault().getSeparator() + "Activity" 
				+ FileSystems.getDefault().getSeparator() + manifestfileName);
		Charset filecharset = StandardCharsets.US_ASCII;

		//Below method checks if Activity directory already exists or not.
		CheckAndCreateActivityDir(targetPath);

		//Below method will create new MANIFEST File inside ACTIVITY directory.
		Files.createFile(manifestPath);
		filelines.add(manifestfileName.split(".txt")[0]);
		filelines.add("Label:" + label);
		filelines.add("Manifest file creation Date and time: " + Date + "  " + timeStamp.toLocalTime());
		filelines.add("command: " + cmd);
		filelines.add("sourcePath: " + sourcePath);
		filelines.add("targetPath: " + targetPath);

		filelines.add("");

		filelines.add("List of files created is as follows: ");
		logFactory.forEach((artifact) -> {
			String line = artifact.getArtifactID() + "\t" + artifact.getFileName() + "\t" + artifact.getFilePath();
			filelines.add(line);
		});
		Files.write(manifestPath, filelines, filecharset, StandardOpenOption.APPEND);

		//This will create separate manifest file for CheckOut command

		if(cmd.equals("checkout")) {
			Path checkOutManifestPath = FileSystems.getDefault().getPath(sourcePath + FileSystems.getDefault().getSeparator() + "Activity"
					+ FileSystems.getDefault().getSeparator() + manifestfileName);
			CheckAndCreateActivityDir(sourcePath);
			if(!checkOutManifestPath.toFile().exists()) {
				Files.createFile(checkOutManifestPath);
			}
			Files.write(checkOutManifestPath, filelines, filecharset, StandardOpenOption.APPEND);
		}
	}

	/**
	 * @param repoPath - The path at which we need to check and create the activity directory.
	 * @description This method creates an Activity directory if it is not present at the path that is specified.
	 * @throws IOException if an I/O error occurs or the parent directory does not exist.
	 **/

	public void CheckAndCreateActivityDir(String repoPath) throws IOException{
		Path activityDirPath = FileSystems.getDefault().getPath(repoPath + FileSystems.getDefault().getSeparator() + "Activity");
		if(!Files.exists(activityDirPath)){
			Files.createDirectory(activityDirPath);
		}
	}
}
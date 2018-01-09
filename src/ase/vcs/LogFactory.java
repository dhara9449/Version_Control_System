package ase.vcs;

/**
 * @author Dhara patel (dharapatel270594@gmail.com)
 * 
 * @description 
 * LogFactory class is a service class for managing logs of the file. 
 * The getter methods here capture a single file with its original filename, artifact ID and absolute path of the particular file.
 * The setter methods set above mentioned values accordingly. 
**/

public class LogFactory {
	
	private String fileName = "";
	private String artifactID = "";
	private String filePath = "";

	public LogFactory(String artifactID, String fileName, String filePath) {
		this.artifactID = artifactID;
		this.fileName = fileName;
		this.filePath = filePath;
	}

	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getArtifactID() {
		return artifactID;
	}
	
	public void setArtifactID(String artifactID) {
		this.artifactID = artifactID;
	}
	
}

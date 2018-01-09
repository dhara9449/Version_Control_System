package ase.vcs;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

/**
 * @author Dhara Patel (dharapatel270594@gmail.com)
 * @version 2.0
 * 
 * @Description
 * This class uses java.nio package to navigate a folder structure tree. 
 * It extends SimpleFileVisitor for default behavior to visit all files and to re-throw all I/O errors.
 * The methods in this class calculates weighted checksum, appends file size followed by file extension and finally creates the ArtifactID.
 **/

public class ProjectTreeHandler extends SimpleFileVisitor<Path>{

	private String source, destination;
	private static long chksumweight[] = {1, 3, 7, 11, 17};
	private Stack<Path> destPathStack;
	private List<LogFactory> logFileList;

	ProjectTreeHandler(String sourcePath, String destPath) {
		this.source = sourcePath;
		this.destination = destPath;
		destPathStack = new Stack<>();
		logFileList = new ArrayList<>();
	}

	/**
	 * @Description This method calculates the weighted checksum 
	 * @param originalFile - The file whose artifact name is to be returned
	 * @return weighted checksum of input file
	 **/

	public static long getArtifactId(File originalFile) {
		int currentCharacter;
		int chksumweightLen = chksumweight.length;
		long artifactId = 0;

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(originalFile))) {
			for (int i=0; (currentCharacter = bufferedReader.read()) > 0; i++) {
				artifactId = artifactId + currentCharacter * chksumweight[i % chksumweightLen];
			}
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return artifactId;
	}

	/**
	 * @Description This method appends file size and file extension to the ArtifactID and returns the entire Artifact Name.
	 * @param file - The file whose artifact code name is to be returned
	 * @return Complete Artifact Id code name 
	 **/

	static String getArtifactName(File file) {
		long artifactid = getArtifactId(file);
		String artifactExtension = getFileExtension(file.getName());

		return String.valueOf(artifactid) + "." + file.length() + "." + artifactExtension;
	}

	/**
	 * @Description This function returns file extension which is to be appended to the artifactName.
	 * @param fileName - The file whose extension is to be returned
	 * @return Extension of the input file
	 **/

	public static String getFileExtension(String fileName) {
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i+1);
		}
		return extension;
	}

	public List<LogFactory> getLogFileList() {
		return logFileList;
	}

	/**
	 * The below necessary methods are overridden from SimpleFileVisitor Class.
	 **/

	@Override
	public FileVisitResult visitFile(Path sourceFilePath, BasicFileAttributes basicFileAttributes) {

		if (basicFileAttributes.isRegularFile()) {
			String fileName = sourceFilePath.getFileName().toString();
			String artifactName = ProjectTreeHandler.getArtifactName(sourceFilePath.toFile());

			Path targetPath = destPathStack.peek().resolve(fileName);
			targetPath.toFile().mkdirs();
			targetPath = targetPath.resolve(artifactName);

			try {
				logFileList.add(new LogFactory(artifactName, fileName, targetPath.toString().replace(destination, "")));
				Files.copy(sourceFilePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes basicFileAttributes) throws IOException {

		String folderPath = directory.toString();
		if (folderPath.equalsIgnoreCase(source + File.separatorChar + "Activity"))
			return FileVisitResult.SKIP_SUBTREE;

		destPathStack.push(Paths.get(folderPath.replace(source, destination)));

		if (!folderPath.equalsIgnoreCase(source)) {
			logFileList.add(new LogFactory("", directory.getFileName().toString(), directory.toString().replace(source, "")));
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path path, IOException ioExc) {
		destPathStack.pop();
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path path, IOException ioExc) {
		ioExc.printStackTrace();
		return FileVisitResult.CONTINUE;
	}
}




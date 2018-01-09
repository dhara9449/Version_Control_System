package ase.vcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Dhara Patel (dharapatel270594@gmail.com)
 * @Description Below class contains the methods to handle core working of the commands entered by the user. 
 * @version 1.0
 **/

public class UserCommandHandler {

	/**
	 * @Description This method handles the checkout command, asks for a label from the user and inserts that label in the manifest file created for checkout.
	 * @param it takes the source path, destination path, manifest filename and object to FileLogs class.
	 **/	
	
	public static void checkoutHandler(String src, String dest,File manifestFile, FileLogs filelog) throws IOException{

		List<LogFactory> logFactory = new ArrayList<>();
		Scanner scanner= new Scanner(System.in);

		try(BufferedReader bufferedReader2 = new BufferedReader(new FileReader(manifestFile))){
			for(int i=0; i<8; i++){
				bufferedReader2.readLine();
			}

			while (bufferedReader2.ready()) {
				String[] entry = bufferedReader2.readLine().split("\t");
				logFactory.add(new LogFactory(entry[0], entry[1], entry[2]));

				if (entry[0].isEmpty()) continue;
				Path destFile = Paths.get(dest + File.separatorChar + entry[2].replace(File.separatorChar + entry[0], ""));
				destFile.getParent().toFile().mkdirs();
				Files.copy(Paths.get(src + File.separatorChar + entry[2]), destFile, StandardCopyOption.REPLACE_EXISTING);
			}

			System.out.println("Do you want to provide label? y/n ");
			String choice2=scanner.next();

			if(choice2.equalsIgnoreCase("y")){
				System.out.println("Enter Label:");
				String checkoutLabel= scanner.next();
				filelog.log("checkout", src, dest, logFactory, checkoutLabel);
			}
			else {
				filelog.log("checkout", src, dest, logFactory, "---");
			}
			scanner.close();
		}	
	}  
	
	public static List<File> getFiles(String dest,List<File> destfileList){

		File destDirec = new File(dest);
		File[] listOfFiles = destDirec.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && (!listOfFiles[i].getName().startsWith("Manifest_"))) {
				destfileList.add(listOfFiles[i]);
			} else if (listOfFiles[i].isDirectory()) {
				getFiles(listOfFiles[i].getAbsolutePath(),destfileList);
			}
		}
		return destfileList;
	}
	
	/**
	 * @Description This method will check if there are any conflicted files by comparing the artifactIds and the filenames 
	 * 				in both the source and target repository trees. If there are no conflicted files, it will return a null list.
	 * 				otherwise, it will create three versions of the conflicted files: an R verstion _MR, a T version _MT
	 * 				and a G version _MG. 
	 * @param cmd line arguments for merge command are the Repository source tree and the target project tree where the user wants to merge the files.
	 * @return The full paths of all the conflicted files. in case if there is no conflict, it returns a null list.
	 **/	
	
	public static List<String> mergeHandler(String src, String dest) throws IOException{
		
		List<File> destfileList= new ArrayList<File>();
		destfileList =	UserCommandHandler.getFiles(dest,destfileList);
		
		List<String> destfilenamesList= new ArrayList<String>();
		List<String> destfilePathList= new ArrayList<String>();
		for(File f: destfileList){
			destfilenamesList.add(f.getName());
			destfilePathList.add(f.getAbsolutePath());   	
		}

		List<String> destartifactIdList = new ArrayList<String>();
		for(File f: destfileList){
			destartifactIdList.add(ProjectTreeHandler.getArtifactName(f));
		}

		List<File> srcfileList= new ArrayList<File>();
		srcfileList =	UserCommandHandler.getFiles(src,srcfileList);
		List<String> srcfilenamesList= new ArrayList<String>();
		List<String> srcfilePathList= new ArrayList<String>();
		for(File f: srcfileList){
			srcfilenamesList.add(f.getName());
			srcfilePathList.add(f.getAbsolutePath());
		}

		List<String> srcartifactIdList = new ArrayList<String>();
		for(File f: srcfileList){
			srcartifactIdList.add(ProjectTreeHandler.getArtifactName(f));
		}

		List<String> conflictPathList = new ArrayList<String>();
		for(int i=0; i<srcfilenamesList.size();i++){
			if(!(destartifactIdList.contains(srcartifactIdList.get(i))) && destfilenamesList.contains(srcfilenamesList.get(i))){
					
				String conflictFileName= srcfilenamesList.get(i);
				String conflictPath = srcfilePathList.get(i);

				for(int j=0;j<destfilePathList.size();j++){
					String destPath = destfilePathList.get(j);
					
					if(destPath.contains(conflictFileName)){
						conflictPathList.add(destPath);
						
						//rename the conflicting file in dest directory with"_MT"
						
						String newPath= destPath;
						File f=new File(newPath);
						String ext= ProjectTreeHandler.getFileExtension(conflictFileName);
						String[] newName= (newPath.split("."+ext));
						File rf1=new File(newName[0].concat("_MT."+ext));
						f.renameTo(rf1);

						//copy conflicting file from src path and rename with "_MR"

						Files.copy(Paths.get(conflictPath), Paths.get(destPath), StandardCopyOption.COPY_ATTRIBUTES);
						File rf2=new File(newName[0].concat("_MR."+ext));
						f.renameTo(rf2);

						//find the conflict file from grandparent directory, copy it and append with "_MG"

						String[] destManifestFiles = new File(dest + File.separatorChar + "Activity" + File.separatorChar).list();
						String destManifestPath = dest + File.separatorChar + "Activity" + File.separatorChar + destManifestFiles[0];            	
						String srcFromManifestFile;
						try(BufferedReader bufferedReader = new BufferedReader(new FileReader(destManifestPath))) {
							bufferedReader.readLine();
							bufferedReader.readLine();
							bufferedReader.readLine();
							bufferedReader.readLine();
							srcFromManifestFile = bufferedReader.readLine().split(" ")[1];
						}

						String[] parentManifestFilelist = new File(srcFromManifestFile + File.separatorChar + "Activity" + File.separatorChar).list();
						String parentManifestFile = srcFromManifestFile + File.separatorChar + "Activity" + File.separatorChar+parentManifestFilelist[0];
						try(BufferedReader bufferedReader2 = new BufferedReader(new FileReader(parentManifestFile))) {
							for(int k=0;k<8;k++){
								bufferedReader2.readLine();
							}
							Path parentPath=Paths.get("");
							while (bufferedReader2.ready()) {
								String[] entry = bufferedReader2.readLine().split("\t");
								if(conflictFileName.equalsIgnoreCase(entry[1])){
									parentPath = Paths.get(srcFromManifestFile + File.separatorChar + entry[2]);
									Files.copy(Paths.get(parentPath+""), Paths.get(destPath), StandardCopyOption.COPY_ATTRIBUTES);
									File rf3=new File(newName[0].concat("_MG."+ext));
									f.renameTo(rf3);
								}			
							}							
						}											
					  break;
					}
				}	
			}			
		}//for ends here
		return conflictPathList;		
	}	
}

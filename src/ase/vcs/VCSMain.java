package ase.vcs;

import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

/**
 * @author Dhara Patel (dharapatel270594@gmail.com)
 * 
 * @Description Below class has the main function of the Version Control System Project AKA Source Control Management Project. 
 * It implements methods for create, checkout, check-in and merge commands.
 * 
 * @version 3.0
 **/

public class VCSMain {
	private static final FileLogs filelog = FileLogs.getInstance();
	public static void main(String[] args) throws IOException {
		String cmdarg = "";
		if (args.length > 0) {
			cmdarg = args[0];
		}

		switch (cmdarg) {
		case "create":
			createProj(args);
			System.out.println("Your file structure has been copied in the specified Target Path.");
			break;
		case "checkout":
			checkOutProj(args);          
			break;
		case "checkin":
			checkinProj(args);
			break;
		case "merge":
			mergeProj(args);
			break;
		default:
			System.out.println("Use the following correct format:");
			System.out.println("java ase.scm.VCSMain <command> <sourcePath> <targetPath>");
			System.out.println("Possible values for <command> are:");
			System.out.println("1. create\n2. checkin\n3. checkout\n4. merge");
			System.out.println();
			break;
		}

	}

	/**
	 * This method executes the 'CREATE' command. It uses ProjectTreeHandler.java for navigating the tree structure,
	 * and copying the files to the Repository. It lets the user provide a label to the Manifest file.
	 * It also creates the MANIFEST file inside ACTIVITY folder using the FileLogs.java class.
	 * @param cmdargs - Command line arguments for Create Command, Source Path and Target Path
	 **/

	private static void createProj(String[] cmdargs) throws IOException {
		String source, target;
		source = cmdargs[1];
		target = cmdargs[2];

		ProjectTreeHandler treeHandler = new ProjectTreeHandler(source, target);
		Path sourcePath = Paths.get(source);
		Files.walkFileTree(sourcePath, treeHandler);
		List<LogFactory> logFactory = treeHandler.getLogFileList();

		System.out.println("Do you want to provide label? y/n ");
		Scanner scanner= new Scanner(System.in);
		String choice2=scanner.next();

		if(choice2.equals("y")){
			System.out.println("Enter Label:");
			String createLabel= scanner.next();
			/**
			 * The below Log method is defined inside FileLogs.java class which will add log details in MANIFEST file.
			 **/
			filelog.log("create", source, target, logFactory, createLabel);
		}
		else {
			filelog.log("create", source, target, logFactory, "---");
		}

		scanner.close();
	}


	/**
	 * This method executes the 'CHECKOUT' command.
	 * the user gives the repository folder name and empty target folder name in a command, he can choose to checkout using
	 * either a label or the manifest filename.
	 * It also creates the MANIFEST file inside ACTIVITY folder of CheckOut directory using the FileLogs.java class.
	 * @param cmdargs - Command line arguments for Checkout Command, Source Path and Target Path
	 **/
	private static void checkOutProj(String[] args) throws IOException {
		String src, dest;

		src = args[1];
		dest = args[2];
		File manifestFile=null;

		Scanner scanner = new Scanner(System.in);      

		System.out.println("Press 1 to checkout using Filename or\nPress 2 to checkout using Label provided earlier");
		int chkOutChoice = scanner.nextInt();   

		if(chkOutChoice==1){
			String[] manifestFiles = new File(src + File.separatorChar + "Activity" + File.separatorChar).list();
			System.out.println("Select the manifest to checkout: " + manifestFiles.length); 
			for (int i = 0; i < manifestFiles.length; i++) {
				System.out.printf("(%d)\t%s\n", i+1, manifestFiles[i]);
			}
			int choice = scanner.nextInt() - 1;
			manifestFile = new File(src + File.separatorChar + "Activity" + File.separatorChar + manifestFiles[choice]);
			UserCommandHandler.checkoutHandler(src,dest,manifestFile,filelog);
		}

		else if(chkOutChoice==2){
			System.out.println("Provide the label using which you want to checkout:");
			String label= scanner.next();

			String[] manifestFiles = new File(src + File.separatorChar + "Activity" + File.separatorChar).list();

			int flag =0;
			for (int i = 0; i < manifestFiles.length; i++) {
				String fileName = src + File.separatorChar + "Activity" + File.separatorChar + manifestFiles[i];            	
				try(BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
					bufferedReader.readLine();
					String label2 = bufferedReader.readLine(); 
					String labelValue = label2.split(":")[1];
					if(labelValue.equals(label)){
						flag=1;
						manifestFile = new File(src + File.separatorChar + "Activity" + File.separatorChar + manifestFiles[i]);
						UserCommandHandler.checkoutHandler(src,dest,manifestFile,filelog);
						//break;
					}
				}
				catch(FileNotFoundException e){
					e.printStackTrace();
				}
			}
			if(flag==0){
				System.out.println("no such label exists.");
			}
			else {
				System.out.println("checkout is done on label entered.");
			}
		}
		else {
			System.out.println("you entered wrong choice. Please try again!");
		}
		scanner.close();
	}       

	/**
	 * This method executes the 'CHECKIN' command.
	 * It also creates the MANIFEST file inside ACTIVITY folder of repository directory using the FileLogs.java class.
	 * It also lets the user provide a label after the Check-in is done.
	 * @param cmdargs - Command line arguments for Check-in Command, Source Path and Target Path
	 **/
	private static void checkinProj(String[] args) throws IOException {
		String src, dest;

		src = args[1];
		dest = args[2];

		ProjectTreeHandler projTreeHandler = new ProjectTreeHandler(src, dest);
		Path path = Paths.get(src);
		Files.walkFileTree(path, projTreeHandler);

		List<LogFactory> logFactory = projTreeHandler.getLogFileList();
		System.out.println("Do you want to provide label? y/n ");
		Scanner scanner= new Scanner(System.in);
		String choice2=scanner.next();

		if(choice2.equalsIgnoreCase("y")){
			System.out.println("Enter Label:");
			String checkinLabel= scanner.next();
			filelog.log("checkin", src, dest, logFactory, checkinLabel);
		}
		else {
			filelog.log("checkin", src, dest, logFactory, "---");
		}
		scanner.close();
	}

	/**
	 * This method executes the 'MERGE' command.
	 * This method calls mergeHandler() method from UserCommandHandler class.
	 * It will print the names of the conflicted files merged in repository target path if any.
	 * @param cmdargs - Command line arguments for Checkout Command, Source Path and Target Path
	 **/

	private static void mergeProj(String[] args) throws IOException {
		String src, dest;

		src = args[1];
		dest = args[2];

		List<String> conflictPathlist= UserCommandHandler.mergeHandler(src, dest);

		if(conflictPathlist.size()==0){
			System.out.println("No conflict");
		}
		else{
			System.out.println("\nList of full paths where the conflicted files are merged:");
			for(int i=0;i<conflictPathlist.size();i++){
				System.out.println("   "+conflictPathlist.get(i));
			}
		}
	}
}




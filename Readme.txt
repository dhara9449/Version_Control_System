#Author Name and Contact Info: 
	Patel Dhara (dharapatel270594@gmail.com),

#Class: CECS 543

#Section: 01 6236

#Project Part:  SCM Project Part3

#Project Name: 543-p3_DPB

#Introduction: 
		This is the third part of our SCM (Source Code Management) project (AKA a VCS: Version Control System). In this part, we add
		one more feature- merge, where we add the ability to merge two project trees. The merge ability helps the user to
		merge a project tree (the R version) that is already in the repo (as represented by a manifest file) into a target project
		tree (the T version) outside the repo. We will assume that the T version has also just been checked-in, so that there is an 
		up-to-date T version manifest file in the repo.
		After the merge command has run, the target (T) project tree will have a single version (R or T, or
		both are the same) of all its conflict-free files, and also for each file where the R and T versions are
		different the target project tree should have three versions of that file each with an altered filename: an
		R version, a T version and a G version (obtained from the G project version). For example, if the
		conflicted filename is foo.java, then there should be in the target project tree in its place the three files:
		foo_MR.java, foo_MT.java, and foo_MG.java.
		
#Requirements:
	1. Java 1.8.x

#List of Files: 
	All the following files reside under ase.vcs package.
	1. VCSMain.java
	2. ProjectTreeHandler.java
	3. LogFactory.java
	4. FileLogs.java 
	5. UserCommandHandler.java

#Instruction to Build, installation and Setup:

	Follow below steps to compile and run the project:

	Windows users: 
	1. In command prompt , goto directory 543-p3_DPB\src.
	2. Type command to compile: "javac ase\vcs\*.java" : This will build the java classes.
	3. Type command to execute: java ase.vcs.VCSMain <command> <src> <target>  : Replace <src> and <target> path accordingly and 
	   use <command> values as applicable: 1. create 2.checkin 3.checkout 4.merge

	Linux users: 
	1. Open Linux terminal and goto directory 543-p3_DPB/src.
	2. Type command: "javac ase/vcs/*.java" : This will build the java classes.
	3. Type command: java ase.vcs.VCSMain <command> <src> <target>  : Replace <src> and <target> path accordingly and 
	   use <command> values as applicable: 1. create 2.checkin 3.checkout 4.merge

#Assumption: We assume that the user inputs correct options whenever asked and will always provide correct label value while checking out using label.

#Usage:
	Windows users:
	From the 543-p3_DPB\src directory:
	-For build: javac ase\vcs\*.java
	-To Run: java ase.vcs.VCSMain <cmd> <src> <dst>

	Linux users:
	From the 543-p3_DPB/src directory:
	-For build: javac ase/vcs/*.java
	-To Run: java ase.vcs.VCSMain <cmd> <src> <dst>

#references: 1) Java 2 Complete Reference (5th Edition) by Herbert Schildt
             2) www.stackoverflow.com

#Bugs or errors: None detected

#Extra features added : None
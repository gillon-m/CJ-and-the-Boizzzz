# Group 12: CJ-and-the-Boizzzz
This Java application is a task scheduler which returns a optimum schedule from a given directed acyclic graph.

This file gives you a little instruction to run this task scheduler program.
To run this program, please open the terminal and go to the directory where the program jar file exists.
You have to pass the following parameters:
	- File name of the dot file to read the graph
	- Number of processors to schedule the input graph on (P)

In addition, you can also specify:
	- Number of cores for execution in parallel (N) 
		by typing -p N
	- Visualisation effect for the search 
		by typing -v
	- Output file name (OUTPUT)
		by typing OUTPUT

To run the file named “scheduler.jar” with input file named “INPUT.dot”, please enter:

java -jar scheduler jar INPUT.dot P [OPTION]

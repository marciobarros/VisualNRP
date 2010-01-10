package br.unirio.visualnrp.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.Project;

/**
 * Class that reads data from a requirements file
 * 
 * @author marcio.barros
 */
public class RequirementReader
{
	private int lineCounter;
	
	/**
	 * Initializes the requirement file reader
	 */
	public RequirementReader()
	{
	}
	
	/**
	 * Loads a file containing requirements
	 */
	public Project execute(Instance instance)
	{
		try
		{
			Project project = new Project(instance);		
			loadMainFile(instance, project);
			loadRiskFile(instance, project);
			return project;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Gets the next line from the file
	 */
	private String nextLine(Scanner scanner) throws Exception
	{
		lineCounter++;
		return scanner.nextLine();
	}
	
	/**
	 * Loads the main file containing an instance's requirements
	 */
	private void loadMainFile(Instance instance, Project project) throws Exception
	{
		try
		{
			String filename = instance.getCategory().getDirectory() + "/" + instance.getName() + ".txt";
			Scanner scanner = new Scanner(new FileInputStream(filename));
			this.lineCounter = 0;
			
			loadRequirements(scanner, project);		
			loadRequirementDependencies(scanner, project);		
			loadCustomers(scanner, project);
			
			scanner.close();
		}
		catch(IOException e)
		{
			addError(e.getMessage());
		}
	}

	/**
	 * Load data related to requirements
	 */
	private void loadRequirements(Scanner scanner, Project project) throws Exception
	{
		String line = nextLine(scanner);
		int numberOfLevels = Integer.parseInt(line);

		for (int i = 0; i < numberOfLevels; i++)
		{
			line = nextLine(scanner);
			int requirementsOnLevel = Integer.parseInt(line);

			line = nextLine(scanner);
			String[] tokens = line.split(" ");
			
			if (requirementsOnLevel != tokens.length)
				addError("Invalid number of cost items for level " + (i+1));

			int count = project.getRequirementCount();
			project.addRequirements(requirementsOnLevel);
			
			for (int j = 0; j < requirementsOnLevel; j++)
				project.setRequirementCost(count + j, Integer.parseInt(tokens[j]));
		}
	}

	/**
	 * Loads data related to requirement dependencies
	 */
	private void loadRequirementDependencies(Scanner scanner, Project project) throws Exception
	{
		String line = nextLine(scanner);
		int numberOfDependencies = Integer.parseInt(line);

		for (int i = 0; i < numberOfDependencies; i++)
		{
			line = nextLine(scanner);
			String[] tokens = line.split(" ");
			
			if (tokens.length != 2)
				addError("Invalid format in dependency");

			int source = Integer.parseInt(tokens[0]);
			int target = Integer.parseInt(tokens[1]);
			project.addRequirementDependency(source-1, target-1);
		}
	}

	/**
	 * Loads data related to customers
	 */
	private void loadCustomers(Scanner scanner, Project project) throws Exception
	{
		String line = nextLine(scanner);
		int numberOfCustomers = Integer.parseInt(line);
		project.setCustomerCount(numberOfCustomers);

		for (int i = 0; i < numberOfCustomers; i++)
		{
			line = nextLine(scanner);
			String[] tokens = line.split(" ");
			
			if (tokens.length <= 2)
				addError("Invalid format in customer - less than 3 fields");

			int profit = Integer.parseInt(tokens[0]);
			project.setCustomerProfit(i, profit);
			
			int requirementCount = Integer.parseInt(tokens[1]);
			
			if (tokens.length != 2 + requirementCount)
				addError("Invalid format in customer - not enough requirement references");

			int[] requirements = new int[requirementCount];
			
			for (int j = 0; j < requirementCount; j++)
				requirements[j] = Integer.parseInt(tokens[j+2])-1;
			
			project.setCustomerRequirements(i, requirements);
		}
	}
	
	/**
	 * Loads the risk file for a given instance
	 */
	private void loadRiskFile(Instance instance, Project project) throws Exception
	{
		String filename = instance.getCategory().getDirectory() + "/" + instance.getName() + "-risk.txt";
		
		if (!new File(filename).exists())
			return;
		
		try
		{
			Scanner scanner = new Scanner(new FileInputStream(filename));
			this.lineCounter = 0;
			
			loadRequirementCostEstimates(scanner, project);
			loadCustomerProfitEstimates(scanner, project);		
			
			scanner.close();
		}
		catch(IOException e)
		{
			addError(e.getMessage());
		}
	}

	/**
	 * Load data related to estimates of the cost of software requirements
	 */
	private void loadRequirementCostEstimates(Scanner scanner, Project project) throws Exception
	{
		String line = nextLine(scanner);
		int numberOfRequirements = Integer.parseInt(line);
		
		if (numberOfRequirements != project.getRequirementCount())
			addError("Requirements count different from instance on risk file");

		for (int i = 0; i < numberOfRequirements; i++)
		{
			line = nextLine(scanner);
			String[] tokens = line.split(" ");
			
			if (tokens.length != 3)
				addError("Invalid number of columns on requirement estimates");
			
			int expectedCost = Integer.parseInt(tokens[0]);
			double minimumCost = Double.parseDouble(tokens[1]);
			double maximumCost = Double.parseDouble(tokens[2]);
			
			if (expectedCost != project.getRequirementCost(i))
				addError("Invalid expected cost for requirement #" + (i+1));
				
			project.setRequirementCostEstimates(i, minimumCost, maximumCost);
		}
	}

	/**
	 * Load data related to estimates of the profit to be earned from customers
	 */
	private void loadCustomerProfitEstimates(Scanner scanner, Project project) throws Exception
	{
		String line = nextLine(scanner);
		int numberOfCustomers = Integer.parseInt(line);
		
		if (numberOfCustomers != project.getCustomerCount())
			addError("Customer count different from instance on risk file");

		for (int i = 0; i < numberOfCustomers; i++)
		{
			line = nextLine(scanner);
			String[] tokens = line.split(" ");
			
			if (tokens.length != 3)
				addError("Invalid number of columns on customer estimates");
			
			int expectedProfit = Integer.parseInt(tokens[0]);
			double minimumProfit = Double.parseDouble(tokens[1]);
			double maximumProfit = Double.parseDouble(tokens[2]);
			
			if (expectedProfit != project.getCustomerProfit(i))
				addError("Invalid expected profit for customer #" + (i+1));
				
			project.setCustomerProfitEstimates(i, minimumProfit, maximumProfit);
		}
	}

	/**
	 * Registers an error during file processing
	 */
	private void addError(String message) throws Exception
	{
		throw new Exception("Line #" + lineCounter + ": " + message);
	}
}
package sobol.problems.requirements.calc;

import java.io.FileWriter;
import java.io.PrintWriter;

import sobol.problems.requirements.algorithm.constructor.Constructor;
import sobol.problems.requirements.algorithm.constructor.GreedyConstructor;
import sobol.problems.requirements.algorithm.constructor.RandomConstructor;
import sobol.problems.requirements.algorithm.search.VisIteratedLocalSearch;
import sobol.problems.requirements.instance.Instance;
import sobol.problems.requirements.model.Project;
import sobol.problems.requirements.reader.RequirementReader;

/**
 * Class that represents the landscape report
 * 
 * @author marciobarros
 */
public class LandscapeReleasePlanning
{
	/**
	 * Number of solutions per costumer
	 */
	private static int SOLUTIONS_PER_CUSTOMER = 100;
	
	private static int MAXEVALUATIONS = 10000000;
	
	private static int SAMPLE_SIZE = 10;
	
	/**
	 * Creates the landscape reports without risk for all instances
	 */
	public boolean execute(Instance instance, String outputFilename) throws Exception
	{
		// TODO gravar vários rounds no mesmo arquivo
		
		// TODO generalizar para todas as instâncias
		
		// TODO diferentes modos ativados por linha de comando
		
		// TODO criar arquivo readme no GitHub
		
		// TODO tirar prefixo sobol dos pacotes
		
		RequirementReader reader = new RequirementReader(instance.getFilename());
		Project project = reader.execute();
		System.out.println("Source: profit=" + project.getTotalProfit() + "; cost=" + project.getTotalCost());

		Constructor constructor = new GreedyConstructor(project);
		int budget = (int) (0.3 * project.getTotalCost());
		VisIteratedLocalSearch visils = new VisIteratedLocalSearch(null, project, budget, MAXEVALUATIONS, SAMPLE_SIZE, constructor);
		boolean[] firstSolution = visils.execute();
		
		Project secondProject = createProjectRemovingSolution(project, firstSolution);
		System.out.println("Solution: profit=" + project.calculateProfit(firstSolution) + "; cost=" + project.calculateCost(firstSolution));
		System.out.println("Target: profit=" + secondProject.getTotalProfit() + "; cost=" + secondProject.getTotalCost());

		Constructor constructor2 = new GreedyConstructor(secondProject);
		visils = new VisIteratedLocalSearch(null, secondProject, budget, MAXEVALUATIONS, SAMPLE_SIZE, constructor2);
		boolean[] secondSolution = visils.execute();

		Project thirdProject = createProjectRemovingSolution(secondProject, secondSolution);
		System.out.println("Solution: profit=" + secondProject.calculateProfit(secondSolution) + "; cost=" + secondProject.calculateCost(secondSolution));
		System.out.println("Target: profit=" + thirdProject.getTotalProfit() + "; cost=" + thirdProject.getTotalCost());

		String landscapeFilename = String.format(outputFilename, instance.getName());
		Constructor constructorRandom = new RandomConstructor(thirdProject);
		createLandscape(landscapeFilename, thirdProject, constructorRandom, 30);
		
		return false;
	}

	/**
	 * Creates a project from a source project removing resolved requirements and customers
	 */
	private Project createProjectRemovingSolution(Project source, boolean[] solution) 
	{
		int attendedCustomers = countAttendedCustomers(solution);
		boolean[] resolvedRequirements = source.getCustomersRequirements(solution);

		Project target = new Project(source.getName());
		target.setCustomerCount(source.getCustomerCount() - attendedCustomers);
		target.addRequirements(source.getRequirementCount());
		
		copyRequirements(source, resolvedRequirements, target);
		copyUnattendedCustomers(source, solution, resolvedRequirements, target);
		return target;
	}

	/**
	 * Counts the number of customers attended by a solution
	 */
	private int countAttendedCustomers(boolean[] solution) 
	{
		int count = 0;
		
		for (int i = 0; i < solution.length; i++)
			if (solution[i])
				count++;
		
		return count;
	}

	/**
	 * Copies all requirements from the source solution to the target, eliminating resolved requirements from dependencies
	 */
	private void copyRequirements(Project source, boolean[] resolvedRequirements, Project target) 
	{
		for (int i = 0; i < source.getRequirementCount(); i++)
		{
			if (resolvedRequirements[i])
				target.setRequirementCost(i, 0);
			else
				target.setRequirementCost(i, source.getRequirementCost(i));
			
			if (source.requirementHasPrecedents(i))
			{
				int position = 0;
				int index = source.getRequirementPrecedentIndex(i, position++);
	
				while (index != -1)
				{
					if (!resolvedRequirements[index])
						target.addRequirementDependency(i, index);

					index = source.getRequirementPrecedentIndex(i, position++);
				}
			}
		}
	}

	/**
	 * Copies unattended customers from a source solution to a target one
	 */
	private void copyUnattendedCustomers(Project source, boolean[] solution, boolean[] resolvedRequirements, Project target) 
	{
		int customerPosition = 0;
		
		for (int i = 0; i < solution.length; i++)
		{
			if (!solution[i])
			{
				int numberRequirements = source.getCustomerRequirementsCount(i);
				int[] requirements = new int[numberRequirements]; 
				int requirementPosition = 0;
				
				for (int j = 0; j < numberRequirements; j++)
				{
					int index = source.getCustomerRequirementIndex(i, j);
					
					if (!resolvedRequirements[index])
						requirements[requirementPosition++] = index;
				}
				
				target.setCustomerProfit(customerPosition, source.getCustomerProfit(i));
				target.setCustomerRequirements(customerPosition, requirements);
				customerPosition++;
			}
		}
	}
	
	/**
	 * Creates the landscape report for a given instance
	 */
	private void createLandscape(String filename, Project project, Constructor constructor, int budgetFactor) throws Exception
	{
		FileWriter outFile = new FileWriter(filename);
		PrintWriter out = new PrintWriter(outFile);
		
		out.println("budget,cust,fit");
		double totalProfit = project.getTotalProfit();
		double totalCost = project.getTotalCost();

		createLandscapeForBudget(out, project, constructor, budgetFactor, totalProfit, totalCost);
		
		out.close();
		outFile.close();
	}
	
	/**
	 * Creates the landscape report for a given instance and budget factor
	 */
	private void createLandscapeForBudget(PrintWriter out, Project project, Constructor constructor, int budgetFactor, double totalProfit, double totalCost) throws Exception
	{
		double availableBudget = project.getTotalCost() * (budgetFactor / 100.0);

		for (int i = 1; i <= project.getCustomerCount(); i++)
		{
			for (int j = 0; j < SOLUTIONS_PER_CUSTOMER; j++)
			{
				boolean[] solution = constructor.generateSolutionWith(i);
				double fitness = evaluate(solution, project, availableBudget, totalProfit, totalCost);
				out.println(budgetFactor + "," + i + "," + fitness);
			}
		}
	}
	
	/**
	 * Calculates the fitness of a given solution
	 */
	private double evaluate(boolean[] solution, Project project, double availableBudget, double totalProfit, double totalCost) throws Exception
	{
		int cost = project.calculateCost(solution);
		
		if (cost > availableBudget)
			return -cost / totalCost;

		int profit = project.calculateProfit(solution);
		return profit / totalProfit;
	}
}
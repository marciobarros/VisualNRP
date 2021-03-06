package br.unirio.visualnrp.calc.landscape;

import java.io.FileWriter;
import java.io.PrintWriter;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.constructor.RandomConstructor;
import br.unirio.visualnrp.algorithm.search.Algorithm;
import br.unirio.visualnrp.algorithm.search.SearchAlgorithm;
import br.unirio.visualnrp.calc.fitness.ProfitFitnessCalculator;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.reader.RequirementReader;

/**
 * Class that represents the landscape report
 * 
 * @author marciobarros
 */
public class ReleaseLandscapeReport
{
	/**
	 * Number of solutions per costumer
	 */
	private static int SOLUTIONS_PER_CUSTOMER = 100;
	
	/**
	 * Creates the landscape reports without risk for all instances
	 */
	public void execute(Instance instance, int budgetFactor, String outputFilename, int rounds) throws Exception
	{
		String landscapeFilename = String.format(outputFilename, instance.getName());
		FileWriter outFile = new FileWriter(landscapeFilename);
		PrintWriter out = new PrintWriter(outFile);
		out.println("budget,round,cust,fit");

		RequirementReader reader = new RequirementReader();
		Project project = reader.execute(instance);
		System.out.println("Source: profit=" + project.getTotalProfit() + "; cost=" + project.getTotalCost());

		Constructor constructor = new RandomConstructor(project);
		int availableBudget = (int) (project.getTotalCost() * budgetFactor / 100.0);
		SearchAlgorithm visils = Algorithm.createAlgorithm(Algorithm.VISILS, null, project, constructor);

		ProfitFitnessCalculator calculator = new ProfitFitnessCalculator(project, availableBudget);
		boolean[] solution = visils.execute(calculator);

		System.out.println("Solution: profit=" + project.calculateProfit(solution) + "; cost=" + project.calculateCost(solution));
		createLandscape(out, project, constructor, budgetFactor, 0);
		
		for (int i = 1; i < rounds; i++)
		{
			project = createProjectRemovingSolution(project, solution);
			System.out.println("Target: profit=" + project.getTotalProfit() + "; cost=" + project.getTotalCost());

			Constructor nextConstructor = new RandomConstructor(project);
			availableBudget = (int) (project.getTotalCost() * budgetFactor / 100.0);
			visils = Algorithm.createAlgorithm(Algorithm.VISILS, null, project, nextConstructor);

			calculator = new ProfitFitnessCalculator(project, availableBudget);
			solution = visils.execute(calculator);

			System.out.println("Solution: profit=" + project.calculateProfit(solution) + "; cost=" + project.calculateCost(solution));
			createLandscape(out, project, nextConstructor, budgetFactor, i);
		}

		out.close();
		outFile.close();
	}

	/**
	 * Creates a project from a source project removing resolved requirements and customers
	 */
	private Project createProjectRemovingSolution(Project source, boolean[] solution) 
	{
		int attendedCustomers = countAttendedCustomers(solution);
		boolean[] resolvedRequirements = source.getCustomersRequirements(solution);

		Project target = new Project(source.getInstance());
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
	 * Creates the landscape report for a given instance and budget factor
	 */
	private void createLandscape(PrintWriter out, Project project, Constructor constructor, int budgetFactor, int round) throws Exception
	{
		double availableBudget = project.getTotalCost() * (budgetFactor / 100.0);

		for (int i = 1; i <= project.getCustomerCount(); i++)
		{
			for (int j = 0; j < SOLUTIONS_PER_CUSTOMER; j++)
			{
				boolean[] solution = constructor.generateSolutionWith(i);
				int fitness = evaluate(solution, project, availableBudget);
				out.println(budgetFactor + "," + round + "," + i + "," + fitness);
			}
		}
	}
	
	/**
	 * Calculates the fitness of a given solution
	 */
	private int evaluate(boolean[] solution, Project project, double availableBudget) throws Exception
	{
		int cost = project.calculateCost(solution);
		
		if (cost > availableBudget)
			return -cost;

		return project.calculateProfit(solution);
	}
}
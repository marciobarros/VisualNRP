package br.unirio.visualnrp.calc.optimizer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.constructor.RandomConstructor;
import br.unirio.visualnrp.algorithm.search.Algorithm;
import br.unirio.visualnrp.algorithm.search.SearchAlgorithm;
import br.unirio.visualnrp.calc.fitness.ProfitFitnessCalculator;
import br.unirio.visualnrp.model.Instance;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.reader.RequirementReader;

/**
 * Class that performs the release planning optimization
 * 
 * @author Marcio
 */
public class ReleasePlanningOptimizer
{
	/**
	 * Number of optimization cycles
	 */
	private static int CYCLES = 30;

	/**
	 * Runs the optimization report for release planning
	 */
	public void execute(List<Instance> instances, int budgetFactor, String outputFilename, int rounds, double interestRate) throws Exception
	{
		PrintWriter out = createOutputFile(outputFilename);

		for (int i = 0; i < instances.size(); i++)
		{
			Instance instance = instances.get(i);
			RequirementReader reader = new RequirementReader();
			Project project = reader.execute(instance);
			System.out.println("Processing " + project.getName() + " ...");
		
			createReportForBudget(out, project, budgetFactor, rounds, interestRate, Algorithm.VISILS);
			createReportForBudget(out, project, budgetFactor, rounds, interestRate, Algorithm.ILS);
		}

		out.close();
	}
	
	/**
	 * Creates the output file and writes the header
	 */
	protected PrintWriter createOutputFile(String outputFilename) throws IOException
	{
		File file = new File(outputFilename);
		
		if (file.getParentFile() != null)
			file.getParentFile().mkdirs();
		
		FileWriter outFile = new FileWriter(outputFilename);
		PrintWriter out = new PrintWriter(outFile);
		out.println("alg,instance,cycle,budget,risk,fit,solution");
		
		return out;
	}

	/**
	 * Creates the optimization report for a given instance and budget factor
	 */
	protected void createReportForBudget(PrintWriter out, Project project, int budgetFactor, int rounds, double interestRate, Algorithm algorithm) throws Exception
	{
		double sum = 0.0;
		double maxFitness = 0;
		
		for (int i = 0; i < CYCLES; i++)
		{
			double fitness = createReportForCycle(project, budgetFactor, rounds, interestRate, algorithm);
			out.println(algorithm.name() + "," + project.getName() + "," + i + "," + budgetFactor + "," + rounds + "," + interestRate + "," + fitness);
			
			sum += fitness;
			if (fitness > maxFitness) maxFitness = fitness;
			System.out.print("*");
		}

		System.out.println(String.format(" %-6s\t%-14s\t%.4f\t%.4f", algorithm.name(), project.getName() + "-" + budgetFactor, (sum/CYCLES), maxFitness));
	}

	/**
	 * Creates a report for a given optimization cycle
	 */
	private double createReportForCycle(Project project, int budgetFactor, int rounds, double interestRate, Algorithm algorithm) throws Exception
	{
		Constructor constructor = new RandomConstructor(project);
		int availableBudget = (int) (project.getTotalCost() * budgetFactor / 100.0);
		SearchAlgorithm searchAlgorithm = Algorithm.createAlgorithm(algorithm, null, project, constructor);

		ProfitFitnessCalculator calculator = new ProfitFitnessCalculator(project, availableBudget);
		boolean[] solution = searchAlgorithm.execute(calculator);
		double presentValue = project.calculateProfit(solution);
		
		for (int i = 1; i < rounds; i++)
		{
			project = createProjectRemovingSolution(project, solution);

			constructor = new RandomConstructor(project);
			availableBudget = (int) (project.getTotalCost() * budgetFactor / 100.0);
			searchAlgorithm = Algorithm.createAlgorithm(algorithm, null, project, constructor);

			calculator = new ProfitFitnessCalculator(project, availableBudget);
			solution = searchAlgorithm.execute(calculator);
			presentValue = presentValue * (1 + interestRate / 100.0) + project.calculateProfit(solution);
		}
		
		return presentValue;
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
}
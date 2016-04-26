package br.unirio.visualnrp.algorithm.search;

import java.io.PrintWriter;
import java.util.Arrays;

import javax.management.RuntimeErrorException;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.solution.Solution;
import br.unirio.visualnrp.calc.IFitnessCalculator;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.support.PseudoRandom;

/**
 * Iterated Local Search for the next release problem
 */
public class IteratedLocalSearch implements SearchAlgorithm
{
	/**
	 * Order under which requirements will be accessed
	 */
	private int[] selectionOrder;
	
	/**
	 * Solution being visited
	 */
	private boolean[] currentSolution;
	
	/**
	 * Fitness of the solution being visited
	 */
	private double currentFitness;
	
	/**
	 * Number of the random restart where the best solution was found
	 */
	private int iterationBestFound;
	
	/**
	 * File where details of the search process will be printed
	 */
	private PrintWriter detailsFile;
	
	/**
	 * Set of requirements to be optimized
	 */
	private Project project;
	
	/**
	 * Available budget to select requirements
	 */
	private int availableBudget;
	
	/**
	 * Number of fitness evaluations available in the budget
	 */
	private int maxEvaluations;
	
	/**
	 * Number of fitness evaluations executed
	 */
	private int evaluations;
	
	/**
	 * Represents a solution of the problem. Utilized during the local search
	 */
	private Solution tmpSolution;
	
	/**
	 * A constructor algorithm for initial solutions generation.
	 */
	private Constructor constructor;

	/**
	 * Initializes the Hill Climbing search process
	 */
	public IteratedLocalSearch(PrintWriter detailsFile, Project project, int budget, int maxEvaluations, Constructor constructor) throws Exception
	{
		this.project = project;
		this.availableBudget = budget;
		this.maxEvaluations = maxEvaluations;
		this.detailsFile = detailsFile;
		this.evaluations = 0;
		this.iterationBestFound = 0;
		this.tmpSolution = new Solution(project);
		this.constructor = constructor;
		createRandomSelectionOrder(project);
		checkSelectionOrder(project);
	}

	/**
	 * Gera uma ordem aleatoria de selecao dos requisitos
	 */
	private void createRandomSelectionOrder(Project project)
	{
		int customerCount = project.getCustomerCount();
		int[] temporaryOrder = new int[customerCount];

		for (int i = 0; i < customerCount; i++)
		{
			temporaryOrder[i] = i;
		}

		this.selectionOrder = new int[customerCount];

		for (int i = 0; i < customerCount; i++)
		{
			int index = (int) (PseudoRandom.randDouble() * (customerCount - i));
			this.selectionOrder[i] = temporaryOrder[index];

			for (int j = index; j < customerCount - 1; j++)
			{
				temporaryOrder[j] = temporaryOrder[j + 1];
			}
		}
	}

	/**
	 * Checks whether the selection order is consistent
	 */
	private void checkSelectionOrder(Project project) 
	{
		int customerCount = project.getCustomerCount();

		for (int i = 0; i < customerCount; i++)
		{
			boolean achou = false;

			for (int j = 0; j < customerCount && !achou; j++)
			{
				if (this.selectionOrder[j] == i)
				{
					achou = true;
				}
			}

			if (!achou)
			{
				throw new RuntimeErrorException(null, "ERRO DE GERACAO DE INICIO ALEATORIO");
			}
		}
	}

	/**
	 * Returns the number of random restarts executed during the search process
	 */
	public int getIterations()
	{
		return evaluations;
	}

	/**
	 * Returns the number of the restart in which the best solution was found
	 */
	public int getIterationBestFound()
	{
		return iterationBestFound;
	}

	/**
	 * Returns the best solution found by the search process
	 */
	public boolean[] getBestSolution()
	{
		return currentSolution;
	}

	/**
	 * Returns the fitness of the best solution
	 */
	public double getFitness()
	{
		return currentFitness;
	}

	/**
	 * Evaluates the fitness of a solution, saving detail information
	 */
	private double evaluate(Solution solution, IFitnessCalculator calculator)
	{
		if (++evaluations % 10000 == 0 && detailsFile != null)
		{
			detailsFile.println(evaluations + "; " + currentFitness);
		}

		return calculator.evaluate(solution, availableBudget, 0);
	}

	/**
	 * Runs a neighborhood visit starting from a given solution
	 */
	private NeighborhoodVisitorResult visitNeighbors(Solution solution, IFitnessCalculator calculator)
	{
		double startingFitness = evaluate(solution, calculator);

		if (evaluations > maxEvaluations)
		{
			return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.SEARCH_EXHAUSTED);
		}

		if (startingFitness > currentFitness)
		{
			return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR, startingFitness);
		}

		int len = project.getCustomerCount();

		for (int i = 0; i < len; i++)
		{
			int customerI = selectionOrder[i];

			solution.flipCustomer(customerI);
			double neighborFitness = evaluate(solution, calculator);

			if (evaluations > maxEvaluations)
			{
				return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.SEARCH_EXHAUSTED);
			}

			if (neighborFitness > startingFitness)
			{
				return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR, neighborFitness);
			}

			solution.flipCustomer(customerI);
		}

		return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.NO_BETTER_NEIGHBOR);
	}

	/**
	 * Performs the local search starting from a given solution
	 */
	private boolean localSearch(boolean[] solution, IFitnessCalculator calculator)
	{
		NeighborhoodVisitorResult result;
		tmpSolution.setAllCustomers(solution);

		do
		{
			result = visitNeighbors(tmpSolution, calculator);

			if (result.getStatus() == NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR && result.getNeighborFitness() > currentFitness)
			{
				Solution.copySolution(tmpSolution.getSolution(), currentSolution);
				this.currentFitness = result.getNeighborFitness();
				this.iterationBestFound = evaluations;
			}

		} while (result.getStatus() == NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR);

		return (result.getStatus() == NeighborhoodVisitorStatus.NO_BETTER_NEIGHBOR);
	}

	/**
	 * Main loop of the algorithm
	 */
	public boolean[] execute(IFitnessCalculator calculator) throws Exception
	{
		int customerCount = project.getCustomerCount();

		this.currentSolution = constructor.generateSolution();
		Solution hcrs = new Solution(project);
		hcrs.setAllCustomers(currentSolution);
		this.currentFitness = evaluate(hcrs, calculator);

		boolean[] bestSol = new boolean[customerCount];
		localSearch(currentSolution, calculator);
		Solution.copySolution(currentSolution, bestSol);
		double bestFitness = this.currentFitness;

		while (evaluations < maxEvaluations)
		{
			boolean[] perturbedSolution = perturbSolution(bestSol, customerCount);
			localSearch(perturbedSolution, calculator);

			if (shouldAccept(currentFitness, bestFitness))
			{
				Solution.copySolution(currentSolution, bestSol);
				bestFitness = this.currentFitness;
			}
		}

		return bestSol;
	}

	/**
	 * Applies the perturbation operator upon a solution
	 */
	private boolean[] perturbSolution(boolean[] solution, int customerCount)
	{
		boolean[] newSolution = Arrays.copyOf(solution, customerCount);
		int amount = 2;

		for (int i = 0; i < amount; i++)
		{
			int customer = PseudoRandom.randInt(0, customerCount-1);
			newSolution[customer] = !newSolution[customer];
		}

		return newSolution;
	}

	/**
	 * Determines whether should accept a given solution
	 */
	private boolean shouldAccept(double solutionFitness, double bestFitness)
	{
		return solutionFitness > bestFitness;
	}
}
package sobol.problems.requirements.algorithm.ils;

import java.io.PrintWriter;
import java.util.Arrays;

import jmetal.util.PseudoRandom;
import sobol.problems.requirements.algorithm.constructor.Constructor;
import sobol.problems.requirements.algorithm.localsearch.NeighborhoodVisitorResult;
import sobol.problems.requirements.algorithm.localsearch.NeighborhoodVisitorStatus;
import sobol.problems.requirements.algorithm.solution.Solution;
import sobol.problems.requirements.model.Project;

/**
 * Iterated Local Search for the next release problem
 */
public class IteratedLocalSearch
{
	/**
	 * Order under which requirements will be accessed
	 */
	protected int[] selectionOrder;
	
	/**
	 * Solution being visited
	 */
	protected boolean[] currentSolution;
	
	/**
	 * Fitness of the solution being visited
	 */
	protected double currentFitness;
	
	/**
	 * Number of the random restart where the best solution was found
	 */
	protected int iterationBestFound;
	
	/**
	 * File where details of the search process will be printed
	 */
	protected PrintWriter detailsFile;
	
	/**
	 * Set of requirements to be optimized
	 */
	protected Project project;
	
	/**
	 * Available budget to select requirements
	 */
	protected int availableBudget;
	
	/**
	 * Number of fitness evaluations available in the budget
	 */
	protected int maxEvaluations;
	
	/**
	 * Number of fitness evaluations executed
	 */
	protected int evaluations;
	
	/**
	 * Represents a solution of the problem. Utilized during the local search
	 */
	protected Solution tmpSolution;
	
	/**
	 * A constructor algorithm for initial solutions generation.
	 */
	protected Constructor constructor;

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
	}

	/**
	 * Gera uma ordem aleatoria de selecao dos requisitos
	 */
	protected void createRandomSelectionOrder(Project project)
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
				System.out.println("ERRO DE GERACAO DE INICIO ALEATORIO");
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
	 * Prints a solution into a string
	 */
	public String printSolution(boolean[] solution)
	{
		String s = "[" + (solution[0] ? "S" : "-");

		for (int i = 1; i < solution.length; i++)
		{
			s += " " + (solution[i] ? "S" : "-");
		}

		return s + "]";
	}

	/**
	 * Copies a source solution to a target one
	 */
	protected void copySolution(boolean[] source, boolean[] target)
	{
		int len = source.length;

		for (int i = 0; i < len; i++)
		{
			target[i] = source[i];
		}
	}

	/**
	 * Evaluates the fitness of a solution, saving detail information
	 */
	protected double evaluate(Solution solution)
	{
		if (++evaluations % 10000 == 0 && detailsFile != null)
		{
			detailsFile.println(evaluations + "; " + currentFitness);
		}

		int cost = solution.getCost();
		return (cost <= availableBudget) ? solution.getProfit() : -cost;
	}

	/**
	 * Runs a neighborhood visit starting from a given solution
	 */
	protected NeighborhoodVisitorResult visitNeighbors(Solution solution)
	{
		double startingFitness = evaluate(solution);

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
			double neighborFitness = evaluate(solution);

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
	protected boolean localSearch(boolean[] solution)
	{
		NeighborhoodVisitorResult result;
		tmpSolution.setAllCustomers(solution);

		do
		{
			result = visitNeighbors(tmpSolution);

			if (result.getStatus() == NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR && result.getNeighborFitness() > currentFitness)
			{
				copySolution(tmpSolution.getSolution(), currentSolution);
				this.currentFitness = result.getNeighborFitness();
				this.iterationBestFound = evaluations;
			}

		} while (result.getStatus() == NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR);

		return (result.getStatus() == NeighborhoodVisitorStatus.NO_BETTER_NEIGHBOR);
	}

	/**
	 * Main loop of the algorithm
	 */
	public boolean[] execute() throws Exception
	{
		int customerCount = project.getCustomerCount();

		this.currentSolution = constructor.generateSolution();
		Solution hcrs = new Solution(project);
		hcrs.setAllCustomers(currentSolution);
		this.currentFitness = evaluate(hcrs);

		boolean[] bestSol = new boolean[customerCount];
		localSearch(currentSolution);
		copySolution(currentSolution, bestSol);
		double bestFitness = this.currentFitness;

		while (evaluations < maxEvaluations)
		{
			boolean[] perturbedSolution = perturbSolution(bestSol, customerCount);
			localSearch(perturbedSolution);

			if (shouldAccept(currentFitness, bestFitness))
			{
				copySolution(currentSolution, bestSol);
				bestFitness = this.currentFitness;
			}
		}

		return bestSol;
	}

	/**
	 * Applies the perturbation operator upon a solution
	 */
	protected boolean[] perturbSolution(boolean[] solution, int customerCount)
	{
		boolean[] newSolution = Arrays.copyOf(solution, customerCount);
		int amount = 2;

		for (int i = 0; i < amount; i++)
		{
			int customer = PseudoRandom.randInt(0, customerCount);
			newSolution[customer] = !newSolution[customer];
		}

		return newSolution;
	}

	/**
	 * Determines whether should accept a given solution
	 */
	protected boolean shouldAccept(double solutionFitness, double bestFitness)
	{
		return solutionFitness > bestFitness;
	}
}
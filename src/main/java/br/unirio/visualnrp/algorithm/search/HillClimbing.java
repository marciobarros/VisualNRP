package br.unirio.visualnrp.algorithm.search;

import java.io.PrintWriter;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.calc.IFitnessCalculator;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.support.PseudoRandom;

/**
 * Hill Climbing searcher for the next release problem
 * 
 * @author Marcio Barros
 */
public class HillClimbing implements SearchAlgorithm
{
	/**
	 * Order under which requirements will be accessed
	 */
	protected int[] selectionOrder;

	/**
	 * Best solution found by the Hill Climbing search
	 */
	protected boolean[] bestSolution;

	/**
	 * Fitness of the best solution found
	 */
	protected double fitness;

	/**
	 * Number of random restart executed
	 */
	protected int randomRestartCount;

	/**
	 * Number of the random restart where the best solution was found
	 */
	protected int restartBestFound;

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
	 * Number of iterations to best solution
	 */
	private int iterationBestFound;
	
	/**
	 * Represents a solution of the problem. Utilized during the local search
	 */
	protected Solution tmpSolution;

	/**
	 * A constructor algorithm for initial solutions generation.
	 */
	protected Constructor constructor;
	
	private int riskImportance;

	/**
	 * Initializes the Hill Climbing search process
	 */
	public HillClimbing(PrintWriter detailsFile, Project project, int budget, int riskImportance, int maxEvaluations, Constructor constructor) throws Exception
	{
		this.project = project;
		this.availableBudget = budget;
		this.riskImportance = riskImportance;
		this.maxEvaluations = maxEvaluations;
		this.detailsFile = detailsFile;
		this.evaluations = 0;
		this.randomRestartCount = 0;
		this.restartBestFound = 0;
		this.iterationBestFound = 0;
		this.tmpSolution = new Solution(project);
		this.constructor = constructor;
		createRandomSelectionOrder(project);
	}

	/**
	 * Gera a ordem default de selecao dos requisitos
	 */
	protected void createDefaultSelectionOrder(Project project)
	{
		int customerCount = project.getCustomerCount();
		this.selectionOrder = new int[customerCount];

		for (int i = 0; i < customerCount; i++)
			this.selectionOrder[i] = i;
	}

	/**
	 * Gera uma ordem aleatoria de selecao dos requisitos
	 */
	protected void createRandomSelectionOrder(Project project)
	{
		int customerCount = project.getCustomerCount();
		int[] temporaryOrder = new int[customerCount];

		for (int i = 0; i < customerCount; i++)
			temporaryOrder[i] = i;

		this.selectionOrder = new int[customerCount];

		for (int i = 0; i < customerCount; i++)
		{
			double random = PseudoRandom.randDouble();
			int index = (int) (random * (customerCount - i));
			this.selectionOrder[i] = temporaryOrder[index];

			for (int j = index; j < customerCount - 1; j++)
				temporaryOrder[j] = temporaryOrder[j + 1];
		}

		for (int i = 0; i < customerCount; i++)
		{
			boolean achou = false;

			for (int j = 0; j < customerCount && !achou; j++)
				if (this.selectionOrder[j] == i)
					achou = true;

			if (!achou)
				System.out.println("ERRO DE GERACAO DE INICIO ALEATORIO");
		}
	}

	/**
	 * Returns the number of random restarts executed during the search process
	 */
	public int getRandomRestarts()
	{
		return randomRestartCount;
	}

	/**
	 * Returns the number of the restart in which the best solution was found
	 */
	public int getRandomRestartBestFound()
	{
		return restartBestFound;
	}

	/**
	 * Returns the best solution found by the search process
	 */
	public boolean[] getBestSolution()
	{
		return bestSolution;
	}

	/**
	 * Returns the fitness of the best solution
	 */
	public double getFitness()
	{
		return fitness;
	}

	/**
	 * Returns the number of iterations executed
	 */
	public int getIterations()
	{
		return 0;
	}

	/**
	 * Returns the iteration on which the best solution was found
	 */
	public int getIterationBestFound() 
	{
		return iterationBestFound;
	}

	/**
	 * Evaluates the fitness of a solution, saving detail information
	 */
	protected double evaluate(Solution solution, IFitnessCalculator calculator)
	{
		if (++evaluations % 10000 == 0 && detailsFile != null)
		{
			detailsFile.println(evaluations + "; " + fitness);
		}

		return calculator.evaluate(solution, availableBudget, riskImportance);
	}

	/**
	 * Runs a neighborhood visit starting from a given solution
	 */
	protected NeighborhoodVisitorResult visitNeighbors(Solution solution, IFitnessCalculator calculator)
	{
		double startingFitness = evaluate(solution, calculator);

		if (evaluations > maxEvaluations)
		{
			return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.SEARCH_EXHAUSTED);
		}

		if (startingFitness > fitness)
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
	protected boolean localSearch(boolean[] solution, IFitnessCalculator calculator)
	{
		NeighborhoodVisitorResult result;
		tmpSolution.setAllCustomers(solution);

		do
		{
			result = visitNeighbors(tmpSolution, calculator);

			if (result.getStatus() == NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR && result.getNeighborFitness() > fitness)
			{
				Solution.copySolution(tmpSolution.getSolution(), bestSolution);
				this.fitness = result.getNeighborFitness();
				this.iterationBestFound = evaluations;
			}

		} while (result.getStatus() == NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR);

		return (result.getStatus() == NeighborhoodVisitorStatus.NO_BETTER_NEIGHBOR);
	}

	/**
	 * Executes the Hill Climbing search with random restarts
	 */
	public boolean[] execute(IFitnessCalculator calculator) throws Exception
	{
		this.bestSolution = constructor.generateSolution();
		Solution hcrs = new Solution(project);
		hcrs.setAllCustomers(bestSolution);
		this.fitness = evaluate(hcrs, calculator);

		int customerCount = project.getCustomerCount();
		boolean[] solution = new boolean[customerCount];
		Solution.copySolution(hcrs.getSolution(), solution);

		while (localSearch(solution, calculator))
		{
			this.randomRestartCount++;
			solution = constructor.generateSolution();
		}

		return bestSolution;
	}
}
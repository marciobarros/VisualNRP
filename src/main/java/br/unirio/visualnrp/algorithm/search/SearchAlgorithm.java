package br.unirio.visualnrp.algorithm.search;

import java.io.PrintWriter;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.calc.IFitnessCalculator;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.support.PseudoRandom;

/**
 * Abstract class that represents a search algorithm
 * 
 * @author Marcio
 */
public abstract class SearchAlgorithm 
{
	/**
	 * File where details of the search process will be printed
	 */
	private PrintWriter detailsFile;

	/**
	 * Set of requirements to be optimized
	 */
	protected Project project;

	/**
	 * Available budget to select requirements
	 */
	private int availableBudget;
	
	/**
	 * Relative importance of risk for the analyst
	 */
	private int riskImportance;

	/**
	 * A constructor algorithm for initial solutions generation.
	 */
	protected Constructor constructor;

	/**
	 * Number of fitness evaluations executed
	 */
	private int evaluations;

	/**
	 * Number of fitness evaluations available in the budget
	 */
	private int maxEvaluations;

	/**
	 * Order under which requirements will be accessed
	 */
	private int[] selectionOrder;

	/**
	 * Best solution found by the Hill Climbing search
	 */
	protected boolean[] currentSolution;

	/**
	 * Fitness of the best solution found
	 */
	protected double currentFitness;

	/**
	 * Number of iterations to best solution
	 */
	private int iterationBestFound;

	/**
	 * Initializes the Hill Climbing search process
	 */
	protected SearchAlgorithm(PrintWriter detailsFile, Project project, int budget, int riskImportance, int maxEvaluations, Constructor constructor) throws Exception
	{
		this.project = project;
		this.availableBudget = budget;
		this.riskImportance = riskImportance;
		this.maxEvaluations = maxEvaluations;
		this.detailsFile = detailsFile;
		this.evaluations = 0;
		this.iterationBestFound = 0;
		this.constructor = constructor;
		createRandomSelectionOrder(project);
		checkRandomSelectionOrder(project);
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
	 * Returns the number of iterations executed
	 */
	public int getIterations()
	{
		return evaluations;
	}

	/**
	 * Returns the maximum number of iterations to be executed
	 */
	public int getMaximumIterations()
	{
		return maxEvaluations;
	}
	
	/**
	 * Returns the iteration on which the best solution was found
	 */
	public int getIterationBestFound()
	{
		return iterationBestFound;
	}

	/**
	 * Sets the iteration on which the best value was found
	 */
	protected void setIterationBestFound(int iteration)
	{
		this.iterationBestFound = iteration;
	}
	
	/**
	 * Gets the index of the requirement in a given index of the selection order
	 */
	protected int getSelectionOrderIndex(int index)
	{
		return selectionOrder[index];
	}
	
	/**
	 * Creates a random order by requirement selection
	 */
	private void createRandomSelectionOrder(Project project)
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
	}

	/**
	 * Checks whether a random order for requirement selection is valid
	 */
	private void checkRandomSelectionOrder(Project project)
	{
		int customerCount = project.getCustomerCount();

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
	 * Evaluates the fitness of a solution, saving detail information
	 */
	protected double evaluate(Solution solution, IFitnessCalculator calculator)
	{
		if (++evaluations % 10000 == 0 && detailsFile != null)
		{
			detailsFile.println(evaluations + "; " + currentFitness);
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
	protected boolean localSearch(boolean[] solution, IFitnessCalculator calculator)
	{
		NeighborhoodVisitorResult result;
		Solution tmpSolution = new Solution(project);
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
	 * Executes the algorithm
	 */
	public abstract boolean[] execute(IFitnessCalculator calculator) throws Exception;
}

/**
 * Set of potential results from visiting neighbours
 */
enum NeighborhoodVisitorStatus
{
	FOUND_BETTER_NEIGHBOR, NO_BETTER_NEIGHBOR, SEARCH_EXHAUSTED
}

/**
 * Class that represents the results of the local search phase
 */
class NeighborhoodVisitorResult
{
	/**
	 * Status in the end of the local search
	 */
	private NeighborhoodVisitorStatus status;

	/**
	 * Fitness of the best neighbor, in case one has been found
	 */
	private double neighborFitness;

	/**
	 * Initializes a successful local search status
	 */
	public NeighborhoodVisitorResult(NeighborhoodVisitorStatus status, double fitness)
	{
		this.status = status;
		this.neighborFitness = fitness;
	}

	/**
	 * Initializes an unsuccessful local search
	 */
	public NeighborhoodVisitorResult(NeighborhoodVisitorStatus status)
	{
		this.status = status;
		this.neighborFitness = 0.0;
	}

	/**
	 * Returns the status of the local search
	 */
	public NeighborhoodVisitorStatus getStatus()
	{
		return status;
	}

	/**
	 * Return the fitness of the best neighbor found, if any
	 */
	public double getNeighborFitness()
	{
		return neighborFitness;
	}
}
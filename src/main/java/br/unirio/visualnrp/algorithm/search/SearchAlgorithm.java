package br.unirio.visualnrp.algorithm.search;

import java.io.PrintWriter;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.calc.fitness.IFitnessCalculator;
import br.unirio.visualnrp.calc.fitness.ProfitFitnessCalculator;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.support.PseudoRandom;

/**
 * Abstract class that represents a search algorithm
 * 
 * @author Marcio
 */
@SuppressWarnings("unused")
public abstract class SearchAlgorithm 
{
	/**
	 * File where details of the search process will be printed
	 */
	private PrintWriter detailsFile;

	/**
	 * Set of requirements to be optimized
	 */
	private Project project;

	/**
	 * Number of fitness evaluations available in the budget
	 */
	private int maxEvaluations;

	/**
	 * A constructor algorithm for initial solutions generation.
	 */
	private Constructor constructor;

	/**
	 * Number of fitness evaluations executed
	 */
	private int evaluationsConsumed;

	/**
	 * Number of iterations to best solution
	 */
	private int iterationBestFound;

	/**
	 * Order under which requirements will be accessed
	 */
	private int[] selectionOrder;

	/**
	 * Initializes the Hill Climbing search process
	 */
	protected SearchAlgorithm(PrintWriter detailsFile, Project project, int maxEvaluations, Constructor constructor) throws Exception
	{
		this.project = project;
		this.maxEvaluations = maxEvaluations;
		this.detailsFile = detailsFile;
		this.evaluationsConsumed = 0;
		this.iterationBestFound = 0;
		this.constructor = constructor;
		createRandomSelectionOrder(project);
		//checkRandomSelectionOrder(project);
	}
	
	/**
	 * Returns the project being optimized
	 */
	protected Project getProject()
	{
		return project;
	}
	
	/**
	 * Returns the solution constructor to be used
	 */
	protected Constructor getConstructor()
	{
		return constructor;
	}

	/**
	 * Returns the number of evaluations consumed during the search
	 */
	public int getEvaluationsConsumed()
	{
		return evaluationsConsumed;
	}

	/**
	 * Returns the maximum number of evaluations to be consumed
	 */
	public int getMaximumEvaluations()
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
				throw new RuntimeException("ERRO DE GERACAO DE INICIO ALEATORIO");
		}
	}

	/**
	 * Evaluates the fitness of a solution, saving detail information
	 */
	protected double evaluate(Solution solution, IFitnessCalculator calculator, double bestFitness)
	{
		if (++evaluationsConsumed % 10000 == 0 && detailsFile != null)
		{
			detailsFile.println(evaluationsConsumed + "; " + bestFitness);
		}

		return calculator.evaluate(solution);
	}

	/**
	 * Runs a neighborhood visit starting from a given solution
	 */
	protected NeighborhoodVisitorResult visitNeighbors(Solution solution, IFitnessCalculator calculator, double bestFitness)
	{
		double startingFitness = evaluate(solution, calculator, bestFitness);

		if (evaluationsConsumed > maxEvaluations)
		{
			return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.SEARCH_EXHAUSTED);
		}

		int customerCount = project.getCustomerCount();

		for (int i = 0; i < customerCount; i++)
		{
			int customerI = selectionOrder[i];
			
			solution.flipCustomer(customerI);
			double neighborFitness = evaluate(solution, calculator, bestFitness);

			if (evaluationsConsumed > maxEvaluations)
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
	protected Solution localSearch(Solution solution, IFitnessCalculator calculator, double bestFitness)
	{
		NeighborhoodVisitorResult result;

		Solution bestLocalSolution = solution.clone();
		double bestLocalFitness = calculator.evaluate(bestLocalSolution);

		do
		{
			result = visitNeighbors(bestLocalSolution, calculator, bestFitness);

			if (result.getStatus() == NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR && result.getNeighborFitness() > bestLocalFitness)
			{
				bestLocalFitness = result.getNeighborFitness();
				this.iterationBestFound = evaluationsConsumed;
			}

		} while (result.getStatus() == NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR);

		return bestLocalSolution;
	}
	
	/**
	 * Executes the algorithm
	 */
	public abstract boolean[] execute(IFitnessCalculator calculator) throws Exception;

	/**
	 * Set of potential results from visiting neighbors
	 */
	public enum NeighborhoodVisitorStatus
	{
		FOUND_BETTER_NEIGHBOR, NO_BETTER_NEIGHBOR, SEARCH_EXHAUSTED
	}
	
	/**
	 * Class that represents the results of the local search phase
	 */
	public class NeighborhoodVisitorResult
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
}
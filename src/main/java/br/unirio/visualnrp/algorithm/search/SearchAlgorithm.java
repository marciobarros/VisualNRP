package br.unirio.visualnrp.algorithm.search;

import br.unirio.visualnrp.calc.IFitnessCalculator;

/**
 * Abstract class that represents a search algorithm
 * 
 * @author Marcio
 */
public interface SearchAlgorithm 
{
	/**
	 * Executes the algorithm
	 */
	boolean[] execute(IFitnessCalculator calculator) throws Exception;

	/**
	 * Returns the fitness of the best solution
	 */
	double getFitness();

	/**
	 * Returns the number of iterations executed
	 */
	int getIterations();
	
	/**
	 * Returns the iteration on which the best solution was found
	 */
	int getIterationBestFound();
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
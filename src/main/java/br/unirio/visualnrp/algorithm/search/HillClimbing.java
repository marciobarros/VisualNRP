package br.unirio.visualnrp.algorithm.search;

import java.io.PrintWriter;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.calc.fitness.IFitnessCalculator;
import br.unirio.visualnrp.model.Project;

/**
 * Hill Climbing searcher for the next release problem
 * 
 * @author Marcio Barros
 */
public class HillClimbing extends SearchAlgorithm
{
	/**
	 * Number of random restart executed
	 */
	private int randomRestartCount;

	/**
	 * Number of the random restart where the best solution was found
	 */
	private int restartBestFound;

	/**
	 * Initializes the Hill Climbing search process
	 */
	public HillClimbing(PrintWriter detailsFile, Project project, int maxEvaluations, Constructor constructor) throws Exception
	{
		super(detailsFile, project, maxEvaluations, constructor);
		this.randomRestartCount = 0;
		this.restartBestFound = 0;
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
	 * Executes the Hill Climbing search with random restarts
	 */
	public boolean[] execute(IFitnessCalculator calculator) throws Exception
	{
		Solution bestSolution = new Solution(getProject(), getConstructor().generateSolution());
		double bestFitness = evaluate(bestSolution, calculator, 0.0);

		Solution solution = localSearch(bestSolution, calculator, bestFitness);
		double fitness = calculator.evaluate(solution);
		
		if (fitness > bestFitness)
		{
			bestSolution = solution;
			bestFitness = fitness;
		}
		
		while (getEvaluationsConsumed() < getMaximumEvaluations())
		{
			this.randomRestartCount++;
			
			Solution startSolution = new Solution(getProject(), getConstructor().generateSolution());
			solution = localSearch(startSolution, calculator, bestFitness);
			fitness = calculator.evaluate(solution);
			
			if (fitness > bestFitness)
			{
				bestSolution = solution;
				bestFitness = fitness;
			}
		}

		return bestSolution.getSolution();
	}
}
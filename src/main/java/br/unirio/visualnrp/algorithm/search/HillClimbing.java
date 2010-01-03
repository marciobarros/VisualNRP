package br.unirio.visualnrp.algorithm.search;

import java.io.PrintWriter;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.calc.IFitnessCalculator;
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
	public HillClimbing(PrintWriter detailsFile, Project project, int budget, int riskImportance, int maxEvaluations, Constructor constructor) throws Exception
	{
		super(detailsFile, project, budget, riskImportance, maxEvaluations, constructor);
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
		this.currentSolution = constructor.generateSolution();
		Solution hcrs = new Solution(project);
		hcrs.setAllCustomers(currentSolution);
		this.currentFitness = evaluate(hcrs, calculator);

		int customerCount = project.getCustomerCount();
		boolean[] solution = new boolean[customerCount];
		Solution.copySolution(hcrs.getSolution(), solution);

		while (localSearch(solution, calculator))
		{
			this.randomRestartCount++;
			solution = constructor.generateSolution();
		}

		return currentSolution;
	}
}
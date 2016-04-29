package br.unirio.visualnrp.algorithm.search;

import java.io.PrintWriter;
import java.util.Arrays;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.calc.IFitnessCalculator;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.support.PseudoRandom;

/**
 * Iterated Local Search for the next release problem
 */
public class IteratedLocalSearch extends SearchAlgorithm
{
	/**
	 * Initializes the ILS search process
	 */
	public IteratedLocalSearch(PrintWriter detailsFile, Project project, int budget, int riskImportance, int maxEvaluations, Constructor constructor) throws Exception
	{
		super(detailsFile, project, budget, riskImportance, maxEvaluations, constructor);
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

		while (getIterations() < getMaximumIterations())
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
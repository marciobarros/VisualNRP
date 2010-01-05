package br.unirio.visualnrp.algorithm.search;

import java.io.PrintWriter;

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
		Solution bestSolution = new Solution(getProject(), getConstructor().generateSolution());
		double bestFitness = evaluate(bestSolution, calculator, 0.0);

		Solution solution = localSearch(bestSolution, calculator, bestFitness);
		double fitness = calculator.evaluate(solution, getAvailableBudget(), getRiskImportance());
		
		if (fitness > bestFitness)
		{
			bestSolution = solution;
			bestFitness = fitness;
		}
		
		while (getEvaluationsConsumed() < getMaximumEvaluations())
		{
			Solution startSolution = applyPerturbation(bestSolution);
			solution = localSearch(startSolution, calculator, bestFitness);
			fitness = calculator.evaluate(solution, getAvailableBudget(), getRiskImportance());
			
			if (fitness > bestFitness)
			{
				bestSolution = solution;
				bestFitness = fitness;
			}
		}

		return bestSolution.getSolution();
	}

	/**
	 * Applies the perturbation operator upon a solution
	 */
	private Solution applyPerturbation(Solution solution)
	{
		Solution perturbedSolution = solution.clone();
		int customerCount = getProject().getCustomerCount();
		int amount = 2;

		for (int i = 0; i < amount; i++)
		{
			int customer = PseudoRandom.randInt(0, customerCount-1);
			perturbedSolution.flipCustomer(customer);
		}

		return perturbedSolution;
	}
}
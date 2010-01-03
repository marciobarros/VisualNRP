package br.unirio.visualnrp.algorithm.search;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.constructor.RandomConstructor;
import br.unirio.visualnrp.calc.IFitnessCalculator;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.support.PseudoRandom;

public class VisIteratedLocalSearch extends SearchAlgorithm
{
	private double bestFitness;
	private boolean[] bestSol;
	private int minCustomers;
	private final int numberSamplingIter;

	public VisIteratedLocalSearch(PrintWriter detailsFile, Project project, int budget, int riskImportance, int maxEvaluations, int numberSamplingIter, Constructor constructor) throws Exception
	{
		super(detailsFile, project, budget, riskImportance, maxEvaluations, constructor);
		this.numberSamplingIter = numberSamplingIter;
	}

	public boolean[] execute(IFitnessCalculator calculator) throws Exception
	{
		int customerCount = project.getCustomerCount();
		bestSol = new boolean[customerCount];

		this.minCustomers = executeRandomSampling(numberSamplingIter, project, calculator);

//		this.currentSolution = new boolean[customerCount];
//		Solution.copySolution(bestSol, currentSolution);
		this.currentSolution = constructor.generateSolutionInInterval(minCustomers, project.getCustomerCount());
		
		Solution hcrs = new Solution(project);
		hcrs.setAllCustomers(currentSolution);
		this.currentFitness = evaluate(hcrs, calculator);

		localSearch(currentSolution, calculator);
		Solution.copySolution(currentSolution, bestSol);
		bestFitness = this.currentFitness;

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

	private boolean[] perturbSolution(boolean[] solution, int customerCount)
	{
		boolean[] newSolution = Arrays.copyOf(solution, customerCount);
		int amount = 2;

		List<Integer> satisfied = new ArrayList<Integer>();
		List<Integer> notSatisfied = new ArrayList<Integer>();
		
		for (int i = 0; i < solution.length; i++)
		{
			if (solution[i] == true)
			{
				satisfied.add(i);
			} 
			else
			{
				notSatisfied.add(i);
			}
		}

		for (int i = 0; i < amount; i++)
		{
			boolean isAddOperation = true;

			if (canRemoveCustomer(satisfied))
			{
				isAddOperation = PseudoRandom.randDouble() <= 0.5;
			} 

			int customer;
			
			if (isAddOperation)
			{
				int rand = PseudoRandom.randInt(0, notSatisfied.size()-1);
				customer = notSatisfied.remove(rand);
				satisfied.add(rand);
			} 
			else
			{
				int rand = PseudoRandom.randInt(0, satisfied.size()-1);
				customer = satisfied.remove(rand);
				notSatisfied.add(rand);
			}

			newSolution[customer] = !newSolution[customer];
		}

		return newSolution;
	}

	private int executeRandomSampling(int numberSamplingIter, Project project, IFitnessCalculator calculator)
	{
		int numberOfCustomersBest = 0;
		Solution hcrs = new Solution(project);
		Constructor sampConstructor = new RandomConstructor(project);
//		Constructor sampConstructor = new GreedyConstructor(project);

		for (int numElemens = 1; numElemens <= project.getCustomerCount(); numElemens++)
		{
			for (int deriv = 0; deriv < numberSamplingIter; deriv++)
			{
				boolean[] solution = sampConstructor.generateSolutionWith(numElemens);
				hcrs.setAllCustomers(solution);
				double solFitness = evaluate(hcrs, calculator);

				if (solFitness > this.bestFitness)
				{
					Solution.copySolution(solution, this.bestSol);
					this.bestFitness = solFitness;
					setIterationBestFound(getIterations());
					numberOfCustomersBest = numElemens;
				}
			}
		}

		return numberOfCustomersBest;
	}

	@Override
	protected NeighborhoodVisitorResult visitNeighbors(Solution solution, IFitnessCalculator calculator)
	{
		double startingFitness = evaluate(solution, calculator);

		if (getIterations() > getMaximumIterations())
		{
			return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.SEARCH_EXHAUSTED);
		}

		if (startingFitness > currentFitness)
		{
			return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR, startingFitness);
		}

		int len = project.getCustomerCount();
		boolean[] solutionAsArray = new boolean[len];
		Solution.copySolution(solution.getSolution(), solutionAsArray);

		for (int i = 0; i < len; i++)
		{
			int customerI = getSelectionOrderIndex(i);
			solutionAsArray[customerI] = (solutionAsArray[customerI] == true) ? false : true;
			int numberOfCustomers = numberOfCustomersServedBySolution(solutionAsArray);

			if (numberOfCustomers >= minCustomers)
			{
				solution.flipCustomer(customerI);
				double neighborFitness = evaluate(solution, calculator);

				if (getIterations() > getMaximumIterations())
				{
					return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.SEARCH_EXHAUSTED);
				}

				if (neighborFitness > startingFitness)
				{
					return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR, neighborFitness);
				}
				
				solution.flipCustomer(customerI);
			}

			solutionAsArray[customerI] = (solutionAsArray[customerI] == true) ? false : true;
		}

		return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.NO_BETTER_NEIGHBOR);
	}

	private boolean canRemoveCustomer(List<Integer> satisfied)
	{
		return (satisfied.size() - 1) >= this.minCustomers;
	}

	private int numberOfCustomersServedBySolution(boolean[] solution)
	{
		int count = 0;
		
		for (int i = 0; i < solution.length; i++)
		{
			if (solution[i])
			{
				count++;
			}
		}

		return count;
	}

	/**
	 * Determines whether should accept a given solution
	 */
	private boolean shouldAccept(double solutionFitness, double bestFitness)
	{
		return solutionFitness > bestFitness;
	}
}
package sobol.problems.requirements.algorithm.search;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jmetal.util.PseudoRandom;
import sobol.problems.requirements.algorithm.constructor.Constructor;
import sobol.problems.requirements.algorithm.constructor.RandomConstructor;
import sobol.problems.requirements.algorithm.solution.Solution;
import sobol.problems.requirements.model.Project;

public class VisIteratedLocalSearch extends IteratedLocalSearch
{
	private double bestFitness;
	private boolean[] bestSol;
	private int minCustomers;
	private int maxCustomers;
	private final int numberSamplingIter;
	private final float intervalSize;

	public VisIteratedLocalSearch(PrintWriter detailsFile, Project project, int budget, int maxEvaluations, int numberSamplingIter, float intervalSize, Constructor constructor) throws Exception
	{
		super(detailsFile, project, budget, maxEvaluations, constructor);
		this.numberSamplingIter = numberSamplingIter;
		this.intervalSize = intervalSize;
	}

	@Override
	public boolean[] execute() throws Exception
	{
		int customerCount = project.getCustomerCount();
		bestSol = new boolean[customerCount];

		this.minCustomers = executeRandomSampling(numberSamplingIter, project);
		this.maxCustomers = calculateIntervalMax(minCustomers, intervalSize, customerCount);

		this.currentSolution = constructor.generateSolutionInInterval(minCustomers, maxCustomers);
		Solution hcrs = new Solution(project);
		hcrs.setAllCustomers(currentSolution);
		this.currentFitness = evaluate(hcrs);

		localSearch(currentSolution);
		Solution.copySolution(currentSolution, bestSol);
		bestFitness = this.currentFitness;

		while (evaluations < maxEvaluations)
		{
			boolean[] perturbedSolution = perturbSolution(bestSol, customerCount);
			localSearch(perturbedSolution);

			if (shouldAccept(currentFitness, bestFitness))
			{
				Solution.copySolution(currentSolution, bestSol);
				bestFitness = this.currentFitness;
			}
		}

		return bestSol;
	}

	@Override
	protected boolean[] perturbSolution(boolean[] solution, int customerCount)
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
			boolean isAddOperation = false;

			if (canAddAndRemoveCustomer(satisfied))
			{
				isAddOperation = PseudoRandom.randDouble() <= 0.5;
			} 
			else if (canAddCustomer(satisfied))
			{
				isAddOperation = true;
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

	private int executeRandomSampling(int numberSamplingIter, Project project)
	{
		int numberOfCustomersBest = 0;
		Solution hcrs = new Solution(project);
		Constructor sampConstructor = new RandomConstructor(project);

		for (int numElemens = 1; numElemens <= project.getCustomerCount(); numElemens++)
		{
			for (int deriv = 0; deriv < numberSamplingIter; deriv++)
			{
				boolean[] solution = sampConstructor.generateSolutionWith(numElemens);
				hcrs.setAllCustomers(solution);
				double solFitness = evaluate(hcrs);

				if (solFitness > this.bestFitness)
				{
					Solution.copySolution(solution, this.bestSol);
					this.bestFitness = solFitness;
					this.iterationBestFound = evaluations;
					numberOfCustomersBest = numElemens;
				}
			}
		}

		return numberOfCustomersBest;
	}

	@Override
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
		boolean[] solutionAsArray = new boolean[len];
		Solution.copySolution(solution.getSolution(), solutionAsArray);

		for (int i = 0; i < len; i++)
		{
			int customerI = selectionOrder[i];
			solutionAsArray[customerI] = (solutionAsArray[customerI] == true) ? false : true;
			int numberOfCustomers = numberOfCustomersServedBySolution(solutionAsArray);

			if (numberOfCustomers >= minCustomers && numberOfCustomers <= maxCustomers)
			{
				solution.flipCustomer(customerI);
				double neighborFitness = evaluate(solution);

				if (evaluations > maxEvaluations)
				{
					return new NeighborhoodVisitorResult(
							NeighborhoodVisitorStatus.SEARCH_EXHAUSTED);
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

	private boolean canAddAndRemoveCustomer(List<Integer> satisfied)
	{
		return canAddCustomer(satisfied) && (satisfied.size() - 1) >= this.minCustomers;
	}

	private boolean canAddCustomer(List<Integer> satisfied)
	{
		return (satisfied.size() + 1) <= this.maxCustomers;
	}

	private int calculateIntervalMax(int numberCustomersBest, float intervalSize, int customerCount)
	{
		int size = Math.round(customerCount * intervalSize);
		int maxNCustomers = numberCustomersBest + size;

		if (maxNCustomers > customerCount)
		{
			maxNCustomers = customerCount;
		}
		
		return maxNCustomers;
	}

	private int numberOfCustomersServedBySolution(boolean[] solution)
	{
		int count = 0;
		
		for (int i = 0; i < solution.length; i++)
		{
			if (solution[i] == true)
			{
				count++;
			}
		}

		return count;
	}
}
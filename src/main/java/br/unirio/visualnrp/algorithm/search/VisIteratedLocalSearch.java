package br.unirio.visualnrp.algorithm.search;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.constructor.RandomConstructor;
import br.unirio.visualnrp.algorithm.solution.Solution;
import br.unirio.visualnrp.calc.IFitnessCalculator;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.support.PseudoRandom;

public class VisIteratedLocalSearch implements SearchAlgorithm
{
	/**
	 * Order under which requirements will be accessed
	 */
	private int[] selectionOrder;
	
	/**
	 * Solution being visited
	 */
	private boolean[] currentSolution;
	
	/**
	 * Fitness of the solution being visited
	 */
	private double currentFitness;
	
	/**
	 * Number of the random restart where the best solution was found
	 */
	private int iterationBestFound;
	
	/**
	 * File where details of the search process will be printed
	 */
	private PrintWriter detailsFile;
	
	/**
	 * Set of requirements to be optimized
	 */
	private Project project;
	
	/**
	 * Available budget to select requirements
	 */
	private int availableBudget;
	
	/**
	 * Number of fitness evaluations available in the budget
	 */
	private int maxEvaluations;
	
	/**
	 * Number of fitness evaluations executed
	 */
	private int evaluations;
	
	/**
	 * Represents a solution of the problem. Utilized during the local search
	 */
	private Solution tmpSolution;
	
	/**
	 * A constructor algorithm for initial solutions generation.
	 */
	private Constructor constructor;


	private double bestFitness;
	private boolean[] bestSol;
	private int minCustomers;
	private int maxCustomers;
	private final int numberSamplingIter;

	public VisIteratedLocalSearch(PrintWriter detailsFile, Project project, int budget, int maxEvaluations, int numberSamplingIter, Constructor constructor) throws Exception
	{
		this.project = project;
		this.availableBudget = budget;
		this.maxEvaluations = maxEvaluations;
		this.detailsFile = detailsFile;
		this.evaluations = 0;
		this.iterationBestFound = 0;
		this.tmpSolution = new Solution(project);
		this.constructor = constructor;
		createRandomSelectionOrder(project);
		this.numberSamplingIter = numberSamplingIter;
	}

	/**
	 * Gera uma ordem aleatoria de selecao dos requisitos
	 */
	private void createRandomSelectionOrder(Project project)
	{
		int customerCount = project.getCustomerCount();
		int[] temporaryOrder = new int[customerCount];

		for (int i = 0; i < customerCount; i++)
		{
			temporaryOrder[i] = i;
		}

		this.selectionOrder = new int[customerCount];

		for (int i = 0; i < customerCount; i++)
		{
			int index = (int) (PseudoRandom.randDouble() * (customerCount - i));
			this.selectionOrder[i] = temporaryOrder[index];

			for (int j = index; j < customerCount - 1; j++)
			{
				temporaryOrder[j] = temporaryOrder[j + 1];
			}
		}

		for (int i = 0; i < customerCount; i++)
		{
			boolean achou = false;

			for (int j = 0; j < customerCount && !achou; j++)
			{
				if (this.selectionOrder[j] == i)
				{
					achou = true;
				}
			}

			if (!achou)
			{
				System.out.println("ERRO DE GERACAO DE INICIO ALEATORIO");
			}
		}
	}

	public boolean[] execute(IFitnessCalculator calculator) throws Exception
	{
		int customerCount = project.getCustomerCount();
		bestSol = new boolean[customerCount];

		this.minCustomers = executeRandomSampling(numberSamplingIter, project, calculator);
		this.maxCustomers = customerCount;

		//sthis.currentSolution = new boolean[customerCount];
		//Solution.copySolution(bestSol, currentSolution);
		this.currentSolution = constructor.generateSolutionInInterval(minCustomers, maxCustomers);
		
		Solution hcrs = new Solution(project);
		hcrs.setAllCustomers(currentSolution);
		this.currentFitness = evaluate(hcrs, calculator);

		localSearch(currentSolution, calculator);
		Solution.copySolution(currentSolution, bestSol);
		bestFitness = this.currentFitness;

		while (evaluations < maxEvaluations)
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
	 * Evaluates the fitness of a solution, saving detail information
	 */
	private double evaluate(Solution solution, IFitnessCalculator calculator)
	{
		if (++evaluations % 10000 == 0 && detailsFile != null)
		{
			detailsFile.println(evaluations + "; " + currentFitness);
		}

		return calculator.evaluate(solution, availableBudget, 0);
	}

	/**
	 * Performs the local search starting from a given solution
	 */
	private boolean localSearch(boolean[] solution, IFitnessCalculator calculator)
	{
		NeighborhoodVisitorResult result;
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

	private int executeRandomSampling(int numberSamplingIter, Project project, IFitnessCalculator calculator)
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
				double solFitness = evaluate(hcrs, calculator);

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

	private NeighborhoodVisitorResult visitNeighbors(Solution solution, IFitnessCalculator calculator)
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
				double neighborFitness = evaluate(solution, calculator);

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

	/*private int calculateIntervalMax(int numberCustomersBest, float intervalSize, int customerCount)
	{
		int size = Math.round(customerCount * intervalSize);
		int maxNCustomers = numberCustomersBest + size;

		if (maxNCustomers > customerCount)
		{
			maxNCustomers = customerCount;
		}
		
		return maxNCustomers;
	}*/

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

	/**
	 * Determines whether should accept a given solution
	 */
	private boolean shouldAccept(double solutionFitness, double bestFitness)
	{
		return solutionFitness > bestFitness;
	}

	/**
	 * Returns the number of random restarts executed during the search process
	 */
	public int getIterations()
	{
		return evaluations;
	}

	/**
	 * Returns the number of the restart in which the best solution was found
	 */
	public int getIterationBestFound()
	{
		return iterationBestFound;
	}

	/**
	 * Returns the fitness of the best solution
	 */
	public double getFitness()
	{
		return currentFitness;
	}
}
package br.unirio.visualnrp.algorithm.search;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import br.unirio.visualnrp.algorithm.constructor.Constructor;
import br.unirio.visualnrp.algorithm.constructor.GreedyConstructor;
import br.unirio.visualnrp.calc.fitness.IFitnessCalculator;
import br.unirio.visualnrp.model.Project;
import br.unirio.visualnrp.support.PseudoRandom;

public class VisIteratedLocalSearch extends SearchAlgorithm
{
	private int minCustomers;
	private final int numberSamplingIter;

	public VisIteratedLocalSearch(PrintWriter detailsFile, Project project, int maxEvaluations, int numberSamplingIter, Constructor constructor) throws Exception
	{
		super(detailsFile, project, maxEvaluations, constructor);
		this.numberSamplingIter = numberSamplingIter;
	}

	public boolean[] execute(IFitnessCalculator calculator) throws Exception
	{
		RandomSamplingResult rsr = executeRandomSampling(numberSamplingIter, getProject(), calculator);
		this.minCustomers = rsr.minCustomers;

		Solution bestSolution = new Solution(getProject(), rsr.solution);
		double bestFitness = rsr.fitness;

		Solution solution = localSearch(bestSolution, calculator, bestFitness);
		double fitness = calculator.evaluate(solution);
		
		if (fitness > bestFitness)
		{
			bestSolution = solution;
			bestFitness = fitness;
		}
		
		while (getEvaluationsConsumed() < getMaximumEvaluations())
		{
			Solution startSolution = applyPerturbation(bestSolution);
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

	/**
	 * Applies the perturbation operator upon a solution
	 */
	private Solution applyPerturbation(Solution solution)
	{
		int customerCount = getProject().getCustomerCount();
		int amount = 2;

		List<Integer> satisfied = new ArrayList<Integer>();
		List<Integer> notSatisfied = new ArrayList<Integer>();
		
		for (int i = 0; i < customerCount; i++)
		{
			if (solution.isCustomerAttended(i))
			{
				satisfied.add(i);
			} 
			else
			{
				notSatisfied.add(i);
			}
		}

		Solution perturbedSolution = solution.clone();

		for (int i = 0; i < amount; i++)
		{
			boolean isAddOperation = true;

			if (satisfied.size() > this.minCustomers)
			{
				isAddOperation = PseudoRandom.randDouble() <= 0.5;
			} 

			if (isAddOperation)
			{
				int rand = PseudoRandom.randInt(0, notSatisfied.size()-1);
				int customer = notSatisfied.remove(rand);
				perturbedSolution.flipCustomer(customer);
				satisfied.add(rand);
			} 
			else
			{
				int rand = PseudoRandom.randInt(0, satisfied.size()-1);
				int customer = satisfied.remove(rand);
				perturbedSolution.flipCustomer(customer);
				notSatisfied.add(rand);
			}
		}

		return perturbedSolution;
	}

	private RandomSamplingResult executeRandomSampling(int numberSamplingIter, Project project, IFitnessCalculator calculator)
	{
		int numberOfCustomersBest = 0;
		int customerCount = project.getCustomerCount();
		
		Solution hcrs = new Solution(project);
		double bestFitness = Double.MIN_VALUE;
		boolean[] bestSolution = new boolean[customerCount];
		
//		Constructor sampConstructor = new RandomConstructor(project);
		Constructor sampConstructor = new GreedyConstructor(project);

		for (int i = 1; i <= customerCount; i++)
		{
			for (int j = 0; j < numberSamplingIter; j++)
			{
				boolean[] solution = sampConstructor.generateSolutionWith(i);
				hcrs.setAllCustomers(solution);
				double solutionFitness = evaluate(hcrs, calculator, bestFitness);

				if (solutionFitness > bestFitness)
				{
					Solution.copySolution(solution, bestSolution);
					bestFitness = solutionFitness;
					
					setIterationBestFound(getEvaluationsConsumed());
					numberOfCustomersBest = i;
				}
			}
		}

		return new RandomSamplingResult(bestSolution, bestFitness, numberOfCustomersBest);
	}

	// TODO generalizar esta rotina (calcular attendedCustomers sempre e chamar canFlipCustomer?)
	@Override
	protected NeighborhoodVisitorResult visitNeighbors(Solution solution, IFitnessCalculator calculator, double bestFitness)
	{
		double startingFitness = evaluate(solution, calculator, bestFitness);

		if (getEvaluationsConsumed() > getMaximumEvaluations())
		{
			return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.SEARCH_EXHAUSTED);
		}

		int customerCount = getProject().getCustomerCount();
		int attendedCustomers = solution.countAttendedCustomers();

		for (int i = 0; i < customerCount; i++)
		{
			int customerI = getSelectionOrderIndex(i);
			int change = solution.isCustomerAttended(customerI) ? -1 : +1;

			if (attendedCustomers + change >= minCustomers)
			{
				solution.flipCustomer(customerI);
				double neighborFitness = evaluate(solution, calculator, bestFitness);

				if (getEvaluationsConsumed() > getMaximumEvaluations())
				{
					return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.SEARCH_EXHAUSTED);
				}

				if (neighborFitness > startingFitness)
				{
					return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR, neighborFitness);
				}

				solution.flipCustomer(customerI);
			}
		}

		return new NeighborhoodVisitorResult(NeighborhoodVisitorStatus.NO_BETTER_NEIGHBOR);
	}
}

class RandomSamplingResult
{
	public boolean[] solution;
	public double fitness;
	public int minCustomers;

	public RandomSamplingResult(boolean[] solution, double fitness, int minCustomers)
	{
		this.solution = solution;
		this.fitness = fitness;
		this.minCustomers = minCustomers;
	}
}
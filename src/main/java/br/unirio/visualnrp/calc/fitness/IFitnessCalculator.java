package br.unirio.visualnrp.calc.fitness;

import br.unirio.visualnrp.algorithm.search.Solution;

/**
 * Interface for the calculation of fitness for landscapes
 * 
 * @author marciobarros
 */
public interface IFitnessCalculator
{
	double evaluate(Solution solution);
}
package br.unirio.visualnrp.algorithm.search;

/**
 * Possible results of the local search phase
 */
public enum NeighborhoodVisitorStatus
{
	FOUND_BETTER_NEIGHBOR, NO_BETTER_NEIGHBOR, SEARCH_EXHAUSTED
}
package br.unirio.visualnrp.support;

import java.util.Random;

/**
 * This code has been taken from JMetal
 */
public class PseudoRandom
{
	private double seed;
	private double[] oldrand = new double[55];
	private int jrand;
	private static PseudoRandom generator = null;

	/**
	 * Constructor without parameters
	 */
	private PseudoRandom()
	{
		this(System.nanoTime());
	}

	/**
	 * Constructor with seed initialization
	 */
	private PseudoRandom(long seedSeed)
	{
		this.seed = (new Random(seedSeed)).nextDouble();
		this.randomize();
	}

	/**
	 * Get seed number for random and start it up 
	 */
	private void randomize()
	{
		for (int j1 = 0; j1 <= 54; j1++)
			oldrand[j1] = 0.0;

		jrand = 0;
		warmupRandom(seed);
	}

	/**
	 * Get randomize off and running 
	 */
	private void warmupRandom(double seed)
	{
		oldrand[54] = seed;
		double new_random = 0.000000001;
		double prev_random = seed;
		
		for (int j1 = 1; j1 <= 54; j1++)
		{
			int ii = (21 * j1) % 54;
			oldrand[ii] = new_random;
			new_random = prev_random - new_random;
			
			if (new_random < 0.0)
				new_random += 1.0;

			prev_random = oldrand[ii];
		}

		advanceRandom();
		advanceRandom();
		advanceRandom();
		jrand = 0;
	}

	/**
	 * Create next batch of 55 random numbers 
	 */
	private void advanceRandom()
	{
		int j1;
		double new_random;
		for (j1 = 0; j1 < 24; j1++)
		{
			new_random = oldrand[j1] - oldrand[j1 + 31];
			if (new_random < 0.0)
			{
				new_random = new_random + 1.0;
			}
			oldrand[j1] = new_random;
		}
		for (j1 = 24; j1 < 55; j1++)
		{
			new_random = oldrand[j1] - oldrand[j1 - 24];
			if (new_random < 0.0)
			{
				new_random = new_random + 1.0;
			}
			oldrand[j1] = new_random;
		}
	}

	/**
	 * Fetch a single random number between 0.0 and 1.0 
	 */
	private double randomPercentile()
	{
		jrand++;
		
		if (jrand >= 55)
		{
			jrand = 1;
			advanceRandom();
		}
		
		return (double) oldrand[jrand];
	}

	/**
	 * Fetch a single random integer between low and high including the bounds 
	 */
	private synchronized int rnd(int low, int high)
	{
		int res;
		
		if (low >= high)
		{
			res = low;
		}
		else
		{
			res = low + (int) (randomPercentile() * (high - low + 1));

			if (res > high)
				res = high;
		}
		
		return res;
	}

	/**
	 * Fetch a single random real number between low and high including the bounds 
	 */
	private synchronized double rndreal(double low, double high)
	{
		return low + (high - low) * randomPercentile();
	}
	
	/**
	 * Initializes the generator using a given seed
	 */
	public static void init(long seed)
	{
		generator = new PseudoRandom(seed);
	}

	/**
	 * Returns a random double value
	 */
	public static double randDouble()
	{
		if (generator == null)
			generator = new PseudoRandom();

		return generator.rndreal(0.0, 1.0);
	}

	/**
	 * Returns a random integer value between a minimum bound and maximum bound
	 */
	public static int randInt(int minBound, int maxBound)
	{
		if (generator == null)
			generator = new PseudoRandom();

		return generator.rnd(minBound, maxBound);
	}

	/**
	 * Returns a random double value between a minimum bound and a maximum bound
	 */
	public static double randDouble(double minBound, double maxBound)
	{
		if (generator == null)
			generator = new PseudoRandom();

		return generator.rndreal(minBound, maxBound);
	}
}
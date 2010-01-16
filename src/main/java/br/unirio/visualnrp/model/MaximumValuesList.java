package br.unirio.visualnrp.model;

import java.util.ArrayList;

/**
 * Class that represents a list of maximum values for the instances under analysis
 * 
 * @author marciobarros
 */
public class MaximumValuesList extends ArrayList<MaximumValues>
{
	private static final long serialVersionUID = 4685508700385137764L;

	/**
	 * Returns the maximum values for an instance and budget
	 */
	public MaximumValues getMaximumValues(Instance instance, int budget)
	{
		for (MaximumValues values : this)
			if (values.getInstance() == instance && values.getBudget() == budget)
				return values;
		
		return null;
	}

}

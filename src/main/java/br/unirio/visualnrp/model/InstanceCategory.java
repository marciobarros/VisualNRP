package br.unirio.visualnrp.model;

import lombok.Getter;

/**
 * Enumeration of instance categories
 * 
 * @author Marcio
 */
public enum InstanceCategory
{
	CLASSIC("classic", "data/requirements/classic"),
	REALISTIC("realistic", "data/requirements/realistic");
	
	private @Getter String name;
	private @Getter String directory;

	/**
	 * Initializes an instance category
	 */
	private InstanceCategory(String name, String directory)
	{
		this.name = name;
		this.directory = directory;
	}
	
	/**
	 * Returns an instance, given its name
	 */
	public static InstanceCategory get(String name)
	{
		for (InstanceCategory category : values())
			if (category.getName().compareTo(name) == 0)
				return category;
		
		return null;
	}
}
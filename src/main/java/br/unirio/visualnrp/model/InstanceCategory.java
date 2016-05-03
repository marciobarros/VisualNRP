package br.unirio.visualnrp.model;


/**
 * Enumeration of instance categories
 * 
 * @author Marcio
 */
public enum InstanceCategory
{
	CLASSIC("classic", "data/requirements/classic"),
	REALISTIC("realistic", "data/requirements/realistic");
	
	private String name;
	private String directory;

	/**
	 * Initializes an instance category
	 */
	private InstanceCategory(String name, String directory)
	{
		this.name = name;
		this.directory = directory;
	}
	
	/**
	 * Returns the name of the instance category
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the directory of the instance category
	 */
	public String getDirectory()
	{
		return directory;
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
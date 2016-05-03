package br.unirio.visualnrp.model;

import java.util.List;

/**
 * Enumeration of the instances available for processing
 * 
 * @author Marcio
 */
public enum Instance 
{
	NRP1("nrp1", InstanceCategory.CLASSIC),
	NRP2("nrp2", InstanceCategory.CLASSIC),
	NRP3("nrp3", InstanceCategory.CLASSIC),
	NRP4("nrp4", InstanceCategory.CLASSIC),
	NRP5("nrp5", InstanceCategory.CLASSIC),
	NRP_E1("nrp-e1", InstanceCategory.REALISTIC),
	NRP_E2("nrp-e2", InstanceCategory.REALISTIC),
	NRP_E3("nrp-e3", InstanceCategory.REALISTIC),
	NRP_E4("nrp-e4", InstanceCategory.REALISTIC),
	NRP_G1("nrp-g1", InstanceCategory.REALISTIC),
	NRP_G2("nrp-g2", InstanceCategory.REALISTIC),
	NRP_G3("nrp-g3", InstanceCategory.REALISTIC),
	NRP_G4("nrp-g4", InstanceCategory.REALISTIC),
	NRP_M1("nrp-m1", InstanceCategory.REALISTIC),
	NRP_M2("nrp-m2", InstanceCategory.REALISTIC),
	NRP_M3("nrp-m3", InstanceCategory.REALISTIC),
	NRP_M4("nrp-m4", InstanceCategory.REALISTIC);

	private String name;

	private InstanceCategory category;

	/**
	 * Initializes the instance
	 */
	private Instance(String name, InstanceCategory category)
	{
		this.name = name;
		this.category = category;
	}
	
	/**
	 * Returns the name of the instance
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the category of the instance
	 */
	public InstanceCategory getCategory()
	{
		return category;
	}
	
	/**
	 * Returns the filename containing the instance data
	 */
	public String getFilename()
	{
		return category.getDirectory() + "/" + name + ".txt";
	}
	
	/**
	 * Returns an instance, given its name
	 */
	public static Instance get(String name)
	{
		for (Instance instance : values())
			if (instance.getName().compareTo(name) == 0)
				return instance;
		
		return null;
	}

	/**
	 * Adds the instances from a given category to a list
	 */
	public static void addInstancesFromCategory(InstanceCategory category, List<Instance> instances) 
	{
		for (Instance instance : values())
			if (instance.getCategory() == category)
				instances.add(instance);
	}
}
package com.ironiacorp.network.protocol.slp;

public final class SLPServiceType
{
	private static final String NAMING_AUTHORITY_PREFIX = ".";
	
	private static final String TYPE_PREFIX = ":";
	
	/**
	 * the abstract type.
	 */
	private String abstractType;

	/**
	 * the concrete type.
	 */
	private String concreteType;
	
	/**
	 * Naming authority.
	 */
	private String namingAuthority;
	
	public static final String DEFAULT_NAMING_AUTHORITY = "IANA";

	private void processAbstractType(String template)
	{
		int i = template.lastIndexOf(NAMING_AUTHORITY_PREFIX);
		if (i != -1) {
			abstractType = template.substring(0, i);
			namingAuthority = template.substring(i + 1);
		} else {
			abstractType = template;
		}
	}
	
	private void processConcreteType(String template)
	{
		int i = template.lastIndexOf(NAMING_AUTHORITY_PREFIX);
		if (i != -1) {
			concreteType = template.substring(0, i);
			namingAuthority = template.substring(i + 1);
		} else {
			concreteType = template;
		}
	}
	
	/**
	 * Creates a new ServiceType instance. 
	 * 
	 * @param serviceType String representation of a ServiceType. It is defined by
	 * a service template:  <abstract-type>:<concrete-type>"
	 */
	public SLPServiceType(String serviceType)
	{
		String[] serviceTemplate = serviceType.split(TYPE_PREFIX);
		if (serviceTemplate.length < 1) {
			throw new IllegalArgumentException("Not abstract type has been defined");
		}
		
		processAbstractType(serviceTemplate[0]);
		try { 
			processConcreteType(serviceTemplate[1]);
		} catch (IndexOutOfBoundsException e) {
			concreteType = null;
		}
	}
		
	/**
	 * is the ServiceType instance an abstract type ?
	 * 
	 * @return true if this is the case.
	 */
	public boolean isAbstract()
	{
		return concreteType == null;
	}

	/**
	 * get the concrete type part of this ServiceType instance.
	 * 
	 * @return a String representing the concrete type.
	 */
	public String getConcreteType()
	{
		return concreteType;
	}

	/**
	 * get the name of the abstract type of this ServiceType instance.
	 * 
	 * @return a String representing the abstract type.
	 */
	public String getAbstractType()
	{
		return abstractType;
	}

	/**
	 * get the naming authority.
	 * 
	 * @return the naming authority.
	 */
	public String getNamingAuthority()
	{
		if (namingAuthority == null) {
			return DEFAULT_NAMING_AUTHORITY;
		} else {
			return namingAuthority;
		}
	}

	/**
	 * The service type string "service:<abstract-type>" matches all services
	 * of that abstract type. If the concrete type is included also, only
	 * these services match the request.
	 * 
	 * Service types with different Naming Authorities are quite distinct.
	 * In other words, service:x.one and service:x.two are different service
	 * types, as are service:abstract.one:y and service:abstract.two:y.
	 */
	public boolean matches(SLPServiceType serviceType)
	{
		// Compare abstract type
		boolean result = serviceType.abstractType.equals(abstractType);
		
		// Compare concrete type (if any)
		if (! isAbstract() && ! serviceType.isAbstract()) {
			try {
				result &= serviceType.concreteType.equals(concreteType);
			} catch (NullPointerException npe) {
				result = false;
			}
		}
		
		// Compare naming authority (if any)
		if (namingAuthority != null || serviceType.namingAuthority != null) {
			try {
				result &= serviceType.namingAuthority.equals(namingAuthority);
			} catch (NullPointerException npe) {
				result = false;
			}
		}
		
		return result;
	}

	public int getLength()
	{
		int length = abstractType.getBytes().length;
		if (concreteType != null) {
			length += TYPE_PREFIX.getBytes().length + concreteType.getBytes().length;
		}
		if (namingAuthority != null) {
			length += NAMING_AUTHORITY_PREFIX.getBytes().length + namingAuthority.getBytes().length;
		}

		return length;
	}
}

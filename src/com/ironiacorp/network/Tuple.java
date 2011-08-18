package com.ironiacorp.network;

public class Tuple<T, U>
{
	private T t;

	private U u;

	public Tuple(T t, U u)
	{
		if (t == null || u == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}
		this.t = t;
		this.u = u;
	}

	public T getT()
	{
		return t;
	}

	public U getU()
	{
		return u;
	}
}

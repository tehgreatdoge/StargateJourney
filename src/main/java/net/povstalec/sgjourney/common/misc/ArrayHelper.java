package net.povstalec.sgjourney.common.misc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

public class ArrayHelper
{
	@Nonnull
	public static int[] growIntArray(@Nonnull int[] array, int x)
	{
		int[] newarray = Arrays.copyOf(array, array.length + 1);
		newarray[array.length] = x;
		
		return newarray;
	}
	
	public static int[] tableToArray(Map<Double, Double> table)
	{
		int[] addressArray = new int[table.size()];
		
		for(int i = 0; i < addressArray.length; i++)
		{
			addressArray[i] = (int) Math.floor(table.get((double) (i + 1)));
		}
		
		return addressArray;
	}
	
	public static boolean differentNumbers(int[] address)
	{
		List<Integer> arrayList = Arrays.stream(address).boxed().toList();
		Set<Integer> arraySet = new HashSet<Integer>(arrayList);
		return (arraySet.size() == address.length);
	}
	
	public static boolean isArrayPositive(int[] array, boolean includeZero)
	{
		for(int i = 0; i < array.length; i++)
		{
			if(includeZero ? array[i] < 0 : array[i] <= 0)
				return false;
		}
		
		return true;
	}
	
	public static boolean isArrayInBounds(int[] array, int lowestAllowed, int highestAllowed)
	{
		for(int i = 0; i < array.length; i++)
		{
			if(array[i] < lowestAllowed || array[i] > highestAllowed)
				return false;
		}
		
		return true;
	}
}

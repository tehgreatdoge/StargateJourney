package net.povstalec.sgjourney.common.stargate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.povstalec.sgjourney.common.misc.ArrayHelper;

public class Address
{
	/*protected int[] addressArray = new int[] {-1, -1, -1, -1, -1, -1, -1, -1};
	protected int length = 0;
	protected boolean hasPointOfOrigin = false;
	
	public Address() {}
	
	public Address addSymbol(int symbol)
	{
		if(this.hasPointOfOrigin)
		{
			StargateJourney.LOGGER.info("Address is finalized and can't accept more symbols");
			return this;
		}
		
		if(symbol < 0)
			return this;
		
		if(length >= 9)
			return this;
		
		this.addressArray[length] = symbol;
		length++;
		
		return this;
	}
	
	public int[] getFullAddressArray()
	{
		return this.addressArray;
	}
	
	public int[] getAddressArray()
	{
		int[] address = new int[length];
		
		for(int i = 0; i < length; i++)
		{
			address[i] = this.addressArray[i];
		}
		
		return address;
	}
	
	public String getAddressString()
	{
		return addressIntArrayToString(getAddressArray());
	}
	
	public int getAddressLength()
	{
		return this.length;
	}
	
	public boolean isComplete()
	{
		return this.length > 6;
	}*/
	
	//============================================================================================
	//*******************************************Static*******************************************
	//============================================================================================
	
	public enum AddressType
	{
		ADDRESS_7_CHEVRON(6, new int[] {1, 2, 3, 4, 5, 6, 0}),
		ADDRESS_8_CHEVRON(7, new int[] {1, 2, 3, 7, 4, 5, 6, 0}),
		ADDRESS_9_CHEVRON(8, new int[] {1, 2, 3, 7, 8, 4, 5, 6, 0});
		
		private int numberOfSymbols;
		private int[] dialedOrder;
		
		AddressType(int numberOfSymbols, int[] dialedOrder)
		{
			this.numberOfSymbols = numberOfSymbols;
			this.dialedOrder = dialedOrder;
		}
		
		public int getNumberOfSymbols()
		{
			return this.numberOfSymbols;
		}
		
		public int[] getDialedOrder()
		{
			return this.dialedOrder;
		}
	}
	
	public static int[] randomAddress(int size, int limit, long seed)
	{
		return randomAddress(0, size, limit, seed);
	}
	
	public static int[] randomAddress(int prefix, int size, int limit, long seed)
	{
		Random random = new Random(seed);
		int[] address = new int[size];
		boolean isValid = false;
		
		while(!isValid)
		{
			for(int i = 0; i < size; i++)
			{
				if(i == 0 && prefix > 0 && prefix < limit)
					address[i] = prefix;
				else
					address[i] = random.nextInt(1, limit);
			}
			if(differentNumbers(address))
				isValid = true;
		}
		
		return address;
	}
	
	//TODO use this somewhere
	public static int[] randomAddress(int size)
	{
		Random random = new Random();
		int[] address = new int[size];
		boolean isValid = false;
		
		while(!isValid)
		{
			for(int i = 0; i < size; i++)
			{
				address[i] = random.nextInt(1, 39);
			}
			if(differentNumbers(address))
				isValid = true;
		}
		
		return address;
	}
	
	public static int[] addressStringToIntArray(String addressString)
	{
		if(addressString == null)
			return new int[0];
		
		String[] stringArray = addressString.split("-");
		int[] intArray = new int[0];
		
		for(int i = 1; i < stringArray.length; i++)
		{

			int number = Character.getNumericValue(stringArray[i].charAt(0));
			int length = stringArray[i].length();
			if(length > 1)
				number = number * 10 + Character.getNumericValue(stringArray[i].charAt(1));
			
			intArray = ArrayHelper.growIntArray(intArray, number);
		}
		
		return intArray;
	}
	
	public static String addressIntArrayToString(int[] array, int offset)
	{
		String address = "-";
		
		for(int i = offset; i < array.length; i++)
		{
			address = address + array[i] + "-";
		}
		return address;
	}
	
	public static String addressIntArrayToString(int[] array)
	{
		return addressIntArrayToString(array, 0);
	}
	
	private static boolean differentNumbers(int[] address)
	{
		List<Integer> arrayList = Arrays.stream(address).boxed().toList();
		Set<Integer> arraySet = new HashSet<Integer>(arrayList);
		return (arraySet.size() == address.length);
	}
	
	public static boolean addressContainsSymbol(int[] address, int symbol)
	{
		for(int i = 0; i < address.length; i++)
		{
			if(address[i] == symbol)
				return true;
		}
		
		return false;
	}
}

package net.povstalec.sgjourney.common.compatibility.cctweaked.methods;

import java.util.Arrays;
import java.util.List;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.stargate.Address;

public class StargateMethods
{
	public static class EngageSymbol implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "engageSymbol";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			int desiredSymbol = arguments.getInt(0);
			
			MethodResult result = context.executeMainThreadTask(() ->
			{
				int feedback = stargate.engageSymbol(desiredSymbol).getCode();
				return new Object[] {feedback};
			});
			
			return result;
		}
	}
	
	public static class DialedAddress implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getAddress";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				// Will only display the dialed Address
				int[] stargateAddress = stargate.isConnected() ? new int[] {} : stargate.getAddress().toArray();
				List<Integer> address = Arrays.stream(stargateAddress).boxed().toList();
				return new Object[] {address};
			});
			
			return result;
		}
	}
	
	public static class ConnectedAddress implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getAddress";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				List<Integer> address = Arrays.stream(stargate.getAddress().toArray()).boxed().toList();
				return new Object[] {address};
			});
			
			return result;
		}
	}
	
	public static class LocalAddress implements InterfaceMethod<AbstractStargateEntity>
	{
		@Override
		public String getName()
		{
			return "getLocalAddress";
		}

		@Override
		public MethodResult use(IComputerAccess computer, ILuaContext context, AbstractStargateEntity stargate, IArguments arguments) throws LuaException
		{
			MethodResult result = context.executeMainThreadTask(() ->
			{
				List<Integer> dialedAddress = Arrays.stream(new Address(stargate.getID()).toArray()).boxed().toList();
				return new Object[] {dialedAddress};
			});
			
			return result;
		}
	}
}
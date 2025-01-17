package net.povstalec.sgjourney.common.init;

import java.util.List;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.StargateNetworkSettings;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.data.Universe;

public class CommandInit
{
	private static final String EMPTY = StargateJourney.EMPTY;
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("stargateNetwork")
						.then(Commands.literal("address")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getAddress))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("stargateNetwork")
						.then(Commands.literal("extragalacticAddress")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getExtragalacticAddress))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("stargateNetwork")
						.then(Commands.literal("getAllStargates")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getStargates))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("stargateNetwork")
						.then(Commands.literal("version")
								.executes(CommandInit::getVersion)))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("stargateNetwork")
						.then(Commands.literal("forceStellarUpdate")
								.executes(CommandInit::forceStellarUpdate)))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("stargateNetwork")
						.then(Commands.literal("getSettings")
								.executes(CommandInit::getSettings)))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("stargateNetwork")
						.then(Commands.literal("setSettings")
								.then(Commands.literal("useDatapackAddresses")
										.then(Commands.argument("useDatapackAddresses", BoolArgumentType.bool())
												.executes(CommandInit::useDatapackAddresses)))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("stargateNetwork")
						.then(Commands.literal("setSettings")
								.then(Commands.literal("generateRandomSolarSystems")
										.then(Commands.argument("generateRandomSolarSystems", BoolArgumentType.bool())
												.executes(CommandInit::generateRandomSolarSystems)))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("stargateNetwork")
						.then(Commands.literal("setSettings")
								.then(Commands.literal("randomAddressFromSeed")
										.then(Commands.argument("randomAddressFromSeed", BoolArgumentType.bool())
												.executes(CommandInit::randomAddressFromSeed)))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		
		
		// Rings Network Commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("ringsNetwork")
						.then(Commands.literal("getAllRings")
								.then(Commands.argument("dimension", DimensionArgument.dimension())
										.executes(CommandInit::getTransportRings))))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
		
		//Dev commands
		dispatcher.register(Commands.literal(StargateJourney.MODID)
				.then(Commands.literal("info").executes(CommandInit::printStargateNetworkInfo))
				.requires(commandSourceStack -> commandSourceStack.hasPermission(2)));
	}
	
	private static int getAddress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		String dimension = DimensionArgument.getDimension(context, "dimension").dimension().location().toString();
		Level level = context.getSource().getPlayer().level();
		
		String currentDimension = level.dimension().location().toString();
		
		// List of Galaxies the dialing Dimension is located in
		ListTag galaxies = Universe.get(level).getGalaxiesFromDimension(currentDimension);
		
		if(galaxies.isEmpty())
		{
			context.getSource().getPlayer().sendSystemMessage(Component.literal("You are not located in any Galaxy").withStyle(ChatFormatting.DARK_RED));
		}
		else
		{
			// Makes a chat message for each galaxy the Dimension is located in
			for(int i = 0; i < galaxies.size(); i++)
			{
				String galaxy = galaxies.getCompound(i).getAllKeys().iterator().next();
				String address = Universe.get(level).getAddressInGalaxyFromDimension(galaxy, dimension);
				if(address.equals(EMPTY))
					context.getSource().getPlayer().sendSystemMessage(Component.literal(dimension + " is not located in " + galaxy).withStyle(ChatFormatting.RED));
				else
				{
					context.getSource().getPlayer().sendSystemMessage(Component.literal("The address of " + dimension + " in " + galaxy + " is:"));
					context.getSource().getPlayer().sendSystemMessage(Component.literal(address).withStyle(ChatFormatting.GOLD));
				}
			}
			
		}
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getExtragalacticAddress(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		String dimension = DimensionArgument.getDimension(context, "dimension").dimension().location().toString();
		Level level = context.getSource().getPlayer().level();
		
		String address = Universe.get(level).getExtragalacticAddressFromDimension(dimension);
		
		context.getSource().getPlayer().sendSystemMessage(Component.literal("The extragalactic address of " + dimension + " is:"));
		context.getSource().getPlayer().sendSystemMessage(Component.literal(address).withStyle(ChatFormatting.LIGHT_PURPLE));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getStargates(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		String dimension = DimensionArgument.getDimension(context, "dimension").dimension().location().toString();
		Level level = context.getSource().getPlayer().level();
		String solarSystem = Universe.get(level).getSolarSystemFromDimension(dimension);
		
		if(!solarSystem.isEmpty())
		{
			context.getSource().getPlayer().sendSystemMessage(Component.literal("Stargates in " + dimension));
			CompoundTag stargates = StargateNetwork.get(level).getSolarSystem(solarSystem);
			stargates.getAllKeys().forEach(stargateID ->
			{
				CompoundTag stargate = stargates.getCompound(stargateID);
				String stargateDimension = stargate.getString("Dimension");
				
				if(stargateDimension.equals(dimension))
				{
					int[] coordinates = stargate.getIntArray("Coordinates");
					context.getSource().getPlayer().sendSystemMessage(Component.literal(
							stargateID + " at X: " + coordinates[0] + " Y: " + coordinates[1] + " Z: " + coordinates[2]).withStyle(ChatFormatting.AQUA));
				}
			});
		}
		else
			context.getSource().getPlayer().sendSystemMessage(Component.literal("No Stargates could be located in " + dimension).withStyle(ChatFormatting.RED));
		
		return Command.SINGLE_SUCCESS;
	}
	
	private static int getVersion(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();
		
		int version = StargateNetwork.get(level).getVersion();
		
		context.getSource().getPlayer().sendSystemMessage(Component.literal("Stargate Network Version: " + version).withStyle(ChatFormatting.GREEN));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int forceStellarUpdate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();
		
		StargateNetwork.get(level).stellarUpdate(level.getServer());
		
		context.getSource().getPlayer().sendSystemMessage(Component.literal("Stellar Update Applied").withStyle(ChatFormatting.RED));
		return Command.SINGLE_SUCCESS;
	}
	
	
	
	private static int getSettings(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();
		
		boolean useDatapackAddresses = StargateNetworkSettings.get(level).useDatapackAddresses();
		boolean generateRandomSolarSystems = StargateNetworkSettings.get(level).generateRandomSolarSystems();
		boolean randomAddressFromSeed = StargateNetworkSettings.get(level).randomAddressFromSeed();
		
		context.getSource().getPlayer().sendSystemMessage(Component.literal("Use Datapack Addresses: " + useDatapackAddresses).withStyle(ChatFormatting.GOLD));
		context.getSource().getPlayer().sendSystemMessage(Component.literal("Generate Random Solar Systems: " + generateRandomSolarSystems).withStyle(ChatFormatting.GOLD));
		context.getSource().getPlayer().sendSystemMessage(Component.literal("Random Addresses From Seed: " + randomAddressFromSeed).withStyle(ChatFormatting.GOLD));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int useDatapackAddresses(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();
		boolean setting = BoolArgumentType.getBool(context, "useDatapackAddresses");
		
		StargateNetworkSettings.get(level).setUseDatapackAddresses(setting);
		
		context.getSource().getPlayer().sendSystemMessage(Component.literal("Stargate Network Settings changed").withStyle(ChatFormatting.YELLOW));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int generateRandomSolarSystems(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();
		boolean setting = BoolArgumentType.getBool(context, "generateRandomSolarSystems");
		
		StargateNetworkSettings.get(level).setGenerateRandomSolarSystems(setting);
		
		context.getSource().getPlayer().sendSystemMessage(Component.literal("Stargate Network Settings changed").withStyle(ChatFormatting.YELLOW));
		return Command.SINGLE_SUCCESS;
	}
	
	private static int randomAddressFromSeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();
		boolean setting = BoolArgumentType.getBool(context, "randomAddressFromSeed");
		
		StargateNetworkSettings.get(level).setRandomAddressFromSeed(setting);
		
		context.getSource().getPlayer().sendSystemMessage(Component.literal("Stargate Network Settings changed").withStyle(ChatFormatting.YELLOW));
		return Command.SINGLE_SUCCESS;
	}
	
	
	
	private static int getTransportRings(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		String dimension = DimensionArgument.getDimension(context, "dimension").dimension().location().toString();
		Level level = context.getSource().getPlayer().level();

		context.getSource().getPlayer().sendSystemMessage(Component.literal("Transport Rings"));
		context.getSource().getPlayer().sendSystemMessage(Component.literal("-------------------------"));
		
		CompoundTag ringsNetwork = TransporterNetwork.get(level).getRings(dimension);
		System.out.println(ringsNetwork);
		List<String> ringsNList = ringsNetwork.getAllKeys().stream().toList();
		for(int i = 0; i < ringsNList.size(); i++)
		{
			int[] coords = ringsNetwork.getCompound(ringsNList.get(i)).getIntArray("Coordinates");
			context.getSource().getPlayer().sendSystemMessage(Component.literal("X: " + coords[0] + " Y: " + coords[1] + " Z: " + coords[2]).withStyle(ChatFormatting.AQUA));
		}
		context.getSource().getPlayer().sendSystemMessage(Component.literal("-------------------------"));
		
		return Command.SINGLE_SUCCESS;
	}
	
	//Only used for console checks
	private static int printStargateNetworkInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException
	{
		Level level = context.getSource().getPlayer().level();

		System.out.println("Dimensions:\n" + Universe.get(level).getDimensions());
		System.out.println("Solar Systems:\n" + Universe.get(level).getSolarSystems());
		System.out.println("Galaxies:\n" + Universe.get(level).getGalaxies());
		System.out.println("Extragalactic Addresses:\n" + Universe.get(level).getExtragalacticAddressInfo());
		System.out.println("=============================");
		System.out.println("Stargates:\n" + StargateNetwork.get(level).getStargates());
		System.out.println("Stargates in Solar Systems:\n" + StargateNetwork.get(level).getSolarSystems());
		//System.out.println("Connections:\n" + StargateNetwork.get(level).getConnections());

		context.getSource().getPlayer().sendSystemMessage(Component.literal("Printed info onto the console"));
		
		return Command.SINGLE_SUCCESS;
	}
}

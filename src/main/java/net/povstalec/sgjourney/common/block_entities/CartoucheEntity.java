package net.povstalec.sgjourney.common.block_entities;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.address_table.AddressTableItem;
import net.povstalec.sgjourney.common.stargate.address_table.MultitypeAddress;

public abstract class CartoucheEntity extends BlockEntity
{
	private static final String EMPTY = StargateJourney.EMPTY;
	
	public static final String ADDRESS_TABLE = "AddressTable";
	public static final String DIMENSION = "Dimension";
	public static final String SYMBOLS = "Symbols";
	public static final String ADDRESS = "Address";

	@Nonnull
	private String addressTable = EMPTY;
	private String dimension;
	
	private String symbols;
	@Nonnull
	private Address address = new Address();
	
	protected CartoucheEntity(BlockEntityType<?> cartouche, BlockPos pos, BlockState state) 
	{
		super(cartouche, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		
		if(level.isClientSide())
			return;
		if(!addressTable.equals(EMPTY))
			setDataFromAddressTable();
		else if(address.getLength() == 0) {
			if (dimension == null)
				setDimension(level);
			this.address.fromString(getAddressFromDimension());
		}
			
		
		if(symbols == null)
			setSymbols(level);
	}
	
	@Override
    public void load(@Nonnull CompoundTag tag)
    {
    	super.load(tag);

    	if(tag.contains(ADDRESS_TABLE)) {
			String tagTable = tag.getString(ADDRESS_TABLE);
    		addressTable = tagTable != null ? tagTable : EMPTY;
		}
    	if(tag.contains(DIMENSION)) {
    		String tagDimension = tag.getString(DIMENSION);
    		dimension = tagDimension != null ? tagDimension : EMPTY;
		}
    	if(tag.contains(SYMBOLS)) {
    		String tagSymbols = tag.getString(SYMBOLS);
    		symbols = tagSymbols != null ? tagSymbols : EMPTY;
		}
		if(tag.contains(ADDRESS)) {
			int[] tagAddress = tag.getIntArray(ADDRESS);
			address = tagAddress != null ? new Address(tagAddress) : new Address();
		}
	}
	
	@Override
    protected void saveAdditional(@Nonnull CompoundTag tag)
	{
		if(addressTable != null)
			tag.putString(ADDRESS_TABLE, addressTable);
		if(dimension != null)
			tag.putString(DIMENSION, dimension);
		if(symbols != null)
			tag.putString(SYMBOLS, symbols);
		
		super.saveAdditional(tag);
	}
	
	@Override
	public AABB getRenderBoundingBox()
    {
        return new AABB(getBlockPos().getX() - 1d, getBlockPos().getY(), getBlockPos().getZ() - 1d,
        		getBlockPos().getX() + 2d, getBlockPos().getY() + 2d, getBlockPos().getZ() + 2d);
    }
	
	public void setDimension(Level level)
	{
		if(level.isClientSide())
			return;
		
		dimension = level.dimension().location().toString();
	}
	
	public void setDataFromAddressTable()
	{
		Level level = getLevel();
		if(level == null || level.isClientSide())
			return;
		//TODO: make this actually work
		AddressTableItem fetchedTable = AddressTableItem.getAddressTable(level, ResourceLocation.tryParse(this.addressTable));
		if (fetchedTable == null) {
			return;
		}
		MultitypeAddress newData = fetchedTable.getRandomAddress(level);
		if (newData == null) {
			return;
		}
		Address newAddress = newData.getAsAddress(level, level.dimension().location().toString());
		if (newAddress == null) {
			return;
		}
		this.addressTable = EMPTY;
		this.address = newAddress;
	}
	
	public void setSymbols(Level level)
	{
		if(level.isClientSide())
			return;
		
		symbols = Universe.get(level).getSymbols(level.dimension().location().toString());
	}
	
	public void setSymbols(String symbols)
	{
		this.symbols = symbols;
	}
	
	public String getSymbols()
	{
		return this.symbols;
	}
	
	public void setAddress(@Nonnull Address address)
	{
		this.address = address;
	}
	
	public Address getAddress()
	{
		return this.address;
	}
	
	public String getAddressFromDimension()
	{
		String galaxy = EMPTY;
		Set<String> galaxies = Universe.get(level).getGalaxiesFromDimension(dimension).getCompound(0).getAllKeys();
		
		Iterator<String> iterator = galaxies.iterator();
		if(iterator.hasNext())
			galaxy = iterator.next();
			
		
		return Universe.get(level).getAddressInGalaxyFromDimension(galaxy, dimension);
	}
	
	//============================== Networking ==============================\\
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.putIntArray(ADDRESS, address.toArray());
		if (dimension != null) 
			tag.putString(DIMENSION, dimension);
		if (symbols != null)
			tag.putString(SYMBOLS, symbols);
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		if (tag == null) {
			return;
		}
		this.load(tag);
	}
	
	@Override
	@Nullable
	public Packet<ClientGamePacketListener> getUpdatePacket() {
  		return ClientboundBlockEntityDataPacket.create(this);
	}

	//=============================== Subtypes ===============================\\
	public static class Stone extends CartoucheEntity
	{
		public Stone(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.STONE_CARTOUCHE.get(), pos, state);
		}
		
	}
	
	public static class Sandstone extends CartoucheEntity
	{
		public Sandstone(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.SANDSTONE_CARTOUCHE.get(), pos, state);
		}
		
	}

}

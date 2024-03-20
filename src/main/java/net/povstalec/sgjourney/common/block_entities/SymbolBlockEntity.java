package net.povstalec.sgjourney.common.block_entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public abstract class SymbolBlockEntity extends BlockEntity
{
	private static final String SYMBOL = "Symbol";
	private static final String EMPTY = "sgjourney:empty";
	
	@Nonnull
	public String symbol = EMPTY;
	
	public SymbolBlockEntity(BlockEntityType<?> entity, BlockPos pos, BlockState state) 
	{
		super(entity, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();

		Level level = getLevel();
		if(level == null || level.isClientSide())
			return;
		
		if(symbol.equals(EMPTY))
			setSymbol(level);
	}
	
	@Override
    public void load(@Nonnull CompoundTag tag)
    {
    	super.load(tag);
    	
    	if(tag.contains(SYMBOL, 8)) {
    		symbol = tag.getString(SYMBOL);
		}
	}
	
	@Override
    protected void saveAdditional(@Nonnull CompoundTag tag)
	{
		if(symbol != null)
			tag.putString(SYMBOL, symbol);
		
		super.saveAdditional(tag);
	}
	
	public void setSymbol(Level level)
	{
		if(level.isClientSide())
			return;
		
		symbol = Universe.get(level).getPointOfOrigin(level.dimension().location().toString());
		this.setChanged();
	}

	//============================== Networking ==============================\\
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.putString(SYMBOL, symbol);
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		if (tag != null) {
			this.load(tag);
		}
	}
	
	@Override
	@Nullable
	public Packet<ClientGamePacketListener> getUpdatePacket() {
  		return ClientboundBlockEntityDataPacket.create(this);
	}

	//=============================== Subtypes ===============================\\
	public static class Stone extends SymbolBlockEntity
	{
		public Stone(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.STONE_SYMBOL.get(), pos, state);
		}
		
	}
	
	public static class Sandstone extends SymbolBlockEntity
	{
		public Sandstone(BlockPos pos, BlockState state)
		{
			super(BlockEntityInit.SANDSTONE_SYMBOL.get(), pos, state);
		}
		
	}

}

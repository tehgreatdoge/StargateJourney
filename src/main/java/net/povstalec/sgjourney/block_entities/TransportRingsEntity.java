package net.povstalec.sgjourney.block_entities;

import java.util.List;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.blocks.TransportRingsBlock;
import net.povstalec.sgjourney.data.BlockEntityList;
import net.povstalec.sgjourney.data.RingsNetwork;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.BlockInit;
import net.povstalec.sgjourney.init.PacketHandlerInit;
import net.povstalec.sgjourney.network.ClientboundRingsUpdatePacket;

public class TransportRingsEntity extends SGJourneyBlockEntity
{
	ItemStack stack0;
	ItemStack stack1;
	ItemStack stack2;
	
    private BlockPos transportPos;
    private BlockPos targetPos;

    public boolean isSender;
    
    public int emptySpace;
    public int ticks;
    public int progress = 0;
    public int transportHeight = 0;
    
    public int transportLight;
    
    private TransportRingsEntity target;
    
    //TODO fix the bug where the entity doesn't load when player is teleported alongside it
	
	public TransportRingsEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.TRANSPORT_RINGS.get(), pos, state, SGJourneyBlockEntity.Type.TRANSPORT_RINGS);
	}

	@Override
	public AABB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }
	
	@Override
	public void addNewToBlockEntityList()
	{
		super.addNewToBlockEntityList();
		RingsNetwork.get(level).addToNetwork(id, BlockEntityList.get(level).getBlockEntities("TransportRings").getCompound(id));
	}
	
	@Override
	public void addToBlockEntityList()
	{
		super.addToBlockEntityList();
		RingsNetwork.get(level).addToNetwork(id, BlockEntityList.get(level).getBlockEntities("TransportRings").getCompound(id));
	}

	@Override
	public void removeFromBlockEntityList()
	{
		super.removeFromBlockEntityList();
		RingsNetwork.get(level).removeFromNetwork(level, id);
	}
	
	public boolean canTransport()
	{
		if(this.isActivated())
			return false;
		
		return true;
	}

//========================================================================================================
//**********************************************Transporting**********************************************
//========================================================================================================
	
	private void activate(BlockPos targetPos, boolean isSender)
	{
		target = (TransportRingsEntity) level.getBlockEntity(targetPos);
		
		if(!targetPos.equals(this.getBlockPos()) && !this.isActivated() && !target.isActivated())
		{
			if(isSender)
			{
				target.activate(getBlockPos(), false);
				this.isSender = true;
			}
			else
				target.isSender = false;
			
			setActivated(true);
			
			emptySpace = getEmptySpace();
			
			transportPos = new BlockPos(getBlockPos().getX(), (getBlockPos().getY() + getEmptySpace()), getBlockPos().getZ());
			
			int difference = Math.abs(this.getTransportHeight() - target.getTransportHeight());
			
			if(this.transportHeight >= target.transportHeight)
				ticks = 0;
			else
				ticks = -difference;
			
			progress = 0;

			this.targetPos = targetPos;
			
			target = (TransportRingsEntity) level.getBlockEntity(targetPos);
			
			transportLight = LevelRenderer.getLightColor(level, this.transportPos);
			
			ForgeChunkManager.forceChunk(level.getServer().getLevel(level.dimension()), StargateJourney.MODID, getBlockPos(), level.getChunk(getBlockPos()).getPos().x, level.getChunk(getBlockPos()).getPos().z, true, true);
		}
		else
			target = null;
	}
	
	private int getTransportHeight()
	{
		if(getEmptySpace() > 0) 
			transportHeight = Math.abs(getEmptySpace() * 4) + 8;
		else 
			transportHeight = Math.abs(getEmptySpace() * 4) - 2;
		return transportHeight;
	}
	
	public void activate(BlockPos targetPos)
	{
		activate(targetPos, true);
	}
	
	public void deactivate()
	{
		isSender = false;
		setActivated(false);
		ticks = 0;
		progress = 0;
		
		ForgeChunkManager.forceChunk(level.getServer().getLevel(level.dimension()), StargateJourney.MODID, this.getBlockPos(), level.getChunk(this.getBlockPos()).getPos().x, level.getChunk(this.getBlockPos()).getPos().z, false, true);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, TransportRingsEntity rings)
	{
		if(level.isClientSide())
			return;
		
		if(rings.isActivated())
		{
			rings.ticks++;
		}
		
		if(rings.ticks > 0 && rings.ticks <= rings.transportHeight + 17)
			rings.progress = rings.ticks;
		else if(rings.ticks >= rings.transportHeight + 42 && rings.progress > 0)
		{
			rings.progress--;
		}
		
		if(rings.ticks == (rings.transportHeight + 22) && rings.isSender && level.getBlockEntity(rings.targetPos) instanceof TransportRingsEntity)
			rings.startTransporting();
		
		if(rings.ticks > 0 && rings.progress <= 0)
			rings.deactivate();
		      
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(rings.worldPosition)), new ClientboundRingsUpdatePacket(pos, rings.ticks, rings.emptySpace, rings.progress, rings.transportHeight, rings.transportLight));
	}
	
	public void getStatus()
	{
	    System.out.println("ID: " + id);
	    if(this.getBlockPos() != null)
	    	System.out.println("Pos: " + this.getBlockPos().getX() + " " + this.getBlockPos().getY() + " " + this.getBlockPos().getZ());
	    if(targetPos != null)
	    	System.out.println("Target: " + targetPos.getX() + " "  + targetPos.getY() + " " + targetPos.getZ());
	    if(transportPos != null)
	    	System.out.println("Transport: " + transportPos.getX() + " "  + transportPos.getY() + " " + transportPos.getZ());
	    System.out.println("Sending: " + isSender);
	    System.out.println("Ticks: " + ticks);
	}
	
	/*public void emergencyDeactivate()
	{
		if(this.targetPos != null)
		{
			((TransportRingsEntity) level.getBlockEntity(this.targetPos)).activated = false;
		}
	}*/
	
// Actual Transporting
	
	private void startTransporting()
	{
  		AABB localBox = new AABB((transportPos.getX() - 1), (transportPos.getY()), (transportPos.getZ() - 1), 
  									(transportPos.getX() + 2), (transportPos.getY() + 3), (transportPos.getZ() + 2));
		List<Entity> localEntities = this.level.getEntitiesOfClass(Entity.class, localBox);
		
		AABB targetBox = new AABB((target.transportPos.getX() - 1), (target.transportPos.getY()), (target.transportPos.getZ() - 1), 
									(target.transportPos.getX() + 2), (target.transportPos.getY() + 3), (target.transportPos.getZ() + 2));
		List<Entity> targetEntities = this.level.getEntitiesOfClass(Entity.class, targetBox);
    	
    	if(!localEntities.isEmpty())
    		localEntities.stream().forEach(this::transportToTarget);
    	
    	if(!targetEntities.isEmpty())
    		targetEntities.stream().forEach(this::transportFromTarget);
	}
	
	private void transportToTarget(Entity entity)
	{
		double x_offset = entity.getX() - transportPos.getX();
		double y_offset = entity.getY() - transportPos.getY();
		double z_offset = entity.getZ() - transportPos.getZ();
		
		//entity.teleportTo((target.transportPos.getX() + x_offset), (target.transportPos.getY() + y_offset), (target.transportPos.getZ() + z_offset));
		//((ServerLevel) level).getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(target.transportPos), 1, entity.getId());
		if(entity instanceof ServerPlayer player)
			player.teleportTo((target.transportPos.getX() + x_offset), (target.transportPos.getY() + y_offset), (target.transportPos.getZ() + z_offset));
		else
			entity.teleportTo((target.transportPos.getX() + x_offset), (target.transportPos.getY() + y_offset), (target.transportPos.getZ() + z_offset));
		
		System.out.println("Transporting to target: " + entity.toString());
	}
	
	private void transportFromTarget(Entity entity)
	{
		double x_offset = entity.getX() - target.transportPos.getX();
		double y_offset = entity.getY() - target.transportPos.getY();
		double z_offset = entity.getZ() - target.transportPos.getZ();
		
		//entity.teleportTo((transportPos.getX() + x_offset), (transportPos.getY() + y_offset), (transportPos.getZ() + z_offset));
		if(entity instanceof ServerPlayer player)
			player.teleportTo((transportPos.getX() + x_offset), (transportPos.getY() + y_offset), (transportPos.getZ() + z_offset));
		else
			entity.teleportTo((transportPos.getX() + x_offset), (transportPos.getY() + y_offset), (transportPos.getZ() + z_offset));
		
		
		System.out.println("Transporting from target: " + entity.toString());
	}
	
// Activation
	
	public boolean isActivated()
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = this.level.getBlockState(pos);
		if(state.is(BlockInit.TRANSPORT_RINGS.get()))
		{
			return this.level.getBlockState(pos).getValue(TransportRingsBlock.ACTIVATED);
		}
		return false;
	}
	
	public void setActivated(boolean active)
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = this.level.getBlockState(pos);
		if(state.is(BlockInit.TRANSPORT_RINGS.get()))
		{
			level.setBlock(pos, state.setValue(TransportRingsBlock.ACTIVATED, active), 2);
		}
	}
	
	private int getEmptySpace()
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = this.level.getBlockState(pos);
		
		if(!state.is(BlockInit.TRANSPORT_RINGS.get()))
			return 0;
		
		if(state.getValue(TransportRingsBlock.FACING) == Direction.DOWN)
		{
			for(int i = 4; i <= 16; i++)
			{
				if(!level.getBlockState(pos.below(i)).getMaterial().isReplaceable() &&
					level.getBlockState(pos.below(i - 1)).getMaterial().isReplaceable() &&
					level.getBlockState(pos.below(i - 2)).getMaterial().isReplaceable() &&
					level.getBlockState(pos.below(i - 3)).getMaterial().isReplaceable())
				{
					return -i + 1;
				}
			}
		}
		else
		{
			for(int i = 1; i <= 16; i++)
			{
				if(level.getBlockState(pos.above(i)).getMaterial().isReplaceable() &&
					level.getBlockState(pos.above(i + 1)).getMaterial().isReplaceable() &&
					level.getBlockState(pos.above(i + 2)).getMaterial().isReplaceable())
				{
					return i;
				}
			}
		}
		return 0;
	}
	
}

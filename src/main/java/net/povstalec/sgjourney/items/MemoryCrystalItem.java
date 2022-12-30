package net.povstalec.sgjourney.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MemoryCrystalItem extends Item
{

	public MemoryCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		if(!level.isClientSide)
		{
			ItemStack memory_crystal = player.getItemInHand(usedHand);
			
        	if(memory_crystal.hasTag() && player.isShiftKeyDown())
        	{
        		memory_crystal.setTag(new CompoundTag());
        	}
		}
        return super.use(level, player, usedHand);
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return stack.hasTag();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
        if(stack.hasTag())
        {
            String location = stack.getTag().getString("location");
            tooltipComponents.add(Component.literal(location).withStyle(ChatFormatting.BLUE));
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}

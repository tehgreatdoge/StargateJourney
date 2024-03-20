package net.povstalec.sgjourney.common.stargate.address_table;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.AddressTableItemInit;

/**
 * A pool whose entries are automatically populated from the generated solar systems.
 */
public class AddressTableGeneratedPool extends AddressTableItem {
    public static final Codec<AddressTableGeneratedPool> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("weight",1).forGetter(AddressTableGeneratedPool::getWeight)
			).apply(instance, AddressTableGeneratedPool::new));

    public AddressTableGeneratedPool(Integer weight) {
        super(weight);
    }
    @Override
    @Nullable
    public MultitypeAddress getRandomAddress(Level level, Random random) {
        List<String> generatedSystems = getGeneratedDimensions(level);

        return MultitypeAddress.fromDimension(generatedSystems.get(random.nextInt(generatedSystems.size())),false);
    }
    protected List<String> getGeneratedDimensions(Level level) {
        List<String> generatedSystems = new ArrayList<>();
        Universe universe = Universe.get(level);
		CompoundTag solarSystems = universe.getSolarSystems();
		solarSystems.getAllKeys().stream().forEach(systemID ->
		{
			boolean generated = universe.getSolarSystem(systemID).getBoolean(Universe.GENERATED);
			if(generated)
			{
				ListTag dimensionList = universe.getDimensionsFromSolarSystem(systemID);
				
				for(int i = 0; i < dimensionList.size(); i++)
				{
                    var dimension = dimensionList.getString(i);
                    if (dimension != null) {
                        generatedSystems.add(dimension);
                    }
				}
			}
		});
        return generatedSystems;
    }
    @Override
    public AddressTableItemType getType() {
        return AddressTableItemInit.GENERATED_POOL.get();
    }
}

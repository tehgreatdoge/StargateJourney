package net.povstalec.sgjourney.common.stargate.address_table;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.init.AddressTableItemInit;

/**
 * A pool whose entries are automatically populated from the generated solar systems.
 * It's weight is based off of the number of entries found * the weight of the pool.
 * This gives the effect of the entries being in the same pool as the AddressTableInjectGeneratedPool.
 */
public class AddressTableInjectGeneratedPool extends AddressTableGeneratedPool {
    public static final Codec<AddressTableInjectGeneratedPool> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("weight",1).forGetter(AddressTableInjectGeneratedPool::getWeight)
			).apply(instance, AddressTableInjectGeneratedPool::new));
    public AddressTableInjectGeneratedPool(Integer weight) {
        super(weight);
    }
    @Override
    public AddressTableItemType getType() {
        return AddressTableItemInit.INJECT_GENERATED_POOL.get();
    }
    @Override
    public Integer getCalculatedWeight(Level level) {
        return getGeneratedDimensions(level).size() * this.weight;
    }
}

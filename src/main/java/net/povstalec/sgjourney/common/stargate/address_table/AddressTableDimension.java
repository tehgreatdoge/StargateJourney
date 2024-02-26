package net.povstalec.sgjourney.common.stargate.address_table;

import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.AddressTableItemInit;
import net.povstalec.sgjourney.common.stargate.Address;

/**
 * An address table item that returns the address of a given dimension, with a preference for intragalactic connections.
 */
public class AddressTableDimension extends AddressTableItem {
    public static final Codec<AddressTableDimension> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("weight",1).forGetter(AddressTableDimension::getWeight),
    		Level.RESOURCE_KEY_CODEC.fieldOf("value").forGetter(AddressTableDimension::getDimension),
    		Level.RESOURCE_KEY_CODEC.optionalFieldOf("origin").forGetter(AddressTableDimension::getOrigin),
    		Codec.BOOL.optionalFieldOf("optional",false).forGetter(AddressTableDimension::getOptional)
			).apply(instance, AddressTableDimension::new));

    protected ResourceKey<Level> dimension;
    protected Optional<ResourceKey<Level>> origin;
    protected Boolean optional;
    public AddressTableDimension(Integer weight, ResourceKey<Level> dimension, Optional<ResourceKey<Level>> origin, Boolean optional) {
        super(weight);
        this.dimension = dimension;
        this.origin = origin;
        this.optional = optional;
    }
    @Override
    public AddressTableItemType getType() {
        return AddressTableItemInit.DIMENSION.get();
    }
    @SuppressWarnings("null")
    @Nullable
    @Override
    public Address getRandomAddress(Level level, Random random) {
        Universe universe = Universe.get(level);
        String address = null;
        if (origin.isPresent()) {
            address = universe.getAddressToDimension(dimension.location().toString(), origin.get().location().toString());
        }
        else {
            address = universe.getAddressToDimension(dimension.location().toString(),level.dimension().location().toString());
        }
        if (address != null) {
            return new Address(address);
        }
        return null;
    }
    public ResourceKey<Level> getDimension() {
        return dimension;
    }
    public Optional<ResourceKey<Level>> getOrigin() {
        return origin;
    }
    @Override
    public Integer getCalculatedWeight(Level level) {
        // If the dimension is optional and we can't find it, just return a weight of 0.
        if (Boolean.TRUE.equals(this.optional) && Universe.get(level).getSolarSystemFromDimension(level.dimension().location().toString()).equals(StargateJourney.EMPTY)) {
            return 0;
        }
        return this.getWeight();
    }
    public Boolean getOptional() {
        return optional;
    }
}

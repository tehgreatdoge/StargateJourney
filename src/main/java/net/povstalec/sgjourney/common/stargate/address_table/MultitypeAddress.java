package net.povstalec.sgjourney.common.stargate.address_table;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.NotImplementedException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.SolarSystem;

/**
 * A class that pairs a type of address with a way to get it and
 * sometimes a place to get it from.
 * Also supports serialization.
 * Should probably become just (de)serialization once the universe is rewritten,
 * as that would allow for it to just be replaced with a bunch of classes that implement
 * "AddressConvertable"
 **/
public class MultitypeAddress {
    private static final String ADDRESS_TAG = "Address";
    private static final String DIMENSION_TAG = "Dimension";
    private static final String SOLAR_SYSTEM_TAG = "SolarSystem";
    private static final String EXTRAGALACTIC_TAG = "Extragalactic";

    @Nullable
    private Address address;
    @Nullable
    private String dimension;
    @Nullable
    private String solarSystem;
    @Nullable
    // TODO: change this to allow for solar systems or dimensions
    private String origin;

    @Nonnull 
    private final AddressType type;

    public MultitypeAddress(@Nonnull Address address) {
        this.address = address;
        this.type = AddressType.ADDRESS;
    }
    public MultitypeAddress(@Nonnull String value, @Nonnull AddressType type) {
        this.type = type;
        if (type == AddressType.ADDRESS) {
            address = new Address(value);
        }
        else if (type == AddressType.DIMENSION || type == AddressType.DIMENSION_EXTRAGALACTIC) {
            dimension = value;
        }
        else if (type == AddressType.SOLAR_SYSTEM || type == AddressType.SOLAR_SYSTEM_EXTRAGALACTIC)  {
            solarSystem = value;
        }
    }
    @SuppressWarnings("null")
    @Nullable
    public static MultitypeAddress fromTag(CompoundTag tag) {
        if (tag.contains(SOLAR_SYSTEM_TAG, Tag.TAG_STRING)) {
            boolean extragalactic = false;
            if (tag.contains(EXTRAGALACTIC_TAG, Tag.TAG_BYTE)) {
                extragalactic = tag.getBoolean(EXTRAGALACTIC_TAG);
            }
            return new MultitypeAddress(tag.getString(SOLAR_SYSTEM_TAG), extragalactic ? AddressType.SOLAR_SYSTEM_EXTRAGALACTIC : AddressType.SOLAR_SYSTEM);
        }
        else if (tag.contains(DIMENSION_TAG, Tag.TAG_STRING)) {
            boolean extragalactic = false;
            if (tag.contains(EXTRAGALACTIC_TAG, Tag.TAG_BYTE)) {
                extragalactic = tag.getBoolean(EXTRAGALACTIC_TAG);
            }
            return new MultitypeAddress(tag.getString(DIMENSION_TAG), extragalactic ? AddressType.DIMENSION_EXTRAGALACTIC : AddressType.DIMENSION);
        }
        else if (tag.contains(ADDRESS_TAG, Tag.TAG_INT_ARRAY)) {
            return new MultitypeAddress(new Address(tag.getIntArray(ADDRESS_TAG)));
        }
        return null;
    }
    @Nonnull
    public static MultitypeAddress fromDimension(@Nonnull String dimension, boolean extragalactic) {
        return new MultitypeAddress(dimension, extragalactic ? AddressType.DIMENSION_EXTRAGALACTIC : AddressType.DIMENSION);
    }
    @Nonnull
    public static MultitypeAddress fromSolarSystem(@Nonnull String solarSystem, boolean extragalactic) {
        return new MultitypeAddress(solarSystem, extragalactic ? AddressType.SOLAR_SYSTEM_EXTRAGALACTIC : AddressType.SOLAR_SYSTEM);
    }
    @Nonnull
    public MultitypeAddress withOrigin(@Nonnull String originDimension) {
        this.origin = originDimension;
        return this;
    }
    /**
     * Returns an Address corresponding to the contents of the MultitypeAddress
     * @param level A level for using to get the Universe
     * @param defaultOrigin The dimension to use as the origin of the address lookup
     * if the MultitypeAddress doesn't have an origin
     * @return Null if the MultitypeAddress's contents could not be found.
     */
    @Nullable
    public Address getAsAddress(@Nonnull Level level, @Nonnull String defaultOrigin) {
        if (origin != null) {
            defaultOrigin = origin;
        }
        if (type == AddressType.ADDRESS) {
            return address.copy();
        }
        Universe universe = Universe.get(level);
        if (type == AddressType.SOLAR_SYSTEM_EXTRAGALACTIC) {
            // FIXME: Once universe is rewritten, this code needs to be a lot safer.
            return new Address(universe.getSolarSystem(solarSystem).getString(Universe.EXTRAGALACTIC_ADDRESS));
        }
        else if (type == AddressType.DIMENSION_EXTRAGALACTIC) {
            // FIXME: as well, although this one is less bad as it just results in an empty address
            return new Address(universe.getExtragalacticAddressFromDimension(dimension));
        }
        // FIXME: These two are okayish, just need to return null and have an overload on getAddressToX for using dimensions instead of SolarSystems
        else if (type == AddressType.SOLAR_SYSTEM) {
            return new Address(universe.getAddressToSolarSystem(solarSystem, universe.getSolarSystemFromDimension(defaultOrigin)));
        }
        else if (type == AddressType.DIMENSION) {
            return new Address(universe.getAddressToDimension(dimension, universe.getSolarSystemFromDimension(defaultOrigin)));
        }

        return null;
    }

    public enum AddressType {
        ADDRESS(),
        SOLAR_SYSTEM(),
        SOLAR_SYSTEM_EXTRAGALACTIC(),
        DIMENSION(),
        DIMENSION_EXTRAGALACTIC();
        
        private AddressType() {

        }
    }
}

package net.povstalec.sgjourney.common.stargate.address_table;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.init.AddressTableItemInit;
import net.povstalec.sgjourney.common.stargate.Address;

public class AddressTableAddress extends AddressTableItem {
    public static final Codec<AddressTableAddress> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("weight",1).forGetter(AddressTableAddress::getWeight),
    		Codec.INT.listOf().fieldOf("value").forGetter(AddressTableAddress::getAddress)
			).apply(instance, AddressTableAddress::new));

    protected List<Integer> address;
    public AddressTableAddress(Integer weight, List<Integer> address) {
        super(weight);
        this.address = address;
    }
    @Override
    public AddressTableItemType getType() {
        return AddressTableItemInit.ADDRESS.get();
    }
    @Override
    @Nonnull
    public Address getRandomAddress(Level level, Random random) {
        return new Address(address.stream().mapToInt(integer -> integer).toArray());
    }
    public List<Integer> getAddress() {
        return address;
    }
}

package net.povstalec.sgjourney.common.stargate.address_table;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.AddressTableItemInit;
import net.povstalec.sgjourney.common.stargate.Address;

public class AddressTableReplaceIfNull extends AddressTableItem {
    public static final Codec<AddressTableReplaceIfNull> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("weight",1).forGetter(AddressTableReplaceIfNull::getWeight),
    		AddressTableItemInit.CODEC.fieldOf("replace").forGetter(AddressTableReplaceIfNull::getReplace),
    		AddressTableItemInit.CODEC.fieldOf("value").forGetter(AddressTableReplaceIfNull::getValue)
			).apply(instance, AddressTableReplaceIfNull::new));

    protected AddressTableItem replace;
    protected AddressTableItem value;
    public AddressTableReplaceIfNull(Integer weight, AddressTableItem replace, AddressTableItem value) {
        super(weight);
        this.replace = replace;
        this.value = value;
    }
    @Override
    public AddressTableItemType getType() {
        return AddressTableItemInit.REPLACE_IF_NULL.get();
    }
    @Override
    @Nullable
    public Address getRandomAddress(Level level, Random random) {
        Address result = value.getRandomAddress(level, random);
        if (result == null) {
            return replace.getRandomAddress(level,random);
        }
        return result;
    }
    public AddressTableItem getReplace() {
        return replace;
    }
    public AddressTableItem getValue() {
        return value;
    }
}

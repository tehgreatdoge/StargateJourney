package net.povstalec.sgjourney.common.stargate.address_table;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.init.AddressTableItemInit;
import net.povstalec.sgjourney.common.stargate.Address;

public class AddressTablePool extends AddressTableItem {
    public static final Codec<AddressTablePool> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("weight",1).forGetter(AddressTablePool::getWeight),
    		AddressTableItemInit.CODEC.listOf().fieldOf("entries").forGetter(AddressTablePool::getEntries)
			).apply(instance, AddressTablePool::new));

    protected List<AddressTableItem> entries;
    public AddressTablePool(Integer weight, List<AddressTableItem> entries) {
        super(weight);
        this.entries = entries;
    }
    @Override
    public AddressTableItemType getType() {
        return AddressTableItemInit.POOL.get();
    }
    @Override
    @Nullable
    public MultitypeAddress getRandomAddress(Level level, Random random) {
        ArrayList<AddressTableItem> items = new ArrayList<>();
        this.getEntries().forEach(entry -> {
            int repeats = entry.getCalculatedWeight(level);
            for (int i=0;i<repeats;i++) {
                items.add(entry);
            }
        });
        
        if (items.isEmpty()) {
            return null;
        }

        return items.get(random.nextInt(items.size())).getRandomAddress(level);
    }
    public List<AddressTableItem> getEntries() {
        return entries;
    }
}

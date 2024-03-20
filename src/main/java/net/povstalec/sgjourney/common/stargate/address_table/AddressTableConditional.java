package net.povstalec.sgjourney.common.stargate.address_table;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.povstalec.sgjourney.common.init.AddressTableItemInit;

/**
 * An address table item that returns a weight of zero if not all conditions are satisfied.
 * It accepts a child item `value` to return when it is called.
 */
public class AddressTableConditional extends AddressTableItem {
    private static final Codec<Pair<String, String>> CONDITION_CODEC = Codec.pair(Codec.STRING.fieldOf("type").codec(), Codec.STRING.fieldOf("value").codec());
    public static final Codec<AddressTableConditional> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("weight",1).forGetter(AddressTableConditional::getWeight),
    		CONDITION_CODEC.listOf().fieldOf("conditions").forGetter(AddressTableConditional::getConditions),
    		AddressTableItemInit.CODEC.fieldOf("value").forGetter(AddressTableConditional::getValue)
			).apply(instance, AddressTableConditional::new));

    protected List<Pair<String, String>> conditions;
    protected AddressTableItem value;
    public AddressTableConditional(Integer weight, List<Pair<String, String>> conditions, AddressTableItem value) {
        super(weight);
        this.conditions = conditions;
        this.value = value;
    }
    @Override
    public AddressTableItemType getType() {
        return AddressTableItemInit.CONDITIONAL.get();
    }
    @Override
    @Nullable
    public MultitypeAddress getRandomAddress(Level level, Random random) {
        return value.getRandomAddress(level, random);
    }
    public List<Pair<String, String>> getConditions() {
        return conditions;
    }
    public AddressTableItem getValue() {
        return value;
    }
    @Override
    public Integer getCalculatedWeight(Level level) {
        for (Pair<String,String> condition : getConditions()) {
            if (condition.getFirst().equals("mod_loaded") && (!ModList.get().isLoaded(condition.getSecond()))) {
                    return 0;
            }
        }
        return getWeight();
    }
    
}

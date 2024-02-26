package net.povstalec.sgjourney.common.stargate.address_table;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.stargate.Address;

public abstract class AddressTableItem {
	public static final ResourceKey<Registry<AddressTableItem>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(StargateJourney.MODID, "address_table"));
    public final Integer weight;

    protected AddressTableItem(Integer weight) {
        this.weight = weight;
    }
    public abstract AddressTableItemType getType();

    public Address getRandomAddress(Level level) {
        return this.getRandomAddress(level, new Random());
    }

    @Nullable
    public abstract Address getRandomAddress(Level level, Random random);
	@Nullable
	public static AddressTableItem getAddressTable(Level level, ResourceLocation table)
	{
		final RegistryAccess registries = level.getServer().registryAccess();
        final Registry<AddressTableItem> registry = registries.registryOrThrow(AddressTableItem.REGISTRY_KEY);
        
        return registry.get(table);
	}
    public Integer getCalculatedWeight(Level level) {
        return this.getWeight();
    }
    protected Integer getWeight() {
        return this.weight;
    }
}

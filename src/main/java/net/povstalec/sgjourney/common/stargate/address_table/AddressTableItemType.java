package net.povstalec.sgjourney.common.stargate.address_table;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

public class AddressTableItemType {
	public static final ResourceLocation ADDRESS_TABLE_ITEM_TYPE_LOCATION = new ResourceLocation(StargateJourney.MODID, "address_table_item_type");
	public static final ResourceKey<Registry<AddressTableItemType>> REGISTRY_KEY = ResourceKey.createRegistryKey(ADDRESS_TABLE_ITEM_TYPE_LOCATION);
	public static final Codec<ResourceKey<AddressTableItemType>> RESOURCE_KEY_CODEC = ResourceKey.codec(ResourceKey.createRegistryKey(ADDRESS_TABLE_ITEM_TYPE_LOCATION));
	
	private final Codec<? extends AddressTableItem> codec;
	
	public AddressTableItemType(Codec<? extends AddressTableItem> codec)
	{
		this.codec = codec;
	}
	
	public Codec<? extends AddressTableItem> getCodec()
	{
		return codec;
	}
}

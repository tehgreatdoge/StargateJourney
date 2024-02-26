package net.povstalec.sgjourney.common.init;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.stargate.address_table.AddressTableAddress;
import net.povstalec.sgjourney.common.stargate.address_table.AddressTableConditional;
import net.povstalec.sgjourney.common.stargate.address_table.AddressTableDimension;
import net.povstalec.sgjourney.common.stargate.address_table.AddressTableGeneratedPool;
import net.povstalec.sgjourney.common.stargate.address_table.AddressTableInjectGeneratedPool;
import net.povstalec.sgjourney.common.stargate.address_table.AddressTableItem;
import net.povstalec.sgjourney.common.stargate.address_table.AddressTableItemType;
import net.povstalec.sgjourney.common.stargate.address_table.AddressTablePool;
import net.povstalec.sgjourney.common.stargate.address_table.AddressTableReplaceIfNull;

public class AddressTableItemInit {
	public static final DeferredRegister<AddressTableItemType> ADDRESS_TABLE_ITEM_TYPES = DeferredRegister.create(AddressTableItemType.ADDRESS_TABLE_ITEM_TYPE_LOCATION, StargateJourney.MODID);
	public static final Supplier<IForgeRegistry<AddressTableItemType>> ADDRESS_TABLE_ITEM_TYPE = ADDRESS_TABLE_ITEM_TYPES.makeRegistry(()->new RegistryBuilder<AddressTableItemType>().setDefaultKey(new ResourceLocation(StargateJourney.MODID, "dimension")));

	public static final RegistryObject<AddressTableItemType> ADDRESS = ADDRESS_TABLE_ITEM_TYPES.register("address", () -> new AddressTableItemType(AddressTableAddress.CODEC));
	public static final RegistryObject<AddressTableItemType> DIMENSION = ADDRESS_TABLE_ITEM_TYPES.register("dimension", () -> new AddressTableItemType(AddressTableDimension.CODEC));
	public static final RegistryObject<AddressTableItemType> POOL = ADDRESS_TABLE_ITEM_TYPES.register("pool", () -> new AddressTableItemType(AddressTablePool.CODEC));
	public static final RegistryObject<AddressTableItemType> CONDITIONAL = ADDRESS_TABLE_ITEM_TYPES.register("conditional", () -> new AddressTableItemType(AddressTableConditional.CODEC));
	public static final RegistryObject<AddressTableItemType> GENERATED_POOL = ADDRESS_TABLE_ITEM_TYPES.register("generated_systems", () -> new AddressTableItemType(AddressTableGeneratedPool.CODEC));
	public static final RegistryObject<AddressTableItemType> INJECT_GENERATED_POOL = ADDRESS_TABLE_ITEM_TYPES.register("inject_generated_systems", () -> new AddressTableItemType(AddressTableInjectGeneratedPool.CODEC));
	public static final RegistryObject<AddressTableItemType> REPLACE_IF_NULL = ADDRESS_TABLE_ITEM_TYPES.register("replace_if_null", () -> new AddressTableItemType(AddressTableReplaceIfNull.CODEC));
	
	public static final Codec<AddressTableItem> CODEC = ExtraCodecs.lazyInitializedCodec(()->
        ADDRESS_TABLE_ITEM_TYPE.get().getCodec().dispatch("type", AddressTableItem::getType, AddressTableItemType::getCodec)
    );

    private AddressTableItemInit() {}

	public static void register(IEventBus eventBus)
	{
		ADDRESS_TABLE_ITEM_TYPES.register(eventBus);
	}
}

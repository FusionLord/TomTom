# TomTom
This is a gps mod for Minecraft.

### How to use:
``` java
	/**
	 * @param mod Your @Mod.instance object.
	 * @param player The player we are setting the gps for.
	 * @param pos The destination position.
	 * @param text The short description of the destination
	 */

	public static void sendTomTomPos(Object mod, EntityPlayer player, BlockPos pos, String text)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setLong("location", pos.toLong());
		tag.setLong("uuid-most", player.getUniqueID().getMostSignificantBits());
		tag.setLong("uuid-least", player.getUniqueID().getLeastSignificantBits());
		tag.setString("text", text);
		FMLInterModComms.sendRuntimeMessage(mod, "tomtom", "setPointer", tag);
	}
```
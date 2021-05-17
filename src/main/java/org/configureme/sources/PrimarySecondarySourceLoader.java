package org.configureme.sources;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 16.05.21 22:41
 */
abstract class PrimarySecondarySourceLoader  implements SourceLoader{
	private SourceLoader primaryLoader;
	private SourceLoader secondaryLoader;

	PrimarySecondarySourceLoader(SourceLoader aPrimaryLoader, SourceLoader aSecondaryLoader){
		primaryLoader = aPrimaryLoader;
		secondaryLoader = aSecondaryLoader;
	}

	@Override
	public boolean isAvailable(ConfigurationSourceKey key) {
		boolean primary = primaryLoader.isAvailable(key);
		return primary ?
				true :
				secondaryLoader.isAvailable(key.toType(ConfigurationSourceKey.Type.FILE));
	}

	@Override
	public long getLastChangeTimestamp(ConfigurationSourceKey key) {
		long primary = primaryLoader.getLastChangeTimestamp(key);
		return primary != -1 ?
				primary :
				secondaryLoader.getLastChangeTimestamp(key.toType(ConfigurationSourceKey.Type.FILE));

	}

	@Override
	public String getContent(ConfigurationSourceKey key) {
		String content = primaryLoader.getContent(key);
		return content != null ?
				content :
				secondaryLoader.getContent(key.toType(ConfigurationSourceKey.Type.FILE));

	}

}

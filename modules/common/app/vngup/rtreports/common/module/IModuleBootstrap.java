package vngup.rtreports.common.module;

public interface IModuleBootstrap {
	/**
	 * Initializes module.
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception;

	/**
	 * Called for cleanup before application exists.
	 * 
	 * @throws Exception
	 */
	public void destroy() throws Exception;
}

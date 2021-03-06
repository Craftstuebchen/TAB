package me.neznamy.tab.platforms.velocity;

import java.util.Map.Entry;

import com.velocitypowered.api.proxy.ProxyServer;

import me.neznamy.tab.shared.placeholders.PlaceholderRegistry;
import me.neznamy.tab.shared.placeholders.Placeholders;
import me.neznamy.tab.shared.placeholders.ServerPlaceholder;

/**
 * Velocity registry to register velocity-only placeholders
 */
public class VelocityPlaceholderRegistry implements PlaceholderRegistry {

	//instance of proxyserver
	private ProxyServer server;
	
	/**
	 * Constructs new instance with given parameter
	 * @param server - instance of proxyserver
	 */
	public VelocityPlaceholderRegistry(ProxyServer server) {
		this.server = server;
	}
	
	@Override
	public void registerPlaceholders() {
		Placeholders.registerPlaceholder(new ServerPlaceholder("%maxplayers%", -1) {
			public String get() {
				return server.getConfiguration().getShowMaxPlayers()+"";
			}
		});
		for (Entry<String, String> servers : server.getConfiguration().getServers().entrySet()) {
			Placeholders.registerPlaceholder(new ServerPlaceholder("%online_" + servers.getKey() + "%", 1000) {
				public String get() {
					return server.getServer(servers.getKey()).get().getPlayersConnected().size()+"";
				}
			});
		}
	}
}
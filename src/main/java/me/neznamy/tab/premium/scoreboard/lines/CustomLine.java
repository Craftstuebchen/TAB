package me.neznamy.tab.premium.scoreboard.lines;

import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.premium.scoreboard.Scoreboard;
import me.neznamy.tab.shared.PacketAPI;
import me.neznamy.tab.shared.packets.PacketPlayOutScoreboardTeam;
import me.neznamy.tab.shared.placeholders.Placeholders;

/**
 * Fully customizable line, to use this class user must follow the following formula in a line
 * "Custom|prefix|name|suffix|number" where even name supports placeholders, however has a chance to flicker on refresh
 * Not for public use
 */
public class CustomLine extends ScoreboardLine {

	private Scoreboard parent;
	private String prefix;
	private String name;
	private String suffix;
	private int score;
	
	public CustomLine(Scoreboard parent, int lineNumber, String prefix, String name, String suffix, int score) {
		super(lineNumber);
		this.parent = parent;
		this.prefix = prefix;
		this.name = name;
		this.suffix = suffix;
		this.score = score;
		refreshUsedPlaceholders();
	}

	@Override
	public void refresh(TabPlayer refreshed, boolean force) {
		String oldName = refreshed.getProperty(teamName + "-name").get();
		boolean prefix = refreshed.getProperty(teamName + "-prefix").update();
		boolean name = refreshed.getProperty(teamName + "-name").update();
		boolean suffix = refreshed.getProperty(teamName + "-suffix").update();
		if (prefix || name || suffix) {
			if (name) {
				//name changed as well
				PacketAPI.removeScoreboardScore(refreshed, oldName, teamName);
				PacketAPI.registerScoreboardScore(refreshed, teamName, refreshed.getProperty(teamName + "-name").get(), 
						refreshed.getProperty(teamName + "-prefix").get(), refreshed.getProperty(teamName + "-suffix").get(), ObjectiveName, score);
			} else {
				//only prefix/suffix changed
				refreshed.sendCustomPacket(PacketPlayOutScoreboardTeam.UPDATE_TEAM_INFO(teamName, refreshed.getProperty(teamName + "-prefix").get(), 
						refreshed.getProperty(teamName + "-suffix").get(), "always", "always", 69));
			}
		}
	}

	@Override
	public void refreshUsedPlaceholders() {
		usedPlaceholders = Placeholders.getUsedPlaceholderIdentifiersRecursive(prefix, name, suffix);
	}

	@Override
	public void register(TabPlayer p) {
		p.setProperty(teamName + "-prefix", prefix);
		p.setProperty(teamName + "-name", name);
		p.setProperty(teamName + "-suffix", suffix);
		PacketAPI.registerScoreboardScore(p, teamName, p.getProperty(teamName + "-name").get(), p.getProperty(teamName + "-prefix").get(),
				p.getProperty(teamName + "-suffix").get(), ObjectiveName, score);
	}

	@Override
	public void unregister(TabPlayer p) {
		if (parent.players.contains(p)) {
			PacketAPI.removeScoreboardScore(p, p.getProperty(teamName + "-name").get(), teamName);
		}
	}
}
package com.slayerboosting;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Slf4j
@PluginDescriptor(
	name = "Slayer Boosting",
	description = "Reminds you which slayer master to visit for milestone bonus point tasks",
	tags = {"slayer", "boosting", "points", "streak", "reminder"}
)
public class SlayerBoostingPlugin extends Plugin
{
	// Varbit IDs for slayer data
	private static final int SLAYER_TASK_STREAK_VARBIT = 4069;
	private static final int SLAYER_POINTS_VARBIT = 4068;
	// VarPlayer 394 = remaining kills on current task
	private static final int SLAYER_TASK_SIZE_VARP = 394;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private SlayerBoostingConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SlayerBoostingOverlay overlay;

	@Inject
	private NpcOverlayService npcOverlayService;

	private final Function<NPC, HighlightedNpc> npcHighlighter = this::highlightNpc;

	@Getter
	private int streak;

	@Getter
	private int points;

	@Getter
	private int taskCount;

	@Getter
	private boolean milestoneActive;

	@Getter
	private SlayerMaster milestoneMaster;

	@Getter
	private int milestoneTaskNumber;

	@Getter
	private int milestonePoints;

	@Getter
	private SlayerMaster nextMaster;

	@Getter
	private int nextTaskPoints;

	@Provides
	SlayerBoostingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SlayerBoostingConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		npcOverlayService.registerHighlighter(npcHighlighter);

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invokeLater(() ->
			{
				readVarbits();
				evaluateRules();
			});
		}

		log.info("Slayer Boosting plugin started");
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		npcOverlayService.unregisterHighlighter(npcHighlighter);
		resetState();
		log.info("Slayer Boosting plugin stopped");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			readVarbits();
			evaluateRules();
		}
		else if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			resetState();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!"slayerboosting".equals(event.getGroup()))
		{
			return;
		}

		evaluateRules();
		npcOverlayService.rebuild();
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		int newStreak = client.getVarbitValue(SLAYER_TASK_STREAK_VARBIT);
		int newPoints = client.getVarbitValue(SLAYER_POINTS_VARBIT);
		int newTaskCount = client.getVarpValue(SLAYER_TASK_SIZE_VARP);

		boolean changed = false;

		if (newStreak != streak)
		{
			log.debug("Slayer streak changed: {} -> {}", streak, newStreak);
			streak = newStreak;
			changed = true;
		}

		if (newPoints != points)
		{
			log.debug("Slayer points changed: {} -> {}", points, newPoints);
			points = newPoints;
		}

		if (newTaskCount != taskCount)
		{
			log.debug("Slayer task count changed: {} -> {}", taskCount, newTaskCount);
			taskCount = newTaskCount;
			changed = true;
		}

		if (changed)
		{
			evaluateRules();
			npcOverlayService.rebuild();
		}
	}

	/**
	 * NPC highlighter function for NpcOverlayService.
	 * When a milestone is active: highlights the milestone master in green and wrong masters in red.
	 * When no milestone is active: highlights the default master in green and wrong masters in red.
	 */
	private HighlightedNpc highlightNpc(NPC npc)
	{
		if (npc.getName() == null)
		{
			return null;
		}

		SlayerMaster master = SlayerMaster.fromNpcName(npc.getName());
		if (master == null)
		{
			return null;
		}

		// Determine who the player should visit right now
		SlayerMaster targetMaster = milestoneActive ? milestoneMaster : config.defaultMaster();

		if (master == targetMaster && config.highlightCorrectMaster())
		{
			Color color = config.correctMasterColor();
			return HighlightedNpc.builder()
				.npc(npc)
				.highlightColor(color)
				.fillColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30))
				.hull(true)
				.name(true)
				.nameOnMinimap(true)
				.borderWidth(2.0f)
				.render(n -> true)
				.build();
		}
		else if (master != targetMaster && config.highlightWrongMasters())
		{
			Color color = config.wrongMasterColor();
			return HighlightedNpc.builder()
				.npc(npc)
				.highlightColor(color)
				.fillColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30))
				.hull(true)
				.name(true)
				.nameOnMinimap(true)
				.borderWidth(2.0f)
				.render(n -> true)
				.build();
		}

		return null;
	}

	/**
	 * Read the initial varbit values from the client.
	 */
	private void readVarbits()
	{
		streak = client.getVarbitValue(SLAYER_TASK_STREAK_VARBIT);
		points = client.getVarbitValue(SLAYER_POINTS_VARBIT);
		taskCount = client.getVarpValue(SLAYER_TASK_SIZE_VARP);
		log.debug("Read varbits: streak={}, points={}, taskCount={}", streak, points, taskCount);
	}

	/**
	 * Evaluate all enabled rules against the current streak to determine
	 * if the next task is a milestone.
	 *
	 * <p>The highest-interval matching rule wins when multiple rules match.</p>
	 */
	private void evaluateRules()
	{
		int nextTask = streak + 1;

		// Collect all enabled rules
		List<BoostingRule> rules = getEnabledRules();

		// Sort by interval descending so the highest-priority rule wins
		rules.sort(Comparator.comparingInt(BoostingRule::getInterval).reversed());

		for (BoostingRule rule : rules)
		{
			if (rule.getInterval() > 0 && nextTask % rule.getInterval() == 0)
			{
				milestoneActive = true;
				milestoneMaster = rule.getMaster();
				milestoneTaskNumber = nextTask;
				milestonePoints = rule.getMaster().getPointsForTask(nextTask,
					config.eliteWesternDiary(), config.eliteKourendDiary());

				nextMaster = milestoneMaster;
				nextTaskPoints = milestonePoints;

				log.debug("Milestone active! Next task #{} -> use {} ({} pts)",
					nextTask, milestoneMaster.getDisplayName(), milestonePoints);
				return;
			}
		}

		// No rule matches — use default master
		milestoneActive = false;
		milestoneMaster = null;
		milestoneTaskNumber = 0;
		milestonePoints = 0;

		nextMaster = config.defaultMaster();
		nextTaskPoints = nextMaster.getPointsForTask(nextTask,
			config.eliteWesternDiary(), config.eliteKourendDiary());
	}

	/**
	 * Collect enabled rules from the config into a list.
	 */
	private List<BoostingRule> getEnabledRules()
	{
		List<BoostingRule> rules = new ArrayList<>();

		if (config.rule1Enabled())
		{
			rules.add(new BoostingRule(config.rule1Interval(), config.rule1Master()));
		}
		if (config.rule2Enabled())
		{
			rules.add(new BoostingRule(config.rule2Interval(), config.rule2Master()));
		}
		if (config.rule3Enabled())
		{
			rules.add(new BoostingRule(config.rule3Interval(), config.rule3Master()));
		}
		if (config.rule4Enabled())
		{
			rules.add(new BoostingRule(config.rule4Interval(), config.rule4Master()));
		}
		if (config.rule5Enabled())
		{
			rules.add(new BoostingRule(config.rule5Interval(), config.rule5Master()));
		}

		return rules;
	}

	private void resetState()
	{
		streak = 0;
		points = 0;
		taskCount = 0;
		milestoneActive = false;
		milestoneMaster = null;
		milestoneTaskNumber = 0;
		milestonePoints = 0;
		nextMaster = null;
		nextTaskPoints = 0;
	}

	/**
	 * Simple data holder for an enabled boosting rule.
	 */
	@Getter
	private static class BoostingRule
	{
		private final int interval;
		private final SlayerMaster master;

		BoostingRule(int interval, SlayerMaster master)
		{
			this.interval = interval;
			this.master = master;
		}
	}
}

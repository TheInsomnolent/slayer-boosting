package com.slayerboosting;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.Color;

@ConfigGroup("slayerboosting")
public interface SlayerBoostingConfig extends Config
{
	// ── General ──────────────────────────────────────────

	@ConfigSection(
		name = "General",
		description = "General plugin settings",
		position = 0
	)
	String generalSection = "general";

	@ConfigItem(
		keyName = "defaultMaster",
		name = "Default Slayer Master",
		description = "The slayer master you normally use for non-milestone tasks",
		position = 0,
		section = generalSection
	)
	default SlayerMaster defaultMaster()
	{
		return SlayerMaster.TURAEL;
	}

	@ConfigItem(
		keyName = "highlightCorrectMaster",
		name = "Highlight Correct Master",
		description = "Draw a green highlight on the correct slayer master NPC",
		position = 1,
		section = generalSection
	)
	default boolean highlightCorrectMaster()
	{
		return true;
	}

	@ConfigItem(
		keyName = "highlightWrongMasters",
		name = "Highlight Wrong Masters",
		description = "Draw a red highlight on wrong slayer master NPCs",
		position = 2,
		section = generalSection
	)
	default boolean highlightWrongMasters()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "correctMasterColor",
		name = "Correct Master Color",
		description = "Color to highlight the correct slayer master",
		position = 3,
		section = generalSection
	)
	default Color correctMasterColor()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		keyName = "wrongMasterColor",
		name = "Wrong Master Color",
		description = "Color to highlight wrong slayer masters",
		position = 4,
		section = generalSection
	)
	default Color wrongMasterColor()
	{
		return Color.RED;
	}

	@ConfigItem(
		keyName = "eliteWesternDiary",
		name = "Elite Western Provinces",
		description = "Enable if you have completed the Elite Western Provinces diary (Nieve/Steve +25% points)",
		position = 5,
		section = generalSection
	)
	default boolean eliteWesternDiary()
	{
		return false;
	}

	@ConfigItem(
		keyName = "eliteKourendDiary",
		name = "Elite Kourend & Kebos",
		description = "Enable if you have completed the Elite Kourend & Kebos diary (Konar boosted points)",
		position = 6,
		section = generalSection
	)
	default boolean eliteKourendDiary()
	{
		return false;
	}

	// ── Rule 1 ───────────────────────────────────────────

	@ConfigSection(
		name = "Rule 1",
		description = "Boosting rule 1 (highest-interval rule wins if multiple match)",
		position = 10
	)
	String rule1Section = "rule1";

	@ConfigItem(
		keyName = "rule1Enabled",
		name = "Enabled",
		description = "Enable this boosting rule",
		position = 0,
		section = rule1Section
	)
	default boolean rule1Enabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "rule1Interval",
		name = "Every N tasks",
		description = "Use the specified master every N tasks (e.g. 10 = every 10th task)",
		position = 1,
		section = rule1Section
	)
	default int rule1Interval()
	{
		return 10;
	}

	@ConfigItem(
		keyName = "rule1Master",
		name = "Slayer Master",
		description = "Which slayer master to use for this milestone",
		position = 2,
		section = rule1Section
	)
	default SlayerMaster rule1Master()
	{
		return SlayerMaster.DURADEL;
	}

	// ── Rule 2 ───────────────────────────────────────────

	@ConfigSection(
		name = "Rule 2",
		description = "Boosting rule 2",
		position = 20,
		closedByDefault = true
	)
	String rule2Section = "rule2";

	@ConfigItem(
		keyName = "rule2Enabled",
		name = "Enabled",
		description = "Enable this boosting rule",
		position = 0,
		section = rule2Section
	)
	default boolean rule2Enabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "rule2Interval",
		name = "Every N tasks",
		description = "Use the specified master every N tasks",
		position = 1,
		section = rule2Section
	)
	default int rule2Interval()
	{
		return 50;
	}

	@ConfigItem(
		keyName = "rule2Master",
		name = "Slayer Master",
		description = "Which slayer master to use for this milestone",
		position = 2,
		section = rule2Section
	)
	default SlayerMaster rule2Master()
	{
		return SlayerMaster.KONAR;
	}

	// ── Rule 3 ───────────────────────────────────────────

	@ConfigSection(
		name = "Rule 3",
		description = "Boosting rule 3",
		position = 30,
		closedByDefault = true
	)
	String rule3Section = "rule3";

	@ConfigItem(
		keyName = "rule3Enabled",
		name = "Enabled",
		description = "Enable this boosting rule",
		position = 0,
		section = rule3Section
	)
	default boolean rule3Enabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "rule3Interval",
		name = "Every N tasks",
		description = "Use the specified master every N tasks",
		position = 1,
		section = rule3Section
	)
	default int rule3Interval()
	{
		return 100;
	}

	@ConfigItem(
		keyName = "rule3Master",
		name = "Slayer Master",
		description = "Which slayer master to use for this milestone",
		position = 2,
		section = rule3Section
	)
	default SlayerMaster rule3Master()
	{
		return SlayerMaster.KONAR;
	}

	// ── Rule 4 ───────────────────────────────────────────

	@ConfigSection(
		name = "Rule 4",
		description = "Boosting rule 4",
		position = 40,
		closedByDefault = true
	)
	String rule4Section = "rule4";

	@ConfigItem(
		keyName = "rule4Enabled",
		name = "Enabled",
		description = "Enable this boosting rule",
		position = 0,
		section = rule4Section
	)
	default boolean rule4Enabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "rule4Interval",
		name = "Every N tasks",
		description = "Use the specified master every N tasks",
		position = 1,
		section = rule4Section
	)
	default int rule4Interval()
	{
		return 250;
	}

	@ConfigItem(
		keyName = "rule4Master",
		name = "Slayer Master",
		description = "Which slayer master to use for this milestone",
		position = 2,
		section = rule4Section
	)
	default SlayerMaster rule4Master()
	{
		return SlayerMaster.KONAR;
	}

	// ── Rule 5 ───────────────────────────────────────────

	@ConfigSection(
		name = "Rule 5",
		description = "Boosting rule 5",
		position = 50,
		closedByDefault = true
	)
	String rule5Section = "rule5";

	@ConfigItem(
		keyName = "rule5Enabled",
		name = "Enabled",
		description = "Enable this boosting rule",
		position = 0,
		section = rule5Section
	)
	default boolean rule5Enabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "rule5Interval",
		name = "Every N tasks",
		description = "Use the specified master every N tasks",
		position = 1,
		section = rule5Section
	)
	default int rule5Interval()
	{
		return 1000;
	}

	@ConfigItem(
		keyName = "rule5Master",
		name = "Slayer Master",
		description = "Which slayer master to use for this milestone",
		position = 2,
		section = rule5Section
	)
	default SlayerMaster rule5Master()
	{
		return SlayerMaster.KONAR;
	}
}

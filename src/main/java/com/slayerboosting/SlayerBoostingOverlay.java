/*
 * Copyright (c) 2026, Matthew Griffiths
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.slayerboosting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

/**
 * Permanent top-left overlay showing slayer boosting status:
 * next task number, next master, streak, current points, expected points.
 */
public class SlayerBoostingOverlay extends OverlayPanel
{
	private static final Color TITLE_COLOR = new Color(255, 152, 31); // RuneLite orange
	private static final Color MILESTONE_COLOR = Color.YELLOW;
	private static final Color CORRECT_MASTER_COLOR = new Color(0, 255, 128);
	private static final Color NORMAL_COLOR = Color.WHITE;

	private final SlayerBoostingPlugin plugin;

	@Inject
	public SlayerBoostingOverlay(SlayerBoostingPlugin plugin)
	{
		super(plugin);
		this.plugin = plugin;
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.MED);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		int streak = plugin.getStreak();
		int nextTask = streak + 1;
		boolean isMilestone = plugin.isMilestoneActive();
		SlayerMaster nextMaster = plugin.getNextMaster();

		// Don't render if we have no data yet (not logged in)
		if (nextMaster == null)
		{
			return null;
		}

		// Title
		panelComponent.getChildren().add(
			TitleComponent.builder()
				.text("Slayer Boosting")
				.color(TITLE_COLOR)
				.build()
		);

		// Next task number (flagged if milestone)
		String nextTaskText;
		Color nextTaskColor;
		if (isMilestone)
		{
			String label = SlayerMaster.getMilestoneLabel(nextTask);
			nextTaskText = "#" + nextTask + " (" + label + " bonus!)";
			nextTaskColor = MILESTONE_COLOR;
		}
		else
		{
			nextTaskText = "#" + nextTask;
			nextTaskColor = NORMAL_COLOR;
		}

		panelComponent.getChildren().add(
			LineComponent.builder()
				.left("Next task:")
				.right(nextTaskText)
				.rightColor(nextTaskColor)
				.build()
		);

		// Next master
		panelComponent.getChildren().add(
			LineComponent.builder()
				.left("Next master:")
				.right(nextMaster.getDisplayName())
				.rightColor(isMilestone ? CORRECT_MASTER_COLOR : NORMAL_COLOR)
				.build()
		);

		// Current streak
		panelComponent.getChildren().add(
			LineComponent.builder()
				.left("Streak:")
				.right(String.valueOf(streak))
				.rightColor(NORMAL_COLOR)
				.build()
		);

		// Current slayer points
		panelComponent.getChildren().add(
			LineComponent.builder()
				.left("Points:")
				.right(String.valueOf(plugin.getPoints()))
				.rightColor(NORMAL_COLOR)
				.build()
		);

		// Expected points from next task
		int nextPts = plugin.getNextTaskPoints();
		panelComponent.getChildren().add(
			LineComponent.builder()
				.left("Next task pts:")
				.right(nextPts > 0 ? String.valueOf(nextPts) : "N/A")
				.rightColor(isMilestone ? MILESTONE_COLOR : NORMAL_COLOR)
				.build()
		);

		return super.render(graphics);
	}
}

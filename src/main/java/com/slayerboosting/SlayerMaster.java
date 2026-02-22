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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Slayer masters in OSRS (excluding Krystilia) with their point values
 * per task streak milestone.
 */
@Getter
@AllArgsConstructor
public enum SlayerMaster
{
	TURAEL("Turael / Aya", new String[]{"Turael", "Aya", "Spria"}, 0, 0, 0, 0, 0, 0),
	MAZCHNA("Mazchna", new String[]{"Mazchna", "Achtryn"}, 6, 30, 90, 150, 210, 300),
	VANNAKA("Vannaka", new String[]{"Vannaka"}, 8, 40, 120, 200, 280, 400),
	CHAELDAR("Chaeldar", new String[]{"Chaeldar"}, 10, 50, 150, 250, 350, 500),
	NIEVE("Nieve / Steve", new String[]{"Nieve", "Steve"}, 12, 60, 180, 300, 420, 600),
	DURADEL("Duradel / Kuradel", new String[]{"Duradel", "Kuradal"}, 15, 75, 225, 375, 525, 750),
	KONAR("Konar quo Maten", new String[]{"Konar quo Maten"}, 18, 90, 270, 450, 630, 900);

	private final String displayName;
	private final String[] npcNames;
	private final int basePoints;
	private final int points10th;
	private final int points50th;
	private final int points100th;
	private final int points250th;
	private final int points1000th;

	/**
	 * Returns the number of points this master awards for a given task streak number.
	 * Checks milestones from highest to lowest.
	 *
	 * @param taskNumber the task number (e.g. 100 for the 100th task)
	 * @return points awarded
	 */
	public int getPointsForTask(int taskNumber)
	{
		if (taskNumber > 0 && taskNumber % 1000 == 0)
		{
			return points1000th;
		}
		if (taskNumber > 0 && taskNumber % 250 == 0)
		{
			return points250th;
		}
		if (taskNumber > 0 && taskNumber % 100 == 0)
		{
			return points100th;
		}
		if (taskNumber > 0 && taskNumber % 50 == 0)
		{
			return points50th;
		}
		if (taskNumber > 0 && taskNumber % 10 == 0)
		{
			return points10th;
		}
		return basePoints;
	}

	/**
	 * Returns the number of points this master awards for a given task streak number,
	 * accounting for achievement diary bonuses.
	 *
	 * @param taskNumber the task number (e.g. 100 for the 100th task)
	 * @param eliteWesternDiary whether Elite Western Provinces diary is complete (Nieve/Steve +25%)
	 * @param eliteKourendDiary whether Elite Kourend &amp; Kebos diary is complete (Konar boosted)
	 * @return points awarded
	 */
	public int getPointsForTask(int taskNumber, boolean eliteWesternDiary, boolean eliteKourendDiary)
	{
		int pts = getPointsForTask(taskNumber);

		if (this == NIEVE && eliteWesternDiary)
		{
			// Elite Western Provinces: +25% points (12->15, 60->75, etc.)
			pts = (int) Math.round(pts * 1.25);
		}
		else if (this == KONAR && eliteKourendDiary)
		{
			// Elite Kourend & Kebos: boosted values
			pts = getKourendDiaryPoints(taskNumber);
		}

		return pts;
	}

	/**
	 * Returns Konar's boosted point values with Elite Kourend &amp; Kebos diary.
	 */
	private int getKourendDiaryPoints(int taskNumber)
	{
		if (taskNumber > 0 && taskNumber % 1000 == 0)
		{
			return 1000;
		}
		if (taskNumber > 0 && taskNumber % 250 == 0)
		{
			return 700;
		}
		if (taskNumber > 0 && taskNumber % 100 == 0)
		{
			return 500;
		}
		if (taskNumber > 0 && taskNumber % 50 == 0)
		{
			return 300;
		}
		if (taskNumber > 0 && taskNumber % 10 == 0)
		{
			return 100;
		}
		return 20;
	}

	/**
	 * Returns the milestone label for a given task number (e.g. "100th", "10th", or "").
	 */
	public static String getMilestoneLabel(int taskNumber)
	{
		if (taskNumber > 0 && taskNumber % 1000 == 0)
		{
			return "1,000th";
		}
		if (taskNumber > 0 && taskNumber % 250 == 0)
		{
			return "250th";
		}
		if (taskNumber > 0 && taskNumber % 100 == 0)
		{
			return "100th";
		}
		if (taskNumber > 0 && taskNumber % 50 == 0)
		{
			return "50th";
		}
		if (taskNumber > 0 && taskNumber % 10 == 0)
		{
			return "10th";
		}
		return "";
	}

	/**
	 * Look up a SlayerMaster by NPC name (case-insensitive).
	 *
	 * @param npcName the in-game NPC name
	 * @return the matching SlayerMaster, or null if not found
	 */
	public static SlayerMaster fromNpcName(String npcName)
	{
		if (npcName == null)
		{
			return null;
		}
		for (SlayerMaster master : values())
		{
			for (String name : master.npcNames)
			{
				if (name.equalsIgnoreCase(npcName))
				{
					return master;
				}
			}
		}
		return null;
	}

	/**
	 * Check whether a given NPC name belongs to any slayer master.
	 */
	public static boolean isSlayerMaster(String npcName)
	{
		return fromNpcName(npcName) != null;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}

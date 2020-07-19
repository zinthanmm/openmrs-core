/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.liquibase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.openmrs.util.H2DatabaseIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeLogDetectiveDatabaseIT extends H2DatabaseIT {
	
	private static final Logger log = LoggerFactory.getLogger(ChangeLogDetectiveDatabaseIT.class);
	
	private static final String VERSION_2_1_X = "2.1.x";
	
	
	@Test
	public void shouldGetInitialLiquibaseSnapshotVersion() throws Exception {
		ChangeLogDetective changeLogDetective = new ChangeLogDetective();
		ChangeLogVersionFinder changeLogVersionFinder = new ChangeLogVersionFinder();
		Map<String, List<String>> changeSetCombinations = changeLogVersionFinder.getChangeLogCombinations();
		updateDatabase(changeSetCombinations.get(VERSION_2_1_X));
		
		/*
		 * The database was initialised with snapshot version 2.1.x so this version is the expected outcome.
		 */
		String expected = VERSION_2_1_X;
		
		String actual = changeLogDetective.getInitialLiquibaseSnapshotVersion("some context", this);
		assertEquals(expected, actual);
	}
	
	@Test
	public void shouldRecogniseThatAllSnapshotsContainUnrunChangeSets() throws Exception {
		ChangeLogDetective changeLogDetective = new ChangeLogDetective();
		try {
			changeLogDetective.getInitialLiquibaseSnapshotVersion("some context", this);
			fail("IllegalStateException was expected but not raised");
		}
		catch (IllegalStateException ise) {
			assertTrue(true, "IllegalStateException was raised as expected");
		}
	}
	
	@Test
	public void shouldGetUnrunLiquibaseUpdateFileNames() throws Exception {
		ChangeLogDetective changeLogDetective = new ChangeLogDetective();
		ChangeLogVersionFinder changeLogVersionFinder = new ChangeLogVersionFinder();
		Map<String, List<String>> snapshotCombinations = changeLogVersionFinder.getSnapshotCombinations();
		updateDatabase(snapshotCombinations.get(VERSION_2_1_X));
		
		/*
		 * The database was initialised with snapshot 2.1.x only so getting all un-run update files is expected
		 * to return all update versions greater than 2.1.x
		 */
		List<String> expected = changeLogVersionFinder
		        .getUpdateFileNames(changeLogVersionFinder.getUpdateVersionsGreaterThan(VERSION_2_1_X));
		
		List<String> actual = changeLogDetective.getUnrunLiquibaseUpdateFileNames(VERSION_2_1_X, "some context", this);
		
		assertEquals(expected, actual);
	}
}

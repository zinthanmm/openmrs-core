/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.web.filter.update;

import java.util.List;
import org.openmrs.liquibase.LiquibaseProvider;
import org.openmrs.util.DatabaseUpdater;
import org.openmrs.util.DatabaseUpdater.OpenMRSChangeSet;
import org.openmrs.util.DatabaseUpdaterLiquibaseProvider;
import org.openmrs.web.filter.StartupFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link UpdateFilter} uses this model object to hold all properties that are edited by the
 * user in the wizard. All attributes on this model object are added to all templates rendered by
 * the {@link StartupFilter}.
 */
public class UpdateFilterModel {
	
	private static final Logger log = LoggerFactory.getLogger(UpdateFilterModel.class);
	
	public List<OpenMRSChangeSet> changes = null;
	
	public Boolean updateRequired = false;
	
	private LiquibaseProvider liquibaseProvider;
	
	/**
	 * Default constructor that sets up some of the properties
	 */
	public UpdateFilterModel() {
		this(new DatabaseUpdaterLiquibaseProvider());
		log.debug("executing default constructor...");
	}
	
	/**
	 * Constructor that allows to inject a Liquibase provider.
	 * 
	 * @param liquibaseProvider a Liquibase provider
	 */
	public UpdateFilterModel(LiquibaseProvider liquibaseProvider) {
		log.debug("executing non-default constructor...");
		this.liquibaseProvider = liquibaseProvider;
		
		updateChanges();
		
		try {
			if (changes != null && !changes.isEmpty()) {
				updateRequired = true;
			} else {
				updateRequired = DatabaseUpdater.updatesRequired();
			}
		}
		catch (Exception e) {
			// do nothing
		}
	}
	
	/**
	 * Convenience method that reads from liquibase again to get the most recent list of changesets that
	 * still need to be run.
	 */
	public void updateChanges() {
		log.debug("executing updateChanges()...");
		try {
			changes = DatabaseUpdater.getUnrunDatabaseChanges(liquibaseProvider);
			
			// not sure why this is necessary...
			if (changes == null && DatabaseUpdater.isLocked()) {
				changes = DatabaseUpdater.getUnrunDatabaseChanges(liquibaseProvider);
			}
		}
		catch (Exception e) {
			log.error("Unable to get the database changes", e);
		}
	}
}

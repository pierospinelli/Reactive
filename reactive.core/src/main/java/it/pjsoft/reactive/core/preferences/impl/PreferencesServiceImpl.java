/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package it.pjsoft.reactive.core.preferences.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;

import it.pjsoft.reactive.core.internal.Activator;
import it.pjsoft.reactive.core.preferences.internal.AbstractPreferences;




/**
 * This is an implementation of the OSGI Preferences Service, Version 1.1.
 */
@Component(immediate=true, name="PreferencesService", service=PreferencesService.class)
public class PreferencesServiceImpl implements PreferencesService {


    public PreferencesServiceImpl() {
    }

    /**
     * @see org.osgi.service.prefs.PreferencesService#getSystemPreferences()
     */
    public synchronized Preferences getSystemPreferences() {
        return AbstractPreferences.systemRoot();
    }

    /**
     * @see org.osgi.service.prefs.PreferencesService#getUserPreferences(java.lang.String)
     */
    public synchronized Preferences getUserPreferences(String name) {
        return null;
    }

    /**
     * @see org.osgi.service.prefs.PreferencesService#getUsers()
     */
    public synchronized String[] getUsers() {
    	return new String[0];
    }


}

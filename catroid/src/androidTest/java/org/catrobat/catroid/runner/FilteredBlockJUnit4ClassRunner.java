/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.runner;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.List;

public class FilteredBlockJUnit4ClassRunner extends BlockJUnit4ClassRunner {

	List<String> methods;

	public FilteredBlockJUnit4ClassRunner(Class<?> testClass, List<String> methods) throws InitializationError {
		super(testClass);
		this.methods = methods;
	}

	@Override
	protected List<FrameworkMethod> getChildren() {
		List<FrameworkMethod> allMethods = super.getChildren();
		List<FrameworkMethod> filteredMethods = new ArrayList<>();

		for (FrameworkMethod method : allMethods) {
			if (methods.contains(method.getName())) {
				filteredMethods.add(method);
			}
		}

		return filteredMethods;
	}
}

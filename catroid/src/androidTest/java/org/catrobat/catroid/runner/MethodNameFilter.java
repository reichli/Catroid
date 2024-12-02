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

import android.util.Log;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

import java.util.List;

public class MethodNameFilter extends Filter {
	private final List<List<String>> methodNames;

	public MethodNameFilter(List<List<String>> methodNames)	{
		super();
		this.methodNames = methodNames;
	}

	@Override
	public boolean shouldRun(Description description) {
		String methodName = description.getMethodName();
		int paramIndex = methodName.indexOf("[");
		if (paramIndex != -1) {
			methodName = methodName.replace(methodName.substring(paramIndex), "");
		}
		Log.i("FLAKY-TESTS", "filter for test: " + methodName);

		for (List<String> methodToRun : this.methodNames)	{
			if (methodToRun.get(0).equalsIgnoreCase(methodName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String describe() {
		return "custom method name filter";
	}
}

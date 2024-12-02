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

package org.catrobat.catroid.testsuites;

import org.catrobat.catroid.runner.FailedTests;
import org.catrobat.catroid.runner.PackagePath;
import org.catrobat.catroid.runner.FilteredTestRunner;
import org.junit.runner.RunWith;

@RunWith(FilteredTestRunner.class)
@PackagePath("org.catrobat.catroid")
@FailedTests(
		// instrumented unit tests
		"PenDownActionTest.testSaveOnePositionChange\n"
		// pull request suite
		+ "FormulaEditorFragmentTest.testUndo\n"
		// RTL tests
		+ "RTLMainMenuTest.testSetLanguageToGerman\n"
		// CLT
		+ "CatrobatTestRunner.run[catrobatTests/formula - testNumberOfLooks.catrobat]\n"
		// quarantined tests
		+ "SettingsFragmentTest.noMultipleSelectAccessibilityProfilesTest\n"
		// parameterized
		+ "UndoTest.testUndoSpinnerActionVisible[SingleScript]\n"
		+ "UndoTest.testUndoSpinnerActionVisible[CompositeBrick]\n"
		+ "UndoTest.checkScriptAfterUndo[CompositeBrick]")
public class RerunFailedJenkinsTestsSuite {
}

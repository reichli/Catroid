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
@FailedTests("DeleteImportedSpriteTest.testDeleteOriginalAndImportedSprites\n"
		+ "DeleteImportedSpriteTest.testOriginalLooksAndSoundsExistAfterDeleteImport\n"
		+ "ImportObjectIntoProjectTest.abortImportWithConflictsTestLocalVar\n"
		+ "ImportObjectIntoProjectTest.rejectProjectImportDialogTest\n"
		+ "FormulaEditorFragmentTest.testUndo\n"
		+ "StagePausedTest.testIgnoreTouchEventsWhenStagePaused\n"
		+ "SettingsFragmentTest.basicSettingsTest\n"
		+ "AddNewActorOrLookDialogTest.addActorOrObjectDialogTest\n"
		+ "ImportLocalSpriteTest.abortImportWithConflicts\n"
		+ "ImportLocalSpriteTest.importObjectAndMergeGlobals\n"
		+ "ReplaceExistingProjectDialogTest.testProjectNameEmpty\n"
		+ "CreateProjectTest.testCreateNewCastProject\n"
		+ "CreateProjectTest.testCreateProjectInMainMenu\n"
		+ "CreateProjectTest.testCreateProjectInProjectList\n"
		+ "FormulaEditorFragmentActivityRecreateRegressionTest.testActivityRecreateDataFragment\n"
		+ "PhiroColorBrickFormulaTest.testPhiroLightRGBShowFormulaEditor[minusTest]\n"
		+ "PhiroColorBrickFormulaTest.testPhiroLightRGBShowFormulaEditor[plusTest]\n"
		+ "PhiroColorBrickNumberTest.testPhiroLightRGBShowDialog[allZeroParametersTest]\n"
		+ "PhiroColorBrickNumberTest.testPhiroLightRGBShowDialog[negativeParametersTest]\n"
		+ "PhiroColorBrickNumberTest.testPhiroLightRGBShowDialog[positiveParametersTest]\n"
		+ "SetPenColorBrickFormulaTest.testPhiroLightRGBShowFormulaEditor[minusTest]\n"
		+ "SetPenColorBrickFormulaTest.testPhiroLightRGBShowFormulaEditor[plusTest]\n"
		+ "SetPenColorBrickNumberTest.testPenColorShowDialog[allZeroParametersTest]\n"
		+ "SetPenColorBrickNumberTest.testPenColorShowDialog[negativeParametersTest]\n"
		+ "SetPenColorBrickNumberTest.testPenColorShowDialog[positiveParametersTest]\n"
		+ "CameraResourceTest.cameraOnTest\n"
		+ "SceneTransitionWithVibrationBrickStageTest.testVibrationContinueOnSceneTransition\n"
		+ "StageResourceFailedTest.testResourceFailedDialog\n"
		+ "ImportLocalSpriteTest.importActorOrObjectTest\n"
		+ "TouchesEdgeTest.testNoCollisionInCenter")
public class RerunFailedJenkinsTestsSuite {
}

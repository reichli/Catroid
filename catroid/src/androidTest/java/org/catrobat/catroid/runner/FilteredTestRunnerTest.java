/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import org.catrobat.catroid.test.catblocks.ScriptSplitUserDefinedBrickTest;
import org.catrobat.catroid.test.content.actions.StopSoundActionTest;
import org.catrobat.catroid.uiespresso.content.brick.stage.SceneTransitionWithSoundBrickStageTest;
import org.catrobat.catroid.uiespresso.content.brick.stage.SceneTransitionWithVibrationBrickStageTest;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.util.List;
import java.util.stream.Collectors;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FilteredTestRunnerTest {

	@PackagePath("org.catrobat.catroid")
	@FailedTests({"ScriptSplitUserDefinedBrickTest.testSplitUserDefinedBrick"})
	class InstrumentedUnitTestRunner {}

	@PackagePath("org.catrobat.catroid")
	@FailedTests({
			"SceneTransitionWithSoundBrickStageTest.testContinueSoundDoesNotStartFromBeginning"})
	class PullRequestTestSuiteRunner {}

	@PackagePath("org.catrobat.catroid")
	@FailedTests({"testEmbroiderySaved.catrobat"})
	class CatrobatLanguageTestRunner {}

	@PackagePath("org.catrobat.catroid")
	@FailedTests({
			"SceneTransitionWithVibrationBrickStageTest.testVibrationContinueOnSceneTransition",
			"StopSoundActionTest.testStopSimultaneousPlayingSounds"})
	class MultipleTestClassesRunner {}

	@PackagePath("org.catrobat.catroid")
	@FailedTests({
			"PhiroColorBrickFormulaTest.testPhiroLightRGBShowFormulaEditor[plusTest]",
			"PhiroColorBrickFormulaTest.testPhiroLightRGBShowFormulaEditor[minusTest]",
			"BricksHelpUrlTest.testBrickHelpUrl[org.catrobat.catroid.content.bricks.StartPlotBrick]"})
	class ParameterizedTestRunner {}

	/*
	* Instrumented Unit Test (x)
	* Pull Request Suite (x)
	* CLT (pending)
	* Multiple Test Methods (x)
	* Parameterized Test
	* Correctly consider Jenkins Output
	 */

	@Test
	public void testInstrumentedUnitTest() throws InitializationError {
		List<ParentRunner> runners =
				new FilteredTestRunner(InstrumentedUnitTestRunner.class).getChildren();

		assertThat(runners.size(), is(1));

		assertThat(runners.get(0), is(instanceOf(BlockJUnit4ClassRunner.class)));
		assertEquals(
				ScriptSplitUserDefinedBrickTest.class,
				runners.get(0).getDescription().getTestClass());
		assertThat(
				getTestMethods(runners.get(0)),
				containsInAnyOrder("testSplitUserDefinedBrick"));
	}

	@Test
	public void testPullRequestSuiteTest() throws InitializationError {
		List<ParentRunner> runners =
				new FilteredTestRunner(PullRequestTestSuiteRunner.class).getChildren();

		assertThat(runners.size(), is(1));

		assertThat(runners.get(0), is(instanceOf(BlockJUnit4ClassRunner.class)));
		assertEquals(
				SceneTransitionWithSoundBrickStageTest.class,
				runners.get(0).getDescription().getTestClass());
		assertThat(
				getTestMethods(runners.get(0)),
				containsInAnyOrder("testContinueSoundDoesNotStartFromBeginning"));
	}

	@Test
	public void testCatrobatLanguageTest() throws InitializationError {
		List<ParentRunner> runners =
				new FilteredTestRunner(CatrobatLanguageTestRunner.class).getChildren();

		assertThat(runners.size(), is(1));
	}

	@Test
	public void testFilterMultipleTestsClasses() throws InitializationError {
		List<ParentRunner> runners =
				new FilteredTestRunner(MultipleTestClassesRunner.class).getChildren();

		assertThat(runners.size(), is(2));

		assertThat(runners.get(0), is(instanceOf(BlockJUnit4ClassRunner.class)));
		assertEquals(StopSoundActionTest.class, runners.get(0).getDescription().getTestClass());
		assertThat(
				getTestMethods(runners.get(0)),
				containsInAnyOrder("testStopSimultaneousPlayingSounds"));

		assertThat(runners.get(1), is(instanceOf(BlockJUnit4ClassRunner.class)));
		assertEquals(
				SceneTransitionWithVibrationBrickStageTest.class,
				runners.get(1).getDescription().getTestClass());
		assertThat(
				getTestMethods(runners.get(1)),
				containsInAnyOrder("testVibrationContinueOnSceneTransition"));
	}

	@Test
	public void testFilterMultipleParameterizedTests() throws InitializationError {
		List<ParentRunner> runners =
				new FilteredTestRunner(ParameterizedTestRunner.class).getChildren();

	}

	public List<String> getTestMethods(ParentRunner runner) {
		return runner.getDescription().getChildren().stream()
				.map(Description::getMethodName)
				.collect(Collectors.toList());
	}
}

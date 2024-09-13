/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.test.platform.app.InstrumentationRegistry;
import dalvik.system.DexFile;

public class FilteredTestRunner extends ParentRunner<ParentRunner> {

	private static final String TAG = FilteredTestRunner.class.getSimpleName();

	private final Map<String, List<Test>> tests;
	private final List<ParentRunner> children;
	private PackagePath pathAnnotation;
	private FailedTests failedTestsAnnotation;

	public FilteredTestRunner(Class<?> suite) throws InitializationError {
		super(suite);
		setAnnotations(suite);
		this.tests = getTest();
		this.children = getChildRunners();
	}

	private void setAnnotations(Class<?> suite) throws InitializationError {
		pathAnnotation = suite.getAnnotation(PackagePath.class);
		if (pathAnnotation == null) {
			throw new InitializationError(
					String.format("class '%s' must have a PackagePath annotation",
							suite.getName()));
		}

		failedTestsAnnotation = suite.getAnnotation(FailedTests.class);
		if (failedTestsAnnotation == null) {
			throw new InitializationError(
					String.format("class '%s' must have a FailedTests annotation",
							suite.getName()));
		}
	}

	private Map<String, List<Test>> getTest() {
		return new JenkinsResultParser()
				.parseTests(List.of(failedTestsAnnotation.value()))
				.stream()
				.collect(Collectors.groupingBy(Test::getClassName));
	}

	private List<ParentRunner> getChildRunners() throws InitializationError {
		List<ParentRunner> runners = new ArrayList<>();

		try {
			// maybe change this to also rerun unit tests? here we are getting the package
			// where the instrumented test is run.
			String packageCodePath =
					InstrumentationRegistry.getInstrumentation().getContext().getPackageCodePath();
			DexFile dexFile = new DexFile(packageCodePath);

			Enumeration<String> iter = dexFile.entries();
			while (iter.hasMoreElements()) {
				String fullyQualifiedClassName = iter.nextElement();
				String[] parts = fullyQualifiedClassName.split("\\.");
				String className = parts[parts.length - 1];
				List<Test> testsToRerun = tests.get(className);

				if (!fullyQualifiedClassName.contains(pathAnnotation.value()) ||
					testsToRerun == null ||
					(!className.endsWith("Test") && !className.contentEquals("CatrobatTestRunner"))) {
					continue;
				}

				Class<?> testClass = Class.forName(fullyQualifiedClassName);
				RunWith runWithAnnotation = testClass.getAnnotation(RunWith.class);

				if (runWithAnnotation != null && runWithAnnotation.value() == Parameterized.class) {
					runners.add(new FilteredParameterizedRunner(testClass, testsToRerun));
				} else {
					BlockJUnit4ClassRunner classRunner = new BlockJUnit4ClassRunner(testClass);
					classRunner.filter(new MethodNameFilter(testsToRerun));
					runners.add(classRunner);
				}
			}
		} catch (Throwable t) {
			Log.e(TAG, t.getMessage(), t);
			throw new InitializationError("Exception during loading Test classes from Dex");
		}

		return runners;
	}

	@Override
	protected List<ParentRunner> getChildren() {
		return children;
	}

	@Override
	protected Description describeChild(ParentRunner child) {
		return Description.createSuiteDescription(child.getTestClass().getName());
	}

	@Override
	protected void runChild(ParentRunner child, RunNotifier notifier) {
		child.run(notifier);
	}

	class JenkinsResultParser {
		private String removeJenkinsPrefixes(String line) {
			while (line.contains("/") && !line.startsWith("org.catrobat.catroid")) {
				line = line.split("/", 2)[1].stripLeading();
			}

			return line;
		}

		private String removePackagePath(String line) {
			while (!line.split("\\.")[0].endsWith("Test") &&
					!line.startsWith("CatrobatTestRunner")) {
				line = line.split("\\.", 2)[1];
			}

			return line;
		}

		private Test mapToTest(String line) {
			String[] parts = line.split("\\.", 2);
			String className = parts[0];
			String method = parts[1];
			String methodName;
			String argumentSet;

			if (method.contains("[")) {
				String[] methodParts = method.split("\\[");
				methodName = methodParts[0];
				argumentSet = methodParts[1].replace("]", "");

				if (className.equalsIgnoreCase("CatrobatTestRunner") &&
						!argumentSet.endsWith(".catrobat")) {
					argumentSet += ".catrobat";
				}
			} else {
				methodName = method;
				argumentSet = "";
			}

			return new Test(className, methodName, argumentSet);
		}

		public List<Test> parseTests(List<String> jenkinsOutput) {
			return jenkinsOutput.stream()
					.map(this::removeJenkinsPrefixes)
					.map(this::removePackagePath)
					.map(this::mapToTest)
					.collect(Collectors.toList());
		}
	}
}

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
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import androidx.test.platform.app.InstrumentationRegistry;
import dalvik.system.DexFile;

public class FilteredTestRunner extends ParentRunner<ParentRunner> {

	private static final String TAG = FilteredTestRunner.class.getSimpleName();

	private final Dictionary<String, List<List<String>>> tests;
	private final List<ParentRunner> children;
	private PackagePath pathAnnotation;
	private FailedTests failedTestsAnnotation;

	public FilteredTestRunner(Class<?> suite) throws InitializationError {
		super(suite);
		setAnnotations(suite);
		this.tests = getTestMethodsToRun();
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

	private Dictionary<String, List<List<String>>> getTestMethodsToRun() {
		Dictionary<String, List<List<String>>> tests = new Hashtable<>();

		for (String test : failedTestsAnnotation.value().split("\n")) {
			String[] parts = test.split("\\.");
			String className = parts[0];
			String methodName = parts[1];
			List<String> methodData;

			if (methodName.contains("[")) {
				String[] methodParts = parts[1].split("\\[");
				methodName = methodParts[0];
				String parameterName = methodParts[1].replace("]", "");

				if (className.equalsIgnoreCase("CatrobatTestRunner")) {
					parameterName += ".catrobat";
				}

				methodData = List.of(methodName, parameterName);
			} else {
				methodData = List.of(methodName);
			}

			List<List<String>> testMethods = tests.get(className);
			if (testMethods == null) {
				testMethods = new ArrayList<>();
				tests.put(className, testMethods);
			}
			testMethods.add(methodData);
		}

		Enumeration<String> iter = tests.keys();
		while (iter.hasMoreElements()) {
			String className = iter.nextElement();
			String methodNames = Objects.requireNonNull(tests.get(className))
					.stream()
					.map(l -> String.join("->", l))
					.reduce("", (accum, n) -> accum + ", " + n)
					.replaceFirst(",", "");

			Log.i("FLAKY-TESTS", className + ": " + methodNames);
		}

		return tests;
	}

	private List<ParentRunner> getChildRunners() throws InitializationError {
		List<ParentRunner> runners = new ArrayList<>();

		try {
			String packageCodePath =
					InstrumentationRegistry.getInstrumentation().getContext().getPackageCodePath();
			DexFile dexFile = new DexFile(packageCodePath);

			Enumeration<String> iter = dexFile.entries();
			while (iter.hasMoreElements()) {
				String fullyQualifiedClassName = iter.nextElement();
				String[] parts = fullyQualifiedClassName.split("\\.");
				String className = parts[parts.length - 1];

				if (fullyQualifiedClassName.contains(pathAnnotation.value())
						&& (className.endsWith("Test") || className.equalsIgnoreCase(
								"CatrobatTestRunner"))
						&& tests.get(className) != null) {

					Log.i("FLAKY-TESTS", "test class matched: "
							+ fullyQualifiedClassName + " - " + className);

					List<List<String>> methods = tests.get(className);

					Class<?> testClass = Class.forName(fullyQualifiedClassName);
					RunWith runWithAnnotation = testClass.getAnnotation(RunWith.class);
					if (runWithAnnotation != null && runWithAnnotation.value() == Parameterized.class) {
						runners.add(new FilteredParameterizedRunner(testClass, methods));
					} else {
						BlockJUnit4ClassRunner classRunner = new BlockJUnit4ClassRunner(testClass);
						classRunner.filter(new MethodNameFilter(methods));
						runners.add(classRunner);
					}
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
}


/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.test.io.asynctask

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.catrobat.catroid.common.Constants.CACHE_DIR
import org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.asynctask.ProjectUnZipperAndImporter
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

private const val AIR_FIGHT_0_5 = "Air fight 0.5"
private const val FALLING_BALLS = "Falling balls"
private const val AIR_FIGHT_0_5_1 = "Air fight 0.5 (1)"

@RunWith(AndroidJUnit4::class)
class ProjectUnZipperAndImporterTest {

    companion object {
        private lateinit var destinationDirectory: File
        private lateinit var projectAirFightFile: File
        private lateinit var projectFallingBallsFile: File
        private lateinit var noProjectNameFile: File

        @BeforeClass
        @JvmStatic
        @Throws(IOException::class)
        fun setUpTestClass() {
            destinationDirectory = File(
                DEFAULT_ROOT_DIRECTORY, ProjectUnZipperAndImporterTest::class.java.simpleName)
            CACHE_DIR.mkdir()

            projectAirFightFile = createFile("Air_fight_0.5.catrobat")
            projectFallingBallsFile = createFile("Falling_balls.catrobat")
            noProjectNameFile = createFile("NoProjectName.catrobat")
        }

        private fun createFile(name: String): File {
            val inputStream =
                InstrumentationRegistry.getInstrumentation().context.assets.open(name)

            return StorageOperations.copyStreamToDir(inputStream, CACHE_DIR, name)
        }

        @AfterClass
        @JvmStatic
        @Throws(IOException::class)
        fun tearDownTestClass() {
            StorageOperations.deleteDir(CACHE_DIR)
        }
    }

    @Before
    fun setUp() {
        destinationDirectory.mkdir()
    }

    @After
    fun tearDown() {
        StorageOperations.deleteDir(destinationDirectory)
    }

    @Test
    @Throws(IOException::class)
    fun testUnzipAndImportSingleProject() {
        val importResult = ProjectUnZipperAndImporter(destinationDirectory)
            .unzipAndImportProjects(arrayOf(projectAirFightFile))
        val importedProjects = FileMetaDataExtractor.getProjectNames(destinationDirectory)

        assertTrue(importResult)
        assertThat(importedProjects, contains(AIR_FIGHT_0_5))
    }

    @Test
    @Throws(IOException::class)
    fun testUnzipAndImportMultipleProjects() {
        val importResult = ProjectUnZipperAndImporter(destinationDirectory)
            .unzipAndImportProjects(arrayOf(projectAirFightFile, projectFallingBallsFile))
        val importedProjects = FileMetaDataExtractor.getProjectNames(destinationDirectory)

        assertTrue(importResult)
        assertThat(importedProjects, contains(AIR_FIGHT_0_5, FALLING_BALLS))
    }

    @Test
    @Throws(IOException::class)
    fun testUnzipAndImportSameProjectTwice() {
        val importResult = ProjectUnZipperAndImporter(destinationDirectory)
            .unzipAndImportProjects(arrayOf(projectAirFightFile, projectAirFightFile))
        val importedProjects = FileMetaDataExtractor.getProjectNames(destinationDirectory)

        assertTrue(importResult)
        assertThat(importedProjects, contains(AIR_FIGHT_0_5, AIR_FIGHT_0_5_1))
    }

    @Test
    @Throws(IOException::class)
    fun testUnzipAndImportMultipleProjectsWithError() {
        val importResult = ProjectUnZipperAndImporter(destinationDirectory)
            .unzipAndImportProjects(arrayOf(projectAirFightFile, noProjectNameFile))
        val importedProjects = FileMetaDataExtractor.getProjectNames(destinationDirectory)

        assertFalse(importResult)
        assertThat(importedProjects, contains(AIR_FIGHT_0_5))
    }

    @Test
    fun testAsyncImport() {
        var listenerCalled = false
        val onFinishedListener: (Boolean) -> Unit = { importResult ->
            assertTrue(importResult)
            listenerCalled = true
        }

        runBlocking {
            ProjectUnZipperAndImporter(destinationDirectory)
                .setListener(onFinishedListener)
                .unZipAndImportAsync(arrayOf(projectAirFightFile))
                .join()

            val importedProjects = FileMetaDataExtractor.getProjectNames(destinationDirectory)

            assertTrue(listenerCalled)
            assertThat(importedProjects, contains(AIR_FIGHT_0_5))
        }
    }
}

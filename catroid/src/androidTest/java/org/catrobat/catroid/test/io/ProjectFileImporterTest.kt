/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.test.io

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.google.android.gms.auth.api.signin.internal.Storage
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.CACHE_DIR
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.io.ProjectFileImporter
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.test.utils.TestUtils
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import java.lang.IllegalArgumentException

class ProjectFileImporterTest {

    companion object {
        private lateinit var testProject: Project
        private lateinit var testDirectory: File
        private lateinit var projectDirectory: File
        private lateinit var projectDirectoryUri: Uri
        private lateinit var catrobatFile: File
        private lateinit var catrobatFileUri: Uri
        private const val testClassName = "ProjectFileImporterTest"
        private val testContext = ApplicationProvider.getApplicationContext<Context>()

        @JvmStatic
        @BeforeClass
        fun setUpSuite() {
            createAndSaveProject()
            setUpTestDirectory()
            moveProjectToTestDirectory()
            createProjectZipArchive()
        }

        private fun createAndSaveProject() {
            testProject = Project(testContext, "Test Project")
            val sprite = Sprite("testSprite")
            val script = StartScript()
            script.addBrick(SetXBrick())
            sprite.addScript(script)
            testProject.defaultScene.addSprite(sprite)
            ProjectManager.getInstance().currentProject = testProject
            ProjectManager.getInstance().currentSprite = sprite
            ProjectManager.getInstance().currentlyEditedScene = testProject.defaultScene
            XstreamSerializer.getInstance().saveProject(testProject)
        }

        private fun setUpTestDirectory() {
            testDirectory = File(CACHE_DIR, testClassName)
            if (testDirectory.exists()) {
                StorageOperations.deleteDir(testDirectory)
            }
            testDirectory.mkdir()
        }

        private fun moveProjectToTestDirectory() {
            projectDirectory = File(testDirectory, testProject.directory.name)
            StorageOperations.copyDir(testProject.directory, projectDirectory)
            projectDirectoryUri = Uri.fromFile(projectDirectory)
        }

        private fun createProjectZipArchive() {
            catrobatFile = File(testDirectory, testProject.directory.name + Constants
                .CATROBAT_EXTENSION)
            ZipArchiver().zip(catrobatFile, testProject.directory.listFiles())
            catrobatFileUri = Uri.fromFile(catrobatFile)
        }

        @JvmStatic
        @AfterClass
        fun tearDownSuite() {
            TestUtils.deleteProjects(testProject.name)
            StorageOperations.deleteDir(testDirectory)
        }
    }

    @After
    fun tearDown() {
        testDirectory.listFiles()
            ?.filter { f -> f.extension == "zip" }
            ?.forEach { f -> f.delete() }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testIfURIWithWrongSchemeIsPassed() {
        val invalidUri = Uri.fromParts("ftp", "/foo/bar", "loremIpsum")
        ProjectFileImporter(testDirectory, testContext.contentResolver,
            ArrayList(listOf(invalidUri)))
    }

    @Test
    fun testImportOfDirectory() {
        val importer = ProjectFileImporter(testDirectory, testContext.contentResolver,
                                           ArrayList(listOf(projectDirectoryUri)))

        assertThat(importer.getDirectories().size, `is`(1))
        assertThat(importer.getDirectories()[0].name, `is`("Test Project"))
        assertThat(importer.getZippedFiles(), `is`(emptyArray()))
    }

    @Test
    fun testImportOfZipArchive() {
        val importer = ProjectFileImporter(testDirectory, testContext.contentResolver,
                                           ArrayList(listOf(catrobatFileUri)))

        assertThat(importer.getZippedFiles().size, `is`(1))
        assertThat(importer.getZippedFiles()[0].name, `is`("Test Project.zip"))
        assertThat(importer.getDirectories(), `is`(emptyArray()))
    }

    @Test
    fun testImportOfMultipleFiles() {
        val importer = ProjectFileImporter(testDirectory, testContext.contentResolver,
            ArrayList(listOf(catrobatFileUri, projectDirectoryUri, catrobatFileUri, projectDirectoryUri)))

        assertThat(importer.getDirectories().size, `is`(2))
        assertThat(importer.getDirectories()[0].name, `is`("Test Project"))
        assertThat(importer.getDirectories()[1].name, `is`("Test Project"))
        assertThat(importer.getZippedFiles().size, `is`(2))
        assertThat(importer.getZippedFiles()[0].name, `is`("Test Project.zip"))
        assertThat(importer.getZippedFiles()[1].name, `is`("Test Project_#0.zip"))
    }
}
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

package org.catrobat.catroid.utils

import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.catrobattestrunner.CatrobatTestRunner
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.io.ZipArchiver
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.util.ArrayList

@RunWith(Parameterized::class)
class CLTScanner(
    private val brickName: String
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<String> = listOf(
            "PlaceAtBrick",
            "SetXBrick",
            "SetYBrick",
            "ChangeXByNBrick",
            "ChangeYByNBrick",
            "GoToBrick",
            "MoveNStepsBrick",
            "TurnLeftBrick",
            "TurnRightBrick",
            "PointInDirectionBrick",
            "PointToBrick",
            "SetRotationStyleBrick",
            "GlideToBrick",
            "GoNStepsBackBrick",
            "ComeToFrontBrick",
            "SetCameraFocusPointBrick",
            "VibrationBrick",
            "SetPhysicsObjectTypeBrick",
            "WhenBounceOffBrick",
            "SetVelocityBrick",
            "TurnLeftSpeedBrick",
            "TurnRightSpeedBrick",
            "SetGravityBrick",
            "SetMassBrick",
            "SetBounceBrick",
            "SetFrictionBrick",
            "PhiroMotorMoveForwardBrick",
            "PhiroMotorMoveBackwardBrick",
            "PhiroMotorStopBrick",
            "FadeParticleEffectBrick"
        )

        private val foldersOfInterest: List<String> = listOf(
            "sensor",
            "direction",
            "motion",
            "physics",
            "bricks/motion"
        )
    }

    @Before
    fun setup() {
        Constants.CACHE_DIRECTORY.listFiles()?.forEach { subfolder -> subfolder.delete() }
    }

    @Test
    fun testExistanceOfBrickInCLT() {
        val languageTests = getCatrobatAssetsFromPath(CatrobatTestRunner.TEST_ASSETS_ROOT)
        var matchingLanguageTests: MutableList<Array<String>> = ArrayList()
        languageTests
            .filter { test -> doesContainBrick(test.joinToString("/"), test[1], brickName) }
            .toCollection(matchingLanguageTests)


        Assert.assertThat(matchingLanguageTests, `is`(emptyList()))
    }

    private fun getCatrobatAssetsFromPath(path: String): List<Array<String>> {
        val parameters: MutableList<Array<String>> = ArrayList()
        val assets = InstrumentationRegistry.getInstrumentation().context.assets.list(path)
        if (null == assets) {
            Assert.fail("Could not load assets")
            return parameters
        }
        for (asset in assets) {
            if (asset.endsWith(Constants.CATROBAT_EXTENSION)) {
                parameters.add(arrayOf(path, asset))
            } else if (asset in foldersOfInterest) {
                parameters.addAll(getCatrobatAssetsFromPath("$path/$asset"))
            }
        }
        return parameters
    }

    private fun doesContainBrick(
        languageTestPath: String, languageTestName: String, brickName: String): Boolean {
        val inputStream = InstrumentationRegistry.getInstrumentation().context.assets
            .open(languageTestPath)
        val destination = File(Constants.CACHE_DIRECTORY, languageTestName)

        ZipArchiver().unzip(inputStream, destination)

        val contents = File(destination, "code.xml").readText()

        return contents.contains(brickName)
    }
}
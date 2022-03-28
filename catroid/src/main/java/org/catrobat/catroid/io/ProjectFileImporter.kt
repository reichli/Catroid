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

package org.catrobat.catroid.io

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import org.catrobat.catroid.common.Constants
import java.io.File
import java.io.IOException
import java.util.ArrayList

class ProjectFileImporter(
    private val cacheDirectory: File,
    private val contentResolver: ContentResolver,
    private val urisToImport: ArrayList<Uri>
) {

    private var zippedFiles: ArrayList<File> = ArrayList()
    private var directories: ArrayList<File> = ArrayList()

    init {
        fillFileLists(urisToImport)
    }

    private fun fillFileLists(urisToImport: ArrayList<Uri>) {
        for (uri in urisToImport) {
            require(uri.scheme == "file") { "importProject has to be called with a file uri. (not a content uri" }
            val project = File(uri.path)

            if (project.isDirectory) {
                directories.add(project)
            } else {
                val zipFileName = uri.lastPathSegment?.replace(Constants.CATROBAT_EXTENSION,
                                                               Constants.ZIP_EXTENSION)

                val zipFile = zipFileName?.let {
                    try {
                        StorageOperations.copyUriToDir(
                            contentResolver, uri,
                            cacheDirectory, zipFileName
                        )
                    } catch (e: IOException) {
                        null
                    }
                }

                zipFile?.let {
                    zippedFiles.add(it)
                }
            }
        }
    }

    fun getZippedFiles(): Array<File> {
        return zippedFiles.toTypedArray()
    }

    fun getDirectories(): Array<File> {
        return directories.toTypedArray()
    }
}
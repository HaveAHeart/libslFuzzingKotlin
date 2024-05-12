package com.spbpu

import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

class FileUtils {
    companion object {
        fun openGenFile(className: String, methodName: String, argName: String): File {
            val filePath = Path("src/test/kotlin/com.spbpu/tmp", "${className}_${methodName}_${argName}_Gen.kt")
            filePath.toFile().createNewFile()
            val genFile = filePath.toFile()
            return genFile
        }

        fun getTemplateFile(type: String): File {
            val baseTemplateName = "${type}LibSLRandomizerTemplate"
            val filePath = Path("src/main/kotlin/com.spbpu/templates", baseTemplateName)
            return filePath.toFile()
        }
    }
}
package com.microtik.core.base.logger

import com.microtik.Microtik
import com.microtik.core.base.cli.exceptions.ApplicationException
import java.io.File
import java.io.FileWriter
import java.io.IOException

class FileTarget(val logFileName: String, val logFilePath: String, val levels: List<String>) : Target {
    private var file: File

    init {
        val baseDir = Microtik.getBaseJarDir()

        val file = File(baseDir + File.separator + logFilePath + logFileName)

        if (!file.parentFile.exists()) {
            try {
                file.parentFile.mkdirs()
                println("The Directory ${file.parentFile.absolutePath} was created.")
            } catch (e: SecurityException) {
                println("Safety error when creating a directory: ${e.message}")
            }
        }

        if (!file.exists()) {
            try {
                file.createNewFile()
                println("File ${file.absoluteFile} was created.")
            } catch (e: IOException) {
                println("Error when creating a file: ${e.message}")
            }
        }

        if (file.exists()) {
            this.file = file
        } else {
            throw ApplicationException("Failed to initialize the logistics file: ${file.absolutePath}")
        }
    }

    @Synchronized
    override fun collect(message: Message) {
        if (message.level !in levels) return

        try {
            val logEntry = "[${message.time}][${message.level.uppercase()}] ${message.message}\n"
            FileWriter(file, true).use { writer -> writer.write(logEntry) }
        } catch (e: IOException) {
            Microtik.error("There was an error when writing to a file ${file.absoluteFile}")
        }
    }
}
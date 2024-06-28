package com.example.dynamite.jsonGeneration

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.GZIPInputStream

fun compressGzip(inputFilePath: String, outputFilePath: String) {
    FileInputStream(inputFilePath).use { fis ->
        FileOutputStream(outputFilePath).use { fos ->
            GZIPOutputStream(fos).use { gzip ->
                fis.copyTo(gzip)
            }
        }
    }
}

fun decompressGzip(inputFilePath: String, outputFilePath: String) {
    FileInputStream(inputFilePath).use { fis ->
        GZIPInputStream(fis).use { gzip ->
            FileOutputStream(outputFilePath).use { fos ->
                gzip.copyTo(fos)
            }
        }
    }
}

fun main() {
    val inputFilePath = "ModelData/experiences.json"
    val compressedFilePath = "ModelData/experiences.json.gz"
    val decompressedFilePath = "ModelData/experiences_decompressed.json"

    // Compress JSON file using GZIP
    compressGzip(inputFilePath, compressedFilePath)
    println("Compressed JSON file has been saved to $compressedFilePath")

    // Decompress JSON file using GZIP
    decompressGzip(compressedFilePath, decompressedFilePath)
    println("Decompressed JSON file has been saved to $decompressedFilePath")
}

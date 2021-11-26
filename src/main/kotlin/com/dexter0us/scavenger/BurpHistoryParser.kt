package com.dexter0us.scavenger

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.net.URI
import java.util.*

class BurpHistoryParser {

    fun historyParser(index: Int, fileLocation: String, fileName: String, filter: Boolean): ProcessResult {
        val channel = Channel<Int>()
        val job = GlobalScope.launch(Dispatchers.Default) {

            val paramList = mutableSetOf<String>()
            val jsList = mutableSetOf<String>()
            val endpointList = mutableSetOf<String>()
            val jsonKeyList = mutableSetOf<String>()

            val proxyHistory = callbacks.proxyHistory

            historySize = proxyHistory.size
            console("History Size: $historySize")

            proxyHistory.forEach {
                if (counter % 25 == 0) {
                    channel.send(counter)
                }
                counter++

                val reqInfo = helpers.analyzeRequest(it.httpService, it.request)
                it.response ?: return@forEach
                val respInfo = helpers.analyzeResponse(it.response)
                val statusCode = respInfo.statusCode.toInt()

                if ((callbacks.isInScope(reqInfo.url)) && (statusCode != 201 || statusCode != 304 || statusCode != 404)) {
                    if (index == 0 || index == 3) {
                        try {
                            val params = reqInfo.parameters
                            for (param in params) {
                                val paramType = param.type.toInt()
                                if (paramType == 0 || paramType == 1 || paramType == 2 || paramType == 6) {
                                    paramList.addAll(setOf(param.name))
                                }
                            }
                        } catch (e: Exception) {
//                    stderr.println("Bad characters.")
                        }
                    }

                    if (index == 2 || index == 3) {
                        try {
                            val path = reqInfo.url.toString()
                            endpointList.addAll(((URI(path)).path).split("/"))

                            if (statusCode == 200 && respInfo.inferredMimeType.lowercase() == "script") {
                                val body = Arrays.copyOfRange(it.response, respInfo.bodyOffset, it.response.size)
                                jsList.addAll(JSParser().parser(helpers.bytesToString(body)))
                            }
                        } catch (e: Exception) {
//                        stderr.println("Bad characters.")
                        }
                    }

                    if (index == 1 || index == 3) {
                        try {
                            if (statusCode == 200 && respInfo.inferredMimeType.lowercase() == "json") {
                                val body = Arrays.copyOfRange(it.response, respInfo.bodyOffset, it.response.size)
                                jsonKeyList.addAll(JsonParser().parser(helpers.bytesToString(body)))
                            }
                        } catch (e: Exception) {
//                       stderr.println("JSON.")
                        }
                    }
                }
            }

            when (index) {
                0 -> run { writeFile(fileLocation, fileName, filterList(paramList, filter) ) }
                1 -> run { writeFile(fileLocation, fileName, filterList(jsonKeyList, filter)) }
                2 -> run { writeFile(fileLocation, fileName, filterList(((endpointList + jsList) as MutableSet<String>), filter)) }
                3 -> run {
                    writeFile(
                        fileLocation,
                        fileName,
                        filterList(((paramList + endpointList + jsList + jsonKeyList) as MutableSet<String>), filter)
                    )
                }
            }
            channel.close()
        }
        return ProcessResult(channel, job)
    }


    val writeFile = { fileLocation: String, fileName: String, wordlist: MutableSet<String> ->
        val absoluteFilePath = FilenameUtils.concat(fileLocation, fileName)
        console("File: $absoluteFilePath")
        FileUtils.writeLines(File(absoluteFilePath), wordlist, true)
        console("Saved!!!")
    }

    private fun filterList(set: MutableSet<String>, bool: Boolean): MutableSet<String> {
        if (bool){
            set.removeIf {
                it.lowercase().endsWith("svg")
                        || it.lowercase().endsWith("png")
                        || it.lowercase().endsWith("jpg")
                        || it.lowercase().endsWith("jpeg")
                        || it.lowercase().endsWith("gif")
                        || it.lowercase().endsWith("ttf")
                        || it.lowercase().endsWith("woff")
                        || it.lowercase().endsWith("woff2")
            }
            return set
        }
        return set
    }
}



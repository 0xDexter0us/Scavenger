package com.dexter0us.scavenger

import burp.IBurpExtenderCallbacks
import burp.IExtensionHelpers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import java.io.PrintWriter


lateinit var callbacks: IBurpExtenderCallbacks
lateinit var helpers: IExtensionHelpers
lateinit var stdout: PrintWriter
lateinit var stderr: PrintWriter

var historySize: Int = 100
var counter = 0

var currJob: Job? = null
val console = { str: String -> stdout.println(str) }

class ProcessResult(val resultChannel: ReceiveChannel<Int>, val job: Job)



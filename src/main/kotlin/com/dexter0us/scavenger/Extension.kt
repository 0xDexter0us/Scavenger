package com.dexter0us.scavenger

import burp.*
import com.dexter0us.scavenger.ui.ScavengerUI
import java.io.PrintWriter
import javax.swing.*


open class Extension : IBurpExtender, IExtensionStateListener {
    companion object{
        const val pluginName = "Scavenger"
        const val version = "0.5.0"
    }

    private var scavUnload = false
    private var burpMenu: JMenuBar? = null
    private var scavMenu: JMenu? = null

    override fun registerExtenderCallbacks(_callbacks: IBurpExtenderCallbacks) {
        callbacks = _callbacks
        helpers = _callbacks.helpers
        stdout = PrintWriter(callbacks.stdout, true)
        stderr = PrintWriter(callbacks.stderr, true)

        callbacks.apply {
            setExtensionName(pluginName)
            registerExtensionStateListener { extensionUnloaded() }
        }

        console("$pluginName v$version Loaded")

        SwingUtilities.invokeLater {
            try {
                burpMenu = getBurpFrame()!!.jMenuBar
                scavMenu = JMenu("Scavenger")
                val listCustomTagsMenu = JMenuItem("Launch Scavenger")
                listCustomTagsMenu.addActionListener { ScavengerUI() }
                scavMenu!!.add(listCustomTagsMenu)
                burpMenu!!.add(scavMenu)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    open fun getBurpFrame(): JFrame? {
        for (frame in JFrame.getFrames()) {
            if (frame.isVisible && frame.title.startsWith("Burp Suite")) {
                return frame as JFrame?
            }
        }
        return null
    }

    override fun extensionUnloaded() {
        stdout.println("Scavenger unloaded")
        scavUnload = true
        burpMenu?.remove(scavMenu)
        burpMenu?.repaint()
        currJob?.cancel()
    }

 }
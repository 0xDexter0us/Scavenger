package com.dexter0us.scavenger.ui

import com.dexter0us.scavenger.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import net.miginfocom.swing.MigLayout
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.image.BufferedImage
import java.io.File
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.swing.*

class ScavengerUI : JFrame("Scavenger"), ActionListener {
    private var chooser = JFileChooser()
    private var selectFolder = JButton()
    private var saveFile = JButton()
    private var twitterButton = JButton()
    private var githubButton = JButton()
    private var blogButton = JButton()
    private var kofiButton = JButton()
    private val listType = arrayOf(
        "Request Parameter List",
        "Json Response Key List",
        "Endpoint List",
        "Combine All Three Lists"
    )
    private var listCBox = JComboBox(listType)
    private var checkBox = JCheckBox()
    private var fileLocation = JTextField()
    private val fileNameTB = JTextField()
    private val progressBar = JProgressBar()
    private val pwd = System.getProperty("user.dir")

    init {

//      Top Panel (Header) ----------------------------------------------------------


        val heading = JLabel().apply {
            text = "Scavenger"
            font = font.deriveFont(32f).deriveFont(Font.BOLD)
        }

        val tagline = JLabel().apply {
            text = "Burp extension to create target specific and tailored wordlist from burp history."
            font = font.deriveFont(16f).deriveFont(Font.ITALIC)
        }


//      Main Panel (Body) ========================================================


        listCBox.addActionListener(this)

        val fileName = projectName(listCBox.selectedIndex)
        fileNameTB.apply {
            text = fileName
            columns = 50
            addFocusListener(object : FocusListener {
                override fun focusGained(e: FocusEvent) {
                    if (text != fileName) {/**/
                    } else text = ""
                }

                override fun focusLost(e: FocusEvent) =
                    if (text != fileName) {/**/
                    } else text = fileName
            })
        }

        fileLocation.apply {
            text = pwd
            columns = 50
            addFocusListener(object : FocusListener {
                override fun focusGained(e: FocusEvent) {
                    if (text != pwd) {/**/
                    } else text = ""
                }

                override fun focusLost(e: FocusEvent) {
                    if (text != pwd) {/**/
                    } else text = pwd
                }
            })
        }

        selectFolder = JButton("Select Folder...")
        selectFolder.addActionListener(this)

        checkBox.apply {
            text = "Exclude words with svg, png, jpg, ttf, woff extension."
            isFocusable = false
            isEnabled = false
        }

        val saveImage = loadImage("save.png")
        when {
            saveImage != null -> {
                saveFile = JButton("Save", saveImage)
                saveFile.componentOrientation = ComponentOrientation.RIGHT_TO_LEFT
                saveFile.iconTextGap = 7
            }
            else -> saveFile = JButton("Save")
        }
        saveFile.addActionListener(this)

        progressBar.apply {
            minimum = 0
            maximum = historySize
            isStringPainted = true
            value = 0
        }


//      Contact Panel (Footer) ========================================================

        val twitterImage = loadImage("twitter.png")
        when {
            twitterImage != null -> {
                twitterButton = JButton("Follow me on Twitter", twitterImage)
                twitterButton.componentOrientation = ComponentOrientation.RIGHT_TO_LEFT
                twitterButton.iconTextGap = 7
            }
            else -> twitterButton = JButton("Follow me on Twitter")
        }
        twitterButton.addActionListener(this)


        val githubImage = loadImage("github.png")
        when {
            githubImage != null -> {
                githubButton = JButton("View Project on Github", githubImage)
                githubButton.componentOrientation = ComponentOrientation.RIGHT_TO_LEFT
                githubButton.iconTextGap = 7
            }
            else -> githubButton = JButton("View Project on Github")
        }
        githubButton.addActionListener(this)


        val blogImage = loadImage("blog.png")
        when {
            blogImage != null -> {
                blogButton = JButton("Checkout my Blog", blogImage)
                blogButton.componentOrientation = ComponentOrientation.RIGHT_TO_LEFT
                blogButton.iconTextGap = 7
            }
            else -> blogButton = JButton("Checkout my Blog")
        }
        blogButton.addActionListener(this)


        val kofiImage = loadImage("ko-fi.png")
        when {
            kofiImage != null -> {
                kofiButton = JButton("Support Project on Ko-Fi", kofiImage)
                kofiButton.componentOrientation = ComponentOrientation.RIGHT_TO_LEFT
                kofiButton.iconTextGap = 7
            }
            else -> kofiButton = JButton("Buy me a Coffee")
        }
        kofiButton.addActionListener(this)


        val northPanel = JPanel().apply {
            layout = MigLayout("align center")
            border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
            add(heading, "bottom, center, span, wrap")
            add(tagline, "top, center, span, wrap")
        }

        val bodyPanel = JPanel().apply {
            layout = MigLayout()
            border = BorderFactory.createEmptyBorder(0, 10, 0, 10)

            add(JLabel("Select List Type:"), "right")
            add(listCBox, "span, growx, wrap, h 30!")
            add(JLabel("Name:"), "right")
            add(fileNameTB, "span, growx, wrap, h 30!")
            add(JLabel("Folder:"), "right")
            add(fileLocation, "growx, split 2, h 30!")
            add(selectFolder, "wrap, span, h 30!")
            add(JSeparator(SwingConstants.HORIZONTAL), "")
            add(checkBox, "span, left, wrap")
            add(JSeparator(SwingConstants.HORIZONTAL), "")
            add(saveFile, "span, center, w 125!, h 30!")
            add(progressBar, "span, center, growx, h 20!")

        }

        val southPanel = JPanel().apply {
            layout = MigLayout("align center")
            border = BorderFactory.createEmptyBorder(2, 0, 10, 0)

            add(JLabel("Created with <3 by Dexter0us"), "span, align center, wrap")
            add(twitterButton, "w 200!, h 30!")
            add(githubButton, "w 200!, h 30!, wrap")
            add(blogButton, "w 200!, h 30!")
            add(kofiButton, "w 200!, h 30!, wrap")
        }

        this.also {
            layout = MigLayout("align center")

            add(northPanel, "dock north")
            add(JSeparator(SwingConstants.HORIZONTAL), "wrap")
            add(bodyPanel, "wrap, align center")
            add(JSeparator(SwingConstants.HORIZONTAL), "wrap")
            add(southPanel, "dock south")

            defaultCloseOperation = DISPOSE_ON_CLOSE
            isResizable = false
            setSize(600, 550)
            isVisible = true
        }
    }


    override fun actionPerformed(e: ActionEvent?) {
        when (e?.source) {
            listCBox -> {
                fileNameTB.text = projectName(listCBox.selectedIndex)
                if (listCBox.selectedIndex == 2 || listCBox.selectedIndex ==3){
                    checkBox.isEnabled = true
                }
            }
            selectFolder -> folderSelector()
            saveFile -> {
                progressBar.value = 0
                saveFile.isEnabled = false
                cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                GlobalScope.launch(Dispatchers.Swing) {
                    currJob?.cancel()

                    val processResult = BurpHistoryParser().historyParser(listCBox.selectedIndex, fileLocation.text, fileNameTB.text, checkBox.isSelected)
                    currJob = processResult.job
                    for (y in processResult.resultChannel) {
                        progressBar.maximum = historySize - 5
                        progressBar.value = y
                    }
                    progressBar.value = historySize
                    saveFile.isEnabled = true
                    cursor = Cursor.getDefaultCursor()
                }
            }
            twitterButton -> openInBrowser("https://twitter.com/0xDexter0us")
            githubButton -> openInBrowser("https://github.com/0xDexter0us/Scavenger")
            blogButton -> openInBrowser("https://dexter0us.com/")
            kofiButton -> openInBrowser("https://ko-fi.com/dexter0us")
        }
    }


    private fun loadImage(filename: String): ImageIcon? {
        val cldr = this.javaClass.classLoader
        val imageURLMain = cldr.getResource(filename)
        if (imageURLMain != null) {
            val scaled = ImageIcon(imageURLMain).image.getScaledInstance(30, 30, Image.SCALE_SMOOTH)
            val scaledIcon = ImageIcon(scaled)
            val bufferedImage = BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB)
            val g = bufferedImage.graphics as Graphics2D
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g.drawImage(scaledIcon.image, null, null)
            return ImageIcon(bufferedImage)
        }
        return null
    }


    private fun folderSelector() {
        chooser = JFileChooser().apply {
            currentDirectory = File(pwd)
            dialogTitle = "Select Folder"
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            isAcceptAllFileFilterUsed = false
            val response = showOpenDialog(null)

            if (response == JFileChooser.APPROVE_OPTION) {
                fileLocation.text = File(selectedFile.absolutePath).toString()
            } else {
                fileLocation.text = currentDirectory.toString()
            }
        }
    }

    private fun projectName(index: Int): String {
        var listType = ""
        when (index) {
            0 -> listType = "parameter-list"
            1 -> listType = "jsonkey-list"
            2 -> listType = "endpoint-list"
            3 -> listType = "combined-list"
        }
        val burpTitle = Extension().getBurpFrame()?.title
        val burpTitleArray = burpTitle!!.split(" - ")
        val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val fileName = burpTitleArray[1] + "-" + listType + "-" + date + ".txt"
        return fileName.replace(" ".toRegex(), "-")
    }

    private fun openInBrowser(url: String) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(url))
        } else {
            alertBox("Unable to open browser.\n Visit: $url")
        }
    }

    private fun alertBox(str: String) {
        JOptionPane.showMessageDialog(this, str, "Scavenger", JOptionPane.PLAIN_MESSAGE)
    }

}
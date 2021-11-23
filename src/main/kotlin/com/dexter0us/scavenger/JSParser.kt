package com.dexter0us.scavenger


open class JSParser {
    private var finalList = mutableListOf<String>()

    private val reg = Regex(
        "[\"']" +                            // Start newline delimiter

                "(((?:[a-zA-Z]{1,10}://|//)" +          // Match a scheme [a-Z]*1-10 or //
                "[^\"'/]{1,}\\." +                      // Match a domain name (any character + dot)
                "[a-zA-Z]{2,}[^\"']{0,})" +             // The domain extension and/or path

                "|" +

                "((?:/|\\.\\./|\\./)" +                 // Start with /,../,./
                "[^\"'><,;| *()%\$^/\\\\\\[\\]]" +      // Next character can't be...
                "[^\"'><,;|()]{1,})" +                  // Rest of the characters can't be

                "|" +

                "([a-zA-Z0-9_\\-/]{1,}/" +              // Relative endpoint with /
                "[a-zA-Z0-9_\\-/]{1,}\\." +             // Resource name
                "(?:[a-zA-Z]{1,4}|action)" +            // Rest + extension (length 1-4 or action)
                "(?:[?|/][^\"|']{0,}|))" +              // ? mark with parameters

                "|" +

                "([a-zA-Z0-9_\\-]{1,}\\." +                                 // filename
                "(?:php|asp|aspx|jsp|json|action|html|js|txt|xml)" +        // . + extension
                "(?:\\?[^\"|']{0,}|)))" +                                   // ? mark with parameters

                "[\"']"                                 // End newline delimiter
    )

    open fun parser(body: String): MutableList<String> {
        val matches = reg.findAll(body)

        matches.forEach {
            finalList.addAll(it.value.split("/"))
        }

        finalList.replaceAll { it.replace(Regex("[:\"']|[\\\\?]\$"), "") }
        finalList.also {
            it.removeIf { s ->
                s.isBlank() || s.isEmpty()
            }
        }
        return finalList.distinct() as MutableList<String>
    }
}
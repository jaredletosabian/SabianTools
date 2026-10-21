package com.sabiantools.extensions

import android.util.Base64
import com.sabiantools.utilities.SabianUtilities
import java.nio.charset.Charset
import java.util.regex.Pattern

private const val SPECIAL_CHARACTERS_REGEX = "[\\<\\>\\/{}%()\\[\\].+*?^$\\\\|]"

/**
 * Converts this object to its JSON representation using the application's
 * standard Gson configuration.
 *
 * @return The JSON representation of this object.
 */
fun Any.toJson(): String {
    return SabianUtilities.GetStandardGson().toJson(this)
}

/**
 * Removes all whitespace characters from this string.
 *
 * Consecutive whitespace characters are replaced with [replaceWith].
 *
 * @param replaceWith The value used to replace whitespace. Defaults to an empty string.
 * @return The resulting string.
 */
fun String.removeAllSpaces(replaceWith: String = ""): String {
    return replace("\\s+".toRegex(), replaceWith)
}

/**
 * Replaces consecutive whitespace characters with a single space.
 *
 * @return The normalized string.
 */
fun String.removeDoubleSpaces(): String {
    return this.removeAllSpaces(" ")
}

/**
 * Converts this string to title case.
 *
 * When [considerSpaces] is `true`, each word is title-cased individually.
 * Otherwise, only the first character of the string is capitalized.
 *
 * @param considerSpaces Whether each space-separated word should be title-cased.
 * @return The formatted string.
 */
fun String.perfectCase(considerSpaces: Boolean = true): String {
    if (!considerSpaces)
        return lowercase().replaceFirstChar(Char::titlecase)
    val all = split("\\s+".toRegex())
    return all.joinToString(" ", transform = {
        it.perfectCase(false)
    })
}

/**
 * Normalizes this string into a consistent human-readable format.
 *
 * The following transformations are applied:
 * - Trims leading and trailing whitespace.
 * - Replaces multiple spaces with a single space.
 * - Converts each word to title case.
 *
 * @return The normalized string.
 */
fun String.perfectForm(): String {
    return this.trim()
        .removeDoubleSpaces()
        .perfectCase()
}

/**
 * Escapes all regular expression special characters within this string.
 *
 * Useful when treating user input as a literal value in a regular expression.
 *
 * @return The escaped string.
 */
fun String.escapeSpecialRegexChars(): String {
    val pattern = Pattern.compile(SPECIAL_CHARACTERS_REGEX)
    return pattern.matcher(this).replaceAll("\\\\$0")
}

/**
 * Replaces all regular expression special characters within this string.
 *
 * @param replacement The value used to replace each special character.
 * @return The resulting string.
 */
fun String.replaceSpecialRegexChars(replacement: String = ""): String {
    val pattern = Pattern.compile(SPECIAL_CHARACTERS_REGEX)
    return pattern.matcher(this).replaceAll(replacement)
}

/**
 * Encodes this string as Base64.
 *
 * @param charset The character set used when converting the string to bytes.
 * @param flags Android Base64 encoding flags.
 * @return The Base64-encoded string.
 */
fun String.toBase64(charset: Charset = Charsets.UTF_8, flags: Int = Base64.DEFAULT): String {
    val bytes = this.toByteArray(charset)
    return Base64.encodeToString(bytes, flags)
}

/**
 * Compares this string with [value] after normalizing both values.
 *
 * Normalization performs the following:
 * - Trims leading and trailing whitespace.
 * - Compares the strings without considering letter case.
 *
 * Examples:
 * ```
 * " BrianSabana ".equalsNormalized("briansabana") // true
 * "Brian".equalsNormalized(" brian ")             // true
 * "Brian".equalsNormalized("Brian1")              // false
 * ```
 *
 * @param value The string to compare against.
 * @return `true` if the normalized strings are equal; otherwise `false`.
 */
fun String.equalsNormalized(value: String): Boolean {
    return this.trim().equals(value.trim(), ignoreCase = true)
}


/**
 * Extracts the first keyword between the specified opening and closing characters.
 *
 * Examples:
 * ```
 * "{select_sku}".extractKeyword()               // "select_sku"
 * "Choose {select_sku} here".extractKeyword()   // "select_sku"
 * "[select_sku]".extractKeyword("[]")           // "select_sku"
 * "(select_sku)".extractKeyword("()")           // "select_sku"
 * "{first} and {second}".extractKeyword()      // "first"
 * "{}".extractKeyword()                       // ""
 * "No keyword here".extractKeyword()          // null
 * "{missing_end".extractKeyword()             // null
 * ```
 *
 * @param containingString Two characters defining the delimiters, such as "{}" or "[]".
 * @return The enclosed keyword, or null if the delimiters are invalid or not found.
 */
fun String.extractKeyword(containingString: String = "{}"): String? {
    // Ensure there is exactly one opening and one closing character.
    if (containingString.length != 2) return null

    // Find the first opening character.
    val start = indexOf(containingString[0])
    if (start == -1) return null

    // Find the first closing character after the opening character.
    val end = indexOf(containingString[1], start + 1)
    if (end == -1) return null

    // Return the text between the delimiters, excluding the delimiters themselves.
    return substring(start + 1, end)
}


/**
 * Replaces all occurrences of a delimited keyword, ignoring case.
 * Other keywords and occurrences outside the delimiters are left unchanged.
 *
 * Example:
 * ```
 * "The {selected_sku} was chosen and {Selected_sku} was ordered as well"
 *     .replaceKeyword("selected_sku", "car")
 * // "The car was chosen and car was ordered as well"
 * ```
 *
 * @param keyword The keyword to match between the delimiters, ignoring case.
 * @param replacement The literal text used to replace every matching placeholder.
 * @param containingString Exactly two characters defining the opening and closing delimiters.
 * Defaults to "{}".
 * @return The string with all matching placeholders replaced, or the original string if
 * [containingString] does not contain exactly two characters or no matching placeholder is found.
 */
fun String.replaceKeyword(
    keyword: String,
    replacement: String,
    containingString: String = "{}"
): String {
    if (containingString.length != 2) return this
    val placeholder = "${containingString[0]}$keyword${containingString[1]}"
    return replace(placeholder, replacement, ignoreCase = true)
}

/**
 * Extracts the first keyword and replaces all matching placeholders,
 * ignoring case. Returns null if no keyword is found.
 *
 * Example:
 * ```
 * "The {selected_sku} was chosen and the person ordered the {Selected_sku} as well"
 *     .replaceFirstKeyword("car")
 * // "The car was chosen and the person ordered the car as well"
 * ```
 *
 * @param replacement The literal text used to replace every placeholder matching the first keyword.
 * @param containingString Exactly two characters defining the opening and closing delimiters.
 * Defaults to "{}".
 * @return The string with all matching placeholders replaced, ignoring case, or `null` if
 * [containingString] does not contain exactly two characters or no complete placeholder is found.
 */
fun String.replaceFirstKeyword(
    replacement: String,
    containingString: String = "{}"
): String? {
    val keyword = extractKeyword(containingString) ?: return null
    return replaceKeyword(keyword, replacement, containingString)
}

/**
 * Determines whether this string contains the specified keyword,
 * ignoring character case.
 *
 * Regular expression special characters in the keyword are escaped.
 *
 * @param keyWord The keyword to search for.
 * @return `true` if the keyword is found; otherwise `false`.
 */
fun String.isAMatchByKeyWord(keyWord: String): Boolean {
    return isAMatchByKeyWord(keyWord, true)
}

/**
 * Determines whether this string contains the specified keyword,
 * ignoring character case.
 *
 * Regular expression special characters in the keyword are escaped.
 *
 * @param keyWord The keyword to search for.
 * @return `true` if the keyword is found; otherwise `false`.
 */
fun String.isAMatchByKeyWordWithReverse(
    keyWord: String,
    escapeSpecialCharacters: Boolean = true
): Boolean {
    val valid = isAMatchByKeyWord(keyWord, escapeSpecialCharacters)
    if (valid) return true
    return keyWord.isAMatchByKeyWord(this, escapeSpecialCharacters)
}

/**
 * Determines whether this string contains the specified keyword,
 * ignoring character case.
 *
 * Optionally escapes regular expression special characters in the keyword
 * before performing the search.
 *
 * @param keyWord The keyword to search for.
 * @param escapeSpecialCharacters Whether regex special characters should be escaped.
 * @return `true` if the keyword is found; otherwise `false`.
 */
fun String.isAMatchByKeyWord(
    keyWord: String,
    escapeSpecialCharacters: Boolean = true
): Boolean {
    if (SabianUtilities.IsStringBlankOrEmpty(this) || SabianUtilities.IsStringBlankOrEmpty(keyWord)) return false
    val searchFor = if (escapeSpecialCharacters) keyWord.escapeSpecialRegexChars() else keyWord
    val pattern = Pattern.compile(
        ".*$searchFor.*",
        Pattern.CASE_INSENSITIVE
    )
    return pattern.matcher(this).matches()
}

/**
 * Extracts the value portion of a dot-separated key.
 *
 * For example:
 * - `key.name` returns `name`
 * - `key.user.email` returns `user.email`
 *
 * The key must begin with [prepend] followed by [dot].
 *
 * @return The extracted value, or `null` if the format is invalid.
 */
fun String.getValueFromDotKey(prepend: String = "key", dot: String = "."): String? {
    val regex = "(${prepend}\\$dot)(.*)"
    val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(this)
    if (matcher.matches()) {
        return try {
            matcher.group(2)
        } catch (e: Throwable) {
            null
        }
    }
    return null
}

/**
 * Returns this string unless it is `null` or blank,
 * in which case the value produced by [defaultValue] is returned.
 */
fun String?.ifNullOrBlank(defaultValue: () -> String?): String? {
    if (this == null)
        return defaultValue()
    return this.ifBlank(defaultValue)
}

/**
 * Determines whether this collection contains a string that matches
 * the specified keyword.
 *
 * When [reverseLook] is enabled, the comparison is also performed in reverse.
 *
 * @return `true` if a match is found.
 */
fun Collection<String>.containsKeyWord(
    keyWord: String,
    reverseLook: Boolean = false
): Boolean {
    return this.any { it.isAMatchByKeyWord(keyWord) || (reverseLook && keyWord.isAMatchByKeyWord(it)) }
}

/**
 * Determines whether this array contains a string that matches
 * the specified keyword.
 *
 * When [reverseLook] is enabled, the comparison is also performed in reverse.
 *
 * @return `true` if a match is found.
 */
fun Array<String>.containsKeyWord(
    keyWord: String,
    reverseLook: Boolean = false
): Boolean {
    return this.any { it.isAMatchByKeyWord(keyWord) || (reverseLook && keyWord.isAMatchByKeyWord(it)) }
}

/**
 * Determines whether this string matches any keyword in the supplied list.
 *
 * @param keyWords The keywords to search for.
 * @param reverseLook Whether comparisons should also be performed in reverse.
 * @return `true` if a match is found.
 */
fun String.matchesWithAnyKeyWord(
    keyWords: List<String>,
    reverseLook: Boolean = false
): Boolean {
    return matchesWithAnyKeyWord(keyWords.toTypedArray())
}

/**
 * Determines whether this string matches any keyword in the supplied array.
 *
 * @param keyWords The keywords to search for.
 * @param reverseLook Whether comparisons should also be performed in reverse.
 * @return `true` if a match is found.
 */
fun String.matchesWithAnyKeyWord(
    keyWords: Array<String>,
    reverseLook: Boolean = false
): Boolean {
    if (keyWords.isEmpty())
        return false
    return keyWords.any { this.isAMatchByKeyWord(it) || (reverseLook && it.isAMatchByKeyWord(this)) }
}

/**
 * Determines whether this string matches any of the supplied regular expressions.
 *
 * @param p The patterns to evaluate.
 * @return `true` if any pattern matches.
 */
fun String.matchesWithAnyKeyWord(p: List<Pattern>): Boolean {
    if (p.isEmpty())
        return false
    return p.any { it.matcher(this).find() }
}

/**
 * Shortens this string to the specified maximum length and appends
 * a trailing indicator if truncation occurs.
 *
 * @param length The maximum length of the resulting string.
 * @param trailing The suffix appended when truncation occurs.
 * @return The shortened string.
 */
fun String.ellipsis(length: Int, trailing: String = "..."): String {
    return SabianUtilities.GetEllipsis(this, length, trailing)
}

/**
 * Returns this string unless it is `null`, blank, or equal to [equals],
 * in which case the value produced by [action] is returned.
 */
fun String?.ifBlankOrEqualTo(
    equals: String,
    action: () -> String
): String {
    if (isNullOrBlank())
        return action()
    return ifEqualTo(equals, action)
}

/**
 * Returns this string unless it is equal to [equals],
 * in which case the value produced by [action] is returned.
 */
fun String.ifEqualTo(
    equals: String,
    action: () -> String
): String {
    if (this == equals)
        return action()
    return this
}

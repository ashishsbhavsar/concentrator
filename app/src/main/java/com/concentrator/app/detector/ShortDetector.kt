package com.concentrator.app.detector

import android.view.accessibility.AccessibilityNodeInfo

data class ShortDetectionResult(
    val isShort: Boolean,
    val signature: String,
    val channel: String?,
    val title: String?
)

class ShortDetector {

    private val shortsViewIdSubstrings = listOf(
        "reel_player",
        "shorts_container",
        "reel_recycler",
        "reel_video",
        "reel_dyn_touch_view"
    )

    private val shortsActionIndicators = listOf(
        "dislike this video",
        "remix",
        "sound used in this short",
        "comments"
    )

    fun inspect(root: AccessibilityNodeInfo?): ShortDetectionResult? {
        if (root == null) return null

        var foundShortsClues = 0
        var extractedChannel: String? = null
        var extractedTitle: String? = null
        var extractedSound: String? = null

        fun traverse(node: AccessibilityNodeInfo?, depth: Int) {
            if (node == null || depth > 25) return

            val viewId = node.viewIdResourceName?.lowercase() ?: ""
            val text = node.text?.toString()?.trim() ?: ""
            val desc = node.contentDescription?.toString()?.trim() ?: ""

            // Check view ID clues
            for (clue in shortsViewIdSubstrings) {
                if (viewId.contains(clue)) {
                    foundShortsClues++
                    break
                }
            }

            // Check for Shorts distinct action button descriptions
            for (indicator in shortsActionIndicators) {
                if (desc.contains(indicator, ignoreCase = true)) {
                    foundShortsClues += 2
                    break
                }
            }

            // Extract channel name / handle (e.g., @creatorexample)
            if (extractedChannel == null) {
                if (viewId.contains("channel_name") || viewId.contains("author")) {
                    if (text.isNotEmpty()) extractedChannel = text
                    else if (desc.isNotEmpty()) extractedChannel = desc
                } else if (text.startsWith("@") || desc.startsWith("@")) {
                    extractedChannel = if (text.startsWith("@")) text else desc
                }
            }

            // Extract video title
            if (extractedTitle == null) {
                if (viewId.contains("title") && !viewId.contains("channel")) {
                    if (text.isNotEmpty()) extractedTitle = text.take(80)
                    else if (desc.isNotEmpty()) extractedTitle = desc.take(80)
                }
            }

            // Extract sound name
            if (extractedSound == null && (viewId.contains("sound") || desc.contains("sound", ignoreCase = true))) {
                if (text.isNotEmpty()) extractedSound = text
                else if (desc.isNotEmpty()) extractedSound = desc
            }

            // Traverse children
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    traverse(child, depth + 1)
                    child.recycle()
                }
            }
        }

        traverse(root, 0)

        // Determine if current screen is YouTube Shorts
        val isShortsScreen = foundShortsClues >= 2 || (foundShortsClues >= 1 && (extractedChannel != null || extractedTitle != null))

        if (!isShortsScreen) return null

        val composite = buildString {
            append(extractedChannel ?: "unknown_channel")
            append("||")
            append(extractedTitle ?: "unknown_title")
            append("||")
            append(extractedSound ?: "")
        }

        val signature = "yt_${composite.hashCode()}_${extractedChannel ?: ""}"

        return ShortDetectionResult(
            isShort = true,
            signature = signature,
            channel = extractedChannel,
            title = extractedTitle
        )
    }
}

package com.concentrator.app.detector

import android.view.accessibility.AccessibilityNodeInfo

data class ReelDetectionResult(
    val isReel: Boolean,
    val signature: String,
    val author: String?
)

class ReelDetector {

    // Common indicators found inside Instagram Reels viewer
    private val reelsViewIdSubstrings = listOf(
        "clips",
        "reel",
        "row_feed_photo_profile_name",
        "clips_author_name",
        "music_title",
        "video_container"
    )

    private val reelsActionKeywords = listOf(
        "like",
        "comment",
        "share",
        "remix",
        "audio",
        "original audio"
    )

    fun inspect(root: AccessibilityNodeInfo?): ReelDetectionResult? {
        if (root == null) return null

        var foundReelClues = 0
        var extractedAuthor: String? = null
        var extractedAudio: String? = null
        var extractedCaption: String? = null

        fun traverse(node: AccessibilityNodeInfo?, depth: Int) {
            if (node == null || depth > 25) return

            val viewId = node.viewIdResourceName?.lowercase() ?: ""
            val text = node.text?.toString()?.trim() ?: ""
            val desc = node.contentDescription?.toString()?.trim() ?: ""

            // Check view ID clues
            for (clue in reelsViewIdSubstrings) {
                if (viewId.contains(clue)) {
                    foundReelClues++
                    break
                }
            }

            // Extract author name
            if (extractedAuthor == null) {
                if (viewId.contains("clips_author") || viewId.contains("profile_name") || viewId.contains("user_name")) {
                    if (text.isNotEmpty()) extractedAuthor = text
                    else if (desc.isNotEmpty()) extractedAuthor = desc
                } else if (text.startsWith("@") || desc.startsWith("@")) {
                    extractedAuthor = if (text.startsWith("@")) text else desc
                }
            }

            // Extract audio title
            if (extractedAudio == null) {
                if (viewId.contains("music") || viewId.contains("audio") || desc.contains("audio", ignoreCase = true)) {
                    if (text.isNotEmpty()) extractedAudio = text
                    else if (desc.isNotEmpty()) extractedAudio = desc
                }
            }

            // Extract description / caption snippet
            if (extractedCaption == null && (viewId.contains("caption") || viewId.contains("description"))) {
                if (text.isNotEmpty()) extractedCaption = text.take(60)
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

        // If we found enough clues that this is indeed an active Reel screen
        val isReelScreen = foundReelClues >= 2 || (extractedAuthor != null && extractedAudio != null)

        if (!isReelScreen) return null

        // Build a unique content signature for this reel
        val composite = buildString {
            append(extractedAuthor ?: "unknown_author")
            append("||")
            append(extractedAudio ?: "unknown_audio")
            append("||")
            append(extractedCaption ?: "")
        }

        val signature = "ig_${composite.hashCode()}_${extractedAuthor ?: ""}"

        return ReelDetectionResult(
            isReel = true,
            signature = signature,
            author = extractedAuthor
        )
    }
}

package com.concentrator.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core Dark Theme Palette
val BackgroundDark = Color(0xFF090D16)
val SurfaceDark = Color(0xFF111827)
val SurfaceElevatedDark = Color(0xFF1E293B)
val SurfaceBorderDark = Color(0xFF334155)

// Accent Colors
val PrimaryIndigo = Color(0xFF6366F1)
val PrimaryIndigoVariant = Color(0xFF4F46E5)
val AccentCyan = Color(0xFF38BDF8)
val AccentEmerald = Color(0xFF10B981)
val AccentAmber = Color(0xFFF59E0B)
val AccentRose = Color(0xFFF43F5E)

// Platform Specific Accents
val InstagramPurple = Color(0xFF833AB4)
val InstagramPink = Color(0xFFFD1D1D)
val InstagramOrange = Color(0xFFFCB045)
val YouTubeRed = Color(0xFFFF0000)
val YouTubeDarkRed = Color(0xFFCC0000)

// Text Colors
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextTertiary = Color(0xFF64748B)

// Gradients
val InstagramBrush = Brush.horizontalGradient(
    colors = listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045))
)

val YouTubeBrush = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFF0000), Color(0xFFE50914))
)

val IndigoGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
)

val GlassCardGradient = Brush.verticalGradient(
    colors = listOf(Color(0x33334155), Color(0x1A1E293B))
)

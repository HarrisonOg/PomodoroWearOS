package com.harrisonog.simplepomodoro.tile

import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import com.harrisonog.simplepomodoro.service.PomodoroService
import java.util.Locale

object TileLayoutBuilder {

    private const val FOCUS_COLOR = 0xFF6B9BD1.toInt()
    private const val BREAK_COLOR = 0xFF98C9A3.toInt()
    private const val TEXT_COLOR = 0xFFE0E0E0.toInt()

    fun buildLayout(context: Context, state: TileState): LayoutElementBuilders.Layout {
        return LayoutElementBuilders.Layout.Builder()
            .setRoot(
                when (state.status) {
                    "running" -> buildRunningLayout(context, state)
                    "paused" -> buildPausedLayout(context, state)
                    else -> buildIdleLayout(context)
                }
            )
            .build()
    }

    private fun buildIdleLayout(context: Context): LayoutElementBuilders.LayoutElement {
        return PrimaryLayout.Builder(
            androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters.Builder()
                .setScreenWidthDp(192)
                .setScreenHeightDp(192)
                .build()
        )
            .setContent(
                LayoutElementBuilders.Column.Builder()
                    .addContent(
                        Text.Builder(context, "Ready")
                            .setTypography(Typography.TYPOGRAPHY_TITLE2)
                            .setColor(ColorBuilders.argb(TEXT_COLOR))
                            .build()
                    )
                    .addContent(
                        LayoutElementBuilders.Spacer.Builder()
                            .setHeight(DimensionBuilders.dp(8f))
                            .build()
                    )
                    .addContent(
                        Text.Builder(context, "Tap to start")
                            .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                            .setColor(ColorBuilders.argb(TEXT_COLOR))
                            .build()
                    )
                    .addContent(
                        LayoutElementBuilders.Spacer.Builder()
                            .setHeight(DimensionBuilders.dp(16f))
                            .build()
                    )
                    .addContent(
                        createButton(
                            context = context,
                            text = "Start",
                            action = TileActions.ACTION_TILE_START,
                            color = FOCUS_COLOR
                        )
                    )
                    .build()
            )
            .build()
    }

    private fun buildRunningLayout(context: Context, state: TileState): LayoutElementBuilders.LayoutElement {
        val phaseText = if (state.phase == "focus") "Focus" else "Break"
        val timeText = formatTime(state.remainingMillis)
        val phaseColor = if (state.phase == "focus") FOCUS_COLOR else BREAK_COLOR

        return PrimaryLayout.Builder(
            androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters.Builder()
                .setScreenWidthDp(192)
                .setScreenHeightDp(192)
                .build()
        )
            .setContent(
                LayoutElementBuilders.Column.Builder()
                    .addContent(
                        Text.Builder(context, phaseText)
                            .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                            .setColor(ColorBuilders.argb(phaseColor))
                            .build()
                    )
                    .addContent(
                        LayoutElementBuilders.Spacer.Builder()
                            .setHeight(DimensionBuilders.dp(8f))
                            .build()
                    )
                    .addContent(
                        Text.Builder(context, timeText)
                            .setTypography(Typography.TYPOGRAPHY_DISPLAY1)
                            .setColor(ColorBuilders.argb(TEXT_COLOR))
                            .build()
                    )
                    .addContent(
                        LayoutElementBuilders.Spacer.Builder()
                            .setHeight(DimensionBuilders.dp(16f))
                            .build()
                    )
                    .addContent(
                        LayoutElementBuilders.Row.Builder()
                            .addContent(
                                createButton(
                                    context = context,
                                    text = "Pause",
                                    action = TileActions.ACTION_TILE_PAUSE,
                                    color = phaseColor
                                )
                            )
                            .addContent(
                                LayoutElementBuilders.Spacer.Builder()
                                    .setWidth(DimensionBuilders.dp(8f))
                                    .build()
                            )
                            .addContent(
                                createButton(
                                    context = context,
                                    text = "Stop",
                                    action = TileActions.ACTION_TILE_STOP,
                                    color = 0xFFCC0000.toInt()
                                )
                            )
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun buildPausedLayout(context: Context, state: TileState): LayoutElementBuilders.LayoutElement {
        val phaseText = if (state.phase == "focus") "Focus" else "Break"
        val timeText = formatTime(state.remainingMillis)
        val phaseColor = if (state.phase == "focus") FOCUS_COLOR else BREAK_COLOR

        return PrimaryLayout.Builder(
            androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters.Builder()
                .setScreenWidthDp(192)
                .setScreenHeightDp(192)
                .build()
        )
            .setContent(
                LayoutElementBuilders.Column.Builder()
                    .addContent(
                        Text.Builder(context, "$phaseText (Paused)")
                            .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                            .setColor(ColorBuilders.argb(phaseColor))
                            .build()
                    )
                    .addContent(
                        LayoutElementBuilders.Spacer.Builder()
                            .setHeight(DimensionBuilders.dp(8f))
                            .build()
                    )
                    .addContent(
                        Text.Builder(context, timeText)
                            .setTypography(Typography.TYPOGRAPHY_DISPLAY1)
                            .setColor(ColorBuilders.argb(TEXT_COLOR))
                            .build()
                    )
                    .addContent(
                        LayoutElementBuilders.Spacer.Builder()
                            .setHeight(DimensionBuilders.dp(16f))
                            .build()
                    )
                    .addContent(
                        LayoutElementBuilders.Row.Builder()
                            .addContent(
                                createButton(
                                    context = context,
                                    text = "Resume",
                                    action = TileActions.ACTION_TILE_RESUME,
                                    color = phaseColor
                                )
                            )
                            .addContent(
                                LayoutElementBuilders.Spacer.Builder()
                                    .setWidth(DimensionBuilders.dp(8f))
                                    .build()
                            )
                            .addContent(
                                createButton(
                                    context = context,
                                    text = "Stop",
                                    action = TileActions.ACTION_TILE_STOP,
                                    color = 0xFFCC0000.toInt()
                                )
                            )
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun createButton(
        context: Context,
        text: String,
        action: String,
        color: Int
    ): LayoutElementBuilders.LayoutElement {
        return LayoutElementBuilders.Box.Builder()
            .setWidth(DimensionBuilders.wrap())
            .setHeight(DimensionBuilders.wrap())
            .setModifiers(
                ModifiersBuilders.Modifiers.Builder()
                    .setPadding(
                        ModifiersBuilders.Padding.Builder()
                            .setAll(DimensionBuilders.dp(8f))
                            .build()
                    )
                    .setBackground(
                        ModifiersBuilders.Background.Builder()
                            .setColor(ColorBuilders.argb(color))
                            .setCorner(
                                ModifiersBuilders.Corner.Builder()
                                    .setRadius(DimensionBuilders.dp(20f))
                                    .build()
                            )
                            .build()
                    )
                    .setClickable(createClickable(context, action))
                    .setSemantics(
                        ModifiersBuilders.Semantics.Builder()
                            .setContentDescription(text)
                            .build()
                    )
                    .build()
            )
            .addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(text)
                    .setFontStyle(
                        LayoutElementBuilders.FontStyle.Builder()
                            .setColor(ColorBuilders.argb(0xFFFFFFFF.toInt()))
                            .setSize(DimensionBuilders.sp(14f))
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun createClickable(context: Context, action: String): ModifiersBuilders.Clickable {
        val intent = Intent(context, TileActionActivity::class.java).apply {
            putExtra("action", action)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return ModifiersBuilders.Clickable.Builder()
            .setOnClick(
                ActionBuilders.LaunchAction.Builder()
                    .setAndroidActivity(
                        ActionBuilders.AndroidActivity.Builder()
                            .setPackageName(context.packageName)
                            .setClassName(TileActionActivity::class.java.name)
                            .addKeyToExtraMapping("action", ActionBuilders.AndroidStringExtra.Builder()
                                .setValue(action)
                                .build())
                            .build()
                    )
                    .build()
            )
            .setId(action)
            .build()
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}

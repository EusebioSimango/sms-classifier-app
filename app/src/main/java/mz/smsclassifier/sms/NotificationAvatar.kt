package mz.smsclassifier.sms

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.net.Uri

internal fun notificationAvatar(context: Context, photoUri: String?): Bitmap {
    val size = 128
    val base = circlePhoto(context, photoUri, size) ?: personCircle(size)
    return withAppBadge(context, base)
}

private fun circlePhoto(context: Context, photoUri: String?, size: Int): Bitmap? {
    val uri = photoUri?.takeIf { it.isNotBlank() } ?: return null
    val decoded = runCatching {
        context.contentResolver.openInputStream(Uri.parse(uri))?.use { BitmapFactory.decodeStream(it) }
    }.getOrNull() ?: return null
    val scaled = Bitmap.createScaledBitmap(decoded, size, size, true)
    val out = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(out)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = BitmapShader(scaled, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
    return out
}

private fun personCircle(size: Int): Bitmap {
    val out = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(out)
    val background = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFE4E7EC.toInt() }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, background)
    val ink = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF5F6368.toInt() }
    canvas.drawCircle(size / 2f, size * 0.38f, size * 0.16f, ink)
    canvas.drawCircle(size / 2f, size * 0.95f, size * 0.28f, ink)
    return out
}

private fun withAppBadge(context: Context, avatar: Bitmap): Bitmap {
    val size = avatar.width
    val canvas = Canvas(avatar)
    val badge = size / 3
    val cx = size - badge / 2f - size * 0.04f
    val cy = size - badge / 2f - size * 0.04f
    val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.WHITE }
    canvas.drawCircle(cx, cy, badge / 2f + 3f, ring)
    val icon = context.packageManager.getApplicationIcon(context.packageName)
    val left = (cx - badge / 2f).toInt()
    val top = (cy - badge / 2f).toInt()
    canvas.save()
    canvas.clipPath(Path().apply {
        addCircle(cx, cy, badge / 2f, Path.Direction.CW)
    })
    icon.setBounds(left, top, left + badge, top + badge)
    icon.draw(canvas)
    canvas.restore()
    return avatar
}

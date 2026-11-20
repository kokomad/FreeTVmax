package com.khader.freetv

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

object Dev {
    const val NAME = "khader abo salha"
    const val EMAIL = "mankhader@gmail.com"

    fun showAbout(ctx: Context) {
        AlertDialog.Builder(ctx)
            .setTitle("📺 فري تي في")
            .setMessage("المطور: $NAME\nGmail: $EMAIL\n\nالقنوات من قوائم عامة مجانية (iptv-org).")
            .setPositiveButton("مراسلة المطور") { _, _ ->
                val i = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$EMAIL"))
                try { ctx.startActivity(i) } catch (e: Exception) {
                    Toast.makeText(ctx, EMAIL, Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("إغلاق", null)
            .show()
    }
}

object External {
    private fun intent(name: String, url: String) =
        Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(url), "video/*").putExtra("title", name)

    private fun tryPkg(ctx: Context, name: String, url: String, pkg: String): Boolean = try {
        ctx.startActivity(intent(name, url).setPackage(pkg)); true
    } catch (e: ActivityNotFoundException) { false }

    fun mx(ctx: Context, name: String, url: String) {
        if (tryPkg(ctx, name, url, "com.mxtech.videoplayer.pro")) return
        if (tryPkg(ctx, name, url, "com.mxtech.videoplayer.ad")) return
        Toast.makeText(ctx, "MX Player غير مثبت على الجوال", Toast.LENGTH_LONG).show()
    }

    fun vlc(ctx: Context, name: String, url: String) {
        if (!tryPkg(ctx, name, url, "org.videolan.vlc"))
            Toast.makeText(ctx, "VLC غير مثبت على الجوال", Toast.LENGTH_LONG).show()
    }

    fun any(ctx: Context, name: String, url: String) {
        try { ctx.startActivity(Intent.createChooser(intent(name, url), "اختر مشغلاً")) }
        catch (e: Exception) { Toast.makeText(ctx, "لا يوجد مشغل متاح", Toast.LENGTH_LONG).show() }
    }

    fun choose(ctx: Context, name: String, url: String, onInternal: (() -> Unit)?) {
        val labels = ArrayList<String>(); val actions = ArrayList<() -> Unit>()
        if (onInternal != null) { labels.add("▶ تشغيل داخلي"); actions.add(onInternal) }
        labels.add("MX Player"); actions.add { mx(ctx, name, url) }
        labels.add("VLC"); actions.add { vlc(ctx, name, url) }
        labels.add("مشغل آخر..."); actions.add { any(ctx, name, url) }
        AlertDialog.Builder(ctx).setTitle(name)
            .setItems(labels.toTypedArray()) { _, i -> actions[i]() }.show()
    }
}

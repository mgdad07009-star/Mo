package com.android.system.framework.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "تم إعادة تشغيل الجهاز، يتم الآن تشغيل خدمات الحماية...")
            
            // ملاحظة: في أندرويد 10+، لا يمكن تشغيل الأنشطة من الخلفية، 
            // لكن يمكن تشغيل الخدمات الأمامية (Foreground Services).
            // خدمة الوصول (Accessibility) ستعمل تلقائياً إذا كانت مفعلة.
            
            val serviceIntent = Intent(context, MonitoringService::class.java)
            context.startService(serviceIntent)
        }
    }
}

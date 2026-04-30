package com.android.system.framework.core

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import java.util.*

class UsageTracker(private val context: Context) {

    private val TAG = "UsageTracker"

    /**
     * جلب إحصائيات الاستخدام لليوم الحالي
     */
    fun getDailyUsageStats(): List<UsageAppInfo> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTime = calendar.timeInMillis

        // جلب الإحصائيات للفترة المحددة (من بداية اليوم حتى الآن)
        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, endTime
        )

        val appUsageList = mutableListOf<UsageAppInfo>()

        if (usageStatsList != null) {
            for (usageStats in usageStatsList) {
                // نأخذ فقط التطبيقات التي تم استخدامها فعلياً (أكثر من 0 ثانية)
                if (usageStats.totalTimeInForeground > 0) {
                    val appName = getAppNameFromPackage(usageStats.packageName)
                    val timeInMinutes = usageStats.totalTimeInForeground / 1000 / 60
                    
                    appUsageList.add(UsageAppInfo(
                        packageName = usageStats.packageName,
                        appName = appName,
                        totalTimeInMinutes = timeInMinutes,
                        lastTimeUsed = usageStats.lastTimeUsed
                    ))
                    
                    Log.d(TAG, "التطبيق: $appName | الوقت: $timeInMinutes دقيقة")
                }
            }
        }

        // ترتيب القائمة من الأكثر استخداماً إلى الأقل
        return appUsageList.sortedByDescending { it.totalTimeInMinutes }
    }

    private fun getAppNameFromPackage(packageName: String): String {
        return try {
            val packageManager = context.packageManager
            val info = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(info).toString()
        } catch (e: Exception) {
            packageName // في حال لم نتمكن من جلب الاسم، نرجع اسم الحزمة
        }
    }
}

/**
 * نموذج بيانات لمعلومات استخدام التطبيق
 */
data class UsageAppInfo(
    val packageName: String,
    val appName: String,
    val totalTimeInMinutes: Long,
    val lastTimeUsed: Long
)

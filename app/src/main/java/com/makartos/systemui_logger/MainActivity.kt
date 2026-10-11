package com.makartos.systemui_logger

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.makartos.systemui_logger.databinding.ActivityMainBinding
import com.makartos.systemui_logger.logger.SystemUILogger
import com.makartos.systemui_logger.logger.ViewHierarchyAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val logger = SystemUILogger()
    private val analyzer = ViewHierarchyAnalyzer()
    private val logAdapter = LogAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.logsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = logAdapter
        }

        addLog("[INIT] SystemUI Logger started")
        addLog("[INIT] Ready to capture SystemUI elements")
    }

    private fun setupListeners() {
        binding.btnCaptureNotifications.setOnClickListener {
            captureNotifications()
        }

        binding.btnCaptureSystemUI.setOnClickListener {
            captureSystemUI()
        }

        binding.btnCopyLogs.setOnClickListener {
            copyLogsToClipboard()
        }

        binding.btnClearLogs.setOnClickListener {
            clearLogs()
        }

        binding.btnExportLogs.setOnClickListener {
            exportLogs()
        }
    }

    private fun captureNotifications() {
        lifecycleScope.launch(Dispatchers.Default) {
            addLog("[CAPTURE] Starting notification analysis...")

            try {
                val notificationInfo = logger.captureNotificationService(this@MainActivity)
                withContext(Dispatchers.Main) {
                    addLog("[NOTIFICATIONS] Service captured")
                    addLog(notificationInfo)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    addLog("[ERROR] ${e.message}")
                }
            }
        }
    }

    private fun captureSystemUI() {
        lifecycleScope.launch(Dispatchers.Default) {
            addLog("[CAPTURE] Starting SystemUI analysis...")

            try {
                val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val runningApps = activityManager.runningAppProcesses

                var systemUIProcess: ActivityManager.RunningAppProcessInfo? = null
                for (app in runningApps) {
                    if (app.processName.contains("com.android.systemui")) {
                        systemUIProcess = app
                        break
                    }
                }

                withContext(Dispatchers.Main) {
                    if (systemUIProcess != null) {
                        addLog("[SYSTEMUI] Found process: com.android.systemui (PID: ${systemUIProcess.pid})")
                        addLog("[SYSTEMUI] Memory: ${systemUIProcess.totalPss} KB")
                        analyzeSystemUIElements()
                    } else {
                        addLog("[ERROR] SystemUI process not found")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    addLog("[ERROR] ${e.message}")
                }
            }
        }
    }

    private fun analyzeSystemUIElements() {
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                val elements = analyzer.analyzeSystemUIStructure()
                withContext(Dispatchers.Main) {
                    addLog("[STRUCTURE] SystemUI Elements Analysis:")
                    addLog(elements)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    addLog("[ERROR] Analysis failed: ${e.message}")
                }
            }
        }
    }

    private fun copyLogsToClipboard() {
        val logs = logAdapter.getLogs().joinToString("\n")
        val clipboard = android.content.ClipboardManager as? android.content.ClipboardManager
            ?: getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("SystemUI Logs", logs)
        clipboard.setPrimaryClip(clip)
        addLog("[INFO] Logs copied to clipboard")
    }

    private fun clearLogs() {
        logAdapter.clear()
        addLog("[INFO] Logs cleared")
    }

    private fun exportLogs() {
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                val logs = logAdapter.getLogs().joinToString("\n")
                val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
                val fileName = "systemui_logs_$timestamp.txt"
                val file = java.io.File(getExternalFilesDir(null), fileName)
                file.writeText(logs)

                withContext(Dispatchers.Main) {
                    addLog("[INFO] Logs exported to: ${file.absolutePath}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    addLog("[ERROR] Export failed: ${e.message}")
                }
            }
        }
    }

    private fun addLog(message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        val logMessage = "[$timestamp] $message"
        logAdapter.addLog(logMessage)
        binding.logsRecyclerView.smoothScrollToPosition(logAdapter.itemCount - 1)
    }
}

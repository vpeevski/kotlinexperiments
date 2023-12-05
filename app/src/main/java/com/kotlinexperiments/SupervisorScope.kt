package com.kotlinexperiments

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

suspend fun main(): Unit = coroutineScope {
    supervisorScope {
        launch {
            delay(1000)
            throw Error("Some error")
        }
        launch {
            delay(2000)
            println("Will be printed")
        }
    }
    delay(1000)
    println("Done")

    println("##### Use supervisorScope #####")
    notifyAnalytics(listOf("A1", "A2", "A3"))
    println("##### DO NOT DO THIS #####")
    sendNotifications(listOf("A1", "A2", "A3"))
    println("Will be NOT PRINTED !!!")
}

suspend fun notifyAnalytics(actions: List<String>) = supervisorScope {
    actions.forEach { action ->
        launch {
            notifyAnalytics(action)
        }
    }
}

suspend fun notifyAnalytics(action: String) {
    if (action == "A2") throw RuntimeException("Invalid action")
    delayCancelable(1000)
    println(action)
}

// DON'T DO THAT!
// supervisorScope cannot be replaced with withContext(SupervisorJob())
suspend fun sendNotifications(
    notifications: List<String>
) =
    withContext(SupervisorJob()) { // Job is the only context that is not inherited. Here SupervisorJob is a parent of withContext coroutine.
        for (notification in notifications) {
            launch {
                notifyAnalytics(notification)
            }
        }
    }
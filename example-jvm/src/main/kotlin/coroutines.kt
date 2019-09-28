//package com.example
//
//import kotlinx.coroutines.*
//import kotlinx.coroutines.channels.*
//import net.ntworld.foundation.util.UUIDGenerator
//import java.lang.Thread.currentThread
//import kotlin.coroutines.CoroutineContext
//
//open class Message(
//    val body: String,
//    val attributes: Map<String, String>
//)
//
//class QueuedMessage(
//    val id: String,
//    body: String,
//    attributes: Map<String, String>,
//    val deleted: Boolean
//) : Message(body, attributes)
//
//interface Queue {
//    fun sendMessage(message: Message)
//
//    fun receiveMessages(): List<Message>
//
//    fun deleteMessage(message: Message)
//}
//
//class FakeQueue : Queue {
//    val messages = mutableMapOf<String, QueuedMessage>()
//
//    override fun sendMessage(message: Message) {
//        val id = UUIDGenerator.generate()
//        messages[id] = QueuedMessage(
//            id = id,
//            body = message.body,
//            attributes = message.attributes,
//            deleted = false
//        )
//    }
//
//    override fun receiveMessages(): List<Message> {
//        return messages.values.filter {
//            !it.deleted
//        }
//    }
//
//    override fun deleteMessage(message: Message) {
//        println("FakedQueue.deleteMessage $message")
//        if (message is QueuedMessage) {
//            val msg = messages[message.id]
//            if (null !== msg) {
//                val replace = QueuedMessage(
//                    id = msg.id,
//                    body = msg.body,
//                    attributes = msg.attributes,
//                    deleted = true
//                )
//                messages[msg.id] = replace
//                println("FakedQueue.deleteMessage $message success ${msg.id}")
//                println("FakedQueue.deleteMessage $message success ${(messages[msg.id] as QueuedMessage).deleted}")
//            }
//        }
//    }
//}
//
//object Global {
//    val queue = FakeQueue()
//    val replyQueue = FakeQueue()
//}
//
//abstract class QueueManager(private val queue: Queue) : CoroutineScope {
//    private val supervisorJob = SupervisorJob()
//    override val coroutineContext: CoroutineContext
//        get() = Dispatchers.IO + supervisorJob
//
//    abstract suspend fun process(message: Message)
//
//    fun start() = launch {
//        val messageChannel = Channel<Message>()
//        repeat(N_WORKERS) { launchWorker(messageChannel) }
//        launchMsgReceiver(messageChannel)
//        println("QueueManager for $queue has started")
//    }
//
//    fun stop() {
//        supervisorJob.cancel()
//    }
//
//    private fun CoroutineScope.launchMsgReceiver(channel: SendChannel<Message>) = launch {
//        repeatUntilCancelled {
//            val messages = queue.receiveMessages()
//            if (messages.isNotEmpty()) {
//                println("${Thread.currentThread().name} Retrieved ${messages.size} messages")
//
//                messages.forEach {
//                    channel.send(it)
//                }
//            }
//            delay(200)
//        }
//    }
//
//    private fun CoroutineScope.launchWorker(channel: ReceiveChannel<Message>) = launch {
//        repeatUntilCancelled {
//            for (msg in channel) {
//                try {
//                    process(msg)
//                } catch (ex: Exception) {
//                    println("${Thread.currentThread().name} exception trying to process message ${msg.body}")
//                    ex.printStackTrace()
//                }
//            }
//        }
//    }
//
////    private suspend fun processMsg(message: Message) {
////        println("${Thread.currentThread().name} Started processing message: ${message.body}")
////        delay((1000L..2000L).random())
////        println("${Thread.currentThread().name} Finished processing of message: ${message.body}")
////    }
//
//    protected fun deleteMessage(message: Message) {
//        queue.deleteMessage(message)
//        println("${Thread.currentThread().name} Message deleted: ${message}")
//    }
//
//    companion object {
//        private const val N_WORKERS = 5
//    }
//}
//
//suspend fun CoroutineScope.repeatUntilCancelled(block: suspend () -> Unit) {
//    while (isActive) {
//        try {
//            block()
//            yield()
//        } catch (ex: CancellationException) {
//            println("coroutine on ${currentThread().name} cancelled")
//        } catch (ex: Exception) {
//            println("${currentThread().name} failed with {$ex}. Retrying...")
//            ex.printStackTrace()
//        }
//    }
//
//    println("coroutine on ${currentThread().name} exiting")
//}
//
//class Worker(queue: Queue) : QueueManager(queue) {
//    override suspend fun process(message: Message) {
//        println("process message ${message.body}")
//        if (message.attributes.containsKey("relayId")) {
//            println("  reply to ${message.attributes["relayId"]} in 1000ms")
//            deleteMessage(message)
//            println(  "message count: ${Global.queue.messages.size}")
//            delay(1000)
//            Global.replyQueue.sendMessage(
//                Message(
//                    body = "reply content",
//                    attributes = message.attributes
//                )
//            )
//        } else {
//            deleteMessage(message)
//        }
//
//    }
//}
//
//class ReplyWaiter(queue: Queue) : QueueManager(queue) {
//    private val map = mutableMapOf<String, Channel<Message>>()
//
//    fun count(): Int {
//        return map.count()
//    }
//
//    override suspend fun process(message: Message) {
//        val relayId = message.attributes["relayId"]
//        if (null !== relayId) {
//            val listener = map[relayId]
//            if (null !== listener) {
//                listener.send(message)
//                map.remove(relayId)
//                deleteMessage(message)
//            }
//        }
//    }
//
//    fun removeListener(relayId: String) {
//        map.remove(relayId)
//    }
//
//    fun addListener(relayId: String, listener: Channel<Message>) {
//        map[relayId] = listener
//    }
//}
//
//fun main_coroutines() {
//    val waiter = ReplyWaiter(Global.replyQueue)
//    val job = GlobalScope.run {
//        Worker(Global.queue).start()
//        waiter.start()
//    }
//
//    try {
//        val message = client(waiter)
//        println("QueryResult: ${message.body}")
//    } catch (exception: Exception) {
//        println("ERROR: ${waiter.count()}")
//        exception.printStackTrace()
//    }
////    runBlocking {
////        try {
////            launch { client(waiter) }
////            launch { client(waiter) }
////            launch { client(waiter) }
////            launch { client(waiter) }
////            launch { client(waiter) }
////        } catch (exception: Exception) {
////            exception.printStackTrace()
////            throw exception
////        }
////    }
//
//
//    Thread.sleep(10000)
//    job.cancel()
//    println("Done!")
//}
//
//fun client(waiter: ReplyWaiter): Message = runBlocking {
//    println("start client code")
//    val relayId = UUIDGenerator.generate()
//    val message = Message(
//        "please process and replied to $relayId", mapOf(
//            "relayId" to relayId
//        )
//    )
//    val channel = Channel<Message>()
//    waiter.addListener(relayId, channel)
//
//    try {
//        withTimeout(200) {
//            Global.queue.sendMessage(message)
//            channel.receive()
//        }
//    } catch (exception: Exception) {
//        if (exception is TimeoutCancellationException) {
//            waiter.removeListener(relayId)
//        }
//        throw exception
//    }
//}
//
////fun main() = runBlocking {
////    val channel = Channel<Int>()
////    launchProcessor(1, channel)
////
////    channel.send(10)
////    withTimeout(100) {
////        val data = channel.receive()
////        println(data)
////    }
////    channel.cancel()
////    println("Done!")
////}
////
////fun CoroutineScope.launchProcessor(id: Int, channel: Channel<Int>) = launch {
////    for (msg in channel) {
////        println("Processor #$id received $msg")
////        delay(10)
////        channel.send(msg * msg)
////    }
////}
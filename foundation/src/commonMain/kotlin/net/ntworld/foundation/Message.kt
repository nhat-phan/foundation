package net.ntworld.foundation

interface Message {
    val id: String?
    val type: String?
    val body: String
    val attributes: Map<String, String>
}

// CommandBus
//   1. create a queue which receive response (if needed)
//   2. convert command to message
//   3. produce a message and send to a queue
//   3a. worker will consume the message and send signal back via response queue
//   3b. wait for response of message in the queue
//   4a. send response back to sender
//   4b. trigger timeout error if needed
// QueryBus
//   1. create a queue which receive response (if needed)
//   2. convert query to message
//   3. produce a message and send to a queue
//   3a. worker will consume the message and send signal back via response queue
//   3b. wait for response of message in the queue
//   4a. send response back to sender
//   4b. trigger timeout error if needed
// EventBus
//   1. create a topic (if needed)
//   2. convert event to message
//   3. send message to a topic
//   4. subscriber will consume the message
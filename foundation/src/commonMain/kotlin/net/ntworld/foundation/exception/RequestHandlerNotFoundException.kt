package net.ntworld.foundation.exception

class RequestHandlerNotFoundException(query: String) : Exception("RequestHandler for $query not found")
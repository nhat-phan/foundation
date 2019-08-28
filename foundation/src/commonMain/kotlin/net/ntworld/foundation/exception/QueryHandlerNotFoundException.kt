package net.ntworld.foundation.exception

class QueryHandlerNotFoundException(query: String) : Exception("QueryHandlerDsl for $query not found")
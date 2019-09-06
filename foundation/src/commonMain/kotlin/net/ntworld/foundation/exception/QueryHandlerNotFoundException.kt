package net.ntworld.foundation.exception

class QueryHandlerNotFoundException(query: String) : Exception("QueryHandler for $query not found")
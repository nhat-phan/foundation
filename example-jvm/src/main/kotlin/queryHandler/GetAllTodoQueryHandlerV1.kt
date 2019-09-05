package queryHandler

import com.example.contract.GetAllTodoQuery
import com.example.contract.GetAllTodoQueryResult
import net.ntworld.foundation.Handler
import net.ntworld.foundation.cqrs.QueryHandler

@Handler(version = 1)
class GetAllTodoQueryHandlerV1 : QueryHandler<GetAllTodoQuery, GetAllTodoQueryResult> {
    override fun handle(query: GetAllTodoQuery): GetAllTodoQueryResult {
        throw Exception()
    }
}
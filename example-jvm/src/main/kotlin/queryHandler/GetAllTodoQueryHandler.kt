package queryHandler

import com.example.contract.GetAllTodoQuery
import com.example.contract.GetAllTodoQueryResult
import net.ntworld.foundation.Handler
import net.ntworld.foundation.cqrs.QueryHandler

@Handler
class GetAllTodoQueryHandler : QueryHandler<GetAllTodoQuery, GetAllTodoQueryResult> {
    override fun handle(query: GetAllTodoQuery): GetAllTodoQueryResult {
        throw Exception()
    }
}
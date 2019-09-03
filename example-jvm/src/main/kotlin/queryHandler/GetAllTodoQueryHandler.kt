package queryHandler

import com.example.contract.GetAllTodoQuery
import net.ntworld.foundation.Handler
import net.ntworld.foundation.cqrs.QueryHandler

@Handler
class GetAllTodoQueryHandler : QueryHandler<GetAllTodoQuery, List<String>> {
    override fun handle(query: GetAllTodoQuery): List<String> {
        return listOf()
    }
}
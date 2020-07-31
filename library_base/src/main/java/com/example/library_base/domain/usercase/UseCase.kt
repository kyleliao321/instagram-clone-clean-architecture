package com.example.library_base.domain.usercase

import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.*

abstract class UseCase<out Type, in Params> {

    /**
     * To use UseCase, the client must provide how the implemented UseCase execute.
     */
    abstract suspend fun run(params: Params): Either<Type, Failure>

    /**
     * By overriding the invoke function, the client can simply call UseCase(params) { ... }
     *
     * Since the UseCase is the bridge between ViewModel and Data Repository, by design,
     * it always involve data manipulation. So we can launch the callback in IO thread.
     */
    suspend operator fun invoke(params: Params, onResult: (Either<Type, Failure>) -> Unit) =
        withContext(Dispatchers.IO) {
            onResult(run(params))
        }
}
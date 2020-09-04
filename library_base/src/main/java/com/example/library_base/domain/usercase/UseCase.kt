/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.library_base.domain.usercase

import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import kotlinx.coroutines.*

/**
 * A abstract class that can be extends to define a use case to run in provided thread.
 *
 * @param Type Return type when extended use case invoke successfully.
 * @param Params Parameters that will be used in side member run function.
 *
 * @property defaultDispatcher The thread that the use case will be running in.
 */
abstract class UseCase<out Type, in Params>(
    var defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * To use UseCase, the client must provide how the implemented UseCase execute.
     */
    abstract suspend fun run(params: Params): Either<Type, Failure>

    /**
     * By overriding the invoke function, the client can simply call UseCase(params) { ... }
     *
     * @param params Parameters that should be re-directed to its own run member function.
     * @param onResult Callback function that will be called when use case complete.
     */
    suspend operator fun invoke(params: Params, onResult: (Either<Type, Failure>) -> Unit = {}) {
        val job = withContext(defaultDispatcher) { return@withContext run(params) }
        onResult(job)
    }
}
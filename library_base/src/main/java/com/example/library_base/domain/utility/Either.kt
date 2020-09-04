/**
 * Copyright (C) 2019 Fernando Cejas Open Source Project
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

package com.example.library_base.domain.utility

/**
 * Helper class to wrap asynchronize operation's result.
 *
 * @param SuccessParam Return type when it's use to indicate operation return successfully.
 * @param FailureParam Return type when it's use to indicate operation return with failure.
 */
sealed class Either<out SuccessParam, out FailureParam> {

    data class Success<out SuccessParam>(val a: SuccessParam): Either<SuccessParam, Nothing>()

    data class Failure<out FailureParam>(val b: FailureParam): Either<Nothing, FailureParam>()

    val isSuccess: Boolean get() = this is Success

    val isFailure: Boolean get() = this is Failure

    fun fold(onSucceed: (SuccessParam) -> Unit = {}, onFail: (FailureParam) -> Unit = {}): Any =
        when (this) {
            is Success -> onSucceed(a)
            is Failure -> onFail(b)
        }
}
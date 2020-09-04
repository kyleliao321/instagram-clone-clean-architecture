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

package com.example.library_base.domain.exception

sealed class Failure {
    /**
     * Indicate that the failure is due to network connection.
     *
     * It can be used in the following scenario:
     *      1. Fail to connect to server.
     */
    object NetworkConnection: Failure()

    /**
     * Indicate that the failure is caused by the invalid action,
     * which fail on server side.
     *
     * It can be used in the following scenario:
     *      1. Server responded with unexpected value.
     *      2. Server request is invalid.
     */
    object ServerError: Failure()

    /**
     * Indicate that the failure is caused by application has no login-user
     */
    object LocalAccountNotFound: Failure()

    /**
     * Indicate that the failure is caused by duplicated user name when register
     */
    object DuplicatedUserName: Failure()

    /**
     * Indicate that the failure is caused by incorrect user name or password
     */
    object LoginUserNameOrPasswordNotMatched: Failure()

    object CameraServiceFail: Failure()

    object PhotoGalleryServiceFail: Failure()

    object PostNotComplete: Failure()

    object CacheNotFound: Failure()

    object ExternalImageDecodeFail: Failure()

}
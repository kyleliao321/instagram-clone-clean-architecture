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

}
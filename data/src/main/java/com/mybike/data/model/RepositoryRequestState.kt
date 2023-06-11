package com.mybike.data.model

data class RepositoryRequestState<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T?): RepositoryRequestState<T> {
            return RepositoryRequestState(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String): RepositoryRequestState<T> {
            return RepositoryRequestState(Status.ERROR, null, msg)
        }

        fun <T> loading(): RepositoryRequestState<T> {
            return RepositoryRequestState(Status.LOADING, null, null)
        }
    }

}

enum class Status { SUCCESS, ERROR, LOADING, PAUSED }

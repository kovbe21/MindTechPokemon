/*
 * Copyright (C) 2017 The Android Open Source Project
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

package hu.ppke.itk.mindtechpokemon.basic

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED;

    val isCompleted get() = this == SUCCESS || this == FAILED
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: Status,
    val msg: String? = null,
    val progress: Long? = null,
    val total: Long? = null,
    val responseCode: Int? = null
) {
    companion object {
        val LOADED =
            NetworkState(Status.SUCCESS)
        val LOADING =
            NetworkState(Status.RUNNING)
        fun error(msg: String?, code: Int? = null) =
            NetworkState(
                Status.FAILED,
                msg,
                responseCode = code
            )
        fun running(progress: Long, total: Long, msg: String? = null) =
            NetworkState(
                Status.RUNNING,
                msg,
                progress,
                total
            )
        fun loaded(total: Int) = NetworkState(
            Status.SUCCESS,
            total = total.toLong()
        )
    }
}

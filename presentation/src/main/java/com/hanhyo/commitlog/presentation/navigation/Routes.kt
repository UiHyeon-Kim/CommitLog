package com.hanhyo.commitlog.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface BottomRoute

@Serializable
data object MainRoute

@Serializable
data object HomeRoute : BottomRoute

@Serializable
data object StatisticsRoute : BottomRoute

@Serializable
data object ReviewRoute : BottomRoute

@Serializable
data class WriteRoute(val commitId: Long? = null)

@Serializable
data class DetailRoute(val commitId: Long)

@Serializable
data object SearchRoute

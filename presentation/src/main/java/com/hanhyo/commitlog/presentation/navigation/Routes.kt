package com.hanhyo.commitlog.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface BottomRoute

@Serializable
data object SplashRoute

@Serializable
data object MainRoute

@Serializable
data object HomeRoute : BottomRoute

@Serializable
data object StatisticsRoute : BottomRoute

@Serializable
data object ReviewRoute : BottomRoute

@Serializable
data object WriteRoute

@Serializable
data object DetailRoute

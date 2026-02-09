package com.hanhyo.commitlog.presentation.common.extension

import androidx.navigation.NavDestination
import kotlin.reflect.KClass

fun NavDestination.isRoute(routeClass: KClass<*>): Boolean =
    route?.split("?")?.firstOrNull() == routeClass.qualifiedName

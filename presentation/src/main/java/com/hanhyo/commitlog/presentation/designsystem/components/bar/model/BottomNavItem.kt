package com.hanhyo.commitlog.presentation.designsystem.components.bar.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hanhyo.commitlog.presentation.R
import com.hanhyo.commitlog.presentation.navigation.BottomRoute
import com.hanhyo.commitlog.presentation.navigation.HomeRoute
import com.hanhyo.commitlog.presentation.navigation.ReviewRoute
import com.hanhyo.commitlog.presentation.navigation.StatisticsRoute
import kotlin.reflect.KClass

enum class BottomNavItem(
    val tabRoute: BottomRoute,
    val tabRouteClass: KClass<*>,
    @get:StringRes val labelResId: Int,
    @get:DrawableRes val iconResId: Int,
) {
    HOME(
        tabRoute = HomeRoute,
        tabRouteClass = HomeRoute::class,
        labelResId = R.string.bottom_nav_home,
        iconResId = R.drawable.ic_home,
    ),
    STATS(
        tabRoute = StatisticsRoute,
        tabRouteClass = StatisticsRoute::class,
        labelResId = R.string.bottom_nav_stats,
        iconResId = R.drawable.ic_bar_chart,
    ),
    REVIEW(
        tabRoute = ReviewRoute,
        tabRouteClass = ReviewRoute::class,
        labelResId = R.string.bottom_nav_review,
        iconResId = R.drawable.ic_review
    )
}

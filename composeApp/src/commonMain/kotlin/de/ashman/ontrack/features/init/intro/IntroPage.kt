package de.ashman.ontrack.features.init.intro

import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.intro_first_page_description
import ontrack.composeapp.generated.resources.intro_first_page_title
import ontrack.composeapp.generated.resources.intro_second_page_description
import ontrack.composeapp.generated.resources.intro_second_page_title
import ontrack.composeapp.generated.resources.intro_third_page_description
import ontrack.composeapp.generated.resources.intro_third_page_title
import ontrack.composeapp.generated.resources.on_track_icon_v1
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class IntroPage(
    val title: StringResource,
    val description: StringResource,
    val image: DrawableResource,
) {
    FIRST_PAGE(
        title = Res.string.intro_first_page_title,
        description = Res.string.intro_first_page_description,
        image = Res.drawable.on_track_icon_v1,
    ),
    SECOND_PAGE(
        title = Res.string.intro_second_page_title,
        description = Res.string.intro_second_page_description,
        image = Res.drawable.on_track_icon_v1,
    ),
    THIRD_PAGE(
        title = Res.string.intro_third_page_title,
        description = Res.string.intro_third_page_description,
        image = Res.drawable.on_track_icon_v1,
    ),
}
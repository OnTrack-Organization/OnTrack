package de.ashman.ontrack.features.init.intro

import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.first_dark
import ontrack.composeapp.generated.resources.first_light
import ontrack.composeapp.generated.resources.fourth_dark
import ontrack.composeapp.generated.resources.fourth_light
import ontrack.composeapp.generated.resources.intro_first_page_description
import ontrack.composeapp.generated.resources.intro_first_page_title
import ontrack.composeapp.generated.resources.intro_fourth_page_description
import ontrack.composeapp.generated.resources.intro_fourth_page_title
import ontrack.composeapp.generated.resources.intro_second_page_description
import ontrack.composeapp.generated.resources.intro_second_page_title
import ontrack.composeapp.generated.resources.intro_third_page_description
import ontrack.composeapp.generated.resources.intro_third_page_title
import ontrack.composeapp.generated.resources.second_dark
import ontrack.composeapp.generated.resources.second_light
import ontrack.composeapp.generated.resources.third_dark
import ontrack.composeapp.generated.resources.third_light
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class IntroPage(
    val title: StringResource,
    val description: StringResource,
    val imageLight: DrawableResource,
    val imageDark: DrawableResource,
) {
    FIRST_PAGE(
        title = Res.string.intro_first_page_title,
        description = Res.string.intro_first_page_description,
        imageLight = Res.drawable.first_light,
        imageDark = Res.drawable.first_dark,
    ),
    SECOND_PAGE(
        title = Res.string.intro_second_page_title,
        description = Res.string.intro_second_page_description,
        imageLight = Res.drawable.second_light,
        imageDark = Res.drawable.second_dark,
    ),
    THIRD_PAGE(
        title = Res.string.intro_third_page_title,
        description = Res.string.intro_third_page_description,
        imageDark = Res.drawable.third_dark,
        imageLight = Res.drawable.third_light,
    ),
    FOURTH_PAGE(
        title = Res.string.intro_fourth_page_title,
        description = Res.string.intro_fourth_page_description,
        imageLight = Res.drawable.fourth_light,
        imageDark = Res.drawable.fourth_dark,
    ),
}
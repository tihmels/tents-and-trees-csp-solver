package de.tihmels.ui

import io.kvision.core.*
import io.kvision.html.*
import io.kvision.panel.VPanel
import io.kvision.panel.flexPanel
import io.kvision.panel.vPanel
import io.kvision.utils.px


fun Container.sidebar(init: VPanel.() -> Unit) {

    vPanel(spacing = 0) {
        maxWidth = 270.px
        minWidth = 210.px
        init.invoke(this)
    }

}

fun Container.sidebarCard(title: String, collapsible: Boolean = false, init: Div.() -> Unit) {

    val allowedChars = ('A'..'Z') + ('a'..'z')
    val random = (1..10)
        .map { allowedChars.random() }
        .joinToString("")

    div(classes = setOf("card", "my-2")) {
        div(className = "card-header") {
            content = title

            if (collapsible) {
                button(
                    text = "",
                    icon = "fas fa-caret-down",
                    style = ButtonStyle.OUTLINESECONDARY,
                    classes = setOf("btn-sm", "shadow-none")
                ) {

                    setAttribute("data-toggle", "collapse")
                    setAttribute("data-target", "#${random}")

                    border = Border(1.px, BorderStyle.HIDDEN, Color("black"))

                    float = PosFloat.RIGHT
                }
            }
        }
        div(classes = setOf("card-body", "test")) {

            if (collapsible) {
                id = random
                addCssClass("collapse")
                addCssClass("show")
            }

            init.invoke(this)
        }
    }
}

fun Container.flexBetween(label: String, init: Span.() -> Unit): Container =
    flexPanel(justify = JustifyContent.SPACEBETWEEN, alignItems = AlignItems.CENTER) {
        span(label, classes = setOf("label", "label-default"))
        span(classes = setOf("badge", "badge-pill", "badge-primary")) {
            init.invoke(this)
        }
    }
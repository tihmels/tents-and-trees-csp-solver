package de.tihmels.ui

import io.kvision.core.*
import io.kvision.html.*
import io.kvision.panel.VPanel
import io.kvision.panel.flexPanel
import io.kvision.panel.vPanel
import io.kvision.utils.px


fun Container.sidebar(init: VPanel.() -> Unit) {

    vPanel(spacing = 0) {
        width = 280.px
        init.invoke(this)
    }

}

fun Container.sidebarCard(title: String, collapsible: Boolean = false, init: Div.() -> Unit) {

    val allowedChars = ('A'..'Z') + ('a'..'z')
    val randomId = (1..5)
        .map { allowedChars.random() }
        .joinToString("")

    div(className = "card my-2") {

        div(className = "card-header") {
            content = title

            if (collapsible) {
                button(
                    text = "",
                    icon = "fas fa-caret-down",
                    style = ButtonStyle.OUTLINESECONDARY,
                    className = "btn-sm fa-xs shadow-none"
                ) {

                    setAttribute("data-bs-toggle", "collapse")
                    setAttribute("data-bs-target", "#${randomId}")

                    border = Border(1.px, BorderStyle.HIDDEN, Color("black"))
                    float = PosFloat.RIGHT
                }
            }
        }
        div(className = "card-body") {

            if (collapsible) {
                id = randomId
                addCssClass("collapse")
                addCssClass("show")
            }

            init.invoke(this)
        }
    }
}

fun Container.flexBetween(label: String, init: Span.() -> Unit): Container =
    flexPanel(justify = JustifyContent.SPACEBETWEEN, alignItems = AlignItems.CENTER) {
        span(label, className = "label label-default")
        span(className = "badge badge-pill badge-primary") {
            init.invoke(this)
        }
    }
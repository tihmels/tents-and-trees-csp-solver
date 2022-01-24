package de.tihmels

import de.tihmels.ui.*
import io.kvision.*
import io.kvision.core.*
import io.kvision.html.*
import io.kvision.panel.flexPanel
import io.kvision.panel.gridPanel
import io.kvision.panel.root
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.utils.perc
import io.kvision.utils.vh

class App : Application() {

    init {
        require("css/kvapp.css")
    }

    override fun start() {

        AppService.connectToServer()

        root("kvapp") {
            div(className = "container") {

                bind(AppService.connectionState) {

                    header(it == ConnectionState.CONNECTED)

                    when (it) {
                        ConnectionState.ESTABLISHING -> establishingView()
                        ConnectionState.DISCONNECTED -> disconnectedView()
                        ConnectionState.CONNECTED -> appView()
                    }
                }
            }
        }
    }

    private fun Container.header(connected: Boolean) {
        div(className = "my-3") {
            flexPanel(alignItems = AlignItems.CENTER, justify = JustifyContent.SPACEBETWEEN) {
                h1("Tents and Trees CSP Solver")
                if (connected) {
                    button("New Puzzle") {
                        onClick {
                            AppService.getPuzzle()
                        }
                    }
                }
            }

            div(className = "wrapper") {
                div(className = "custom-progress-bar") {
                    span(className = "custom-progress-bar-fill") {
                        style {
                            val assignments = ClientStateService.puzzleState.sub { it.assignment }
                            bind(assignments) {
                                val progressInPercent = ClientStateService.getCurrentProgressInPercent()
                                width = progressInPercent.perc

                                if (progressInPercent == 100F) {
                                    addCssClass("color-change-2x")
                                } else {
                                    removeCssClass("color-change-2x")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun Container.appView() {

        AppService.getPuzzle()
        AppService.getConfigurationData()

        gridPanel(
            templateColumns = "minmax(0, 1fr) auto",
            columnGap = 25,
            templateRows = "auto",
        ) {

            val puzzleStore = ClientStateService.puzzleState.sub { it.puzzle }

            div().bind(puzzleStore) {
                tentGrid(it)
            }

            sidebar {
                div().bind(puzzleStore) {
                    tentData(it)
                }
                div().bind(ConfigurationService.configurationData) {
                    settings(it)
                }
                div {
                    actions()
                }
            }
        }
    }

    private fun Container.establishingView() {

        flexPanel(
            justify = JustifyContent.CENTER,
            alignContent = AlignContent.CENTER,
            alignItems = AlignItems.CENTER,
            direction = FlexDirection.COLUMN
        ) {
            minHeight = 70.vh
            div(className = "loader")
            h5("Establishing Connection ...")
        }

    }

    private fun Container.disconnectedView() {
        flexPanel(
            justify = JustifyContent.CENTER,
            alignContent = AlignContent.CENTER,
            alignItems = AlignItems.CENTER,
            direction = FlexDirection.COLUMN
        ) {
            minHeight = 70.vh
            h5("Disconnected")
        }
    }
}

fun main() {
    startApplication(::App, module.hot, CoreModule, BootstrapModule, BootstrapCssModule, BootstrapSelectModule, FontAwesomeModule)
}

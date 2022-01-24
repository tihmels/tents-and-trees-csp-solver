package de.tihmels.ui

import de.tihmels.CSPStatistics
import de.tihmels.PuzzleStateService
import de.tihmels.TentsAndTrees
import io.kvision.core.*
import io.kvision.html.div
import io.kvision.panel.flexPanel
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.utils.perc

fun Container.tentData(puzzle: TentsAndTrees?) {

    val statisticsHistory = mutableListOf<CSPStatistics>()

    puzzle?.let {

        sidebarCard("General", true) {

            flexBetween("Dimensions") {
                content = "${it.rows} x ${it.cols}"
            }

            flexBetween("Trees") {
                content = it.trees.count().toString()
            }

            flexBetween("Variables") {
                content = it.variables.count().toString()
            }

            val statisticsStore = PuzzleStateService.puzzleState.sub { it.statistics }

            div().bind(statisticsStore) {

                if (it.solved) statisticsHistory.add(it)

                flexPanel(
                    direction = FlexDirection.ROW,
                    justify = JustifyContent.FLEXSTART,
                    alignItems = AlignItems.CENTER,
                    wrap = FlexWrap.NOWRAP
                ) {

                    div("Steps", className = "label label-default") {
                        addCssStyle(Style { flexBasis = 40.perc })
                    }

                    flexPanel(
                        direction = FlexDirection.ROW,
                        justify = JustifyContent.FLEXEND,
                        wrap = FlexWrap.NOWRAP,
                        alignItems = AlignItems.CENTER,
                        spacing = 3
                    ) {

                        overflow = Overflow.HIDDEN
                        addCssStyle(Style { flexGrow = 1 })

                        for (statistic in statisticsHistory) {
                            div(className = "badge badge-pill badge-info") {
                                content = statistic.totalSteps.toString()
                            }
                        }

                        if (!it.solved) {
                            div(className = "badge badge-pill badge-info") {
                                content = it.totalSteps.toString()
                            }
                        }

                    }

                }

                flexPanel(
                    direction = FlexDirection.ROW,
                    justify = JustifyContent.FLEXEND,
                    wrap = FlexWrap.NOWRAP,
                    alignItems = AlignItems.CENTER,
                ) {
                    div("Errors", className = "label label-default") {
                        addCssStyle(Style { flexBasis = 40.perc })
                    }

                    flexPanel(
                        direction = FlexDirection.ROW,
                        justify = JustifyContent.FLEXEND,
                        wrap = FlexWrap.NOWRAP,
                        alignItems = AlignItems.CENTER,
                        spacing = 3
                    ) {

                        overflow = Overflow.HIDDEN
                        addCssStyle(Style { flexGrow = 1 })

                        for (statistic in statisticsHistory) {
                            div(className = "badge badge-pill badge-info") {
                                content = statistic.totalErrors.toString()
                            }
                        }

                        if (!it.solved) {
                            div(className = "badge badge-pill badge-info") {
                                content = it.totalErrors.toString()
                            }
                        }
                    }
                }

                flexPanel(
                    direction = FlexDirection.ROW,
                    justify = JustifyContent.FLEXEND,
                    wrap = FlexWrap.NOWRAP,
                    alignItems = AlignItems.CENTER,
                ) {
                    div("Dead-Ends", className = "label label-default") {
                        addCssStyle(Style { flexBasis = 40.perc })
                    }

                    flexPanel(
                        direction = FlexDirection.ROW,
                        justify = JustifyContent.FLEXEND,
                        wrap = FlexWrap.NOWRAP,
                        alignItems = AlignItems.CENTER,
                        spacing = 3
                    ) {

                        overflow = Overflow.HIDDEN
                        addCssStyle(Style { flexGrow = 1 })

                        for (statistic in statisticsHistory) {
                            div(className = "badge badge-pill badge-info") {
                                content = statistic.deadEnds.toString()
                            }
                        }

                        if (!it.solved) {
                            div(className = "badge badge-pill badge-info") {
                                content = it.deadEnds.toString()
                            }
                        }
                    }
                }

            }


        }
    }

}

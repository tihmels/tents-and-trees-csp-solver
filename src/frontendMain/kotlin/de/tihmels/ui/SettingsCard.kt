package de.tihmels.ui

import de.tihmels.Configuration
import de.tihmels.ConfigurationData
import de.tihmels.ConfigurationService
import io.kvision.core.Container
import io.kvision.core.StringPair
import io.kvision.form.formPanel
import io.kvision.form.range.Range
import io.kvision.form.select.SimpleSelect

fun Container.settings(cData: ConfigurationData) {

    sidebarCard("Settings", true) {

        var isActive = false

        ConfigurationService.settingsForm = formPanel<Configuration> {

            val preProcessingStrategies = cData.preProcessingStrategies.map { StringPair(it.key.toString(), it.value) }
            add(
                Configuration::preProcessingStrategy,
                SimpleSelect(
                    options = preProcessingStrategies,
                    label = "Preprocessing Strategy"
                )
            )

            val variableSelectionStrategies =
                cData.variableSelectionStrategies.map { StringPair(it.key.toString(), it.value) }
            add(
                Configuration::variableSelectionHeuristic,
                SimpleSelect(options = variableSelectionStrategies, label = "Variable Selection Heuristic")
            )

            val domainSelectionStrategies =
                cData.domainSelectionStrategies.map { StringPair(it.key.toString(), it.value) }
            add(
                Configuration::domainSelectionHeuristic,
                SimpleSelect(options = domainSelectionStrategies, label = "Value Selection Heuristic")
            )

            val constraintPropagationStrategies =
                cData.constraintPropagationStrategies.map { StringPair(it.key.toString(), it.value) }
            add(
                Configuration::constraintPropagationStrategy,
                SimpleSelect(options = constraintPropagationStrategies, label = "Constraint Propagation")
            )

            val (min, max) = cData.speedRange ?: Pair(1, 10)
            add(
                Configuration::backtrackingSpeed,
                Range(min = min, max = max, label = "Speed", step = 1).apply {
                    subscribe {
                        if (it != null && isActive) {
                            ConfigurationService.updateConfiguration()
                        }
                    }
                }
            )

        }.apply {
            setData(ConfigurationService.cspConfiguration)
            isActive = true
        }
    }

}
package de.tihmels.ui

import de.tihmels.Configuration
import de.tihmels.ConfigurationData
import de.tihmels.ConfigurationService
import io.kvision.core.Container
import io.kvision.core.StringPair
import io.kvision.form.formPanel
import io.kvision.form.range.Range
import io.kvision.form.select.Select

fun Container.settings(configurationData: ConfigurationData) {

    sidebarCard("Settings", true) {

        var isActive = false

        ConfigurationService.settingsForm = formPanel<Configuration> {

            val preProcessingStrategies =
                configurationData.preProcessingStrategies.map { StringPair(it.key.toString(), it.value) }
            add(
                Configuration::preProcessingStrategy,
                Select(options = preProcessingStrategies, label = "Preprocessing").apply {
                    subscribe {
                        if (!it.isNullOrBlank() && isActive) {
                            ConfigurationService.updateConfiguration()
                        }
                    }
                })

            val varStrategies =
                configurationData.variableSelectionStrategies.map { StringPair(it.key.toString(), it.value) }
            add(
                Configuration::variableSelectionHeuristic,
                Select(options = varStrategies, label = "Variable Selection Heuristic").apply {
                    subscribe {
                        if (!it.isNullOrBlank() && isActive) {
                            ConfigurationService.updateConfiguration()
                        }
                    }
                }
            )

            val domainStrategies =
                configurationData.domainSelectionStrategies.map { StringPair(it.key.toString(), it.value) }
            add(
                Configuration::domainSelectionHeuristic,
                Select(options = domainStrategies, label = "Domain Selection Heuristic").apply {
                    subscribe {
                        if (!it.isNullOrBlank() && isActive) {
                            ConfigurationService.updateConfiguration()
                        }
                    }
                })

            val constraintPropagation =
                configurationData.constraintPropagationStrategies.map { StringPair(it.key.toString(), it.value) }
            add(
                Configuration::constraintPropagationStrategy,
                Select(options = constraintPropagation, label = "Constraint Propagation").apply {
                    subscribe {
                        if (!it.isNullOrBlank() && isActive) {
                            ConfigurationService.updateConfiguration()
                        }
                    }
                }
            )

            val (min, max) = configurationData.speedRange ?: Pair(1, 10)
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
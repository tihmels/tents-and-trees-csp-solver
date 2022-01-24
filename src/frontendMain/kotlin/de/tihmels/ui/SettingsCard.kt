package de.tihmels.ui

import de.tihmels.Configuration
import de.tihmels.ConfigurationData
import de.tihmels.ConfigurationService
import io.kvision.core.Container
import io.kvision.core.StringPair
import io.kvision.form.formPanel
import io.kvision.form.range.Range
import io.kvision.form.select.select
import io.kvision.form.select.simpleSelect


fun Container.settings(configurationData: ConfigurationData) {

    sidebarCard("Settings", true) {

        var isActive = false

        ConfigurationService.settingsForm = formPanel<Configuration> {

            val preProcessingStrategies =
                configurationData.preProcessingStrategies.map { StringPair(it.key.toString(), it.value) }
            add(
                Configuration::preProcessingStrategy,
                simpleSelect(
                    options = preProcessingStrategies,
                    label = "Preprocessing Strategy"
                ).apply {
                    subscribe {
                        if (!it.isNullOrBlank() && isActive) {
                            ConfigurationService.updateConfiguration()
                        }
                    }
                })

            val variableSelectionStrategies =
                configurationData.variableSelectionStrategies.map { StringPair(it.key.toString(), it.value) }
            add(
                Configuration::variableSelectionHeuristic,
                simpleSelect(options = variableSelectionStrategies, label = "Variable Selection Heuristic").apply {
                    subscribe {
                        if (!it.isNullOrBlank() && isActive) {
                            ConfigurationService.updateConfiguration()
                        }
                    }
                }
            )

            val domainSelectionStrategies =
                configurationData.domainSelectionStrategies.map { StringPair(it.key.toString(), it.value) }
            add(
                Configuration::domainSelectionHeuristic,
                simpleSelect(options = domainSelectionStrategies, label = "Value Selection Heuristic").apply {
                    subscribe {
                        if (!it.isNullOrBlank() && isActive) {
                            ConfigurationService.updateConfiguration()
                        }
                    }
                })

            val constraintPropagationStrategies =
                configurationData.constraintPropagationStrategies.map { StringPair(it.key.toString(), it.value) }
            add(
                Configuration::constraintPropagationStrategy,
                simpleSelect(options = constraintPropagationStrategies, label = "Constraint Propagation").apply {
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
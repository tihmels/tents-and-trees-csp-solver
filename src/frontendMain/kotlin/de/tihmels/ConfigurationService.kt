package de.tihmels

import io.kvision.form.FormPanel
import io.kvision.state.ObservableValue

object ConfigurationService {

    val configurationData = ObservableValue(ConfigurationData())

    var settingsForm: FormPanel<Configuration> = FormPanel()
    var cspConfiguration = Configuration()

    fun setConfigurationData(cData: ConfigurationData) {
        cspConfiguration = cData.getDefault()

        settingsForm.setData(cspConfiguration)

        configurationData.value = cData
    }

    fun updateConfiguration() {

        cspConfiguration = settingsForm.getData()

        AppService.websocketHandler.send(
            CMessage(
                CMessageType.SetConfiguration(cspConfiguration)
            )
        )
    }
}

fun ConfigurationData.getDefault(): Configuration {

    val preProcessingDefault = preProcessingStrategies.keys.first().toString()
    val variableSelectionDefault = variableSelectionStrategies.keys.first().toString()
    val domainSelectionDefault = domainSelectionStrategies.keys.first().toString()
    val constraintPropagationDefault = constraintPropagationStrategies.keys.first().toString()
    val speedDefault = speedRange?.first

    return Configuration(
        preProcessingDefault,
        variableSelectionDefault,
        domainSelectionDefault,
        constraintPropagationDefault,
        speedDefault
    )
}
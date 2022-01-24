package de.tihmels.csp

import de.tihmels.*
import de.tihmels.csp.constraint.Constraint
import de.tihmels.csp.heuristic.value.ISelectDomainHeuristic
import de.tihmels.csp.heuristic.variable.ISelectVariableHeuristic
import de.tihmels.csp.preprocessor.IPreProcessor
import de.tihmels.csp.propagation.IConstraintPropagation
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class CSP(
    private val variables: List<Location>,
    private val domains: Map<Location, MutableList<Domain>>,
    configuration: CSPConfiguration
) {

    private val constraints: MutableMap<Location, MutableList<Constraint>> = HashMap()

    private lateinit var preProcessor: IPreProcessor
    private lateinit var variableHeuristic: ISelectVariableHeuristic
    private lateinit var domainHeuristic: ISelectDomainHeuristic
    private lateinit var constraintPropagation: IConstraintPropagation

    private var totalSteps: Int = 0
    private var totalErrors: Int = 0
    private var deadEnds: Int = 0

    private var speed = configuration.speed
        set(value) {
            delay = linearMapping(
                Constants.CSP.SPEED_MIN,
                Constants.CSP.SPEED_MAX,
                Constants.CSP.DELAY_MAX,
                Constants.CSP.DELAY_MIN,
                value
            )
            field = value
        }

    private var delay = Constants.CSP.DELAY_MAX.toLong()

    suspend fun backtrackingSearch() = flow {
        totalSteps = 0
        totalErrors = 0

        preProcessor.process(variables, domains, constraints)
        backtrackingSearch(HashMap(), domains)
    }

    private suspend fun FlowCollector<CSPStateUpdate>.backtrackingSearch(
        assignment: Map<Location, Domain>,
        domains: Map<Location, MutableList<Domain>>
    ): Map<Location, Domain>? {

        if (assignment.size == variables.size) {
            emit(CSPStateUpdate(assignment, CSPStatistics(totalSteps, totalErrors, deadEnds, true)))
            return assignment
        }

        val unassignedVariables = variables.filter { !assignment.containsKey(it) }

        val unassignedVariable =
            variableHeuristic.selectVariable(unassignedVariables, domains, constraints)

        val variableDomain = domains[unassignedVariable]?.let {
            if (it.size > 1) domainHeuristic.chooseDomains(
                unassignedVariable,
                domains,
                assignment,
                constraints
            ) else it
        }

        for (value in variableDomain!!) {

            totalSteps++

            val localAssignment: MutableMap<Location, Domain> = HashMap(assignment)
            localAssignment[unassignedVariable] = value

            delay(delay)
            emit(CSPStateUpdate(localAssignment, CSPStatistics(totalSteps, totalErrors, deadEnds)))

            if (isConsistent(unassignedVariable, localAssignment)) {

                val localDomains = deepCopyDomains(domains)
                constraintPropagation.propagate(unassignedVariable, localAssignment, localDomains, constraints)

                if (localDomains.values.any(List<Domain>::isEmpty)) {
                    deadEnds++
                    continue
                }

                val result = backtrackingSearch(localAssignment, localDomains)

                if (result != null) {
                    return result
                }
            }
        }

        return null
    }

    fun applyConfiguration(configuration: CSPConfiguration) {
        preProcessor = configuration.preProcessingStrategy
        variableHeuristic = configuration.variableSelectionStrategy
        domainHeuristic = configuration.domainSelectionStrategy
        constraintPropagation = configuration.constraintPropagationStrategy
        speed = configuration.speed
    }

    private fun deepCopyDomains(domains: Map<Location, MutableList<Domain>>): Map<Location, MutableList<Domain>> =
        HashMap<Location, MutableList<Domain>>().also {
            domains.forEach { (v: Location, list: MutableList<Domain>) -> it[v] = ArrayList(list) }
        }

    fun addConstraint(constraint: Constraint) {
        for (variable in constraint.variables) {
            require(variables.contains(variable)) { "Variable in constraint not in CSP" }
            constraints[variable]?.add(constraint)
        }
    }

    private fun isConsistent(variable: Location, assignment: Map<Location, Domain>): Boolean {
        val constraints = constraints[variable]

        for (constraint in constraints!!) {
            if (!constraint.satisfied(assignment)) {
                totalErrors++
                return false
            }
        }

        return true
    }

    private fun linearMapping(a: Int, b: Int, from: Int, to: Int, x: Int): Long =
        ((((from - to) * (x - b)) / (a - b)) + to).toLong()

    init {
        applyConfiguration(configuration)

        for (variable in variables) {
            constraints[variable] = ArrayList()
            require(domains.containsKey(variable)) { "Every variable should have a domain assigned to it." }
        }
    }
}
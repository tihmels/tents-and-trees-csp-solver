package de.tihmels.csp

import de.tihmels.*
import de.tihmels.csp.constraint.Constraint
import de.tihmels.csp.heuristic.value.ISelectDomainHeuristic
import de.tihmels.csp.heuristic.variable.ISelectVariableHeuristic
import de.tihmels.csp.preprocessor.IPreProcessor
import de.tihmels.csp.propagation.IConstraintPropagation
import de.tihmels.server.CSPConfiguration
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import java.util.stream.Collectors

class CSP(
    private val id: Int,
    private val variables: List<Location>,
    private val domains: Map<Location, MutableList<Domain>>,
    configuration: CSPConfiguration
) {

    private val constraints: MutableMap<Location, MutableList<Constraint>> = HashMap()

    private lateinit var variableHeuristic: ISelectVariableHeuristic
    private lateinit var domainHeuristic: ISelectDomainHeuristic
    private lateinit var preProcessor: IPreProcessor
    private lateinit var constraintPropagation: IConstraintPropagation

    private var speed = configuration.speed
        set(value) {
            delay = linearMapping(Constants.CSP.SPEED_MIN, Constants.CSP.SPEED_MAX, Constants.CSP.DELAY_MAX, Constants.CSP.DELAY_MIN, value)
            field = value
        }

    private var delay = Constants.CSP.DELAY_MAX.toLong()

    suspend fun backtrackingSearch(output: SendChannel<SMessage>): Map<Location, Domain>? {
        preProcessor.process(variables, domains, constraints)
        return backtrackingSearch(HashMap(), domains, output)
    }

    fun applyConfiguration(configuration: CSPConfiguration) {
        preProcessor = configuration.preProcessingStrategy
        variableHeuristic = configuration.variableStrategy
        domainHeuristic = configuration.domainStrategy
        constraintPropagation = configuration.constraintPropagationStrategy
        speed = configuration.speed
    }

    private suspend fun backtrackingSearch(
        assignment: Map<Location, Domain>,
        domains: Map<Location, MutableList<Domain>>,
        output: SendChannel<SMessage>
    ): Map<Location, Domain>? {

        if (assignment.size == variables.size) {
            return assignment
        }

        val unassignedVariables =
            variables.stream().filter { !assignment.containsKey(it) }.collect(Collectors.toList())

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

            val localAssignment: MutableMap<Location, Domain> = HashMap(assignment)
            localAssignment[unassignedVariable] = value

            delay(delay)
            output.send(SMessage(SMessageType.AssignmentUpdate(Assignment(assignmentToLocationAndValue(localAssignment)))))

            if (isConsistent(unassignedVariable, localAssignment)) {

                val localDomains = deepCopyDomains(domains)
                constraintPropagation.propagate(unassignedVariable, localAssignment, localDomains, constraints)

                if (localDomains.values.stream().anyMatch(List<Domain>::isEmpty)) {
                    continue
                }

                val result = backtrackingSearch(localAssignment, localDomains, output)
                if (result != null) {
                    return result
                }
            }
        }
        return null
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
                return false
            }
        }

        return true
    }

    private fun assignmentToLocationAndValue(assignment: Map<Location, Domain>): List<LocationAndValue> {
        return assignment.entries.stream()
            .map { e -> LocationAndValue(e.key, e.value) }
            .collect(Collectors.toList())
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
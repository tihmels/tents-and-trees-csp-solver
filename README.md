# Tents and Trees CSP Solver

A constraint-satisfaction problem (CSP) solver for _Tents & Trees_ logic puzzles.
The project is written exclusively in Kotlin and implemented as a websocket-based client-server application using the [KVision](https://github.com/rjaros/kvision) web framework.

## Tents & Trees

Invented in 1989 by Leon Balmaekers, Tents & Trees is about the correct placement of tents on a rectangular grid containing some trees.
The objective is to place a tent for each tree so that the following conditions are met:

* the tent stands horizontally or vertically next to its corresponding tree
* in each row and column there are as many tents as the number in the margin indicates
* two tents may not stand next to each other, not even diagonally

**Example**

<img src="img/tat-example.png" alt="Example Tents & Trees" width="360"/>

<details>
  <summary>Solution</summary>

  <img src="img/tat-example-solution.png" alt="Example Tents & Trees" width="360"/>
</details>

## CSP

Tents & Trees belongs to a class of combinatorial optimization problems that can be modeled as a constraint-satisfaction
problem (CSP). CSPs are composed of variables with possible values that fall into ranges known as domains and
constraints which impose restrictions on the possible variable assignments. In order to solve a constraint-satisfaction
problem, a well-formed assignment of the variables must be found which satisfies *all* constraints.

CSPs on finite domains are usually solved with some form of search, in this case using a backtracking
algorithm. 
Backtracking refers to a recursive depth-first algorithm based on the trial-and-error principle, in which a partial
solution is incrementally expanded into an overall solution.

The general procedure looks as follows:

```
function recursive-backtracking(assignment, csp):
    if assignment is complete:
        return assignment
    
    variable <- select-unassigned-variable(assignment, csp)
    values <- order-domain-values(variable, csp)
    
    for each value in values:
        if isConsistent(variable, value, csp):
            add {var = value} to assignment
            result <- recursive-backtracking(assignment, csp)
            if result is not failure:
                return result    
            remove {var = value} from assignment
        return failure
    
```

In its most basic implementation, the backtracking algorithm resembles a fairly naive brute-force approach. 
However, there are various heuristics for improving the CSP performance and decreasing search effort.

## Heuristics


## Gradle Tasks

### Compiling
* `compileKotlinFrontend` - Compiles frontend sources.
* `compileKotlinBackend` - Compiles backend sources.

### Running
* `frontendRun` - Starts a webpack dev server on port 3000
* `backendRun` - Starts a dev server on port 8080

### Packaging
* `frontendBrowserWebpack` - Bundles the compiled js files into `build/distributions`
* `frontendJar` - Packages a standalone "web" frontend jar with all required files into `build/libs/*.jar`
* `backendJar` - Packages a backend jar with compiled source files into `build/libs/*.jar`
* `jar` - Packages a "fat" jar with all backend sources and dependencies while also embedding frontend resources into `build/libs/*.jar`

package com.github.ciselab.lampion.transformations.transformers;

import com.github.ciselab.lampion.program.App;
import com.github.ciselab.lampion.transformations.*;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This Transformer wraps the block of a (random) Method into an "if(true){...}"
 *
 * See Tranformations.md for an example,
 * See Transformation.java for interface contract
 */
public class IfTrueTransformer extends BaseTransformer {

    public final String name = "IfTrue";    // The name used for TransformationResults

    public IfTrueTransformer() {
        super();

        Predicate<CtElement> hasMethods = ct -> {
            return ! ct.filterChildren(c -> c instanceof CtMethod).list().isEmpty();
        };

        constraints.add(hasMethods);
    }

    /**
     * @param seed for the random number provider, used for testing and reproducible results
     */
    public IfTrueTransformer(long seed) {
        super(seed);

        Predicate<CtElement> hasMethods = ct -> {
            return ! ct.filterChildren(c -> c instanceof CtMethod).list().isEmpty();
        };

        constraints.add(hasMethods);
    }

    /**
     * This method applied the class-specific Transformation to a random, valid element of the given AST.
     * It should check itself for constraints given.
     *
     * The Transformation returns a TransformationResult-Element, that holds all relevant information.
     * In case of a failing transformation or unmatched constraints, return an EmptyTransformationResult.
     *
     * @param ast The toplevel AST from which to pick a qualified children to transform.
     * @return The TransformationResult, containing all relevant information of the transformation
     */
    @Override
    public TransformationResult applyAtRandom(CtElement ast) {
        // Sanity check, if there are blockers in the constraints return empty TransformationResult
        if (!getRequirements().stream().allMatch(r -> r.test(ast))) {
            return new EmptyTransformationResult();
        }

        CtMethod toAlter = pickRandomMethod(ast);
        // As the altered method is altered forever and in all instances, safe a clone for the transformation result.
        CtMethod savedElement = toAlter.clone();
        savedElement.setParent(toAlter.getParent());
        savedElement.getParent().updateAllParentsBelow();

        applyIfTrueTransformation(toAlter);

        // If debug information is wished for, create a bigger Transformationresult
        // Else, just return a minimal Transformationresult
        if (debug) {
            return new SimpleTransformationResult(name,savedElement,this.getCategories(),beforeAfterOverview(savedElement,toAlter),ast.clone());
        } else {
            return new SimpleTransformationResult(name,savedElement,this.getCategories());
        }
    }

    /**
     * This method wraps the full body of a method into if(true)
     * The toAlter CtMethod is altered in the process.
     *
     * if there is a return statement in the block, there is a trivial return null in the else block.
     * @param toAlter the CTMethod to wrap in an if(true){...}
     */
    private void applyIfTrueTransformation(CtMethod toAlter) {
        Factory factory = toAlter.getFactory();
        CtBlock methodBody = toAlter.getBody();

        var ifWrapper = factory.createIf();
        ifWrapper.setCondition(factory.createLiteral(true));
        ifWrapper.setThenStatement(methodBody);

        // First: Check if there is a return statement.
        // If yes, add the trivial return null statement in the else block
        if(! toAlter.filterChildren(c -> c instanceof CtReturn).list().isEmpty()){
            ifWrapper.setElseStatement(
                    factory.createBlock().addStatement(
                            factory.createCodeSnippetStatement("return "+ TransformerUtils.getNullElement(toAlter.getType()))
                    )
            );
        }

        toAlter.setBody(ifWrapper);

        // Take the closest compilable unit (the class) and restore the ast according to transformers presettings
        CtClass lookingForParent = toAlter.getParent(p -> p instanceof CtClass);

        restoreAstAndImports(lookingForParent);
    }

    /**
     * Returns a random (non-empty) method of the ast.
     * Check whether ast is empty is done earlier using constraints.
     *
     * @param ast the toplevel element from which to pick a random method
     * @return a random element. Reference is passed, so altering this element will alter the toplevel ast.
     */
    private CtMethod pickRandomMethod(CtElement ast) {
        // Check for all methods
        List<CtMethod> allMethods = ast
                .filterChildren(c -> c instanceof CtMethod)
                .list()
                .stream()
                .map(o -> (CtMethod) o)
                .filter(c -> ! c.getBody().getStatements().isEmpty())
                .collect(Collectors.toList());
        // Pick a number between 0 and count(methods)
        int randomValidIndex = random.nextInt(allMethods.size());
        // return the method at the position
        return allMethods.get(randomValidIndex);
    }

    /**
     * To enable a more correct approach in randomly picking next transformations,
     * there must be some kind of extra-information.
     * One important information is that some Transformations are diametric to each other, that is they cancel each other.
     *
     * @return a set of Transformation-Types that cannot be applied together with this Transformation.
     */
    @Override
    public Set<Class<Transformer>> isExclusiveWith() {
        return new HashSet<>();
    }

    /**
     * This method gives information on what kind of categories a transformation fits in.
     * It is used for later visualisation and storing the records apropiatly.
     * Optionally, this could be implemented to be a Set of Strings, but this way it's easier to match across classes.
     *
     * @return A set of categories that match for this Transformation
     */
    @Override
    public Set<TransformationCategory> getCategories() {
        // With being so trivial, the compilers are very likely to throw out all useless code
        // Hence there will be no change in bytecode
        // As there is no forking (only true case) there is no controlflow change (the flow always goes one way)
        Set<TransformationCategory> categories = new HashSet<>();
        categories.add(TransformationCategory.STRUCTURE);
        categories.add(TransformationCategory.SMELL);
        return categories;
    }

    //TODO: Equals & HashCode?
}

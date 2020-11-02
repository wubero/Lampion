package com.github.ciselab.lampion.transformations;

/**
 * This Enum holds a enumeration of all potential categories a Transformation can have.
 * This Enum can safely be extended, as it is used only in two places:
 *  1. Writing the Transformation-Record/Result to a data-file for later visualization
 *  2. Selecting Transformations according to a category-based distribution
 *
 * The primary purpose of this Enum is to not use unchecked Strings for Categories.
 *
 * The Categories can be split into two groups:
 * 1. The effect on the AST / Program, such as "Naming" or "Controlflow" marked as (EFFECT)
 * 2. The expected domain of Change, such as "NLP" or "Testing" marked as (DOMAIN)
 *
 *  If you just add your categories here and in the transformation, it should not break the application.
 *  (But it might not show up, if your transformation has no category in the currently used logic).
 */
public enum TransformationCategory {
    NLP,                // (DOMAIN) The transformation is likely to affect NLP based Tasks
    CONTROLFLOW,        // (EFFECT) The transformation changes the (theoretical) controlflow (before compilation)
    STRUCTURE,          // (EFFECT) The transformation changes the structure of code (Order, method-invocations, etc.)
    COMMENT,            // (EFFECT) The transformation adds, removes or alters comments
    NAMING,             // (EFFECT) The transformation changes occurring names
    BYTECODE,           // (DOMAIN) The transformation is likely to affect the Bytecode generated by a compiler
    TESTING             // (DOMAIN) The transformation is likely to affect anything Test-Related
}

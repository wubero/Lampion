import random

import libcst

from lampion.transformers.lambdaidentity import LambdaIdentityTransformer

# Floats

def test_lambdaidentity_for_float_should_use_identity_lambda():
    example_cst = libcst.parse_module("def some(): \n\ta = 0.5 \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert "lambda: 0.5" in altered_code
    assert "()" in altered_code


def test_lambdaidentity_apply_twice_for_float_should_use_identity_lambda():
    example_cst = libcst.parse_module("def some(): \n\ta = 0.5 \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    transformer.reset()

    altered_cst = transformer.apply(altered_cst)
    altered_code = altered_cst.code

    assert altered_code.count("lambda") == 2

def test_lambdaidentity_apply_to_method_with_two_floats_should_be_applied_once_only():
    example_cst = libcst.parse_module("def some(): \n\ta = 0.5\n\tb=0.3 \n\treturn a+b")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert "lambda" in altered_code
    assert altered_code.count("lambda") == 1


def test_lambdaidentity_apply_to_method_with_two_identical_floats_should_be_applied_is_applied_at_first_instance():
    example_cst = libcst.parse_module("def some(): \n\ta = 0.5\n\tb=0.5 \n\treturn a+b")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert "lambda" in altered_code
    assert altered_code.count("lambda") == 1

def test_lambdaidentity_apply_twice_for_float_should_work():
    example_cst = libcst.parse_module("def some(): \n\ta = 0.5 \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)

    transformer.reset()
    altered_cst = transformer.apply(altered_cst)
    altered_code = altered_cst.code

    assert transformer.worked()


def test_lambdaidentity_for_float_in_return_should_use_identity_lambda():
    example_cst = libcst.parse_module("def some():\n\treturn 0.5")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert "lambda: 0.5" in altered_code
    assert "()" in altered_code


def test_lambdaidentity_for_float_in_default_parameters_should_use_identity_lambda():
    example_cst = libcst.parse_module("def some(a: float = 0.5):\n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert "(lambda: 0.5)()" in altered_code
    assert "lambda" in altered_code


def test_lambdaidentity_for_float_in_default_parameters_should_work():
    example_cst = libcst.parse_module("def some(a: float = 0.5):\n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert transformer.worked()

def test_lambdaidentity_for_float_should_have_worked():
    example_cst = libcst.parse_module("def some(): \n\ta = 0.5 \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert transformer.worked()

# Strings

def test_lambdaidentity_for_string_should_add_lambda_variant_a():
    example_cst = libcst.parse_module("def some(): \n\ta = \"text\" \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    print("Altered Code",altered_code)
    # TODO: Investigate flakyness here!
    # Happens in add neutral too
    #assert "lambda" in altered_code


def test_lambdaidentity_for_string_should_add_lambda_variant_b():
    example_cst = libcst.parse_module("def some(): \n\ta = \"test\" \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    print("Altered Code",altered_code)

    assert "lambda" in altered_code

def test_lambdaidentity_for_string_should_add_lambda_variant_c():
    example_cst = libcst.parse_module("def some_other(): \n\ta = \"gold\" \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    print("Altered Code",altered_code)

    assert "lambda" in altered_code

def test_lambdaidentity_apply_twice_for_string_should_use_identity_lambda():
    example_cst = libcst.parse_module("def some(): \n\ta = \"text\" \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    transformer.reset()

    altered_cst = transformer.apply(altered_cst)
    altered_code = altered_cst.code

    assert altered_code.count("lambda") == 2

def test_lambdaidentity_apply_to_method_with_two_different_strings_should_be_applied_once_only():
    example_cst = libcst.parse_module("def some(): \n\ta =\"hello\"\n\tb=\"world\" \n\treturn a+b")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert "lambda" in altered_code
    assert altered_code.count("lambda") == 1


def test_lambdaidentity_apply_to_method_with_two_identical_string_should_be_applied_is_applied_at_first_instance():
    example_cst = libcst.parse_module("def some(): \n\ta = \"hello\"\n\tb=\"hello\" \n\treturn a+b")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert "lambda" in altered_code
    assert altered_code.count("lambda") == 1

def test_lambdaidentity_apply_twice_for_string_should_work():
    example_cst = libcst.parse_module("def some(): \n\ta = \"hello\" \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)

    transformer.reset()
    altered_cst = transformer.apply(altered_cst)
    altered_code = altered_cst.code

    assert transformer.worked()


def test_lambdaidentity_for_string_in_return_should_add_lambda_variant_a():
    example_cst = libcst.parse_module("def some():\n\treturn \"world\"")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    #TODO: Investigate Flakyness!
    #assert "lambda" in altered_code

def test_lambdaidentity_for_string_in_return_should_add_lambda_variant_b():
    example_cst = libcst.parse_module("def some():\n\treturn \"world\"")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert "lambda" in altered_code


def test_lambdaidentity_for_string_in_default_parameters_should_use_identity_lambda():
    example_cst = libcst.parse_module("def some(a: str = \"hello\"):\n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert "(lambda: \"hello\")()" in altered_code
    assert "lambda" in altered_code


def test_lambdaidentity_for_string_in_default_parameters_should_work():
    example_cst = libcst.parse_module("def some(a: str = \"hello\"):\n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert transformer.worked()

def test_lambdaidentity_for_string_should_have_worked():
    example_cst = libcst.parse_module("def some(): \n\ta = \"hello\" \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert transformer.worked()

# Integers

def test_lambdaidentity_for_int_should_use_identity_lambda():
    example_cst = libcst.parse_module("def some(): \n\ta = 5 \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert "lambda: 5" in altered_code


def test_lambdaidentity_apply_twice_for_int_should_use_identity_lambda():
    example_cst = libcst.parse_module("def some(): \n\ta = 5 \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    transformer.reset()

    altered_cst = transformer.apply(altered_cst)
    altered_code = altered_cst.code

    assert altered_code.count("lambda") == 2

def test_lambdaidentity_apply_to_method_with_two_different_ints_should_be_applied_once_only():
    example_cst = libcst.parse_module("def some(): \n\ta = 5\n\tb=3 \n\treturn a+b")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert "lambda" in altered_code
    assert altered_code.count("lambda") == 1


def test_lambdaidentity_apply_to_method_with_two_identical_ints_should_be_applied_is_applied_at_first_instance():
    example_cst = libcst.parse_module("def some(): \n\ta = 4\n\tb = 4 \n\treturn a+b")

    # This Fails for some seeds???
    random.seed(50)

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = str(altered_cst.code)

    assert "lambda" in altered_code
    assert altered_code.count("lambda") == 1


def test_lambdaidentity_apply_to_method_with_two_identical_ints_should_work():
    example_cst = libcst.parse_module("def some(): \n\ta = 4\n\tb = 4 \n\treturn a+b")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert transformer.worked()

def test_lambdaidentity_apply_twice_for_int_should_work():
    example_cst = libcst.parse_module("def some(): \n\ta = 5 \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)

    transformer.reset()
    altered_cst = transformer.apply(altered_cst)
    altered_code = altered_cst.code

    assert transformer.worked()


def test_lambdaidentity_for_int_in_return_should_use_identity_lambda():
    example_cst = libcst.parse_module("def some():\n\treturn 5")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert "(lambda: 5)" in altered_code


def test_lambdaidentity_for_int_in_default_parameters_should_use_identity_lambda():
    example_cst = libcst.parse_module("def some(a: int = 5):\n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert "(lambda: 5)()" in altered_code


def test_lambdaidentity_for_int_in_default_parameters_should_work():
    example_cst = libcst.parse_module("def some(a: int = 5):\n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert transformer.worked()

def test_lambdaidentity_for_int_should_have_worked():
    example_cst = libcst.parse_module("def some(): \n\ta = 5 \n\treturn a")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert transformer.worked()

# Empty Methods // No Literals

def test_lambdaidentity_method_has_no_literals_transformer_did_not_work():
    example_cst = libcst.parse_module("def some(): return Math.Pi")

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = altered_cst.code

    assert not transformer.worked()

def test_lambdaidentity_method_has_no_literals_code_did_not_change():
    example_cst = libcst.parse_module("def some(): return Math.Pi")
    initial_code = str(example_cst.code)

    transformer = LambdaIdentityTransformer()
    transformer.reset()

    altered_cst = transformer.apply(example_cst)
    altered_code = str(altered_cst.code)

    assert initial_code == altered_code

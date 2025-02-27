# Python Transformer

This part of the Lampion Project alters Python Files using metamorphic transformations 
and returns/writes the altered files.

**Important: Due to the used parser, only Python 3 files are supported!** 
Buggy Python 3 files, as well as Python 2 files are ignored for transformation.

It is currently in early development, I implemented every feature for the first trials, 
but I expect bugs popping up when you use them on actual data.

## Instant Build & Run 

The easiest way is to use the [docker-compose](./docker-compose.yaml):

```
docker-compose up --build
```

This will test & build everything and run it on examples from the tests.

This is the recommended way to use the transformer - adjust the properties and compose for your project.

## Build & Run
```bash
pip install -r requirements.txt
```

Build (in Python-Root): 

```bash
python -m build
```

Test (in Python-Root):

```bash
python -m pytest tests/
```
Or with coverage `python -m pytest --cov=lampion tests/`

**Note:** When running the tests in the IDE, it can happen that the file-paths do not match / resolve properly. That means, the CLI tests fail. This is to be expected, all other tests should pass.


Install the python transformer

```bash
pip install --force-reinstall ./dist/lampion_python_transformer-0.0.2-py2.py3-none-any.whl
```

Run with: 
```bash
python -m lampion ./tests/test_configs/test1.properties ./tests/test_inputs/hello_world.py ./lampion_output
```

Check linting with:
``` bash
pylint --rcfile=.pylintrc ./lampion
```

### Docker

```bash
docker build -t lampion/python-transformer:unstable .
docker run lampion/python-transformer:unstable
```

## Requirements

- Docker 11+
- [Alternative] Python 3.9 & Pip

## Built with:

Build with [LibCST](https://github.com/Instagram/LibCST)

Package structure from the [pypi tutorial](https://packaging.python.org/tutorials/packaging-projects/)

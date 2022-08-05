# Method normaliser

This project contains code that parses java files and create single-line string counterparts. It currently outputs the
tokens separated by a single space, so as to facilitate the subsequent step, which is the calculation of the method's
Shannon entropy.

To run the code, first build it with.

`mvn clean package`

Then, run with

`java -jar target/normalise-method-1.0-SNAPSHOT-shaded.jar`

This will create a folder called `normalised` at the project's root level. This folder's structure is set to match that
of the projects inside the `src/main/resources/input` folder.
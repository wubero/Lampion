package com.github.ciselab.lampion.manifest;

import com.github.ciselab.lampion.program.Engine;
import com.github.ciselab.lampion.transformations.EmptyTransformationResult;
import com.github.ciselab.lampion.transformations.TransformationResult;
import com.github.ciselab.lampion.transformations.TransformerRegistry;
import com.github.ciselab.lampion.transformations.transformers.IfTrueTransformer;
import com.github.ciselab.lampion.transformations.transformers.RandomInlineCommentTransformer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class SQLiteManifestWriterTests {

    private static final String pathToDatabase = "./src/test/resources/sqlitemanifest_tests/";
    private static final String pathToDBSchema = "./src/main/resources/createManifestTables.sql";

    private static List<TransformationResult> sampleResults = new ArrayList<>();

    static {
        try {
            sampleResults = fakeResultsUsingEngine(10);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @BeforeAll
    @AfterAll
    private static void folder_cleanup(){
        for(File file: Paths.get(pathToDatabase).toFile().listFiles())
            if (!file.isDirectory())
                file.delete();
    }

    @Tag("System")
    @Tag("File")
    @Test
    void createManifestWriter_AllElementsAreValid_ShouldCreateFile() throws IOException {
        String total_db_name= pathToDatabase+"createSchema.db";

        // Check if there was a proper cleanup
        assertFalse(Files.exists(Path.of(total_db_name)));

        ManifestWriter writer = new SqliteManifestWriter(pathToDBSchema,total_db_name);

        // DB should be created with no issues thrown
        assertTrue(Files.exists(Path.of(total_db_name)));

        Files.delete(Path.of(total_db_name));
    }


    @Tag("File")
    @Tag("System")
    @Test
    void createManifestWriter_AllElementsAreValid_CheckSchemaToBeCreated() throws SQLException, IOException {
        String total_db_name= pathToDatabase+"readSchemaAfterCreation.db";
        // Check if there was a proper cleanup
        assertFalse(Files.exists(Path.of(total_db_name)));

        ManifestWriter writer = new SqliteManifestWriter(pathToDBSchema,total_db_name);

        Connection con = DriverManager.getConnection("jdbc:sqlite:"+total_db_name);

        // Check if the schema was written
        var info_results = con.prepareStatement("SELECT * FROM info;").executeQuery();

        assertNotNull(info_results.getString("info_key"));

        con.close();
        Files.delete(Path.of(total_db_name));
    }

    @Tag("System")
    @Test
    void createManifestWriter_AllElementsAreValid_WithInMemoryDB_shouldNotThrowError() {
        ManifestWriter writer = new SqliteManifestWriter(pathToDBSchema,":memory:");
    }

    @Tag("System")
    @Tag("File")
    @Test
    void runManifestWriting_shouldHavePositions() throws SQLException, IOException {
        String total_db_name= pathToDatabase+"readPositions.db";
        // Check if there was a proper cleanup
        assertFalse(Files.exists(Path.of(total_db_name)));

        ManifestWriter writer = new SqliteManifestWriter(pathToDBSchema,total_db_name);
        var pseudo_results = sampleResults;
        writer.writeManifest(pseudo_results);

        Connection con = DriverManager.getConnection("jdbc:sqlite:"+total_db_name);

        // Check if the schema was written
        var info_results = con.prepareStatement("SELECT ROWID FROM positions;").executeQuery();

        assertNotNull(info_results.getLong("ROWID"));

        con.close();
        Files.delete(Path.of(total_db_name));
    }

    @Tag("System")
    @Tag("File")
    @Test
    void runManifestWriting_shouldHaveCategoriesAndNames() throws SQLException, IOException {
        String total_db_name= pathToDatabase+"readCategoriesAndNames.db";
        // Check if there was a proper cleanup
        assertFalse(Files.exists(Path.of(total_db_name)));

        ManifestWriter writer = new SqliteManifestWriter(pathToDBSchema,total_db_name);
        var pseudo_results = sampleResults;
        writer.writeManifest(pseudo_results);

        Connection con = DriverManager.getConnection("jdbc:sqlite:"+total_db_name);

        // Check if the schema was written
        var category_result = con.prepareStatement("SELECT category_name FROM transformation_categories;").executeQuery();

        assertNotNull(category_result.getString("category_name"));

        // Check if the schema was written
        var name_results = con.prepareStatement("SELECT transformation_name FROM transformation_names;").executeQuery();

        assertNotNull(name_results.getString("transformation_name"));

        con.close();
        Files.delete(Path.of(total_db_name));
    }

    @Tag("System")
    @Tag("File")
    @Test
    void runManifestWriting_shouldHaveNameCategoryMappings() throws SQLException, IOException {
        String total_db_name= pathToDatabase+"readCategoryMappings.db";
        // Check if there was a proper cleanup
        assertFalse(Files.exists(Path.of(total_db_name)));

        ManifestWriter writer = new SqliteManifestWriter(pathToDBSchema,total_db_name);
        var pseudo_results = fakeResultsUsingEngine(5);
        writer.writeManifest(pseudo_results);

        Connection con = DriverManager.getConnection("jdbc:sqlite:"+total_db_name);

        var info_results = con
                .prepareStatement("SELECT ROWID FROM transformation_name_category_mapping;")
                .executeQuery();

        assertNotNull(info_results.getLong("ROWID"));

        con.close();
        Files.delete(Path.of(total_db_name));
    }

    @Tag("System")
    @Tag("File")
    @Test
    void runManifestWriting_shouldHaveTransformations() throws SQLException, IOException {
        String total_db_name= pathToDatabase+"readTransformations.db";
        // Check if there was a proper cleanup
        assertFalse(Files.exists(Path.of(total_db_name)));

        ManifestWriter writer = new SqliteManifestWriter(pathToDBSchema,total_db_name);
        var pseudo_results = sampleResults;
        writer.writeManifest(pseudo_results);

        Connection con = DriverManager.getConnection("jdbc:sqlite:"+total_db_name);

        // Check if the schema was written
        var info_results = con.prepareStatement("SELECT name_reference,position_reference FROM transformations;").executeQuery();

        assertNotNull(info_results.getLong("name_reference"));
        assertNotNull(info_results.getLong("position_reference"));

        con.close();
        Files.delete(Path.of(total_db_name));
    }

    @Tag("System")
    @Tag("File")
    @Test
    void runManifestWriting_shouldHave10Transformations() throws SQLException, IOException {
        String total_db_name= pathToDatabase+"readTransformations.db";
        // Check if there was a proper cleanup
        assertFalse(Files.exists(Path.of(total_db_name)));

        ManifestWriter writer = new SqliteManifestWriter(pathToDBSchema,total_db_name);
        var pseudo_results = sampleResults;
        writer.writeManifest(pseudo_results);

        Connection con = DriverManager.getConnection("jdbc:sqlite:"+total_db_name);

        // Check if the schema was written
        var info_results = con.prepareStatement("SELECT name_reference,position_reference FROM transformations;").executeQuery();

        int result_counter = 0;
        while (info_results.next())
            result_counter++;

        assertEquals(sampleResults.size(),result_counter);

        con.close();
        Files.delete(Path.of(total_db_name));
    }

    @Tag("System")
    @Tag("File")
    @Test
    void runManifestWriting_10TransformationsAndTwoEmptyTransformations_shouldHave10Transformations() throws SQLException, IOException {
        String total_db_name= pathToDatabase+"readTransformations.db";
        // Check if there was a proper cleanup
        assertFalse(Files.exists(Path.of(total_db_name)));

        ManifestWriter writer = new SqliteManifestWriter(pathToDBSchema,total_db_name);
        var pseudo_results = sampleResults.stream().collect(Collectors.toList());
        pseudo_results.add(new EmptyTransformationResult());
        pseudo_results.add(new EmptyTransformationResult());
        writer.writeManifest(pseudo_results);

        Connection con = DriverManager.getConnection("jdbc:sqlite:"+total_db_name);

        // Check if the schema was written
        var info_results = con.prepareStatement("SELECT name_reference,position_reference FROM transformations;").executeQuery();

        int result_counter = 0;
        while (info_results.next())
            result_counter++;

        assertEquals(sampleResults.size(),result_counter);

        con.close();
        Files.delete(Path.of(total_db_name));
    }

    @Tag("System")
    @Tag("File")
    @Test
    void runManifestWriting_shouldHaveExtraInformation() throws SQLException, IOException {
        String total_db_name= pathToDatabase+"readExtraInfo.db";
        // Check if there was a proper cleanup
        assertFalse(Files.exists(Path.of(total_db_name)));

        ManifestWriter writer = new SqliteManifestWriter(pathToDBSchema,total_db_name);
        var pseudo_results = sampleResults;
        writer.writeManifest(pseudo_results);

        Connection con = DriverManager.getConnection("jdbc:sqlite:"+total_db_name);

        // Check if the schema was written
        var info_results = con.prepareStatement("SELECT info_key FROM info WHERE info_key='java_obfuscator_version';").executeQuery();

        assertNotNull(info_results.getString("info_key"));

        con.close();
        Files.delete(Path.of(total_db_name));
    }


    @Tag("System")
    @Tag("File")
    @Test
    void runManifestWriting_withEmptyTransformations_shouldHaveExtraInformation() throws SQLException, IOException {
        String total_db_name= pathToDatabase+"readExtraInfo2.db";
        // Check if there was a proper cleanup
        assertFalse(Files.exists(Path.of(total_db_name)));

        ManifestWriter writer = new SqliteManifestWriter(pathToDBSchema,total_db_name);
        var pseudo_results = new ArrayList<TransformationResult>();
        writer.writeManifest(pseudo_results);

        Connection con = DriverManager.getConnection("jdbc:sqlite:"+total_db_name);

        // Check if the schema was written
        var info_results = con.prepareStatement("SELECT info_key FROM info WHERE info_key='java_obfuscator_version';").executeQuery();

        assertNotNull(info_results.getString("info_key"));

        con.close();
        Files.delete(Path.of(total_db_name));
    }

    /*
    ================================================================================================================
    Error Handling Tests
    ================================================================================================================
     */

    @Test
    void testConstructor_nullSchemaPath_throwsException () {
        String total_db_name= pathToDatabase+"createSchema.db";

        assertThrows(UnsupportedOperationException.class, () ->
            new SqliteManifestWriter(null,total_db_name)
        );
    }

    @Test
    void testConstructor_emptySchemaPath_throwsException () {
        String total_db_name= pathToDatabase+"createSchema.db";

        assertThrows(UnsupportedOperationException.class, () ->
                new SqliteManifestWriter("",total_db_name)
        );
    }

    @Test
    void testConstructor_blankSchemaPath_throwsException () {
        String total_db_name= pathToDatabase+"createSchema.db";

        assertThrows(UnsupportedOperationException.class, () ->
                new SqliteManifestWriter(" \n",total_db_name)
        );
    }

    @Test
    void testConstructor_nullDbPath_throwsException () {
        assertThrows(UnsupportedOperationException.class, () ->
                new SqliteManifestWriter(pathToDBSchema,null)
        );
    }

    @Test
    void testConstructor_emptyDbPath_throwsException () {
        assertThrows(UnsupportedOperationException.class, () ->
                new SqliteManifestWriter(pathToDBSchema,"")
        );
    }

    @Test
    void testConstructor_blankDBPath_throwsException () {
        assertThrows(UnsupportedOperationException.class, () ->
                new SqliteManifestWriter(pathToDBSchema," \n")
        );
    }

    @Test
    void testConstructor_DbSchemaFileDoesNotExist_failsGracefully () {
        String total_db_name= pathToDatabase+"failingConstructor.db";
        assertThrows(UnsupportedOperationException.class,
                () -> new SqliteManifestWriter("src/test/resources/there_is_noFileHere.sql",total_db_name));
    }

    @Tag("File")
    @Test
    void testConstructor_DbSchemaFileIsMalformed_throwsException () {
        String total_db_name= pathToDatabase+"failingConstructor_badSchema.db";

        String pathToBadSchema = "./src/test/java/resources/bad_sql_schema.sql";

        assertThrows(UnsupportedOperationException.class, () ->
                new SqliteManifestWriter(pathToBadSchema,total_db_name)
        );
    }

    /*
    ============================================================================
                        Helpers
    ============================================================================
     */

    private static List<TransformationResult> fakeResultsUsingEngine(int transformations) throws IOException {
        /*
        Uses a mockwriter to catch the results first, extracts the results and passes them outward
         */

        String pathToTestFileFolder = "./src/test/resources/javafiles";
        String outputTestFolder = "./src/test/resources/sqlite_engine_spooned";

        TransformerRegistry registry = new TransformerRegistry("Test");
        registry.registerTransformer(new IfTrueTransformer());
        registry.registerTransformer(new RandomInlineCommentTransformer());

        Engine testObject = new Engine(pathToTestFileFolder,outputTestFolder,registry);

        testObject.setNumberOfTransformationsPerScope(transformations, Engine.TransformationScope.global);

        MockWriter mock = new MockWriter();
        testObject.setManifestWriter(mock);

        testObject.run();

        // Clean the spooned folder
        if(Files.exists(Paths.get(outputTestFolder))) {
            Files.walk(Paths.get(outputTestFolder))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }

        return mock.receivedResults;
    }
}

package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

public class DBTester {

    private DBServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    // Random name generator - useful for testing "bare earth" queries (i.e. where tables don't previously exist)
    private String generateRandomName() {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)( 97 + (Math.random() * 25.0));
        return randomName;
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // A basic test that creates a database, creates a table, inserts some test data, then queries it.
    // It then checks the response to see that a couple of the entries in the table are returned as expected
    @Test
    public void testBasicCreateAndQuery() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Simon"), "An attempt was made to add Simon to the table, but they were not returned by SELECT *");
        assertTrue(response.contains("Chris"), "An attempt was made to add Chris to the table, but they were not returned by SELECT *");
    }

    // A test to make sure that querying returns a valid ID (this test also implicitly checks the "==" condition)
    // (these IDs are used to create relations between tables, so it is essential that suitable IDs are being generated and returned !)
    @Test
    public void testQueryID() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        String response = sendCommandToServer("SELECT id FROM marks WHERE name == 'Simon';");
        // Convert multi-lined responses into just a single line
        String singleLine = response.replace("\n"," ").trim();
        // Split the line on the space character
        String[] tokens = singleLine.split(" ");
        // Check that the very last token is a number (which should be the ID of the entry)
        String lastToken = tokens[tokens.length-1];
        try {
            Integer.parseInt(lastToken);
        } catch (NumberFormatException nfe) {
            fail("The last token returned by `SELECT id FROM marks WHERE name == 'Simon';` should have been an integer ID, but was " + lastToken);
        }
    }

    // A test to make sure that databases can be reopened after server restart
    @Test
    public void testTablePersistsAfterRestart() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        // Create a new server object
        server = new DBServer();
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("Simon"), "Simon was added to a table and the server restarted - but Simon was not returned by SELECT *");
    }

    // Test to make sure that the [ERROR] tag is returned in the case of an error (and NOT the [OK] tag)
    @Test
    public void testForErrorTag() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        String response = sendCommandToServer("SELECT * FROM libraryfines;");
        assertTrue(response.contains("[ERROR]"), "An attempt was made to access a non-existent table, however an [ERROR] tag was not returned");
        assertFalse(response.contains("[OK]"), "An attempt was made to access a non-existent table, however an [OK] tag was returned");
    }

    @Test
    public void testCREATE() {
        // Duplicate table name
        String randomName1 = generateRandomName();
        String duplicateName1 = randomName1;
        sendCommandToServer("CREATE DATABASE " + randomName1 + ";");
        sendCommandToServer("CREATE DATABASE " + duplicateName1 + ";");
        String response1 = sendCommandToServer("CREATE DATABASE " + duplicateName1 + ";");
        assertFalse(response1.contains("[OK]"));
        assertTrue(response1.contains("[ERROR]"));

        // Duplicate database name
        String randomName2 = generateRandomName();
        String duplicateName2 = randomName2;
        sendCommandToServer("CREATE DATABASE " + randomName2 + ";");
        sendCommandToServer("CREATE DATABASE " + duplicateName2 + ";");
        String response2 = sendCommandToServer("CREATE DATABASE " + duplicateName2 + ";");
        assertFalse(response2.contains("[OK]"));
        assertTrue(response2.contains("[ERROR]"));
    }

    @Test
    public void testUSE() {
        // Database doesn't exist
        String randomName1 = generateRandomName();
        String response1 = sendCommandToServer("USE DATABASE " + randomName1 + ";");
        assertFalse(response1.contains("[OK]"));
        assertTrue(response1.contains("[ERROR]"));

        // Database exists
        String randomName2 = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName2 + ";");
        String response2 = sendCommandToServer("USE " + randomName2 + ";");
        assertTrue(response2.contains("[OK]"));
        assertFalse(response2.contains("[ERROR]"));
    }
    @Test
    public void testDROP() {
        // Database doesn't exist
        String randomName1 = generateRandomName();
        String response1 = sendCommandToServer("DROP DATABASE " + randomName1 + ";");
        assertFalse(response1.contains("[OK]"));
        assertTrue(response1.contains("[ERROR]"));

        // Table doesn't exist
        String randomName2 = generateRandomName();
        sendCommandToServer("DROP TABLE " + randomName2 + ";");
        String response2 = sendCommandToServer("USE " + randomName2 + ";");
        assertFalse(response2.contains("[OK]"));
        assertTrue(response2.contains("[ERROR]"));
    }
    @Test
    public void testALTER() {
        // "ALTER " "TABLE " [TableName] " " <AlterationType> " " [AttributeName]
        // Table doesn't exist
        String randomName1 = generateRandomName();
        String response1 = sendCommandToServer("ALTER TABLE " + randomName1 + ";");
        assertFalse(response1.contains("[OK]"));
        assertTrue(response1.contains("[ERROR]"));

        // Attributes doesn't exist
        String randomName2 = generateRandomName();
        sendCommandToServer("CREATE TABLE " + randomName2 + " (ee, rr, tt, yy);");
        String response2 = sendCommandToServer("ALTER TABLE " + randomName2 + " DROP kk ;");
        assertFalse(response2.contains("[OK]"));
        assertTrue(response2.contains("[ERROR]"));

        // AlterationType invalid
        String randomName3 = generateRandomName();
        sendCommandToServer("CREATE TABLE " + randomName3 + " (ee, rr, tt, yy);");
        String response3 = sendCommandToServer("ALTER TABLE " + randomName3 + "TT kk ;");
        assertFalse(response3.contains("[OK]"));
        assertTrue(response3.contains("[ERROR]"));
    }

    @Test
    public void testINSERT() {
        // "INSERT " "INTO " [TableName] " VALUES" "(" <ValueList> ")"
        // Table doesn't exist
        String randomName1 = generateRandomName();
        String response1 = sendCommandToServer("INSERT INTO " + randomName1 + " VALUES ('Simon', 65, TRUE);");
        assertFalse(response1.contains("[OK]"));
        assertTrue(response1.contains("[ERROR]"));

        // Attributes number incorrect
        String randomName2 = generateRandomName();
        sendCommandToServer("CREATE TABLE " + randomName2 + " (ee, rr, tt, yy);");
        String response2 = sendCommandToServer("INSERT INTO " + randomName2 + " VALUES ('Simon', 65, TRUE);");
        assertFalse(response2.contains("[OK]"));
        assertTrue(response2.contains("[ERROR]"));

        // ValueLis invalid
        String randomName3 = generateRandomName();
        sendCommandToServer("CREATE TABLE " + randomName2 + " (ee, rr, tt, yy);");
        String response3 = sendCommandToServer("INSERT INTO " + randomName3 + " VALUES (Simon, 65, TRUE);");
        assertFalse(response3.contains("[OK]"));
        assertTrue(response3.contains("[ERROR]"));
    }

    @Test
    public void testSELECT() {
        // "SELECT " <WildAttribList> " FROM " [TableName] | "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
        // Table doesn't exist
        String randomName1 = generateRandomName();
        String response1 = sendCommandToServer("SELECT * FROM " + randomName1 + " ;");
        assertFalse(response1.contains("[OK]"));
        assertTrue(response1.contains("[ERROR]"));

        // WildAttribList invalid
        String randomName2 = generateRandomName();
        sendCommandToServer("CREATE TABLE " + randomName2 + " (ee, rr, tt, yy);");
        String response2 = sendCommandToServer("SELECT nn, mm FROM " + randomName2 + " ;");
        assertFalse(response2.contains("[OK]"));
        assertTrue(response2.contains("[ERROR]"));

        // Condition invalid
        String randomName3 = generateRandomName();
        sendCommandToServer("CREATE TABLE " + randomName3 + " (ee, rr, tt, yy);");
        sendCommandToServer("INSERT INTO " + randomName3 + " VALUES ('Simon', 65, TRUE, 1.0);");
        sendCommandToServer("INSERT INTO " + randomName3 + " VALUES ('Simon', 65, TRUE, 3.0);");
        sendCommandToServer("INSERT INTO " + randomName3 + " VALUES ('Simon', 65, TRUE, 2.0);");
        String response3 = sendCommandToServer("SELECT rr, tt FROM " + randomName3 + " WHERE mm = 'Simon';");
        assertFalse(response3.contains("[OK]"));
    }

    @Test
    public void testUPDATE() {
        // "UPDATE " [TableName] " SET " <NameValueList> " WHERE " <Condition>
        // Table doesn't exist
        String randomName1 = generateRandomName();
        String response1 = sendCommandToServer("UPDATE " + randomName1 + " SET ee=1, tt=2, yy=3 WHERE ee='Simon';");
        assertFalse(response1.contains("[OK]"));
        assertTrue(response1.contains("[ERROR]"));

        // NameValueList invalid
        String randomName2 = generateRandomName();
        sendCommandToServer("CREATE TABLE " + randomName2 + " (ee, rr, tt, yy);");
        sendCommandToServer("INSERT INTO " + randomName2 + " VALUES ('Simon', 65, TRUE, 1.0);");
        sendCommandToServer("INSERT INTO " + randomName2 + " VALUES ('Simon', 65, TRUE, 3.0);");
        sendCommandToServer("INSERT INTO " + randomName2 + " VALUES ('Simon', 65, TRUE, 2.0);");
        String response2 = sendCommandToServer("UPDATE " + randomName2 + " SET kk=1, ll=2, yy=3 WHERE ee='Simon';");
        assertFalse(response2.contains("[OK]"));
        assertTrue(response2.contains("[ERROR]"));

        // Condition invalid
        String randomName3 = generateRandomName();
        sendCommandToServer("CREATE TABLE " + randomName3 + " (ee, rr, tt, yy);");
        sendCommandToServer("INSERT INTO " + randomName3 + " VALUES ('Simon', 65, TRUE, 1.0);");
        sendCommandToServer("INSERT INTO " + randomName3 + " VALUES ('Simon', 65, TRUE, 3.0);");
        sendCommandToServer("INSERT INTO " + randomName3 + " VALUES ('Simon', 65, TRUE, 2.0);");
        String response3 = sendCommandToServer("UPDATE " + randomName3 + " SET ee=1, rr=2, yy=3 WHERE mm='Simon';");
        assertFalse(response3.contains("[OK]"));
        assertTrue(response3.contains("[ERROR]"));
    }

    @Test
    public void testDELETE() {
        // "DELETE " "FROM " [TableName] " WHERE " <Condition>
        // Table doesn't exist
        String randomName1 = generateRandomName();
        String response1 = sendCommandToServer("DELETE FROM " + randomName1 + " WHERE ee='Simon';");
        assertFalse(response1.contains("[OK]"));
        assertTrue(response1.contains("[ERROR]"));

        // Condition invalid
        String randomName3 = generateRandomName();
        sendCommandToServer("CREATE TABLE " + randomName3 + " (ee, rr, tt, yy);");
        sendCommandToServer("INSERT INTO " + randomName3 + " VALUES ('Simon', 65, TRUE, 1.0);");
        sendCommandToServer("INSERT INTO " + randomName3 + " VALUES ('Simon', 65, TRUE, 3.0);");
        sendCommandToServer("INSERT INTO " + randomName3 + " VALUES ('Simon', 65, TRUE, 2.0);");
        String response3 = sendCommandToServer("DELETE FROM " + randomName3 + " WHERE gg='Simon';");
        assertFalse(response3.contains("[OK]"));
        assertTrue(response3.contains("[ERROR]"));
    }

    @Test
    public void testJOIN() {
        // "JOIN " [TableName] " AND " [TableName] " ON " [AttributeName] " AND " [AttributeName]
        // Table doesn't exist
        String randomName1 = generateRandomName();
        String randomName2 = generateRandomName();
        String response1 = sendCommandToServer("JOIN " + randomName1 + " AND "  + randomName2 + " ON jj AND kk");
        assertFalse(response1.contains("[OK]"));
        assertTrue(response1.contains("[ERROR]"));

        // AttributeName invalid
        String randomName5 = generateRandomName();
        String randomName6 = generateRandomName();
        sendCommandToServer("CREATE TABLE " + randomName5 + " (ee, rr, tt, yy);");
        sendCommandToServer("CREATE TABLE " + randomName6 + " (ee, rr, tt, yy);");
        sendCommandToServer("INSERT INTO " + randomName5 + " VALUES ('Simon', 65, TRUE, 1.0);");
        sendCommandToServer("INSERT INTO " + randomName5 + " VALUES ('Simon', 65, TRUE, 3.0);");
        sendCommandToServer("INSERT INTO " + randomName5 + " VALUES ('Simon', 65, TRUE, 2.0);");
        sendCommandToServer("INSERT INTO " + randomName6 + " VALUES ('Simon', 65, TRUE, 1.0);");
        sendCommandToServer("INSERT INTO " + randomName6 + " VALUES ('Simon', 70, TRUE, 3.0);");
        sendCommandToServer("INSERT INTO " + randomName6 + " VALUES ('Simon', 70, TRUE, 2.0);");
        String response2 = sendCommandToServer("JOIN " + randomName5 + " AND "  + randomName6 + " ON jj AND kk");
        assertFalse(response2.contains("[OK]"));
        assertTrue(response2.contains("[ERROR]"));
    }

}



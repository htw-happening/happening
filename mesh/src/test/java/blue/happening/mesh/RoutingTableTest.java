package blue.happening.mesh;

import junit.framework.TestCase;


public class RoutingTableTest extends TestCase {

    private RoutingTable routingTable;

    protected void setUp() throws Exception {
        super.setUp();
        routingTable = new RoutingTable();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        routingTable = null;
    }
}

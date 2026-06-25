package domain.transportation;

import enums.SiteType;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Site Domain Class Tests")
public class SiteTest {

    private Site site;
    private DeliveryZone testZone;

    @BeforeEach
    void setUp() {
        testZone = new DeliveryZone(1, "North Zone");
        site = new Site(
            101,
            "Main Supllier",
            "123 Logistics Way",
            "555-0199",
            "Alice Manager",
            SiteType.SUPPLIER, 
            testZone
        );
    }

    @Test
    @DisplayName("Should successfully create a site with correct basic properties")
    void testValidSiteCreation() {
        assertEquals(101, site.getId(), "ID should match the initialized value");
        assertEquals("Main Supllier", site.getName(), "Name should match the initialized value");
        assertEquals("123 Logistics Way", site.getAddress(), "Address should match the initialized value");
        assertEquals("555-0199", site.getPhoneNumber(), "Phone number should match the initialized value");
        assertEquals("Alice Manager", site.getContactPerson(), "Contact person should match the initialized value");
        assertEquals(SiteType.SUPPLIER, site.getSiteType(), "SiteType should match the initialized value");
        assertEquals(testZone, site.getDeliveryZone(), "DeliveryZone should match the initialized instance");
    }

    @Test
    @DisplayName("Should return exactly the same string instances and format")
    void testStringIntegrity() {
        assertNotNull(site.getName(), "Name should not be null");
        assertNotNull(site.getAddress(), "Address should not be null");
        assertNotNull(site.getPhoneNumber(), "Phone number should not be null");
        assertNotNull(site.getContactPerson(), "Contact person should not be null");
        
        assertTrue(site.getPhoneNumber().contains("-"), "Phone number format should remain intact");
    }

    @Test
    @DisplayName("Should accurately identify if the site is a branch")
    void testIsBranchLogic() {
        // The default site in setUp is SUPPLIER so it should NOT be a BRANCH
        assertFalse(site.isBranch(), "isBranch should return false for a non-branch site type");
        
        Site branchSite = new Site(
            102, 
            "Downtown Branch", 
            "456 Market St", 
            "555-0200", 
            "Bob StoreManager", 
            SiteType.BRANCH, 
            testZone
        );
        
        assertTrue(branchSite.isBranch(), "isBranch should return true for a BRANCH site type");
    }
}
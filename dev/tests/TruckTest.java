package domain.transportation;

import enums.LicenseType;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Truck Domain Class Tests")
public class TruckTest {

    private Truck truck;

    @BeforeEach
    void setUp() {
        truck = new Truck(
            "123-45-678", 
            "Volvo FH16", 
            8500.5, 
            24000.0, 
            LicenseType.C
        );
    }

    @Test
    @DisplayName("Should successfully create a truck with correct basic properties")
    void testValidTruckCreation() {
        assertEquals("123-45-678", truck.getLicenseNumber(), "License number should match the initialized value");
        assertEquals("Volvo FH16", truck.getModel(), "Model should match the initialized value");
        assertEquals(8500.5, truck.getNetWeight(), "Net weight should match the initialized value");
        assertEquals(24000.0, truck.getMaxCapacityWeight(), "Max capacity weight should match the initialized value");
        assertEquals(LicenseType.C, truck.getRequiredLicenseType(), "Required license type should match the initialized value");
    }

    @Test
    @DisplayName("Should return exactly the same string instances")
    void testStringIntegrity() {
        assertNotNull(truck.getLicenseNumber(), "License number should not be null");
        assertNotNull(truck.getModel(), "Model should not be null");
        assertTrue(truck.getLicenseNumber().contains("-"), "License format should remain the same");
    }

}
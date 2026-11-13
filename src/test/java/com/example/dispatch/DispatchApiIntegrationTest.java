package com.example.dispatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DispatchApiIntegrationTest {
    @Autowired private MockMvc mvc;

    @Test void acceptsOrdersVehiclesAndReturnsDispatchPlan() throws Exception {
        mvc.perform(post("/api/dispatch/orders").contentType(MediaType.APPLICATION_JSON).content("""
                {"orders":[{"orderId":"IT-ORD-1","latitude":28.6139,"longitude":77.2090,"address":"Connaught Place","packageWeight":12,"priority":"HIGH"}]}
                """))
                .andExpect(status().isAccepted()).andExpect(jsonPath("$.status").value("success"));
        mvc.perform(post("/api/dispatch/vehicles").contentType(MediaType.APPLICATION_JSON).content("""
                {"vehicles":[{"vehicleId":"IT-VEH-1","capacity":50,"currentLatitude":28.7041,"currentLongitude":77.1025,"currentAddress":"Karol Bagh"}]}
                """))
                .andExpect(status().isAccepted());
        mvc.perform(get("/api/dispatch/plan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dispatchPlan[0].vehicleId").value("IT-VEH-1"))
                .andExpect(jsonPath("$.dispatchPlan[0].totalLoad").value(12.0))
                .andExpect(jsonPath("$.dispatchPlan[0].assignedOrders[0].orderId").value("IT-ORD-1"));
    }

    @Test void rejectsInvalidOrderPayload() throws Exception {
        mvc.perform(post("/api/dispatch/orders").contentType(MediaType.APPLICATION_JSON)
                .content("{\"orders\":[{\"orderId\":\"BAD\",\"latitude\":120,\"longitude\":0,\"address\":\"X\",\"packageWeight\":-1,\"priority\":\"HIGH\"}]}"))
                .andExpect(status().isBadRequest());
    }
}

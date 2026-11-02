package com.banca.digital.infrastructure;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** Prueba de punta a punta: HTTP -> controlador -> caso de uso -> JPA (H2 en memoria). */
@SpringBootTest
@AutoConfigureMockMvc
class CajeroIntegrationTest {

    private static final String CLAVE = "Clave12345";
    private static final String PIN = "4321";

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    // ---------- helpers ----------

    private String registrarYLogin() throws Exception {
        String correo = UUID.randomUUID() + "@test.com";
        mvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content("""
                {"nombre":"Cliente Prueba","correo":"%s","telefono":"3001234567",
                 "direccion":"Calle 1 # 2-3","clave":"%s","pin":"%s"}""".formatted(correo, CLAVE, PIN)))
                .andExpect(status().isCreated());

        String respuesta = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"%s\",\"clave\":\"%s\"}".formatted(correo, CLAVE)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode nodo = json.readTree(respuesta);
        return nodo.get("token").asText();
    }

    private ResultActions depositar(String token, long monto) throws Exception {
        return mvc.perform(post("/api/cuentas/depositos").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content("{\"monto\":" + monto + "}"));
    }

    private ResultActions retirar(String token, long monto, String pin) throws Exception {
        return mvc.perform(post("/api/cajero/retiros").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"monto\":" + monto + ",\"pin\":\"" + pin + "\"}"));
    }

    // ---------- pruebas ----------

    @Test
    void endpointsProtegidosExigenToken() throws Exception {
        mvc.perform(get("/api/cuentas/saldo")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/cajero/retiros").contentType(MediaType.APPLICATION_JSON)
                .content("{\"monto\":10000,\"pin\":\"1234\"}")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginConClaveIncorrectaDevuelve401() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"nadie@test.com\",\"clave\":\"incorrecta1\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value("CREDENCIALES_INVALIDAS"));
    }

    @Test
    void consultaSaldoYDeposito() throws Exception {
        String token = registrarYLogin();

        mvc.perform(get("/api/cuentas/saldo").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(0));

        depositar(token, 150_000).andExpect(status().isOk()).andExpect(jsonPath("$.saldo").value(150_000));
        depositar(token, 0).andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("MONTO_INVALIDO"));
        depositar(token, -5).andExpect(status().isBadRequest());
    }

    @Test
    void actualizaDatosPersonalesYCambiaClave() throws Exception {
        String token = registrarYLogin();

        mvc.perform(put("/api/usuarios/perfil").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"telefono\":\"3109998877\",\"direccion\":\"Carrera 27 # 10-20\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telefono").value("3109998877"));

        mvc.perform(put("/api/usuarios/perfil").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"correo\":\"no-es-correo\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("CORREO_INVALIDO"));

        mvc.perform(put("/api/usuarios/clave").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"claveActual\":\"equivocada1\",\"claveNueva\":\"NuevaClave99\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("CLAVE_ACTUAL_INCORRECTA"));

        mvc.perform(put("/api/usuarios/clave").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"claveActual\":\"" + CLAVE + "\",\"claveNueva\":\"NuevaClave99\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void retiroExitosoEntregaMenorCantidadDeBilletes() throws Exception {
        String token = registrarYLogin();
        depositar(token, 2_000_000).andExpect(status().isOk());

        retirar(token, 180_000, PIN)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montoRetirado").value(180_000))
                .andExpect(jsonPath("$.billetes", hasSize(4)))
                .andExpect(jsonPath("$.billetes[0].denominacion").value(100_000))
                .andExpect(jsonPath("$.billetes[0].cantidad").value(1))
                .andExpect(jsonPath("$.totalBilletes").value(4))
                .andExpect(jsonPath("$.nuevoSaldo").value(1_820_000))
                .andExpect(jsonPath("$.disponibleHoy").value(820_000));
    }

    @Test
    void retiroRechazaPinMontoMinimoYMontoNoFraccionable() throws Exception {
        String token = registrarYLogin();
        depositar(token, 500_000).andExpect(status().isOk());

        retirar(token, 50_000, "0000").andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("PIN_INCORRECTO"));
        retirar(token, 50_000, "12").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("PIN_FORMATO_INVALIDO"));
        retirar(token, 5_000, PIN).andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("MONTO_MINIMO"));
        retirar(token, 25_000, PIN).andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("MONTO_NO_FRACCIONABLE"))
                .andExpect(jsonPath("$.mensaje", containsString("$25.000")));
        retirar(token, 600_000, PIN).andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("SALDO_INSUFICIENTE"));

        // Ningún rechazo debe haber tocado el saldo
        mvc.perform(get("/api/cuentas/saldo").header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.saldo").value(500_000));
    }

    @Test
    void topeDiarioAcumuladoDeUnMillon() throws Exception {
        String token = registrarYLogin();
        depositar(token, 3_000_000).andExpect(status().isOk());

        retirar(token, 800_000, PIN).andExpect(status().isOk());
        retirar(token, 180_000, PIN).andExpect(status().isOk());        // acumulado 980.000
        retirar(token, 30_000, PIN).andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("TOPE_DIARIO_EXCEDIDO"))
                .andExpect(jsonPath("$.mensaje", containsString("Disponible hoy: $20.000")));
        retirar(token, 20_000, PIN).andExpect(status().isOk())          // justo en el tope
                .andExpect(jsonPath("$.disponibleHoy").value(0));
        retirar(token, 10_000, PIN).andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("TOPE_DIARIO_EXCEDIDO"));
    }
}

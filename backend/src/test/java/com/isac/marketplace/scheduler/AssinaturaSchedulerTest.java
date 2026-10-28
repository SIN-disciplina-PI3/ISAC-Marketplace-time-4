package com.isac.marketplace.scheduler;

import com.isac.marketplace.service.AssinaturaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AssinaturaScheduler")
class AssinaturaSchedulerTest {

    @Mock
    private AssinaturaService assinaturaService;

    @InjectMocks
    private AssinaturaScheduler scheduler;

    @Test
    @DisplayName("Deve executar rotina de inativação delegando ao AssinaturaService")
    void deveExecutarRotinaInativacao() {
        when(assinaturaService.inativarAssinaturasVencidas()).thenReturn(3);

        int resultado = scheduler.executarRotinaInativacao();

        assertEquals(3, resultado);
        verify(assinaturaService).inativarAssinaturasVencidas();
    }
}

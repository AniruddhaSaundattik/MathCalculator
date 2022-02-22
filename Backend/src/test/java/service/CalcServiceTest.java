package service;

import config.CalcConfig;
import model.Request;
import model.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import repository.AuditRepository;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CalcServiceTest {

    @InjectMocks
    CalcService calcService;

    @Mock
    CalcConfig config;

    @Mock
    AuditRepository repo;

    @Test
    void calcWithAudit() {
        Mockito.doReturn("test").when(config).getPropertyValue(Mockito.any());
        Request request = new Request();
        request.setExpr(new String[]{"1+2", "2+3"});
        Result result = calcService.calcWithAudit(request);
        assertEquals(500, result.getResponseCode());
    }
}
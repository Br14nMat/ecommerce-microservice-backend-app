package com.selimhorri.app.unit;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.domain.PaymentStatus;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.exception.wrapper.PaymentNotFoundException;
import com.selimhorri.app.repository.PaymentRepository;
import com.selimhorri.app.service.impl.PaymentServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private AutoCloseable closeable;

    private Payment payment;
    private PaymentDto paymentDto;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        payment = Payment.builder()
                .paymentId(1)
                .orderId(100)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        paymentDto = PaymentDto.builder()
                .paymentId(1)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        orderDto = OrderDto.builder()
                .orderId(100)
                .orderDate(LocalDateTime.now())
                .orderDesc("Test Order")
                .orderFee(99.99)
                .build();
    }

    @Test
    void testFindAll_ReturnsListOfPaymentDtos() {
        when(paymentRepository.findAll()).thenReturn(List.of(payment));
        when(restTemplate.getForObject(contains("/100"), eq(OrderDto.class))).thenReturn(orderDto);

        List<PaymentDto> result = paymentService.findAll();

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getOrderDto().getOrderId());
        assertEquals("Test Order", result.get(0).getOrderDto().getOrderDesc());

        verify(paymentRepository).findAll();
        verify(restTemplate).getForObject(contains("/100"), eq(OrderDto.class));
    }

    @Test
    void testFindById_ValidId_ReturnsPaymentDto() {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(payment));
        when(restTemplate.getForObject(contains("/100"), eq(OrderDto.class))).thenReturn(orderDto);

        PaymentDto result = paymentService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getPaymentId());
        assertEquals(100, result.getOrderDto().getOrderId());

        verify(paymentRepository).findById(1);
        verify(restTemplate).getForObject(contains("/100"), eq(OrderDto.class));
    }

    @Test
    void testFindById_InvalidId_ThrowsPaymentNotFoundException() {
        when(paymentRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.findById(99));

        verify(paymentRepository).findById(99);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void testDeleteById_CallsRepositoryDelete() {
        doNothing().when(paymentRepository).deleteById(1);

        paymentService.deleteById(1);

        verify(paymentRepository).deleteById(1);
    }
}

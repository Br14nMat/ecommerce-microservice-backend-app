package com.selimhorri.app.integration;

import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.domain.PaymentStatus;
import com.selimhorri.app.dto.OrderDto;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.repository.PaymentRepository;
import com.selimhorri.app.service.PaymentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();

        Payment payment = Payment.builder()
                .orderId(123)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        paymentRepository.save(payment);

        OrderDto mockOrder = OrderDto.builder()
                .orderId(123)
                .orderDate(LocalDateTime.now())
                .orderDesc("Descripción de prueba")
                .orderFee(150.0)
                .build();

        Mockito.when(restTemplate.getForObject(contains("/123"), eq(OrderDto.class)))
                .thenReturn(mockOrder);
    }

    @Test
    void testFindAllPayments() {
        List<PaymentDto> payments = paymentService.findAll();

        assertThat(payments).hasSize(1);

        PaymentDto payment = payments.get(0);
        assertThat(payment.getOrderDto().getOrderId()).isEqualTo(123);
        assertThat(payment.getIsPayed()).isTrue();
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);

        assertThat(payment.getOrderDto()).isNotNull();
        assertThat(payment.getOrderDto().getOrderId()).isEqualTo(123);
        assertThat(payment.getOrderDto().getOrderDesc()).isEqualTo("Descripción de prueba");
        assertThat(payment.getOrderDto().getOrderFee()).isEqualTo(150.0);
        assertThat(payment.getOrderDto().getOrderDate()).isNotNull();
    }

    @Test
    void testFindPaymentById() {
        Payment payment = Payment.builder()
                .orderId(456)
                .isPayed(false)
                .paymentStatus(PaymentStatus.IN_PROGRESS)
                .build();

        payment = paymentRepository.save(payment);

        OrderDto mockOrder = OrderDto.builder()
                .orderId(456)
                .orderDate(LocalDateTime.now())
                .orderDesc("Pedido pendiente")
                .orderFee(75.0)
                .build();

        Mockito.when(restTemplate.getForObject(contains("/456"), eq(OrderDto.class)))
                .thenReturn(mockOrder);

        PaymentDto result = paymentService.findById(payment.getPaymentId());

        assertThat(result).isNotNull();
        assertThat(result.getPaymentId()).isEqualTo(payment.getPaymentId());
        assertThat(result.getOrderDto().getOrderId()).isEqualTo(456);
        assertThat(result.getIsPayed()).isFalse();
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.IN_PROGRESS);

        assertThat(result.getOrderDto()).isNotNull();
        assertThat(result.getOrderDto().getOrderId()).isEqualTo(456);
        assertThat(result.getOrderDto().getOrderDesc()).isEqualTo("Pedido pendiente");
        assertThat(result.getOrderDto().getOrderFee()).isEqualTo(75.0);
    }

}

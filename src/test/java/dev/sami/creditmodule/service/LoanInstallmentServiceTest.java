package dev.sami.creditmodule.service;

import dev.sami.creditmodule.dto.PaidInstallmentsDTO;
import dev.sami.creditmodule.entity.LoanInstallment;
import dev.sami.creditmodule.repository.LoanInstallmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanInstallmentServiceTest {

    @Mock
    private LoanInstallmentRepository installmentRepository;

    @InjectMocks
    private LoanInstallmentService loanInstallmentService;

    private LoanInstallment createSampleInstallment(Long loanId, BigDecimal amount, int monthOffset) {
        return LoanInstallment.builder()
                .id(1L)
                .loanId(loanId)
                .amount(amount)
                .paidAmount(BigDecimal.ZERO)
                .isPaid(false)
                .dueDate(LocalDate.now().plusMonths(monthOffset))
                .build();
    }

    @Test
    void testCreateInstallment() {
        LoanInstallment installment = createSampleInstallment(1L, BigDecimal.valueOf(100), 1);
        
        when(installmentRepository.save(installment)).thenReturn(installment);

        loanInstallmentService.createInstallment(installment);

        verify(installmentRepository).save(installment);
    }

    @Test
    void testGetInstallmentsByLoan() {
        Long loanId = 1L;
        List<LoanInstallment> installments = Arrays.asList(
            createSampleInstallment(loanId, BigDecimal.valueOf(100), 1),
            createSampleInstallment(loanId, BigDecimal.valueOf(100), 2)
        );

        when(installmentRepository.findByLoanIdOrderByDueDate(loanId)).thenReturn(installments);

        List<LoanInstallment> result = loanInstallmentService.getInstallmentsByLoan(loanId);

        assertEquals(2, result.size());
        verify(installmentRepository).findByLoanIdOrderByDueDate(loanId);
    }

    @Test
    void testGetPendingInstallments() {
        Long loanId = 1L;
        List<LoanInstallment> pendingInstallments = Arrays.asList(
            createSampleInstallment(loanId, BigDecimal.valueOf(100), 1),
            createSampleInstallment(loanId, BigDecimal.valueOf(100), 2)
        );

        when(installmentRepository.findByLoanIdAndIsPaidFalseOrderByDueDate(loanId))
            .thenReturn(pendingInstallments);

        List<LoanInstallment> result = loanInstallmentService.getPendingInstallments(loanId);

        assertEquals(2, result.size());
        verify(installmentRepository).findByLoanIdAndIsPaidFalseOrderByDueDate(loanId);
    }

    @Test
    void testPayInstallmentsFullPayment() {
        List<LoanInstallment> pendingInstallments = Arrays.asList(
            createSampleInstallment(1L, BigDecimal.valueOf(100), 1),
            createSampleInstallment(1L, BigDecimal.valueOf(100), 2)
        );
        BigDecimal paymentAmount = BigDecimal.valueOf(200);

        when(installmentRepository.save(pendingInstallments.get(0))).thenReturn(pendingInstallments.get(0));
        when(installmentRepository.save(pendingInstallments.get(1))).thenReturn(pendingInstallments.get(1));

        PaidInstallmentsDTO result = loanInstallmentService.payInstallments(pendingInstallments, paymentAmount);

        assertEquals(2, result.getInstallmentsPaid());
        assertEquals(paymentAmount, result.getAmountSpent());
        verify(installmentRepository).save(pendingInstallments.get(0));
        verify(installmentRepository).save(pendingInstallments.get(1));
    }

    @Test
    void testPayInstallmentsPartialPayment() {
        List<LoanInstallment> pendingInstallments = Arrays.asList(
            createSampleInstallment(1L, BigDecimal.valueOf(100), 1),
            createSampleInstallment(1L, BigDecimal.valueOf(100), 2)
        );
        BigDecimal paymentAmount = BigDecimal.valueOf(150);

        when(installmentRepository.save(pendingInstallments.get(0))).thenReturn(pendingInstallments.get(0));

        PaidInstallmentsDTO result = loanInstallmentService.payInstallments(pendingInstallments, paymentAmount);

        assertEquals(1, result.getInstallmentsPaid());
        assertEquals(BigDecimal.valueOf(100), result.getAmountSpent());
        verify(installmentRepository).save(pendingInstallments.get(0));
    }

    @Test
    void testPayInstallmentsInsufficientAmount() {
        List<LoanInstallment> pendingInstallments = Arrays.asList(
            createSampleInstallment(1L, BigDecimal.valueOf(100), 1),
            createSampleInstallment(1L, BigDecimal.valueOf(100), 2)
        );
        BigDecimal paymentAmount = BigDecimal.valueOf(50);

        PaidInstallmentsDTO result = loanInstallmentService.payInstallments(pendingInstallments, paymentAmount);

        assertEquals(0, result.getInstallmentsPaid());
        assertEquals(BigDecimal.ZERO, result.getAmountSpent());
    }

    @Test
    void testPayInstallmentsEmptyList() {
        List<LoanInstallment> pendingInstallments = List.of();
        BigDecimal paymentAmount = BigDecimal.valueOf(100);

        PaidInstallmentsDTO result = loanInstallmentService.payInstallments(pendingInstallments, paymentAmount);

        assertEquals(0, result.getInstallmentsPaid());
        assertEquals(BigDecimal.ZERO, result.getAmountSpent());
    }
} 
package com.denisneagu.primenumberapi.service.impl;

import com.denisneagu.primenumberapi.dto.PrimeNumberExecutionResponse;
import com.denisneagu.primenumberapi.dto.PrimeNumberResponse;
import com.denisneagu.primenumberapi.enums.Algorithm;
import com.denisneagu.primenumberapi.exception.UnknownAlgorithmException;
import com.denisneagu.primenumberapi.service.AlgorithmService;
import com.denisneagu.primenumberapi.service.PrimeNumberService;
import com.denisneagu.primenumberapi.util.Constant;
import com.denisneagu.primenumberapi.util.PrimeNumberExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrimeNumberServiceImpl implements PrimeNumberService {
    private final AlgorithmService algorithmService;

    private long[] getPrimeNumbersAtAlgorithm(long startAt, long limit, Algorithm algorithm) {
        if (algorithm == null) {
            throw new UnknownAlgorithmException(Constant.UNKNOWN_ALGORITHM);
        }

        switch (algorithm) {
            case NAIVE_TRIAL_DIVISION:
                return algorithmService.getPrimeNumbersUsingNaiveTrialDivision(startAt, limit);
            default:
                throw new UnknownAlgorithmException(Constant.UNKNOWN_ALGORITHM);
        }
    }

    @Override
    public PrimeNumberResponse getPrimeNumbers(long limit, Algorithm algorithm, boolean cache) {
            PrimeNumberExecutionResponse<long[]> executionResponse = PrimeNumberExecution
                    .getPrimeNumberWithExecutionTime(() -> {
                        return getPrimeNumbersAtAlgorithm(2, limit, algorithm);
                    });

            return new PrimeNumberResponse(
                    algorithm,
                    false,
                    executionResponse.execDurationTimeInNs(),
                    executionResponse.execDurationTimeInMs(),
                    executionResponse.response());
    }
}
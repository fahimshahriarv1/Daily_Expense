package com.fahimshahriarv1.dailyexpense.domain.usecase.account

import com.fahimshahriarv1.dailyexpense.domain.model.Income
import com.fahimshahriarv1.dailyexpense.domain.repository.IncomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIncomesUseCase @Inject constructor(
    private val incomeRepository: IncomeRepository
) {
    operator fun invoke(): Flow<List<Income>> = incomeRepository.getAllIncomes()
}

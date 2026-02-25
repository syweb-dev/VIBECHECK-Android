/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package top.lucanex.top.vibecheck.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FileRepository(application)
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _budget = MutableStateFlow(0.0)
    val budget: StateFlow<Double> = _budget.asStateFlow()
    private val _budgetResetDay = MutableStateFlow(1)
    val budgetResetDay: StateFlow<Int> = _budgetResetDay.asStateFlow()
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    init {
        loadTransactions()
        loadBudget()
        checkRecurringTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            _transactions.value = repository.getAllTransactions()
        }
    }

    private fun loadBudget() {
        viewModelScope.launch(Dispatchers.IO) {
            _budget.value = repository.getBudget()
            _budgetResetDay.value = repository.getBudgetResetDay()
        }
    }

    private fun checkRecurringTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkAndGenerateRecurringTransactions()
            _transactions.value = repository.getAllTransactions()
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTransaction(transaction)
            _transactions.value = repository.getAllTransactions()
        }
    }

    fun addRecurringTransaction(rt: RecurringTransaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRecurringTransaction(rt)
            // Check if the new rule triggers immediate generation
            repository.checkAndGenerateRecurringTransactions()
            _transactions.value = repository.getAllTransactions()
        }
    }

    fun setBudget(amount: Double, autoResetDay: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setBudget(amount, autoResetDay)
            _budget.value = amount
            _budgetResetDay.value = autoResetDay.coerceIn(1, 31)
        }
    }

    fun clearBudgetOnly() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearBudgetOnly()
            _budget.value = 0.0
            _budgetResetDay.value = repository.getBudgetResetDay()
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransaction(id)
            _transactions.value = repository.getAllTransactions()
        }
    }

    fun clearAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAll()
            _transactions.value = emptyList()
            _budget.value = 0.0
            _budgetResetDay.value = 1
        }
    }

    fun postUiMessage(message: String) {
        _uiMessage.value = message
    }

    fun consumeUiMessage() {
        _uiMessage.value = null
    }
}

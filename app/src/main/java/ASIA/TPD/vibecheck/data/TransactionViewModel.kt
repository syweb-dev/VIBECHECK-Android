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

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            _transactions.value = repository.getAllTransactions()
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTransaction(transaction)
            _transactions.value = repository.getAllTransactions()
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
        }
    }
}

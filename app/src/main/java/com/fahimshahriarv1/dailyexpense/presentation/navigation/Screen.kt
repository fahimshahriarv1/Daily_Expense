package com.fahimshahriarv1.dailyexpense.presentation.navigation

sealed class Screen(val route: String, val label: String) {
    data object Expense : Screen("expense", "Expenses")
    data object Stats : Screen("stats", "Stats")
    data object Account : Screen("account", "Accounts")
}

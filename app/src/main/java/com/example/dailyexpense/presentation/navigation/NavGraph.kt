package com.example.dailyexpense.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dailyexpense.presentation.account.AccountScreen
import com.example.dailyexpense.presentation.expense.ExpenseScreen
import com.example.dailyexpense.presentation.stats.StatsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Expense.route,
        modifier = modifier
    ) {
        composable(Screen.Expense.route) {
            ExpenseScreen()
        }
        composable(Screen.Stats.route) {
            StatsScreen()
        }
        composable(Screen.Account.route) {
            AccountScreen()
        }
    }
}

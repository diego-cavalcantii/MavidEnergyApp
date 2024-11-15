package com.example.mavidenergyapp

import android.view.View
import androidx.navigation.NavController

class Menu (
    private val rootView: View,
    private val navController: NavController
){
    fun setupMenu() {
        rootView.findViewById<View>(R.id.buttonHistory)?.setOnClickListener{
            navController.navigate(R.id.historyFragment)
        }
        rootView.findViewById<View>(R.id.buttonConsulta)?.setOnClickListener{
            navController.navigate(R.id.homeFragment)
        }
        rootView.findViewById<View>(R.id.buttonCuriosidade)?.setOnClickListener{
            navController.navigate(R.id.curiositiesFragment)
        }
    }
}
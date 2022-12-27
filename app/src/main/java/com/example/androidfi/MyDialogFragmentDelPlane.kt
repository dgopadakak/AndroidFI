package com.example.androidfi

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class MyDialogFragmentDelPlane: DialogFragment()
{
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val arguments: Bundle? = arguments
        val planeName = arguments?.getString("name")
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Будет удален самолет: $planeName")
            .setTitle("Внимание!")
            .setPositiveButton("Продолжить"
            ) { _, _ -> (activity as MainActivity?)?.delTask() }
            .setNegativeButton("Отмена") { _, _ -> }
        return builder.create()
    }
}
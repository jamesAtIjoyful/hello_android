package com.hello

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlertDialogFragment : DialogFragment() {
    companion object INSTANCE {
        const val TAG = "AlertDialogFragment"
    }

    private val viewMode: MainViewModel by activityViewModels()

    @Inject
    lateinit var model: MainViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        println("${model.sayHello()}, ${viewMode.hashCode()}, ${model.hashCode()}")
        return AlertDialog.Builder(requireContext())
            .setTitle("Hello World")
            .create()
    }
}
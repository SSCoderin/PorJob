package com.ssc.projob.ui.my_applied

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ssc.projob.databinding.FragmentMyAppliedBinding
import com.ssc.projob.databinding.FragmentSlideshowBinding
import com.ssc.projob.ui.my_applied.MyAppliedViewModel

class MyApplied : Fragment() {

    private var _binding: FragmentMyAppliedBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(MyAppliedViewModel::class.java)

        _binding = FragmentMyAppliedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
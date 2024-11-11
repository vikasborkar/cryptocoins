package com.example.cryptocoins

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.example.cryptocoins.databinding.FragmentBottomSheetFilterBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private val args: FilterBottomSheetFragmentArgs by navArgs()

    private var _binding: FragmentBottomSheetFilterBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.let {
            val sheet = it as BottomSheetDialog
            sheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.apply {
            chipActive.isChecked = args.filter.isActive ?: false
            chipInactive.isChecked =
                if (args.filter.isActive != null) args.filter.isActive == false
                else false
            chipOnlyCoins.isChecked = args.filter.type == Filter.TYPE_COIN
            chipOnlyTokens.isChecked = args.filter.type == Filter.TYPE_TOKEN
            chipNew.isChecked = args.filter.isNew
        }
    }

    override fun onDismiss(dialog: DialogInterface) {

        val filter = Filter(
            isActive = if (binding.groupActive.checkedChipIds.size == 0) null else binding.chipActive.isChecked,
            type = if (binding.groupType.checkedChipIds.size == 0) "" else if (binding.chipOnlyCoins.isChecked) Filter.TYPE_COIN else Filter.TYPE_TOKEN,
            isNew = binding.chipNew.isChecked
        )

        setFragmentResult(REQ_FILTER, bundleOf(ARG_FILTER to filter))

        super.onDismiss(dialog)
    }

    companion object {
        const val REQ_FILTER = "FilterBottomSheetFragment.REQ_FILTER"
        const val ARG_FILTER = "FilterBottomSheetFragment.ARG_FILTER"
    }
}
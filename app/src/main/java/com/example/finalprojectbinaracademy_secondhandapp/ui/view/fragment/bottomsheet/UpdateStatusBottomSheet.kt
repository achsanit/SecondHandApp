package com.example.finalprojectbinaracademy_secondhandapp.ui.view.fragment.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.finalprojectbinaracademy_secondhandapp.R
import com.example.finalprojectbinaracademy_secondhandapp.databinding.FragmentUpdateStatusBottomSheetBinding
import com.example.finalprojectbinaracademy_secondhandapp.ui.view.fragment.dialog.LoadingDialog
import com.example.finalprojectbinaracademy_secondhandapp.ui.viewmodel.SaleListViewModel
import com.example.finalprojectbinaracademy_secondhandapp.utils.Status
import com.example.finalprojectbinaracademy_secondhandapp.utils.errorToast
import com.example.finalprojectbinaracademy_secondhandapp.utils.successToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class UpdateStatusBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentUpdateStatusBottomSheetBinding? = null
    private val binding get() = _binding!!
    private var status: String = ""
    private val saleListViewModel: SaleListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateStatusBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUpdateStatus()
        radioButtonCheck()

        binding.btnUpdateStatus.setOnClickListener {
            updateStatusProduct()
        }

    }

    private fun radioButtonCheck() {
        binding.rgUpdateStatus.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rbAcc -> {
                    status = "sold"
                }
                R.id.rbDeclined -> {
                    status = "available"
                }
            }
        }
    }

    private fun updateStatusProduct() {
        val productId = arguments?.getInt(ID_PRODUCT)
        val idOrder = arguments?.getInt(ID_ORDER)

        if (productId != null && idOrder != null) {
            saleListViewModel.patchStatusProduct(productId, status)
            if (status == "available") {
                saleListViewModel.patchOrder(idOrder, "declined")
            } else {
                saleListViewModel.patchOrder(idOrder, "accepted")
            }
        }
    }

    private fun checkUpdateStatus() {
        val loadingDialog = LoadingDialog(requireContext())
        saleListViewModel.patchStatusProduct.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    loadingDialog.startLoading()
                }
                Status.SUCCESS -> {
                    loadingDialog.dismissLoading()
                    Toast(requireContext()).successToast("update berhasil...",requireContext())
                    dismiss()
                    findNavController().navigateUp()
                }
                Status.ERROR -> {
                    loadingDialog.dismissLoading()
                    Toast(requireContext()).errorToast(it.message.toString(), requireContext())
                }
            }
        }
    }

    companion object {
        const val TAG = "ModalBottomSheetChangeStatus"
        const val ID_PRODUCT = "idProduct"
        const val ID_ORDER = "idOrder"
    }
}
package com.example.finalprojectbinaracademy_secondhandapp.ui.view.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.finalprojectbinaracademy_secondhandapp.data.remote.model.GetSellerOrderResponseItem
import com.example.finalprojectbinaracademy_secondhandapp.databinding.FragmentInfoPenawarBinding
import com.example.finalprojectbinaracademy_secondhandapp.ui.view.fragment.bottomsheet.BottomSheetAcceptBid
import com.example.finalprojectbinaracademy_secondhandapp.ui.view.fragment.bottomsheet.UpdateStatusBottomSheet
import com.example.finalprojectbinaracademy_secondhandapp.ui.view.fragment.dialog.LoadingDialog
import com.example.finalprojectbinaracademy_secondhandapp.ui.viewmodel.SaleListViewModel
import com.example.finalprojectbinaracademy_secondhandapp.utils.Status
import com.example.finalprojectbinaracademy_secondhandapp.utils.convertISOTimeToDate
import com.example.finalprojectbinaracademy_secondhandapp.utils.errorToast
import com.example.finalprojectbinaracademy_secondhandapp.utils.rupiah
import org.koin.androidx.viewmodel.ext.android.viewModel


class InfoPenawar : Fragment() {
    private var _binding: FragmentInfoPenawarBinding? = null
    private val binding get() = _binding!!
    private val saleListViewModel: SaleListViewModel by viewModel()
    private val args: InfoPenawarArgs by navArgs()
    private var idOrder: Int = 0
    private var productId: Int = 0
    private var phoneNumber: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoPenawarBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getInfoPenawar()
        checkUpdateOrder()

        binding.btnAcc.setOnClickListener {
            accDialog()
        }
        binding.btnTolak.setOnClickListener {
            declineDialog()
        }
        binding.ivBackPenawar.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnStatus.setOnClickListener {
            bottomSheetChangeSatus()
        }
        binding.btnHubungi.setOnClickListener {
            intentToWasap(phoneNumber.toString(),"")
        }
    }

    private fun getInfoPenawar() {
        saleListViewModel.getSellerOrderById(args.idOrder)
        saleListViewModel.diminatiById.observe(viewLifecycleOwner) {
            when(it.status) {
                Status.SUCCESS -> {
                    it.data?.let { data ->
                        setupView(data)
                        idOrder = data.id
                        productId = data.productId
                        phoneNumber = data.user.phoneNumber.toLong()
                    }
                }
                Status.ERROR -> {
                    Toast(requireContext()).errorToast(it.message.toString(),requireContext())
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupView(data: GetSellerOrderResponseItem?) {
        data?.let {
            binding.tvNameProductDiminati.text = data.productName
            binding.tvPriceProductDiminati.text = rupiah(data.basePrice.toDouble())
            binding.tvBidPriceDIminati.text = "Ditawar ${rupiah(data.price.toDouble())}"
            binding.tvDateOrderDiminati.text = convertISOTimeToDate(data.createdAt)

            Glide.with(requireContext())
                .load(data.product.imageUrl)
                .centerCrop()
                .into(binding.ivProductDiminati)

            binding.tvBuyerName.text = data.user.fullName
            binding.tvBuyerCity.text = data.user.city

            if (it.status == "accepted") {
                binding.bidAcc.visibility = View.VISIBLE
                binding.accOrDecline.visibility = View.GONE
            } else if (it.status == "declined") {
                binding.accOrDecline.visibility = View.GONE
                binding.tvBidPriceDIminati.paintFlags = binding.tvBidPriceDIminati.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }

    private fun bottomSheetSuccessAcc() {
        val bottomSheet = BottomSheetAcceptBid()
        val bundle = Bundle()
        bundle.putInt(BottomSheetAcceptBid.ID_ORDER, idOrder)
        bottomSheet.arguments = bundle
        bottomSheet.show(parentFragmentManager, BottomSheetAcceptBid.TAG)
    }

    private fun bottomSheetChangeSatus() {
        val bottomSheet = UpdateStatusBottomSheet()
        val bundle = Bundle()
        bundle.putInt(UpdateStatusBottomSheet.ID_PRODUCT,productId)
        bundle.putInt(UpdateStatusBottomSheet.ID_ORDER,idOrder)
        bottomSheet.arguments = bundle
        bottomSheet.show(parentFragmentManager, UpdateStatusBottomSheet.TAG)
    }

    private fun accDialog() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Terima")
        dialog.setMessage("Apakah anda yakin ingin menerima tawaran ini?")

        dialog.setCancelable(true)
        dialog.setPositiveButton("YES"){dialogInterface, p1 ->
            if (idOrder != 0) {
                val status = "accepted"
                saleListViewModel.patchOrder(idOrder,status)
            }
        }

        dialog.setNegativeButton("NO"){dialogInterface, p1->
            dialogInterface.dismiss()
        }

        dialog.show()
    }

    private fun declineDialog() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Tolak")
        dialog.setMessage("Apakah anda yakin ingin menolak tawaran ini?")

        dialog.setCancelable(true)
        dialog.setPositiveButton("YES"){dialogInterface, p1 ->
            if (idOrder != 0) {
                val status = "declined"
                saleListViewModel.patchOrder(idOrder,status)
            }
        }

        dialog.setNegativeButton("NO"){dialogInterface, p1->
            dialogInterface.dismiss()
        }

        dialog.show()
    }

    private fun checkUpdateOrder() {
        val loadingDialog = LoadingDialog(requireContext())
        saleListViewModel.patchOrder.observe(viewLifecycleOwner) {
            when(it.status) {
                Status.SUCCESS -> {
                    loadingDialog.dismissLoading()
                    if (it.data?.status == "accepted") {
                        bottomSheetSuccessAcc()
                        binding.accOrDecline.visibility = View.GONE
                        binding.bidAcc.visibility = View.VISIBLE
                    } else {
                        binding.accOrDecline.visibility = View.GONE
                        binding.tvBidPriceDIminati.paintFlags = binding.tvBidPriceDIminati.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                }
                Status.ERROR -> {
                    Toast(requireContext()).errorToast(it.message.toString(),requireContext())
                    loadingDialog.dismissLoading()
                }
                Status.LOADING -> {
                    loadingDialog.startLoading()
                }
            }
        }
    }

    private fun intentToWasap(phone: String, message: String) {
//        if (appInstalledOrNot("com.whatsapp")) {
        val sendIntent = Intent("android.intent.action.MAIN")
        sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("62$phone") + "@s.whatsapp.net" ) //phone number without "+" prefix
        startActivity(sendIntent)
//        } else {
//            Toast.makeText(requireContext(), "Whats app not installed on your device", Toast.LENGTH_SHORT).show();
//        }
    }

    private fun appInstalledOrNot(url: String): Boolean {
        val packageManager = activity?.packageManager
        val app_installed: Boolean
        app_installed = try {
            packageManager?.getPackageInfo(url, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
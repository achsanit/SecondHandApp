package com.example.finalprojectbinaracademy_secondhandapp.ui.view.fragment.daftarJualvp

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.finalprojectbinaracademy_secondhandapp.data.local.model.SellerProduct
import com.example.finalprojectbinaracademy_secondhandapp.databinding.FragmentProdukBinding
import com.example.finalprojectbinaracademy_secondhandapp.ui.adapter.SellerProductAdapter
import com.example.finalprojectbinaracademy_secondhandapp.ui.adapter.SellerProductOnClick
import com.example.finalprojectbinaracademy_secondhandapp.ui.view.fragment.DaftarJualDirections
import com.example.finalprojectbinaracademy_secondhandapp.ui.viewmodel.SaleListViewModel
import com.example.finalprojectbinaracademy_secondhandapp.utils.Status
import com.example.finalprojectbinaracademy_secondhandapp.utils.successToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class Produk : Fragment() {
    private var _binding: FragmentProdukBinding? = null
    private val binding get() = _binding!!
    private val saleListViewModel: SaleListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProdukBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSellerProduct()
    }

    private fun getSellerProduct() {
        saleListViewModel.getSellerProduct()
        saleListViewModel.listProduct.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    setupListProduct(it.data)
                    binding.shimmerProduct.stopShimmer()
                    binding.shimmerProduct.visibility = View.GONE
                    binding.recylcerProduct.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.shimmerProduct.stopShimmer()
                    binding.shimmerProduct.visibility = View.GONE
                    binding.recylcerProduct.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    binding.shimmerProduct.startShimmer()
                    binding.shimmerProduct.visibility = View.VISIBLE
                    binding.recylcerProduct.visibility = View.GONE
                }
            }
        }
    }

    private fun setupListProduct(data: List<SellerProduct>?) {
        val recycler = binding.recylcerProduct
        data?.let {
            val adapter = SellerProductAdapter(object : SellerProductOnClick {
                override fun onItemClick(data: SellerProduct) {
                    val action = DaftarJualDirections.actionDaftarJualToBuyerDetailProduk(data.id)
                    findNavController().navigate(action)
                }

                override fun onItemLongClick(data: SellerProduct) {
                    Toast(requireContext()).successToast("${data.id}",requireContext())
                    vibratePhone()
                }

                override fun onHeaderClick() {
                    val action = DaftarJualDirections.actionDaftarJualToSellerPostProduct()
                    findNavController().navigate(action)
                }
            })
            adapter.SubmitListWithHeader(data)
            recycler.layoutManager = GridLayoutManager(requireContext(), 2)
            recycler.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun vibratePhone() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.EFFECT_TICK))
        } else {
            vibrator.vibrate(100)
        }
    }

}
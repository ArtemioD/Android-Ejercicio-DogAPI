package com.example.dogapi


import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dogapi.databinding.FragmentDogsBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DogsFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var binding: FragmentDogsBinding

    private lateinit var imgDogRandom: ImageView
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var dogAdapter: DogAdapter
    private var listadoImagenes = mutableListOf<String>()
    private lateinit var searchView: SearchView
    private lateinit var shimmerList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDogsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        imgDogRandom = binding.ivDogRandom
        shimmer = binding.shimmer
        recyclerView = binding.recyclerView
        searchView = binding.searchView
        shimmerList = binding.shimmerList

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        dogAdapter = DogAdapter(listadoImagenes)
        recyclerView.adapter = dogAdapter

        searchView.setOnQueryTextListener(this)

        showRandomImage()
    }

    private fun showRandomImage() {
        CoroutineScope(Dispatchers.IO).launch {
            shimmer.visibility = View.VISIBLE
            val call = getRetrofit().create(ApiService::class.java)
                .getRandomImg(URL_RANDOM_IMG)

            activity?.runOnUiThread {
                if (call.isSuccessful) {
                    val img = call.body()?.message
                    Picasso.get().load(img).into(imgDogRandom)
                    shimmer.visibility = View.GONE
                } else {
                    showError("Error al cargar la imagen")
                }
            }
        }

    }

    private fun showRazaList(raza: String) {
        shimmer.visibility = View.GONE
        imgDogRandom.visibility = View.GONE
        shimmerList.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiService::class.java)
                .getListImg("breed/$raza/images")

            activity?.runOnUiThread {
                if (call.isSuccessful) {
                    val images = call.body()?.message ?: emptyList()
                    listadoImagenes.clear()
                    listadoImagenes.addAll(images)
                    dogAdapter.notifyDataSetChanged()
                    shimmerList.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                } else {
                    showError("Error al cargar la lista imagenes")
                    shimmerList.visibility = View.GONE
                    imgDogRandom.visibility = View.VISIBLE
                }

            }
        }

    }

    private fun hideKeyboard() {
        // para ocultar el teclado desde fragment
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.viewRoot.windowToken, 0)
    }

    private fun showError(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            // en menuscula y sin espacios antes y después del String
            val queryClean = query.lowercase().trim()
            showRazaList(queryClean)
            hideKeyboard()
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }

    companion object {
        const val BASE_URL = "https://dog.ceo/api/"
        const val URL_RANDOM_IMG = "breeds/image/random"
    }

}
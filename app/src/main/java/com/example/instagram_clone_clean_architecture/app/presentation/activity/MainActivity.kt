package com.example.instagram_clone_clean_architecture.app.presentation.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.instagram_clone_clean_architecture.R
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.service.IntentService
import com.example.instagram_clone_clean_architecture.databinding.ActivityMainBinding
import com.example.library_base.domain.extension.getJpegByteArray
import com.example.library_base.domain.extension.setupNavControllerWithNavCallback
import com.example.library_base.presentation.activity.InjectionActivity
import com.example.library_base.presentation.navigation.NavigationManager
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.instance
import timber.log.Timber
import java.io.*

class MainActivity: InjectionActivity() {

    private val navController get() = appNavGraph.findNavController()

    private val navigationManager: NavigationManager by instance()

    private val intentService: IntentService by instance()

    private val viewModel: MainViewModel by instance()

    private val observer = Observer<MainViewModel.ViewState>() {
        Timber.d(it.toString())
    }

    private lateinit var binding: ActivityMainBinding

    fun loginSucceed() {
        viewModel.loadData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("Main Activity is created!")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initIntentService()
        setNavEventListener()
        setBottomNavigationController()
    }

    override fun onStart() {
        super.onStart()
        viewModel.stateLiveData.observe(this, observer)
        viewModel.loadData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IntentService.PHOTO_GALLERY_SERVICE_CODE -> {
                val uriString = data!!.dataString
                val uri = Uri.parse(uriString)
                viewModel.cacheUserSelectedImage(uri)
            }
            IntentService.CAMERA_SERVICE_CODE -> {
                val uriString = data!!.dataString
                val uri = Uri.parse(uriString)
                viewModel.cacheUserSelectedImage(uri)
            }
        }
    }

    private fun setNavEventListener() {
        navigationManager.setNavEventCallbackListener {
            try {
                navController.navigate(it)
            } catch (exception: Exception) {
                // Safe-Args cannot resolve navigation between features.
                // Thus, we must catch the exception when it cannot find destination,
                // and use the traditional way to do navigation.
                // By assigning destination id as action id, we can extract required information
                // from safe-args-generated navDir.
                if (exception is java.lang.IllegalArgumentException) {
                    navController.navigate(it.actionId, it.arguments)
                } else {
                    throw exception
                }
            }
        }
    }

    private fun setBottomNavigationController() {
        bottomNav.selectedItemId = R.id.featureSearchNavGraph

        bottomNav.setupNavControllerWithNavCallback(navController) setupNavControllerWithCallback@{
            when (it.itemId) {
                R.id.featureSearchNavGraph -> {
                    viewModel.onNavigateToSearch()
                    return@setupNavControllerWithCallback true
                }
                R.id.featureProfileNavGraph -> {
                    viewModel.onNavigateToProfile()
                    return@setupNavControllerWithCallback true
                }
                else -> throw IllegalArgumentException("Unknown destination ${it.itemId}")
            }
        }
    }

    private fun initIntentService() {
        intentService.init(this)
    }

    private fun getBitmapFromContentResolver(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(this.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    }
}
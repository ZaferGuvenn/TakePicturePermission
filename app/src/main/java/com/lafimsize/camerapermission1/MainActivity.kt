package com.lafimsize.camerapermission1

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.lafimsize.camerapermission1.databinding.ActivityMainBinding
import java.io.File
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    private lateinit var getPictureLauncher:ActivityResultLauncher<Intent>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var permissionArray:Array<String>
    private lateinit var binding:ActivityMainBinding


    private var permissionsGranted=false
    private var takePicUri:Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerLaunchers()


        val permission1=android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permission2=android.Manifest.permission.READ_EXTERNAL_STORAGE
        val permission3=android.Manifest.permission.CAMERA

        permissionArray= arrayOf(permission1,permission2,permission3)

        requestPermissionLauncher.launch(permissionArray)

    }

    private fun registerLaunchers(){

        requestPermissionLauncher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->


            permissions?.let { it ->
                val checkFalses=it.values.filter { !it }.any{ !it }
                if (!checkFalses){
                    permissionsGranted=false
                    Toast.makeText(applicationContext,"Tüm izinleri kabul etmelisiniz!", Toast.LENGTH_SHORT).show()
                }else{
                    permissionsGranted=true
                }


            }


        }


        takePictureLauncher=registerForActivityResult(ActivityResultContracts.TakePicture()){

            it?.let {
                if (it){
                    binding.imageView.setImageURI(takePicUri)
                    println(takePicUri)

                }
            }

        }


        getPictureLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

            it?.let {

                val picUri=it.data?.data

                binding.imageView.setImageURI(picUri)

            }
        }


    }

    fun addPicture(view: View) {

        val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getPictureLauncher.launch(intentToGallery)

    }


    fun takePicture(view: View) {
        val directory=getExternalFilesDir(Environment.DIRECTORY_ALARMS)
        val img=File(directory,"${Calendar.getInstance().timeInMillis}.jpg")
        takePicUri=FileProvider.getUriForFile(applicationContext,packageName,img)
        println(takePicUri)
        if (permissionsGranted){
            takePictureLauncher.launch(takePicUri)
        }else{
            requestPermissionLauncher.launch(permissionArray)
        }


    }

}
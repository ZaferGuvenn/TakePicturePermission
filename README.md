# TakePicturePermission
 1. Xml klasörü oluşturup içerisine dosya.xml dosyası oluşturup aşağıdaki kodu yazalım:

		<paths>
		      <external-path
		         name="external_files"
		         path="."/>

		</paths>

 2. Manifest xml dosyamıza istediğimiz permissionları ekleyip application tagları arasına provider tagı ve aşağıdakileri ekliyoruz:


        <provider
            android:authorities="com.lafimsize.camerapermission1"
            android:name="androidx.core.content.FileProvider"
            android:exported="false"
            android:grantUriPermissions="true">

            <meta-data android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/dosya"/>

        </provider>
//yukarıdaki providerı kameradan çektiğimiz resmi galeriye yazmak için oluşturmamız gerekiyor.

 3. MainActivity.kt dosyamız:

Aşağıda launcherlarımız ve onları register ettiğimiz fonksiyonumuz var

    class MainActivity : AppCompatActivity() {
        private lateinit var getPictureLauncher:ActivityResultLauncher<Intent>//galeriden resim almak için
        private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>//kameradan resim çekmek ve bunu galeriye kaydetmek için
    
        private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>//write-read ve camera izni almak için. Multiple olarak izin istediğimiz için teker teker yapmak yerine bir dizi olarak gönderelim.
    
        private lateinit var permissionArray:Array<String>//izinlerimizi tutan dizimiz
        private lateinit var binding:ActivityMainBinding
    
    
        private var permissionsGranted=false//kamera izni kontrolü
        private var takePicUri:Uri? = null//kameradan çektiğimiz resmin urisi
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
    
            binding=ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            registerLaunchers()//initialize ya da register işlemini ilk olarak yapmamız gerek.
    
    
            val permission1=android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            val permission2=android.Manifest.permission.READ_EXTERNAL_STORAGE
            val permission3=android.Manifest.permission.CAMERA
    
            permissionArray= arrayOf(permission1,permission2,permission3)
    
            requestPermissionLauncher.launch(permissionArray)//izinler dizimizi göterdik
    
        }
    
        private fun registerLaunchers(){//launcherları initialize ettiğimiz yer.
    
            requestPermissionLauncher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
    
    //burada birden fazla permission verdiğimiz için hepsinin true olmasını sağlamamız gerek. Bu yüzden 1 false bile olsa permissionGranted false olacak.
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
    
    //resim çekme launcherı
            takePictureLauncher=registerForActivityResult(ActivityResultContracts.TakePicture()){
    
                it?.let {
                    if (it){
                        binding.imageView.setImageURI(takePicUri)
                        println(takePicUri)
    
                    }
                }
    
            }
    
    //burada ise galeriden resim alıp gösteriyoruz
            getPictureLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
    
                it?.let {
    
                    val picUri=it.data?.data
    
                    binding.imageView.setImageURI(picUri)
    
                }
            }
    
    
        }
    //aşağıdaki fonksiyon galeriden resim seç butonu onclick olduğunda çalışacak
        fun addPicture(view: View) {
    
            val intentToGallery=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getPictureLauncher.launch(intentToGallery)
    
        }
    
    //aşağıdaki fonksiyon kameradan resim çek butonu onclik olduğunda çalışacak
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


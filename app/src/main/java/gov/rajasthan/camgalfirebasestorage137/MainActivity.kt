package gov.rajasthan.camgalfirebasestorage137

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.BitmapCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import gov.rajasthan.camgalfirebasestorage137.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val iCam = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val iGall = Intent(Intent.ACTION_GET_CONTENT)
        iGall.type = "image/*"

        val camLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val imgBitmap = it.data!!.extras!!.get("data") as Bitmap
                    binding.imageView.setImageBitmap(imgBitmap)

                    val baos = ByteArrayOutputStream()

                    imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                    val imgBytes = baos.toByteArray()

                    updateProfilePic(imgBytes)


                }
            }

        val galLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val imgBitmap = MediaStore.Images.Media.getBitmap(
                        applicationContext.contentResolver,
                        it.data!!.data
                    )
                    binding.imageView.setImageBitmap(imgBitmap)

                    val baos = ByteArrayOutputStream()

                    imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                    val imgBytes = baos.toByteArray()

                    updateProfilePic(imgBytes)

                }
            }


        binding.btnGal.setOnClickListener {
            galLauncher.launch(iGall)
        }
        binding.btnCam.setOnClickListener {
            camLauncher.launch(iCam)
        }


    }

    fun updateProfilePic(imgBytes : ByteArray){
        val storageRef = Firebase.storage
        val timeStamp = Calendar.getInstance().timeInMillis
        val imgRef =
            storageRef.reference.child("app_images/profile_pic/IMG_$timeStamp.png")


        imgRef.putBytes(imgBytes)
            .addOnSuccessListener {
                Log.d("Success", "${it.metadata}")

                imgRef.downloadUrl.addOnSuccessListener {
                    Log.d("ImgUrl", "$it")
                    var firestore = Firebase.firestore
                    /* firestore
                         .collection("Users")
                         .document("bvhsbvhsbvksjdbvjsdb") //logged in user UID
                         .update("profile_pic", it.toString())*/

                    firestore
                        .collection("Users")
                        .document("bvhsbvhsbvksjdbvjsdb")
                        .collection("profile_pictures")
                        .add(mapOf(
                            "imgPath" to "$it"
                        ))

                    firestore
                        .collection("Users")
                        .document("bvhsbvhsbvksjdbvjsdb") //logged in user UID
                        .set(mapOf(
                            "name" to "Raman",
                            "email" to "svnbvbsdv@gmail.com",
                            "profile_pic" to "$it"
                        ))

                   /* firestore
                        .collection("Users")
                        .document("bvhsbvhsbvksjdbvjsdb")
                        .collection("profile_pictures")
                        .document("exbPFSNazdz7iTDUO2hg")
                        .delete()*/


                }.addOnFailureListener {

                }


            }.addOnFailureListener {
                Log.d("Failure", "${it.message}")
                it.printStackTrace()
            }
    }


}
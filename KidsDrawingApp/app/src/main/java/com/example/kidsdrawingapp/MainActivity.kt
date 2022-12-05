package com.example.kidsdrawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.iterator

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null
    private var ibBrushSelector: ImageButton? = null
    private var mSelectedPaintBtn: ImageButton? = null

    var openGalleryLauncher:ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ){
        result->
        if(result.resultCode == RESULT_OK && result.data!=null){
            var imageBg :ImageView = findViewById(R.id.iv_background)
            imageBg.setImageURI(result.data!!.data)
        }
    }

    private var requestPermission:ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){
        permissions->
        Log.i("Permission dekho",permissions.toString())
        permissions.entries.forEach{
            val permissionName = it.key
            val isGranted = it.value

            if(isGranted){
                Toast.makeText(this,"Permission granted, now you can read files",Toast.LENGTH_LONG).show()
                val pickIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                openGalleryLauncher.launch(pickIntent)
            }
            else{
                if(permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
                    Toast.makeText(this,"Permission not granted",Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setBrushSize(20.0f)
        ibBrushSelector = findViewById(R.id.ib_brush)
        ibBrushSelector?.setOnClickListener {
            displayBrushSizeDialog()
        }

        var llPaintColors: LinearLayout = findViewById(R.id.ll_paint_colors)
        mSelectedPaintBtn = llPaintColors[1] as ImageButton
        mSelectedPaintBtn!!.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.palette_pressed
            )
        )

        for (btn in llPaintColors) {
            btn.setOnClickListener {
                changeColor(it)
            }
        }

        var ibImagePickerBtn:ImageButton = findViewById(R.id.ib_image_picker)
        ibImagePickerBtn.setOnClickListener{
           requestStoragePermission()
        }

        var undoBtn :Button = findViewById(R.id.btn_undo)
        var redoBtn:Button  =findViewById(R.id.btn_redo)

        undoBtn.setOnClickListener(){
            drawingView?.onUndo()
        }

        redoBtn.setOnClickListener(){
            drawingView?.onRedo()
        }
    }


    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            showRationaleDialog("Kids drawing app","You shud give permission for accessing data")
        }
        else{
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    private fun displayBrushSizeDialog() {
        var brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.brush_size_selector)
        brushDialog.setTitle("Brush Size: ")

        var ibSmall: ImageButton = brushDialog.findViewById(R.id.ib_small)
        var ibMedium: ImageButton = brushDialog.findViewById(R.id.ib_medium)
        var ibLarge: ImageButton = brushDialog.findViewById(R.id.ib_large)

        ibSmall.setOnClickListener {
            drawingView?.setBrushSize(10.0f)
            brushDialog.dismiss()
        }

        ibMedium.setOnClickListener {
            drawingView?.setBrushSize(20.0f)
            brushDialog.dismiss()
        }
        ibLarge.setOnClickListener {
            drawingView?.setBrushSize(30.0f)
            brushDialog.dismiss()
        }
        brushDialog.show()


    }

    private fun changeColor(view: View?) {

        if (view == mSelectedPaintBtn) return

        var currentColorBtn = view as ImageButton
        drawingView?.setPaintColor(currentColorBtn!!.tag.toString())

        currentColorBtn.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.palette_pressed)
        )

        mSelectedPaintBtn?.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.palette_normal)
        )

        mSelectedPaintBtn = view
    }

    private fun showRationaleDialog(title:String,message:String){
        var alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(title).setMessage(message).setPositiveButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alertBuilder.create().show()
    }
}